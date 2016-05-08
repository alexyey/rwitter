package com.alexyey.rwitter.exception;

/*Exception thrown in case of message related exceptions*/
public class MessageException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8995022905930268031L;
	public String message;

    public MessageException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }    
	
}
