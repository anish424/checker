package com.checker.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChargeDto implements Serializable {

	private static final long serialVersionUID = -5389209313814685430L;

	private Integer id;
	private String name;
	private boolean selected = false;
}
