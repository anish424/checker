package com.checker.service;

import com.checker.dto.NoticeDto;
import com.checker.exception.EmailException;

public interface NoticeService {
	
	public void sendNotice(NoticeDto notice) throws EmailException;

}
