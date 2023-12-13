package com.checker.exception;

import javax.servlet.ServletException;

public class JwtException extends ServletException{

	private static final long serialVersionUID = -6044440623182294616L;

	public JwtException(String string, Exception e) {
		super(string, e);
	}

	public JwtException(String string) {
		super(string);
	}

}
