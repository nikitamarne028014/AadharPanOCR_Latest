package com.psl.pancard_ocr.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.psl.pancard_ocr.DTO.PanCardDetails;
import com.psl.pancard_ocr.Utility.JSONArrayGenerator;

@Service
public class PanOcrServiceImpl {

	@Autowired
	PanCardDetails panCardDetails;
	
	@Autowired
	JSONArrayGenerator jsonArrayGenerator;
			
	public PanCardDetails getPanCardDetails(MultipartFile fileToBeParsed){

		//JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();
		DateValidator validator = new DateValidator();
		String panCardValidatorRE = "[A-Z]{5}\\d{4}[A-Z]{1}";
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<Long> topValue = new ArrayList<Long>();
		ArrayList<Long> heightValue = new ArrayList<Long>();
		String jsonResponse;
		
		try {
			
			jsonResponse = jsonArrayGenerator.getJSONArray(fileToBeParsed);
			
			if(jsonResponse != null)
			{
			//Read JSON file
			Object obj = jsonParser.parse(jsonResponse);
			long top ;
			long height ;

			JSONArray data = (JSONArray) obj;

			for(int i=0;i<data.size();i++) {

				JSONObject jobj = (JSONObject) data.get(i);
				String text = (String) jobj.get("text");
				top = (long) jobj.get("top");
				height = (long) jobj.get("height");

				if(text.equalsIgnoreCase("INCOME") || text.equalsIgnoreCase("TAX") || text.equalsIgnoreCase("DEPARTMENT") 
						|| text.equalsIgnoreCase("GOVT.") || text.equalsIgnoreCase("GOVERNMENT")
						|| text.equalsIgnoreCase("OF") || text.equalsIgnoreCase("INDIA") || text.equalsIgnoreCase("PERMANENT")
						|| text.equalsIgnoreCase("ACCOUNT") || text.equalsIgnoreCase("NUMBER") || text.contains("GOVT") || text.contains("INCOME") 
						|| text.contains("TAX") || text.contains("DEPARTMENT")) {
					continue;
				}
				else {

					if(validator.isValidDate(text))
					{
						System.out.println("BirthDate is: "+text);
						panCardDetails.setDOB(text);
						
					}
						
					if(text.matches(panCardValidatorRE))
					{
						System.out.println("Pan Card Number is: "+text);
						panCardDetails.setPanNumber(text);
						break;
					}

					if(hasSpecialCharacters(text.trim()))
						continue;

					if(isUpperCase(text.trim()) && (!hasSpecialCharacters(text.trim())))
					{
						name.add(text);
						topValue.add(top);
						heightValue.add(height);
					}
				}
			}

			/* System.out.println("List of name is: "+name);
            System.out.println("Top values: "+topValue);
            System.out.println("Height values: "+heightValue);
			 */
			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			Long element1;
			Long element2; 

			for(int i=0,j=0;i<topValue.size()-1 && j<heightValue.size()-1;i++,j++)
			{
				element1 = topValue.get(i);
				element2 = heightValue.get(j);

				float difference = (((float)element1) / element2) ;

				if(difference >= 6.20 && difference <=9.0){
					sb.append(name.get(i)+" ");
				}

				if(difference >=10 && difference <=12.50)
				{
					sb1.append(name.get(i)+" ");
				}
			}
			
			System.out.println("Final Name is: "+sb.toString());
			panCardDetails.setPanHolderName(sb.toString());
			System.out.println("Fathers Name is: "+sb1.toString());
			panCardDetails.setFathersName(sb1.toString());

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return panCardDetails;

	}

	public static boolean isUpperCase(String text) { 
		return ((text == text.toUpperCase()) && (!(text.chars().allMatch( Character::isDigit ))) && (!(text.chars().anyMatch(Character :: isDigit)))); 
	} 

	public static boolean hasSpecialCharacters(String text)
	{
		char[] textsChars = text.toCharArray();
		Pattern specialCharacters = Pattern.compile ("[-!@$%&*()_+=|<>?{}\\[\\]~-�#.\"]");

		for (Character ch : textsChars) {
			Matcher hasSpecial = specialCharacters.matcher(ch.toString());
			if(hasSpecial.find())
				return true;
		}
		return false;
	}

	public static boolean isPanNumber(String text)
	{
		String panCardValidatorRE = "[A-Z]{5}\\d{4}[A-Z]{1}";
		boolean isMatchingPANCriteria = text.length()==10;
		StringBuilder sb = new StringBuilder();
		if(isMatchingPANCriteria)
		{
			System.out.println("Pan criteria is matching");
			String initialFiveAlphabets = text.substring(0,5);
			char[] initialFiveAlphabetsCharArray = initialFiveAlphabets.toCharArray();
			for (char ch : initialFiveAlphabetsCharArray) {
				if(Character.isDigit(ch))
				{
					if((Integer.parseInt(Character.toString(ch)) == 0) || (Integer.parseInt(Character.toString(ch)) == 5 ))
					{
						if((Integer.parseInt(Character.toString(ch)) == 0))
						{
							sb.append("O");
						}
						else
						{
							sb.append("S");
						}
					}
				}
				else
				{
					sb.append(ch);
				}
			}

			String lastFourDigits = text.substring(5, 9);
			char[] lastFourDigitsCharArray = lastFourDigits.toCharArray();
			for (char ch : lastFourDigitsCharArray) {
				if(Character.isDigit(ch))
				{
					sb.append(ch);
				}
				else
				{
					if(ch == 'O' || ch == 'S')
					{
						if(ch == 'O')
							sb.append(0);
						else sb.append(5);
					}
				}
			}

			String lastAlphabet = text.substring(9);
			char[] lastAlphabetArray = lastAlphabet.toCharArray();
			for (char ch : lastAlphabetArray) {
				if(Character.isDigit(ch))
				{
					if((Integer.parseInt(Character.toString(ch)) == 0) || (Integer.parseInt(Character.toString(ch)) == 5 ))
					{
						if((Integer.parseInt(Character.toString(ch)) == 0))
						{
							sb.append("O");
						}
						else
						{
							sb.append("S");
						}
					}
				}
				else
				{
					sb.append(ch);
				}
			}

			System.out.println("Correct pan number: "+sb.toString());
			return true;

		}
		else
			return false;

	}


}
