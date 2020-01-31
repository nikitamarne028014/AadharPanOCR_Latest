package com.psl.pancard_ocr.Util;

import org.springframework.stereotype.Component;

@Component
public class PanOcrConstants {

	public static String PAN_CARD_VALIDATOR_REGEX = "[A-Z]{5}\\d{4}[A-Z]{1}";
	public static String SPECIAL_CHARS_REGEX = "[-!@$%&*()_+=|<>?{}\\[\\]~-�#.\"]";
	public static Double FIRSTNAME_MEAN_DIFFERENCE_MIN = 6.20;
	public static Double FIRSTNAME_MEAN_DIFFERENCE_MAX = 9.0;
	public static Double LASTNAME_MEAN_DIFFERENCE_MIN = 10.0;
	public static Double LASTNAME_MEAN_DIFFERENCE_MAX = 12.50;
	public static Integer TOP_DIFFERENCE_MIN = 0;
	public static Integer TOP_DIFFERENCE_MAX = 10;
}
