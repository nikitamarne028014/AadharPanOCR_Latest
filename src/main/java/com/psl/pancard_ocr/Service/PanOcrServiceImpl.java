package com.psl.pancard_ocr.Service;

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
import com.psl.pancard_ocr.Util.PanOcrConstants;
import com.psl.pancard_ocr.Utility.DateValidator;
import com.psl.pancard_ocr.Utility.JSONArrayGenerator;

@Service
public class PanOcrServiceImpl {

	@Autowired
	PanCardDetails panCardDetails;
	
	@Autowired
	JSONArrayGenerator jsonArrayGenerator;
	
	@Autowired
	DateValidator validator;
	
	@Autowired
	static PanOcrConstants constants;
    
	public PanCardDetails getPanCardDetails(MultipartFile fileToBeParsed){
		
		panCardDetails.reInitializePanObject();

		//JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();
		
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<Long> topValue = new ArrayList<Long>();
		ArrayList<Long> heightValue = new ArrayList<Long>();
		ArrayList<Long> shortListedFirstNameTopValue = new ArrayList<Long>();
		ArrayList<String> shortListedFirstName = new ArrayList<String>();
		ArrayList<Long> shortListedLastNameTopValue = new ArrayList<Long>();
		ArrayList<String> shortListedLastName = new ArrayList<String>();
		
		String jsonResponse;
		
		try {
			
			jsonResponse = jsonArrayGenerator.getJSONArray(fileToBeParsed);
			
			if(jsonResponse != null)
			{
				
			//set image URL
			String imgUrl = jsonArrayGenerator.getFilePathFromFolder(fileToBeParsed);	
			panCardDetails.setImgUrl(imgUrl);
			
			
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

					if(hasSpecialCharacters(text.trim()))
						continue;
					
					if(validator.isValidDate(text))
					{
						System.out.println("BirthDate is: "+text.trim());
						panCardDetails.setDOB(text.trim());
						
					}
						
					if(text.matches(constants.PAN_CARD_VALIDATOR_REGEX))
					{
						System.out.println("Pan Card Number is: "+text.trim());
						panCardDetails.setPanNumber(text.trim());
						break;
					}
					else
					{
						isPanNumber(text);
					}

					

					if(isUpperCase(text.trim()) && (!hasSpecialCharacters(text.trim())))
					{
						name.add(text);
						topValue.add(top);
						heightValue.add(height);
					}
				}
			}

			 System.out.println("List of name is: "+name);
            System.out.println("Top values: "+topValue);
            System.out.println("Height values: "+heightValue);
			 
			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			Long element1;
			Long element2; 

			for(int i=0,j=0;i<topValue.size() && j<heightValue.size();i++,j++)
			{
				element1 = topValue.get(i);
				element2 = heightValue.get(j);

				float difference = (((float)element1) / element2) ;

				if(difference >= constants.FIRSTNAME_MEAN_DIFFERENCE_MIN && difference <= constants.FIRSTNAME_MEAN_DIFFERENCE_MAX){
					//sb.append(name.get(i)+" ");
					shortListedFirstName.add(name.get(i));
					shortListedFirstNameTopValue.add(topValue.get(i));
					
				}

				if(difference >=constants.LASTNAME_MEAN_DIFFERENCE_MIN && difference <= constants.LASTNAME_MEAN_DIFFERENCE_MAX)
				{
					//sb1.append(name.get(i)+" ");
					shortListedLastName.add(name.get(i));
					shortListedLastNameTopValue.add(topValue.get(i));
				}
			}
			
			String FirstName = getName(shortListedFirstName, shortListedFirstNameTopValue);
			String LastName = getName(shortListedLastName, shortListedLastNameTopValue);
			System.out.println("First Name is: "+FirstName);
			panCardDetails.setPanHolderName(FirstName);
			System.out.println("Fathers Name is: "+LastName);
			panCardDetails.setFathersName(LastName);

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
		Pattern specialCharacters = Pattern.compile(constants.SPECIAL_CHARS_REGEX);

		for (Character ch : textsChars) {
			Matcher hasSpecial = specialCharacters.matcher(ch.toString());
			if(hasSpecial.find())
				return true;
		}
		return false;
	}

	public boolean isPanNumber(String text)
	{
		boolean isMatchingPANCriteria = text.length()==10;
		StringBuilder sb = new StringBuilder();
		if(isMatchingPANCriteria && !hasSpecialCharacters(text) && (text == text.toUpperCase()))
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

			System.out.println("Correct pan number: "+sb.toString().trim());
			if(sb.toString().trim().matches(constants.PAN_CARD_VALIDATOR_REGEX))
			{
				panCardDetails.setPanNumber(sb.toString().trim());
			}
			return true;

		}
		else
			return false;

	}
	
	public String getName(ArrayList<String> name, ArrayList<Long> topValues)
	{
		Long element1;
		Long element2;
		Long difference;
		Long difference2;
		StringBuffer sb = new StringBuffer();
		ArrayList<String> finalName = new ArrayList<String>();
		
		System.out.println("Shortlisted name: "+name);
		System.out.println("Shortlisted Top values: "+topValues);
		
		if(name.size() == 1)
		{
			finalName.add(name.get(0));
		}
		else{
			for(int i=0,j=0; i<topValues.size() && j<name.size();i++,j++)
			{
				
				element1 = topValues.get(i);
				
				if(i == topValues.size()-1)
					break;
				else
				    element2 = topValues.get(i+1);
				
				difference = element1 - element2;
				difference2 = element2 - element1;
				
				if(difference >= constants.TOP_DIFFERENCE_MIN && difference < constants.TOP_DIFFERENCE_MAX 
						|| difference2 >=constants.TOP_DIFFERENCE_MIN && difference2 <= constants.TOP_DIFFERENCE_MAX)
				{
					if(!(finalName.contains(name.get(j))))
						finalName.add(name.get(j));
					
					
					if(!(finalName.contains(name.get(j+1))))
					    finalName.add(name.get(j+1));
				
				}
				
				
			}
		}

		for (String finalname : finalName) {
			sb.append(finalname+" ");
		}
		return sb.toString().trim();
			
	}
}
