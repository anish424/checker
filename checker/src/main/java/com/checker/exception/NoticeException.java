package com.checker.exception;

public class NoticeException extends Exception{
	
	private static final long serialVersionUID = -4475714342782207717L;

	public NoticeException(String string, Exception e) {
		super(string, e);
	}

	public NoticeException(String string) {
		super(string);
	}

}
