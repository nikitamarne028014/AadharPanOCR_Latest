package com.psl.pancard_ocr.Util;

import org.springframework.stereotype.Component;

@Component
public class AadharOcrConstants {

	public static Integer TOP_DIFFERENCE_MIN = 0;
	public static Integer TOP_DIFFERENCE_MAX = 10;
	public static Integer MEAN_DIFFERENCE_MIN = 7;
	public static Integer MEAN_DIFFERENCE_MAX = 12;
	public static String SPECIAL_CHARS_REGEX = "[-!@$%&*()_+=|<>?{}\\[\\]~-�#.\"]";
	public static String FEMALE = "Female";
	public static String MALE = "Male";
	public static Integer LENGTH_OF_DIGITS = 4;
}
