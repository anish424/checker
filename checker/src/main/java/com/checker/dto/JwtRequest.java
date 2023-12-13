package com.checker.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
	@NotBlank(message="username can't be blank") 
	@Email(message="username should be a valid email address") 
	private String username;
	@NotBlank(message="password can't be blank")  
	private String password;
	
}