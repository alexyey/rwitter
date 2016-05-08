package com.alexyey.rwitter.exception;

import javax.servlet.http.HttpServletResponse;

/* Exception thrown in case of generally unauthorized operations
 * within logged requests
 */
public class UnAuthorizedException extends Exception {

	private HttpServletResponse respose;
	/**
	 * 
	 */
	private static final long serialVersionUID = 401677141324605500L;

	public UnAuthorizedException(HttpServletResponse response) {
		super();
		this.respose = response;
	}

	public HttpServletResponse getRespose() {
		return respose;
	}

	public void setRespose(HttpServletResponse respose) {
		this.respose = respose;
	}
	
	
}
