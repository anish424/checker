package com.checker.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto implements Serializable {

	private static final long serialVersionUID = 8209236825216008645L;
	
	private String noticeTo;
	private String noticeFrom;
	private String noticeSubject;
	private String bodyStart;
	private List<ChargeDto> charges;
	private String bodyEnd;
	private Integer autoSendDuration;

}
