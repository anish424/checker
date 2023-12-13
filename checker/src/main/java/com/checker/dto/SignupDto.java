package com.checker.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class SignupDto extends JwtRequest {

	private static final long serialVersionUID = -5512088888689293066L;
	@NotBlank(message="name can't be blank") 
	private String name;

}
