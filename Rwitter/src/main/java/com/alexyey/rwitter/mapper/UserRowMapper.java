package com.alexyey.rwitter.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.alexyey.rwitter.model.User;

/* Mapper for the User Model used in the related DAO*/
public class UserRowMapper implements RowMapper<User>
{
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new User(rs.getString("user_tag"),
				        rs.getString("name"),
				        rs.getString("surname"),
				        rs.getInt("age"),
				        rs.getString("email"));
	}
	
}