package com.alexyey.rwitter.model;

/* Represent the Message model*/
public class Message {

	private Long messageId;
	private String content;
	private String creatorId;
	private Long parentId;
	
	public Message() {
		super();
	}
	public Message(Long messageId, 
			String content,
			String creatorId,
			Long parentId) {
		super();
		this.messageId = messageId;
		this.content = content;
		this.creatorId = creatorId;
		this.parentId = parentId;
	}
	
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	

}
