package com.checker.exception;

public class RecruiterException extends Exception {
	
	private static final long serialVersionUID = -4781428281765243403L;

	public RecruiterException(String string, Exception e) {
		super(string, e);
	}

	public RecruiterException(String string) {
		super(string);
	}
	
}
