package com.psl.pancard_ocr.Service;

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
import com.psl.pancard_ocr.Utility.JSONArrayGenerator;

@Service
public class AadharOcrServiceImpl {
	
	@Autowired
	AadharCardDetails aadharCardDetails;
	
	@Autowired
	JSONArrayGenerator jsonArrayGenerator;
	
	public AadharCardDetails getAadharCardDetails(MultipartFile fileToBeParsed){
		
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
					
					if(text.equalsIgnoreCase("Female") || text.contains("Fem") || text.contains("male") 
							|| text.equalsIgnoreCase("male"))
					{
						if(text.equalsIgnoreCase("Female") || text.contains("Fem"))
						{
							System.out.println("Gender is : "+text);
							aadharCardDetails.setGender(text);
						}
						else if(text.contains("Male") || text.equalsIgnoreCase("Male"))
						{
							System.out.println("Gender is : "+text);
							aadharCardDetails.setGender(text);
						}
					}
					
					if(validator.isValidDate(text))
					{
						System.out.println("BirthDate is: "+text); 
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
		
				if((text.length() == 4) && (text.chars().allMatch( Character::isDigit )))
					digits.add(text);
					
				float difference = (((float)element1) / element2) ;

				if(difference >= 7 && difference <=12){
					if((!(text.chars().anyMatch(Character :: isDigit))) && (!(text.equalsIgnoreCase("Male"))) && (!(text.equalsIgnoreCase("Female"))))
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
			
		
	
			String FinalName = getName(shortListedName, shortListedTopValue);
			
			StringBuffer sb2 = new StringBuffer();
			
			System.out.println("Final Name is: "+FinalName.trim());
			aadharCardDetails.setCardHolderName(FinalName.trim());
			//System.out.println("Aadhar Card Number is: "+sb1.toString());
			
			if(digits.size() == 4)
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
		Pattern specialCharacters = Pattern.compile ("[-!@$%&*()_+=|<>?{}\\[\\]~-�#.\"]");

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
			if(difference >= 0 && difference < 10)
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
