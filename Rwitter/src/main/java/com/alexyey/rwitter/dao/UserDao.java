package com.alexyey.rwitter.dao;

import java.util.List;
import java.util.Map;

import com.alexyey.rwitter.model.Message;
import com.alexyey.rwitter.model.User;

/* interface for the DAO related to the User Entity */
public interface UserDao {
	
	/*Method that selects an user using the provided credentials, returning a null
	    * value if there is no user recovered by the means of them
	 */
	public User authenticate(String username, String password);
	
	/* Method that retrieve the informations of a user indentified by
	 * userTag, returning a null value if no user is found
	   */
	public User retrieve(String userId);

    public Map<String,User> retrieveFollowers(String followedId);
    
    /* Method that creates the row in user_followed table using userTag as follower
	 *  and followedId as followed
	    */
	public void follow(String userTag, String followedId);

	/* Method that deletes the row in user_followed table identified by 
	    * userTag (follower) and followedId (followed)
	    */
	public void unfollow(String userTag, String followedId);

	/* Method that provides the list of the users followed by the one
	    * identified by followerId
	 */
	public Map<String,User> retrieveFollowed(String userId);
	
	/*Method that provides the list of ids of the followed users of the one selected
	    * by userId
	*/
	public List<String> retrieveFollowedTagList(String userId);

	/*Method that provides the Integer count for the followers of an user identiified
	    * by followedId that userTag, in case no element is found a null value is returned.
	 */
	public Integer retrieveFollowersCount(String userTag, String followedId);
	
	/*Method that inserts the provided user, setting up the password
	    * with the provided name (for prototypal purposes)
	 */
	public User insert(User user);
	
	/* Method that provide a validation for the selected credentials, recovering
	    * the count on the user table for the provided elements and returning a
	    * positive statement if the count equals zero.
	*/
	public void delete(String userId);
	
}
