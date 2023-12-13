package com.checker.exception;

public class CandidateException extends Exception {
	
	private static final long serialVersionUID = 9153472471684572542L;

	public CandidateException(String string, Exception e) {
		super(string, e);
	}

	public CandidateException(String string) {
		super(string);
	}
}
