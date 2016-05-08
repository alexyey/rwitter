package com.alexyey.rwitter.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alexyey.rwitter.exception.UserException;
import com.alexyey.rwitter.message.MessageDaoImpl;
import com.alexyey.rwitter.model.Message;
import com.alexyey.rwitter.model.User;

@Component
public class UserManager {

	@Autowired
	UserDaoImpl userDao;
	
	@Autowired
	MessageDaoImpl messageDao;
	
	/* Follower Removing method, it takes the logged user and 
	 * the followerId as parameters and updates their followers
	 * list for the user selected by followedId and updates the
	 * followed list for the logged user. it returns a message
	   or an exception in case the user it is already following
	   the selected user, or if such user does not exists
	 */
	public String follow(String userTag, String followedId) throws UserException {
		
		
		User followedUser = userDao.retrieve(followedId);
		if (followedUser != null) {
			Integer count = userDao.retrieveFollowersCount(userTag,followedId);
			if (count == 1) {
				throw new UserException("FollowedUser");
			} else {
				userDao.follow(userTag, followedId);
				return "Now following User: " + followedId;
			}
		} else {
			throw new UserException("NotExists");
		}
		
		
	}
	
	/* Follower Removing method, it takes the logged user and 
	 * the followerId as parameters and updates their followers
	 * list for the user selected by followedId and updates the
	 * followed list for the logged user. it returns a message
	   or an exception in case the user it is not already following
	   the selected user, or if such user does not exists
	 */
	public String unfollow(String userTag, String followedId) throws UserException {
		
		User followedUser = userDao.retrieve(followedId);
		if (followedUser != null) {
			Integer count = userDao.retrieveFollowersCount(userTag,followedId);
			if (count == 0) {
				throw new UserException("NotFollowing");
			} else {
				userDao.unfollow(userTag,followedId);
				return "You've stopped following User: " + followedId;
			}
		} else {
			throw new UserException("NotExists");
		}
		
	}
	
	/* Method that retrieves the list of followers of the user identified
	 * by userId, if an user with such id does not exists an appropriate
	 * message within UserException is thrown, otherwise returns a list,
	 * even empty of followes
	 */
	public List<User> getFollowerList(String userId) throws UserException {
		
		User user = userDao.retrieve(userId);
		if (user != null) {
			Map<String,User> followers = userDao.retrieveFollowers(userId);
			if (followers != null) {
				return new ArrayList<User>(followers.values());
			} else {
				return new ArrayList<User>();
			}
		} else {
			throw new UserException("NotExists");
		}
	}
	
	/* Method that retrieves the list of users followed by the one identified
	 * by userId, if an user with such id does not exists an appropriate
	 * message within UserException is thrown, otherwise returns a list,
	 * even empty of followed users
	 */
    public List<User> getFollowedList(String userId) throws UserException {
    	
		User user = userDao.retrieve(userId);
		if (user != null) {
			Map<String,User> followed = userDao.retrieveFollowed(userId);
			if (followed != null) {
				return new ArrayList<User>(followed.values());
			} else {
				return new ArrayList<User>();
			}
		} else {
			throw new UserException("NotExists");
		}
	}

    /* Method that in case the user selected with userId exists it returns a list of messages
     * related to the user and the ones it follows. in case the user does not exists return 
     * an appropriate exception
     */
	public List<Message> recoverMessages(String userId, String search) throws UserException {
		
		User user = userDao.retrieve(userId);
		if (user != null) {
			/* Retrieve the list of followed key tags in order to
			 * procede to a general extraction of the selected 
			 * elements
			 */
			List<String> userTagList = userDao.retrieveFollowedTagList(userId);
			userTagList.add(userId);
			/*Retrieve the requested messages*/
			return messageDao.retrieveMessages(userTagList, search);
		} else {
			throw new UserException("NotExists");
		}
		
	}

	/* Attempt to retrieve an user using username and password, returnin the related
	 * data if found, or sendind an UserException if nothing is found
	 */
	public User authenticate(String username, String password) throws UserException {
		User user = userDao.authenticate(username, password);
		if (user != null) {
			return user;
		} else {
			throw new UserException("NotValid");
		}
	}


	/*Attempt to remove the user identified by userTag, and if
	 * there is not a user with such id it throws an UserException
	 */
	public String delete(String userTag) throws UserException {
		User user = userDao.retrieve(userTag);
		if (user != null) {
			userDao.delete(userTag);
			return "User " + userTag + " correctly removed";
		} else {
			throw new UserException("NotExists");
		}
	}
	
	/*Attempt to insert an user, if there is already an user with the
	 * provided mail or userTag it throws an appropriate UserException
	 */
	public User insert(User user) throws UserException {
		
		if (userDao.validate(user.getUserTag(),user.getEmail())) {
			userDao.insert(user);
			return user;
		} else {
			throw new UserException("NotValidInsert");
		}
	}
	
}
