package com.psl.pancard_ocr.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import com.psl.pancard_ocr.DTO.AadharCardDetails;
import com.psl.pancard_ocr.Util.AadharOcrConstants;
import com.psl.pancard_ocr.Utility.DateValidator;
import com.psl.pancard_ocr.Utility.JSONArrayGenerator;

@Service
public class AadharOcrServiceImpl {
	
	@Autowired
	AadharCardDetails aadharCardDetails;
	
	@Autowired
	JSONArrayGenerator jsonArrayGenerator;
	
	@Autowired
	static AadharOcrConstants constants;
	
	
	public AadharCardDetails getAadharCardDetails(MultipartFile fileToBeParsed){
		
		//Re-initialize AadharCardDetails Object
		aadharCardDetails.reInitializeAadharObject();
		
		//JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();
		DateValidator validator = new DateValidator();
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<Long> topValue = new ArrayList<Long>();
		ArrayList<Long> heightValue = new ArrayList<Long>();
		ArrayList<Long> shortListedTopValue = new ArrayList<Long>();
		ArrayList<String> shortListedName = new ArrayList<String>();
		String jsonResponse;
		
	
		try {
			
			jsonResponse = jsonArrayGenerator.getJSONArray(fileToBeParsed);
			
			if(jsonResponse != null)
			{
					
			System.out.println("Received Json Response..!!");
			
			//get Image URL
			String imgUrl = jsonArrayGenerator.getFilePathFromFolder(fileToBeParsed);	
			aadharCardDetails.setImgUrl(imgUrl);
				
			//Read JSON file
			Object obj = jsonParser.parse(jsonResponse); 
			long top ;
			long height ;
			
			JSONArray data = (JSONArray) obj;
			
			for (int i = 0; i < data.size(); i++) {
				
				JSONObject jobj = (JSONObject) data.get(i);
				String text = (String) jobj.get("text");
				top = (long) jobj.get("top");
				height = (long) jobj.get("height");
				
				if(text.equalsIgnoreCase("GOVERNMENT") || text.equalsIgnoreCase("OF") || text.equalsIgnoreCase("INDIA")
						|| text.contains("GOVERN") || text.equalsIgnoreCase("YEAR") || text.contains("Year") 
						|| text.equalsIgnoreCase("BIRTH") || text.contains("Birth"))
				{
					continue;
				}
				else
				{

					if(hasSpecialCharacters(text.trim()) || isUpperCase(text) || isLowerCase(text))
						continue;
					else
					{
						name.add(text);
						topValue.add(top);
						heightValue.add(height);
					}
					
					if(text.equalsIgnoreCase(constants.FEMALE) || text.contains("Fem") || text.contains("male") 
							|| text.equalsIgnoreCase(constants.MALE))
					{
						if(text.equalsIgnoreCase(constants.FEMALE) || text.contains("Fem"))
						{
							System.out.println("Gender is : "+text.trim());
							aadharCardDetails.setGender(text.trim());
						}
						else if(text.contains(constants.MALE) || text.equalsIgnoreCase(constants.MALE))
						{
							System.out.println("Gender is : "+text.trim());
							aadharCardDetails.setGender(text.trim());
						}
					}
					
					if(validator.isValidDate(text.trim()))
					{
						System.out.println("BirthDate is: "+text.trim()); 
					}
			
				}
			}
			
			
			StringBuffer sb1 = new StringBuffer();
			Long element1;
			Long element2; 
			ArrayList<String> digits = new ArrayList<String>();
			
			for (int i=0,j = 0;i<topValue.size() && j < heightValue.size();j++,i++) {
				
				element1 = topValue.get(i);
				element2 = heightValue.get(j);
				String text = name.get(i);
		
				if((text.length() == constants.LENGTH_OF_DIGITS) && (text.chars().allMatch( Character::isDigit )))
					digits.add(text);
					
				float difference = (((float)element1) / element2) ;

				if(difference >= constants.MEAN_DIFFERENCE_MIN && difference <= constants.MEAN_DIFFERENCE_MAX){
					if((!(text.chars().anyMatch(Character :: isDigit))) && (!(text.equalsIgnoreCase(constants.MALE))) && (!(text.equalsIgnoreCase(constants.FEMALE))))
					{
						shortListedName.add(text);
						shortListedTopValue.add(element1);
						//sb.append(name.get(i)+" ");
					}
						
				}

				/*if(difference >=11 && difference <=13)
				{
					if((text.length() == 4) && (text.chars().allMatch( Character::isDigit )))
						sb1.append(name.get(i)+" ");
				}*/
				
			}
			
			System.out.println("ShortListed names: "+shortListedName);
			System.out.println("top values: "+shortListedTopValue);
		
	
			String FinalName = getName(shortListedName, shortListedTopValue);
			
			StringBuffer sb2 = new StringBuffer();
			
			System.out.println("Final Name is: "+FinalName.trim());
			aadharCardDetails.setCardHolderName(FinalName.trim());
			//System.out.println("Aadhar Card Number is: "+sb1.toString());
			
			if(digits.size() == constants.LENGTH_OF_DIGITS)
			{
				for (int i = 0; i < digits.size(); i++) {
					if(i==0)
					{
						aadharCardDetails.setYearOfBirth(digits.get(i).trim());
						System.out.println("Year of Birth: "+digits.get(i).trim());
					}
						
					else
					{
						sb2.append(digits.get(i)+" ");	
					}
						
				}
				aadharCardDetails.setAadharNumber(sb2.toString().trim());
				System.out.println("Aadhar Card Number : "+sb2.toString().trim());
				
			}
		}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aadharCardDetails;
		
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
	
	public static boolean isUpperCase(String text) { 
		return ((text == text.toUpperCase()) && (!(text.chars().allMatch( Character::isDigit ))) && (!(text.chars().anyMatch(Character :: isDigit)))); 
	} 
	
	public static boolean isLowerCase(String text){
		return ((text == text.toLowerCase()) && (!(text.chars().allMatch( Character::isDigit ))) && (!(text.chars().anyMatch(Character :: isDigit))));	
	}
	
	public String getName(ArrayList<String> name, ArrayList<Long> topValues)
	{
		Long element1;
		Long element2;
		Long difference;
		Long difference2;
		StringBuffer sb = new StringBuffer();
		ArrayList<String> finalName = new ArrayList<String>();
		
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
		for (String finalname : finalName) {
			sb.append(finalname+" ");
		}
		return sb.toString().trim();
			
	}
	
	
}
