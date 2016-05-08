package com.alexyey.rwitter.user;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alexyey.rwitter.dao.UserDao;
import com.alexyey.rwitter.exception.UnAuthorizedException;
import com.alexyey.rwitter.exception.UserException;
import com.alexyey.rwitter.model.Message;
import com.alexyey.rwitter.model.User;

@CrossOrigin(origins = "*")
@RestController
public class UserController {



	@Autowired
	UserManager manager;

	/* Basic connection endpoint by the means of a username and password, 
	 * requested for further operations
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public User connect(@RequestParam(value="username") String username,
			@RequestParam(value="password") String password) throws UserException {
		User user = manager.authenticate(username,password);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				username, password,
				AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return user;
	}


	/* Endpoint used to unfollow an user, it will throw an exception if required
	 * on the current user, if there is no user identified by userId, or
	 * if the currently logged user already has it as a follwed user
	 */
	@RequestMapping(value = "user/{userId}/follow" , method = RequestMethod.POST)
	public String follow (HttpServletResponse response,
			@PathVariable String userId) 
					throws UserException, UnAuthorizedException {
		try {
			String userTag = (String) SecurityContextHolder.getContext().
					getAuthentication().getPrincipal();
			if (userId.equals(userTag))
				throw new UserException("SameUser");
			return manager.follow(userTag, userId);
		} catch (UserException e) {
			throw e;
		} catch (Exception e) {
			throw new UnAuthorizedException(response);
		}

	}

	/* Endpoint used to unfollow an user, it will throw an exception if required
	 * on the current user, if there is no user identified by userId, or
	 * if the currently logged user does not have it as a follwed user
	 */
	@RequestMapping(value = "user/{userId}/unfollow" , method = RequestMethod.POST)
	public String unfollow (HttpServletResponse response,
			@PathVariable String userId) 
					throws UserException, UnAuthorizedException {
		try {
			String userTag = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (userId.equals(userTag))
				throw new UserException("SameUser");
			return manager.unfollow(userTag, userId);
		} catch (UserException e) {
			throw e;
		} catch (Exception e) {
			throw new UnAuthorizedException(response);
		}
	}

	/*Endpoint used to recover the messages that has been created by the user or any
	 * user currently followed, filtering the content using the optional search parameter
	 */
	@RequestMapping(value = "user/{userId}/messages" )
	public List<Message> getMessages (@PathVariable String userId,
			@RequestParam(value = "search", 
			required = false) String search) throws UserException {
		return manager.recoverMessages(userId, search);
	}

	/*Endpoint used to recover the list of users followed by the user*/
	@RequestMapping("user/{userId}/followed")
	public List<User> getFollowedList (@PathVariable String userId) throws UserException {
		return manager.getFollowedList(userId);
	}

	/* Endpoint used to recover the list of followers of the user*/
	@RequestMapping("user/{userId}/followers")
	public List<User> getFollowersList (@PathVariable String userId) throws UserException {
		return manager.getFollowerList(userId);
	}

	@RequestMapping(value = "user/{userId}" ,method= RequestMethod.DELETE)
	public String delete(HttpServletResponse response,
			@PathVariable String userId) 
					throws UserException,UnAuthorizedException {
		try {
			String userTag = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (!userId.equals(userTag))
				throw new UnAuthorizedException(response);
			return manager.delete(userTag);
		} catch (UserException e) {
			throw e;
		} catch (Exception e) {
			throw new UnAuthorizedException(response);
		}

	}

	/*Method that is used to register a new User, if the provided
	 * userName or email has been already used an exception will
	 * be thrown. (Being a proptotype the password is created as 
	 * the provided name)
	 */
	@RequestMapping(value = "/register", method= RequestMethod.POST)
	public User insert(@RequestBody User user) throws UserException{
		return manager.insert(user);
	}

	/*Exception Handler for the UserController, it returns an appropriate
	 * message for the provided errors
	 */
	@ExceptionHandler({UserException.class})
	public String userError(UserException e) {
		switch (e.getMessage()) {
		case "NotExists": return "This User Does Not Exists!";
		case "NotValid": return "Not Valid Credentials!";
		case "NotFollowing": return "Not Following This User!";
		case "FollowedUser": return "Already Following This User!";
		case "SameUser": return "Not a valid operation on the logged user!";
		case "NotValidInsert": return "Username or Email Already Used!";
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



