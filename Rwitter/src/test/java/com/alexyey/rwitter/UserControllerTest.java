package com.alexyey.rwitter;

	
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.alexyey.rwitter.RwitterAppBoot;
import com.alexyey.rwitter.model.User;
import com.fasterxml.jackson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

	
/*Test suite for the User related enpoints, it uses a fixed order having some dependency between some of the test 
 * units, it has been choosed to test the whole set of endpoints in a integration-like flux
 *  rather than considering a strict indipendency
 * of the units
 */

	@RunWith(SpringJUnit4ClassRunner.class)
	@SpringApplicationConfiguration(classes = RwitterAppBoot.class)
	@WebAppConfiguration
	@SuppressWarnings("unchecked")
	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public class UserControllerTest {


	    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
	            MediaType.APPLICATION_JSON.getSubtype(),
	            Charset.forName("utf8"));
	    
	    private MediaType messageType = new MediaType(MediaType.TEXT_PLAIN.getType(),
	    										    MediaType.TEXT_PLAIN.getSubtype(),
	    										    Charset.forName("utf8"));

	    private MockMvc mockMvc;
	    private ObjectMapper mapper;
	  

	    @SuppressWarnings("rawtypes")
		private HttpMessageConverter mappingJackson2HttpMessageConverter;

	    
	    @Autowired
	    private WebApplicationContext webApplicationContext;

	  

	    @Autowired
	    void setConverters(HttpMessageConverter<?>[] converters) {

	        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
	                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

	        Assert.assertNotNull("the JSON message converter must not be null",
	                this.mappingJackson2HttpMessageConverter);
	    }

	    @Before
	    public void setup() throws Exception {
	        this.mockMvc = webAppContextSetup(webApplicationContext).build();
	        this.mapper = new ObjectMapper();
	        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	       

	    }
	    
	    /* Test the login endpoint*/
	    @Test 
	    public void test_0_login() throws Exception {
	    	
	    	/* Try to access a nonexisting user*/
	    	this.mockMvc.perform(post("/login")
	    			            .param("username", "Delhi")
	    			            .param("password", "Ala"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Not Valid Credentials!")));
	    	
	    	/* Accessing different users*/
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Chor")
		            .param("password", "Claire"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.userTag", is("Chor")))
            .andExpect(jsonPath("$.name", is("Claire")))
            .andExpect(jsonPath("$.age", is(28)));
	    	
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Bhel")
		            .param("password", "Bob"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.userTag", is("Bhel")))
            .andExpect(jsonPath("$.name", is("Bob")))
            .andExpect(jsonPath("$.age", is(25)));
	    	
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Alix")
		            .param("password", "Alice"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.userTag", is("Alix")))
            .andExpect(jsonPath("$.name", is("Alice")))
            .andExpect(jsonPath("$.age", is(32)));
	    	
	    	/* Trying to access an existing user with the wrong
	    	 * password*/
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Alix")
		            .param("password", "Bob"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Not Valid Credentials!")));
	    	
	    }
	    
	    @Test
	    public void test_0_insert() throws Exception {
	    	
	    	/*Insertion of a new user*/
	    	User user = new User("Dehli","Donny","D",23,"D");
	    	this.mockMvc.perform(post("/register")
	    				.contentType(contentType)
	    				.content(mapper.writer()
	    						.withDefaultPrettyPrinter()
	    						.writeValueAsBytes(user)))
	    	.andExpect(status().isOk());
	    	
	    	/* Trying to insert users with already set username or
	    	 * email
	    	 */
	    	user = new User("Dehli2","Donny2","D2",23,"D");
	    	this.mockMvc.perform(post("/register")
	    				.contentType(contentType)
	    				.content(mapper.writer()
	    						.withDefaultPrettyPrinter()
	    						.writeValueAsBytes(user)))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Username or Email Already Used!")));
	    	
	    	user = new User("Dehli","Donny2","D2",23,"D2");
	    	this.mockMvc.perform(post("/register")
	    				.contentType(contentType)
	    				.content(mapper.writer()
	    						.withDefaultPrettyPrinter()
	    						.writeValueAsBytes(user)))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Username or Email Already Used!")));
	    }
	    
	    @Test
	    public void test_1_delete() throws Exception {
	    	
	    	/*Trying to perform the delete of an user
	    	 * while unauthorized (logged with Alix)
	    	 */
	    	this.mockMvc.perform(delete("/user/Dehli"))
	    	.andExpect(status().isUnauthorized());
	    	
	    	/*Trying to perform the delete while
	    	 * logged with the proper user
	    	 */
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Dehli")
		            .param("password", "Donny"))
	    	.andExpect(status().isOk());
	    	
	    	this.mockMvc.perform(delete("/user/Dehli"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
			.andExpect(content().string(is("User Dehli correctly removed")));
	    	
	    	/*Trying to perform the delete of an user
	    	 * already deleted
	    	 */
	    	this.mockMvc.perform(delete("/user/Dehli"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
			.andExpect(content().string(is("This User Does Not Exists!")));
	    	
	    	/* Attempt to login with an unexisting user
	    	 */
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Delhi")
		            .param("password", "Donny"))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(messageType))
	    			.andExpect(content().string(is("Not Valid Credentials!")));
	    	
			this.mockMvc.perform(post("/login")
		            .param("username", "Alix")
		            .param("password", "Alice"))
			.andExpect(status().isOk());
	    	
			/*Trying to perform the delete of an user
	    	 * while unauthorized (logged with Alix)
	    	 */
			this.mockMvc.perform(delete("/user/Dehli"))
	    	.andExpect(status().isUnauthorized());
	    }

	   
	    @Test
		public void test_2_getFollowed() throws Exception {
			
	    	/* Trying to perform the request with an unexisting user*/
			this.mockMvc.perform(get("/user/Delhi/followed"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(messageType))
		    .andExpect(content().string(is("This User Does Not Exists!")));
		    
	    	/* Verifying the lists of several users*/
			this.mockMvc.perform(get("/user/Alix/followed")
		            .contentType(contentType))
					.andExpect(status().isOk())
					.andExpect(content().contentType(contentType))
					.andExpect(jsonPath("$", hasSize(1)))
					.andExpect(jsonPath("$[0].userTag", is("Bhel")));
			
			this.mockMvc.perform(get("/user/Bhel/followed")
		            .contentType(contentType))
					.andExpect(status().isOk())
					.andExpect(content().contentType(contentType))
					.andExpect(jsonPath("$", hasSize(2)))
					.andExpect(jsonPath("$[0].userTag", is("Alix")))
					.andExpect(jsonPath("$[1].userTag", is("Chor")));
			
			this.mockMvc.perform(get("/user/Chor/followed")
		            .contentType(contentType))
					.andExpect(status().isOk())
					.andExpect(content().contentType(contentType))
					.andExpect(jsonPath("$", hasSize(0)));
		}

		@Test
	    public void test_4_getFollowers() throws Exception {
	        
	    	/* Trying to perform the request with an unexisting user*/
	    	this.mockMvc.perform(get("/user/Delhi/followers"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("This User Does Not Exists!")));
	        
	    	/* Verifying the lists of several users*/
	    	this.mockMvc.perform(get("/user/Alix/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Bhel")));
	    	
	    	this.mockMvc.perform(get("/user/Bhel/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Alix")));
	    	
	    	this.mockMvc.perform(get("/user/Chor/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Bhel")));
	    }
	    
	    @Test
	    public void test_5_follow() throws Exception {
	    	
	    	/* Trying to perform the request with the same user*/
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Alix")
		            .param("password", "Alice"))
	    	.andExpect(status().isOk());
	        
	    	this.mockMvc.perform(post("/user/Alix/follow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Not a valid operation on the logged user!")));
	    	
	    	/* Trying to perform the request with an unexisting user*/
	    	this.mockMvc.perform(post("/user/Delhi/follow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("This User Does Not Exists!")));
	    	
	    	/* Trying to perform the request with an already followed user*/
	    	this.mockMvc.perform(post("/user/Bhel/follow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Already Following This User!")));
	    	
	    	/* Trying to perform the request and verifying the consistency
	    	 * of the lists on the test users*/
	    	this.mockMvc.perform(post("/user/Chor/follow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
	    	.andExpect(jsonPath("$", is("Now following User: Chor")));
	    	
	    	this.mockMvc.perform(get("/user/Chor/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(2)))
	    			.andExpect(jsonPath("$[1].userTag", is("Alix")));
	        
	    	this.mockMvc.perform(get("/user/Alix/followed")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(2)))
	    			.andExpect(jsonPath("$[1].userTag", is("Chor")));
	    	
	    	this.mockMvc.perform(get("/user/Bhel/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Alix")));
	    }
	    
	    @Test
	    public void test_6_unfollow() throws Exception {
	    	
	    	/* Trying to perform the request with the same user*/
	    	this.mockMvc.perform(post("/user/Alix/unfollow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("Not a valid operation on the logged user!")));
	    	
	    	/* Trying to perform the request with an unexisting user*/
	    	this.mockMvc.perform(post("/user/Delhi/unfollow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("This User Does Not Exists!")));
	    	
	    	/* Trying to perform the request and verifying the consistency
	    	 * of the lists on the test users*/
	    	this.mockMvc.perform(post("/user/Chor/unfollow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
	    	.andExpect(jsonPath("$", is("You've stopped following User: Chor")));
	    	
	    	this.mockMvc.perform(get("/user/Chor/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Bhel")));
	        
	    	this.mockMvc.perform(get("/user/Alix/followed")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Bhel")));
	    	
	    	this.mockMvc.perform(get("/user/Bhel/followers")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].userTag", is("Alix")));
	    	
	    	/* Attempt on an user not followed by the currently logged User*/
	    	this.mockMvc.perform(post("/user/Chor/unfollow"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
	    	.andExpect(jsonPath("$", is("Not Following This User!")));
	    	
	    }
	    
	    
	    @Test
	    public void test_7_getMessages() throws Exception {
	        
	    	/* Trying to perform the request with an unexisting user*/
	    	this.mockMvc.perform(get("/user/Delhi/messages"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(messageType))
            .andExpect(content().string(is("This User Does Not Exists!")));
	    	
	    	/* Verifiying several requests using different users and search params*/
	    	this.mockMvc.perform(get("/user/Bhel/messages")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(5)))
	    			.andExpect(jsonPath("$[0].content",is("What a lovely day! @Lovely @Day")))
	    	        .andExpect(jsonPath("$[1].content",is("What a lovely cake! @Lovely @Cake")))
	    	        .andExpect(jsonPath("$[2].content",is("Cake is a Lie! @Cake @Lie")))
	    	        .andExpect(jsonPath("$[2].creatorId",is("Alix")))
	    	        .andExpect(jsonPath("$[3].content",is("Hello World! @Hello @World")))
	    	        .andExpect(jsonPath("$[3].creatorId",is("Chor")))
	    	  		.andExpect(jsonPath("$[4].content",is("Hi There!")))
	    	  		.andExpect(jsonPath("$[4].creatorId",is("Chor")));
	    	
	    	this.mockMvc.perform(get("/user/Bhel/messages?search=@Cake")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(2)))
	    	        .andExpect(jsonPath("$[0].content",is("What a lovely cake! @Lovely @Cake")))
	    	        .andExpect(jsonPath("$[1].content",is("Cake is a Lie! @Cake @Lie")))
	    	        .andExpect(jsonPath("$[1].creatorId",is("Alix")));
	    	
	    	this.mockMvc.perform(get("/user/Chor/messages")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(2)))
	    			.andExpect(jsonPath("$[0].content",is("Hello World! @Hello @World")))
	    	        .andExpect(jsonPath("$[0].creatorId",is("Chor")))
	    	        .andExpect(jsonPath("$[1].content",is("Hi There!")))
	    			.andExpect(jsonPath("$[1].creatorId",is("Chor")));
	    	
	    	this.mockMvc.perform(get("/user/Chor/messages?search=@Hello")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].content",is("Hello World! @Hello @World")))
	    	        .andExpect(jsonPath("$[0].creatorId",is("Chor")));
	    	
	    	this.mockMvc.perform(get("/user/Chor/messages?search=@Cake")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(0)));
	    	
	    	/* Operating a change in the followers list and verifiying the
	    	 * variations on the recovered lists
	    	 */
	    	this.mockMvc.perform(post("/login")
		            .param("username", "Chor")
		            .param("password", "Claire"));
	    	this.mockMvc.perform(post("/user/Alix/follow"));
	    	
	    	this.mockMvc.perform(get("/user/Chor/messages?search=@Cake")
	                .contentType(contentType))
	    			.andExpect(status().isOk())
	    			.andExpect(content().contentType(contentType))
	    			.andExpect(jsonPath("$", hasSize(1)))
	    			.andExpect(jsonPath("$[0].content",is("Cake is a Lie! @Cake @Lie")))
	    	        .andExpect(jsonPath("$[0].creatorId",is("Alix")));
	    	
	    	this.mockMvc.perform(post("/user/Alix/unfollow"));
	    	

	    }

	   
		protected String json(Object o) throws IOException {
	        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
	        this.mappingJackson2HttpMessageConverter.write(
	                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
	        return mockHttpOutputMessage.getBodyAsString();
	    }
	}

