package com.psl.pancard_ocr.Utility;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateValidator {

	 public static String dateValidatorRE = "(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[1-9][0-9][0-9][0-9]$";
	 
	 public static boolean isValidDate(String inDate) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        dateFormat.setLenient(false);
	        try {
	        	if(inDate.matches(dateValidatorRE))
	        	{
	        		 dateFormat.parse(inDate.trim());
	        	}
	        	else
	        		return false;
	           
	        } catch (ParseException pe) {
	        	
	        	if(inDate.matches(dateValidatorRE))
	        		return true;
	        	else
	        		return false;
	        }
	        return true;
	    }
	 
	 //This function is not used
	 public static Date getBirthDate(String validatedDate)
	 {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		
		Date formatedDate;
		String dOBDate;
		try {
			
			//Parse the Date
			formatedDate = dateFormat2.parse(validatedDate);	
			dOBDate = dateFormat.format(formatedDate);
			
				
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		 
	 }

}
