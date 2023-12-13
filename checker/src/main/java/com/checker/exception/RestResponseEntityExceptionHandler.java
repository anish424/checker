package com.checker.exception;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

	private static final String ERROR_MSG = "Internal Server error, Please try again later";

	@ExceptionHandler(value = JwtException.class)
	public ResponseEntity<Object> exception(JwtException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = CandidateException.class)
	public ResponseEntity<Object> exception(CandidateException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = NoticeException.class)
	public ResponseEntity<Object> exception(NoticeException exception) {
		String errorMSg = StringUtils.isBlank(exception.getMessage()) ? ERROR_MSG : exception.getMessage();
		return new ResponseEntity<>(errorMSg, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = RecruiterException.class)
	public ResponseEntity<Object> exception(RecruiterException exception) {
		String errorMSg = StringUtils.isBlank(exception.getMessage()) ? ERROR_MSG : exception.getMessage();
		return new ResponseEntity<>(errorMSg, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {

			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		String errorMSg = StringUtils.isBlank(exception.getMessage()) ? ERROR_MSG : exception.getMessage();
		return new ResponseEntity<>(errorMSg, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
