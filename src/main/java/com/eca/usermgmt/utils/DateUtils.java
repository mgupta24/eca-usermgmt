package com.eca.usermgmt.utils;

import com.eca.usermgmt.exception.UserManagementException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtils {

	private DateUtils() {
	}

	public static LocalDateTime stringToLocalDateTime(String date) {
		log.info("DateUtils::stringToLocalDateTime {} ",date);
		try{
			var parse = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			return parse.atTime(LocalTime.now());
		}catch (Exception e) {
			log.error("DateUtils Unable to Parse date {} ",e.getMessage());
			throw new UserManagementException("Lease StartDate/EndDate Cannot be blank");
		}
	}
}
