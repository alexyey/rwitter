package com.alexyey.rwitter.model;

/* Represents the app user model */
public class User {
	
	private String userTag;
	private String name; 
    private String surname;
    private Integer age;
	private String email;
	
	public User() {
		super();
	}

	public User(String userTag, 
				String name,
				String surname,
				Integer age,
				String email) {
		super();
		this.userTag = userTag;
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.email = email;
	}
	
	public String getUserTag() {
		return userTag;
	}
	public void setUserTag(String userTag) {
		this.userTag = userTag;
	}
	public String getName() {
		return name;
	}
	public void setName(String userName) {
		this.name = userName;
	}
    public String getSurname() {
		return surname;
	}
    public void setSurname(String surname) {
		this.surname = surname;
	}
    public Integer getAge() {
		return age;
	}
    public void setAge(Integer age) {
		this.age = age;
	}
    public String getEmail() {
		return email;
	}
    public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userTag == null) ? 0 : userTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userTag == null) {
			if (other.userTag != null)
				return false;
		} else if (!userTag.equals(other.userTag))
			return false;
		return true;
	}
	
	

	
}
