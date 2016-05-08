package com.alexyey.rwitter.message;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alexyey.rwitter.dao.MessageDao;
import com.alexyey.rwitter.exception.MessageException;
import com.alexyey.rwitter.exception.UnAuthorizedException;
import com.alexyey.rwitter.exception.UserException;
import com.alexyey.rwitter.model.Message;

@CrossOrigin(origins = "*")
@RestController
public class MessageController {


	@Autowired
	MessageManager manager;

	/*Endpoint used to recover a list of replies*/
	@RequestMapping("message/{messageId}/replies")
	public List<Message> getRepliesList (@PathVariable Long messageId) throws MessageException {
		return manager.getRepliesList(messageId);
	}

	/*Endpoint to insert a message, if the owner of the message and the currently
	 * logged user are not the same a 401 error is thrown*/
	@RequestMapping(value = "message/insert", method= RequestMethod.POST)
	public Message insertMessage (HttpServletResponse response,
			@RequestBody Message message) 
					throws MessageException, UnAuthorizedException {
		try {
			String userTag = (String) SecurityContextHolder.
					getContext().getAuthentication().getPrincipal();
			String creatorId = message.getCreatorId();
			if (creatorId == null ||
					(!creatorId.equals(userTag))) {
				throw new UnAuthorizedException(response);
			} else {
				return manager.insert(message);
			}
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new UnAuthorizedException(response);
		}
	}

	/*Endpoint used to delete a message, fi the owner and the currently logged
	 * user are not the same a 401 error is thrown*/
	@RequestMapping(value = "message/{messageId}" , method = RequestMethod.DELETE)
	public String deleteMessage(HttpServletResponse response,
			@PathVariable Long messageId)  throws MessageException, UnAuthorizedException {
		try {
			String userTag = (String) SecurityContextHolder.
					getContext().getAuthentication().getPrincipal();
			String reply = manager.delete(messageId,userTag);
			if (reply == null) {
				throw new UnAuthorizedException(response);
			} else {
				return reply;
			}
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new UnAuthorizedException(response);
		}
	}

	/*Handles MessageException, and return an appropriate message*/
	@ExceptionHandler({MessageException.class})
	public String userError(MessageException e) {
		switch (e.getMessage()) {
		case "NotExists": return "This Message Does Not Exists!";
		case "NotValid": return "Message Data Not Valid!";
		case "AlreadyUsed": return "The messageId is already used!";
		}
		return null;
	}

	/*If this Exception is recovered the response will be provided
	 * so that Error 401 is the result
	 */
	@ExceptionHandler({UnAuthorizedException.class})
	public void authError(UnAuthorizedException e) {
		try {
			e.getRespose().sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
	}

}



