package com.alexyey.rwitter.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.alexyey.rwitter.dao.MessageDao;
import com.alexyey.rwitter.mapper.MessageRowMapper;
import com.alexyey.rwitter.model.Message;

@Component
@Repository
@SuppressWarnings(value = { "rawtypes","unchecked" })
/* implementation for the MessageDao interface using namedParameterJdbcTemplate*/
public class MessageDaoImpl implements MessageDao{

	 @Autowired
	 private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	 /*Method that retrieves the messages created by the users with the id contained into the list users,
	  * being optionally filtered using search param
	  */
	 @Override
	   public List<Message> retrieveMessages(List<String> users, String search) {
		   String query = "SELECT DISTINCT m.message_id, m.content, m.creator_id, m.parent_id " +
					   " FROM message m LEFT JOIN message_keyword mk ON m.message_id = mk.message_id  " +
				       " WHERE m.creator_id IN (:users) AND " +
					   " ((:search IS null) OR (mk.keyword = :search))";
		   Map namedParameters = new HashMap();
		   List<Message> messages = new ArrayList<Message>();
		   namedParameters.put("users", users);
		   namedParameters.put("search", search);
		   namedParameterJdbcTemplate.queryForList(query, namedParameters).forEach(
				   row->messages.add(new Message((Long) row.get("message_id"),
						   			(String) row.get("content"),
						   			(String) row.get("creator_id"),
						   			(Long) row.get("parent_id"))));
		   return messages;
	   }

	 /* Method that retrieves the message identified by messageId*/
	@Override
	public Message retrieve(Long messageId) {
		String query = "SELECT * from message WHERE message_id = :messageId";
		Map namedParameters = new HashMap();
		namedParameters.put("messageId", messageId);
		try {
			return namedParameterJdbcTemplate.queryForObject(query, 
					namedParameters, new MessageRowMapper());
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/* Method that recovers the replies of a message identified by parentId*/
	@Override
	public List<Message> retrieveReplies(Long parentId) {
		String query = "SELECT m.content, m.creator_id, m.parent_id " + 
	                       "from message m WHERE parent_id = :messageId";
		Map namedParameters = new HashMap();
		namedParameters.put("messageId", parentId);
		List<Message> messages = new ArrayList<Message>();
        namedParameterJdbcTemplate.queryForList(query, namedParameters).forEach(
				   row->messages.add(new Message((Long) row.get("message_id"),
						   			(String) row.get("content"),
						   			(String) row.get("creator_id"),
						   			(Long) row.get("parent_id"))));
        return messages;
	}
	
	/* Method that deletes the keywords of a message identified by messageId*/
	@Override
	public void deleteKeywords(Long messageId) {
		String query = "Delete From message_keyword Where message_id = :messageId";
		Map namedParameters = new HashMap();
		namedParameters.put("messageId", messageId);
		namedParameterJdbcTemplate.update(query, namedParameters);
	}

	/* Method that deletes a message identified by messageId*/
	@Override
	public void delete(Long messageId) {
		deleteKeywords(messageId); //first remove the keywords
		String query = "Delete From message Where message_id = :messageId";
		Map namedParameters = new HashMap();
		namedParameters.put("messageId", messageId);
		namedParameterJdbcTemplate.update(query, namedParameters);
	}

	/* Method that inserts a message and recovers the generated key*/
	@Override
	public Message insert(Message message) {
		 String query = "INSERT INTO message (message_id,content,creator_id,parent_id)" 
				       + "VALUES (:messageId,:content,:creatorId,:parentId)";
		 Map namedParameters = new HashMap();
		 namedParameters.put("messageId", message.getMessageId());
         namedParameters.put("content", message.getContent());
		 namedParameters.put("creatorId", message.getCreatorId());
		 namedParameters.put("parentId", message.getParentId());
		 namedParameterJdbcTemplate.update(query,namedParameters);
		 return message;
	}

	
   /*Method that inserts as a batchUpdate the keywords extracted from a message
    * into the related table
    */
   @Override
	public void insertKeyword(Long messageId, String keyword) {
	   String query = "INSERT INTO message_keyword (message_id, keyword) VALUES (:messageId,:keyword)";
	 
					
	   		Map namedParameters = new HashMap();
	   		namedParameters.put("messageId", messageId);
	   		namedParameters.put("keyword", keyword);
			
			namedParameterJdbcTemplate.update(query,namedParameters);
	}

   /*Method that checks if the selected id is valid
    (for prototype purposes only)
    */
   public boolean validate(Long messageId) {
		String query = "SELECT Count(*) from message m WHERE message_id = :messageId";
		Map namedParameters = new HashMap();
		namedParameters.put("messageId", messageId);
		Boolean isValid = namedParameterJdbcTemplate.
				queryForObject(query, namedParameters, Integer.class) == 0 ? true : false;
		return isValid;

   }

}



