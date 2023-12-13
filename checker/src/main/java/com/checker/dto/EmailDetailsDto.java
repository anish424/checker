package com.checker.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Annotations
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmailDetailsDto {

	private String recipient;
	private String msgBody;
	private String subject;
	private String templateName;
	private Map<String, String> templateTokens ;
}
