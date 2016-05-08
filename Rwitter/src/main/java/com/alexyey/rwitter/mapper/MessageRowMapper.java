package com.alexyey.rwitter.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.alexyey.rwitter.model.Message;
import com.alexyey.rwitter.model.User;

/* Mapper for the Message Model used in the related DAO*/
public class MessageRowMapper implements RowMapper<Message> {

	public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Message(rs.getLong("message_id"),
				           rs.getString("content"),
				           rs.getString("creator_id"),
				           rs.getLong("parent_id"));
	}
	
}
