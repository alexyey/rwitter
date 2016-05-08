package com.alexyey.rwitter.dao;

import java.util.List;

import com.alexyey.rwitter.model.Message;

/* interface for the DAO related to the Message Entity */
public interface MessageDao {

	/* Method used to retireve messages of a collection of users, filtering it
	 * optionally by search param
	 */
	public List<Message> retrieveMessages(List<String> users, String search);
	
	/* Method used to retrieve a message*/
	public Message retrieve(Long messageId);
	
	/* Method used to recover the replies of a message*/
	public List<Message> retrieveReplies(Long parentId);
	
	/*Method uset to delete a message identified by an id*/
	public void delete(Long messageId);
	
	/*Method used to delete the keywords of a message*/
	public void deleteKeywords(Long messageId);
	
	/* Method that inserts a message and recovers the generated key*/
	public Message insert(Message messageId);
	
	/*Method that inserts a keyword of a message
	    */
	void insertKeyword(Long messageId, String keyword);
	
}
