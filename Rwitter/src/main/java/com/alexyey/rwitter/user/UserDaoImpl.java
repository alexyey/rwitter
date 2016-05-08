package com.alexyey.rwitter.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tomcat.util.net.jsse.openssl.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.alexyey.rwitter.dao.UserDao;
import com.alexyey.rwitter.mapper.UserRowMapper;
import com.alexyey.rwitter.model.Message;
import com.alexyey.rwitter.model.User;

@Component
@Repository
@SuppressWarnings(value = { "rawtypes","unchecked" })
/* implementation for the UserDao interface using namedParameterJdbcTemplate*/
public class UserDaoImpl implements UserDao {


	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/* Method that retrieve the informations of a user indentified by
	 * userTag, returning a null value if no user is found
	    */
	@Override
	public User retrieve(String userId) {
		String query = "SELECT * from user u WHERE user_tag = :userTag";
		Map namedParameters = new HashMap();
		namedParameters.put("userTag", userId);
		try {
			return namedParameterJdbcTemplate.queryForObject(query, namedParameters, new UserRowMapper());
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/* Method that provides the list of the users following the one
	    * identified by followedId
	    */
	@Override
	public Map<String, User> retrieveFollowers(String followedId) {
    	String query = "SELECT f.user_tag, f.name, f.surname, f.age, f.email FROM user u JOIN user_followed " + 
    							" uf ON u.user_tag = uf.followed_id JOIN "  + 
    			             	" user f ON uf.follower_id = f.user_tag "  +
    			     			" WHERE u.user_tag = :followedId ";
    	
		Map namedParameters = new HashMap();
	   	Map<String,User> followers = new HashMap<String,User>();
	   	namedParameters.put("followedId", followedId);
 	   	namedParameterJdbcTemplate.queryForList(query, namedParameters).forEach(
 	   			row->followers.put((String)row.get("user_tag"),
 	   				new User((String)row.get("user_tag"),
 		   					 (String)row.get("name"),
 		   					 (String)row.get("surname"),
 		   					 (Integer)row.get("age"),
 		   					 (String)row.get("email"))));
	   	return followers;
    }

	/* Method that creates the row in user_followed table using userTag as follower
	 *  and followedId as followed
	    */
   @Override
   public void follow(String userTag, String followedId) {
	   String query = "INSERT INTO user_followed (follower_id, followed_id) VALUES (:userTag,:followedId)";
	   Map namedParameters = new HashMap();
	   namedParameters.put("userTag", userTag);
	   namedParameters.put("followedId", followedId);
	   namedParameterJdbcTemplate.update(query, namedParameters);
   }

   /* Method that deletes the row in user_followed table identified by 
    * userTag (follower) and followedId (followed)
    */
   @Override
   public void unfollow(String userTag, String followedId) {
	   String query = "DELETE FROM user_followed WHERE follower_id = :userTag AND " +
			   		  								   " followed_id = :followedId";
	   Map namedParameters = new HashMap();
	   namedParameters.put("userTag", userTag);
	   namedParameters.put("followedId", followedId);
	   namedParameterJdbcTemplate.update(query, namedParameters);
	}
   
   /* Method that provides the list of the users followed by the one
    * identified by followerId
    */
   @Override
   public Map<String, User> retrieveFollowed(String followerId) {
	   String query = "SELECT u.user_tag, u.name, u.surname, u.age, u.email FROM user u JOIN user_followed " + 
				 " uf ON u.user_tag = uf.followed_id JOIN "  + 
				 " user f ON uf.follower_id = f.user_tag "  +
				 " WHERE f.user_tag = :followedId";
	    Map namedParameters = new HashMap();
	   	Map<String,User> followed = new HashMap<String,User>();
	   	namedParameters.put("followedId", followerId);
	   	namedParameterJdbcTemplate.queryForList(query, namedParameters).forEach(
	   			row->followed.put((String)row.get("user_tag"),
	   			new User((String)row.get("user_tag"),
	   					 (String)row.get("name"),
	   					 (String)row.get("surname"),
	   					 (Integer)row.get("age"),
	   					 ((String) row.get("email")))));
	   	return followed;
   }
   
   /*Method that provides the list of ids of the followed users of the one selected
    * by userId
    */
   @Override
   public List<String> retrieveFollowedTagList(String userId) {
	   String query = "SELECT followed_id FROM user_followed WHERE follower_id = :userTag";
	   Map namedParameters = new HashMap();
	   namedParameters.put("userTag", userId);
	   return namedParameterJdbcTemplate.queryForList(query, namedParameters,String.class);
   }

  
   /*Method that selects an user using the provided credentials, returning a null
    * value if there is no user recovered by the means of them
    */
   @Override
   public User authenticate(String userTag, String password) {
	   String query = "SELECT * from user WHERE user_tag = :userTag AND "+
			   		                            " password = :password";
	   Map namedParameters = new HashMap();
	   namedParameters.put("userTag", userTag);
	   namedParameters.put("password", password);
	   try {
		   return namedParameterJdbcTemplate.queryForObject(query, namedParameters, new UserRowMapper());
	   } catch(EmptyResultDataAccessException e) {
			return null;
	   }
   }


   /*Method that provides the Integer count for the followers of an user identiified
    * by followedId that userTag, in case no element is found a null value is returned.
    */
   @Override
   public Integer retrieveFollowersCount(String userTag, String followedId) {
	   String query = "Select Count(follower_id) From user_followed" +
                       " WHERE follower_id = :userTag " +
			           " AND followed_id = :followedId";
		Map namedParameters = new HashMap();
		namedParameters.put("userTag", userTag);
		namedParameters.put("followedId", followedId);
		try {
			return namedParameterJdbcTemplate.queryForObject(query, namedParameters, Integer.class);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
   }

   /*Method that inserts the provided user, setting up the password
    * with the provided name (for prototypal purposes)
    */
   @Override
   public User insert(User user) {
	   String query = "INSERT INTO user (user_tag,"+
					   "name,password,surname,age,"+
					   "email) VALUES (:userTag,:name,"+
					   ":password,:surname,:age,:email)";
	   Map namedParameters = new HashMap();
	   namedParameters.put("userTag", user.getUserTag());
	   namedParameters.put("name", user.getName());
	   namedParameters.put("surname", user.getSurname());
	   namedParameters.put("age", user.getAge());
	   namedParameters.put("email", user.getEmail());
	   namedParameters.put("password", user.getName());
	   namedParameterJdbcTemplate.update(query, namedParameters);
	   return user;
   }


   /*Method that deletes the user with the provided userTag*/
   @Override
   public void delete(String userTag) {
	   String query = "Delete From user Where user_tag = :userTag";
	   Map namedParameters = new HashMap();
	   namedParameters.put("userTag", userTag);
	   namedParameterJdbcTemplate.update(query, namedParameters);
   }


   /* Method that provide a validation for the selected credentials, recovering
    * the count on the user table for the provided elements and returning a
    * positive statement if the count equals zero.
    */
   public boolean validate(String userTag, String email) {
		String query = "SELECT Count(*) from user u WHERE user_tag = :userTag OR email = :email";
		Map namedParameters = new HashMap();
		namedParameters.put("userTag", userTag);
		namedParameters.put("email", email);
		Boolean isValid = namedParameterJdbcTemplate.
				queryForObject(query, namedParameters, Integer.class) == 0 ? true : false;
		return isValid;
   }

}
