package com.alexyey.rwitter.exception;

/*Exception thrown in case of user related exceptions*/
public class UserException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8995022905930268031L;
	public String message;

    public UserException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }    
	
}
