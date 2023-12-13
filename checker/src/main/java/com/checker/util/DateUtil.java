package com.checker.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {
	
private SimpleDateFormat format = new SimpleDateFormat("yyyy");

	public String getPresentYear() {
		return format.format(new Date());
	}
}
