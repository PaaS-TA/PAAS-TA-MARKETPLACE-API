package org.openpaas.paasta.marketplace.api.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtils {

	public static final String	TIME_ZONE_ID = "Asia/Seoul";
	public static final String	END_DATE 	= "9999-12-31 23:59:59";
	public static final String	FORMAT_1	= "yyyy-MM-dd HH:mm:ss";
	public static final String	FORMAT_2	= "yyyy-MM-dd";
	public static final String	FORMAT_3	= "yyyyMMdd";
	public static final String	FORMAT_4	= "yyyy.MM.dd";
	public static final String	FORMAT_5	= "yyyy";
	public static final String	FORMAT_6	= "MMdd";
	public static final String	FORMAT_7	= "yyyy/MM/dd";
	public static final String	FORMAT_8	= "yyyy/MM/dd HH:mm:ss";
	public static final String	FORMAT_9	= "yyyy-MM-DD'T'HH:mm:ss";
	public static final String	FORMAT_10	= "yyyyMMddHHmmss";
	public static final String	FORMAT_11	= "yyyyMM";
	public static final String	FORMAT_12	= "HHmmss";
	public static final String	FORMAT_13	= "yyyyMMddHHmm";
	public static final String	FORMAT_14	= "yyyy년MM월dd일";
	public static final String	FORMAT_15	= "yy/MM/dd";
	public static final String	FORMAT_16	= "yyyyMMddHH";
	
	/**
	 * 현재시간을 format 형식으로 리턴
	 * 
	 * @Method Name : getCurrentDate
	 * @description :
	 *
	 * @param format
	 * @return
	 */
	public static String getCurrentDate(String format) {
		String today = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(format));
		log.info("today: " + today);

		return today;
	}

	/**
	 * 주어진 LocalDateTime 을 format 형식으로 리턴
	 * 
	 * @Method Name : getConvertDate
	 * @description :
	 *
	 * @param format
	 * @return
	 */
	public static String getConvertDate(LocalDateTime target, String format) {
		String result = target.atZone(ZoneId.of(TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(format));
		log.info("date: " + result);

		return result;
	}

}
