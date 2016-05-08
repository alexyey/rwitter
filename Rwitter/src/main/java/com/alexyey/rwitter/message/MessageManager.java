package com.alexyey.rwitter.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alexyey.rwitter.exception.MessageException;
import com.alexyey.rwitter.model.Message;

@Component
public class MessageManager {

	@Autowired
	MessageDaoImpl messageDao;

	/* Method that recovers the list of replies for a message identified by messageId,
	 * throwing NotExists if there is no message identified by the provided id
	 */
	public List<Message> getRepliesList(Long messageId) throws MessageException {
		Message message = messageDao.retrieve(messageId);
		if (message != null) {
			return messageDao.retrieveReplies(messageId);			
		} else {
			throw new MessageException("NotExists");
		}
	}

	/* Method that manage the insertion of a new message, the operation
	 * includes the detection of the keywords of the message (assuming in
	 * the prototype that every word is separated by a blank space,
	 * and updates the related table using a simple method (The parallel
	 * stream and single entry are used under the assumption that the
	 * number of elements that required insertion, considering the whole
	 * number of words are few enough to not require strictly a batch
	 * update solution (Prototype only)
	 */
	public Message insert(Message message) throws MessageException {
		try {
			/*Verifying if there is already a message with the id*/
			if (!messageDao.validate(message.getMessageId()))
				throw new MessageException("AlreadyUsed");
			/* If there is a parentId verifies if it is present
			 * on the database
			 */
			Long parentId = message.getParentId();
			if (parentId != null && 
					messageDao.retrieve(parentId) == null)
				throw new MessageException("NotExists");
			/* Insert the message and every keyword extracted*/
			messageDao.insert(message);
			String content = message.getContent();
			Stream<String> messageContentStream = Arrays.stream(content.split(" "));
			messageContentStream.parallel().filter
			     (chunck -> chunck.contains("@"))
			     .forEach(key -> messageDao
			    		 .insertKeyword(message.getMessageId(),key));
			return message;
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException("NotValid");
		}
	}

	/* Method that manage the delete of a message identified by messageId. It
	 * throws appropriate exceptions if a message with such an id does not exists.
	 * If the creator of the message is not the currently logged user it return a 
	 * null value, not proceeding further. The positive response return a message.
	 */
	public String delete(Long messageId,String userTag) throws MessageException {
		Message message = messageDao.retrieve(messageId);
		if (message != null) {
			if (!message.getCreatorId()
					.equals(userTag)) { 
				return null;
			} else {
				messageDao.delete(messageId);
				return "This Message Has Been Deleted";
			}
		} else {
			throw new MessageException("NotExists");
		}
	}

	
	
}
