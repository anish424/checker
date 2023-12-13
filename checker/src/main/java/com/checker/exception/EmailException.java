package com.checker.exception;

import org.springframework.mail.MailPreparationException;

public class EmailException extends MailPreparationException{

	private static final long serialVersionUID = -7362760574712409071L;

	public EmailException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public EmailException(String string) {
		super(string);
	}

}
