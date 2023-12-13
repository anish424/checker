package com.checker.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "NOTICE")
@NoArgsConstructor
@ToString
public class Notice {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "NOTICE_CREATE_DATE",nullable = false)
	private Timestamp createDate;

	@Column(name = "NOTICE_AUTO_SEND_DURATION")
	private Integer autoSendDuration;
	
	@Column(name = "NOTICE_BODY")
	private String noticeBody;
	
	@Column(name = "NOTICE_SUBJECT")
	private String noticeSubject;
	
	@Column(name = "NOTICE_TO")
	private String noticeTo;

}
