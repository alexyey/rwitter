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
import com.alexyey.rwitter.model.Message;
import com.alexyey.rwitter.model.User;
import com.fasterxml.jackson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RwitterAppBoot.class)
@WebAppConfiguration
@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageControllerTest {


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

	/*Test the insert endpoint*/
	@Test 
	public void test_0_insert() throws Exception {

		/* Attempt to perform an insert of a message
		 * with a nonexisting parent
		 */
		Message message = new Message();
		message.setContent("@Just For @Fun");
		message.setCreatorId("Alix");
		message.setMessageId((long) 6);
		message.setParentId((long) 7);

		this.mockMvc.perform(post("/message/insert")
				.contentType(contentType)
				.content(mapper.writer()
						.withDefaultPrettyPrinter()
						.writeValueAsBytes(message)))
		.andExpect(status().isUnauthorized());

		this.mockMvc.perform(post("/login")
				.param("username", "Alix")
				.param("password", "Alice"))
		.andExpect(status().isOk());

		this.mockMvc.perform(post("/message/insert")
				.contentType(contentType)
				.content(mapper.writer()
						.withDefaultPrettyPrinter()
						.writeValueAsBytes(message)))
		.andExpect(status().isOk())
		.andExpect(content().contentType(messageType))
		.andExpect(content().string(is("This Message Does Not Exists!")));

		/* Attempt to perform an insert of a
		 * message and verify that there
		 * are changes in the recoverable
		 * data
		 */
		
		message.setParentId(null);

		this.mockMvc.perform(post("/message/insert")
				.contentType(contentType)
				.content(mapper.writer()
						.withDefaultPrettyPrinter()
						.writeValueAsBytes(message)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.creatorId",is("Alix")))
		.andExpect(jsonPath("$.content",is("@Just For @Fun")));

		this.mockMvc.perform(get("/user/Alix/messages")
				.contentType(contentType))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(4)))
		.andExpect(jsonPath("$[3].content",is("@Just For @Fun")))
		.andExpect(jsonPath("$[3].creatorId",is("Alix")));

		this.mockMvc.perform(get("/user/Alix/messages?search=@Fun")
				.contentType(contentType))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].content",is("@Just For @Fun")));

		/* Attempt to perform an insert with an existing
		 * messageId
		 */
		
		this.mockMvc.perform(post("/message/insert")
				.contentType(contentType)
				.content(mapper.writer()
						.withDefaultPrettyPrinter()
						.writeValueAsBytes(message)))
		.andExpect(status().isOk())
		.andExpect(content().contentType(messageType))
		.andExpect(content().string(is("The messageId is already used!")));

	}

	/*Test the delete endpoint*/
	@Test 
	public void test_1_delete() throws Exception {

		/*Attempt to perform an unathorized action*/
		this.mockMvc.perform(post("/login")
				.param("username", "Bhel")
				.param("password", "Bob"))
		.andExpect(status().isOk());

		this.mockMvc.perform(delete("/message/6"))
		.andExpect(status().isUnauthorized());

		/*Delete a message and verify that there is a change
		 * in the recovered data*/
		this.mockMvc.perform(post("/login")
				.param("username", "Alix")
				.param("password", "Alice"))
		.andExpect(status().isOk());

		this.mockMvc.perform(delete("/message/6"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(messageType))
		.andExpect(content().string(is("This Message Has Been Deleted")));

		this.mockMvc.perform(get("/user/Alix/messages")
				.contentType(contentType))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(3)));

		this.mockMvc.perform(get("/user/Alix/messages?search=@Fun")
				.contentType(contentType))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(0)));

		/*Attempt to perform on a nonexisting message*/
		this.mockMvc.perform(delete("/message/6"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(messageType))
		.andExpect(content().string(is("This Message Does Not Exists!")));;
	}

	/*Test the getReplies endpoint*/
	@Test 
	public void test_2_getReplies() throws Exception {

		/*Verifying a request for the replies of a message*/
		this.mockMvc.perform(post("/login")
				.param("username", "Alix")
				.param("password", "Alice"))
		.andExpect(status().isOk());

		this.mockMvc.perform(get("/message/2/replies"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)));

		/*Inserting a new message with a parentId*/
		Message message = new Message();
		message.setContent("@Just For @Fun");
		message.setCreatorId("Alix");
		message.setMessageId((long) 6);
		message.setParentId((long) 2);

		this.mockMvc.perform(post("/message/insert")
				.contentType(contentType)
				.content(mapper.writer()
						.withDefaultPrettyPrinter()
						.writeValueAsBytes(message)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.creatorId",is("Alix")))
		.andExpect(jsonPath("$.content",is("@Just For @Fun")));

		/*Verifying that the reply list has changed*/
		this.mockMvc.perform(get("/message/2/replies"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[1].content",is("@Just For @Fun")));
		
		/*Remove the inserted data*/
		this.mockMvc.perform(delete("/message/6"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(messageType))
		.andExpect(content().string(is("This Message Has Been Deleted")));


	}


	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(
				o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
