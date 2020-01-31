package com.psl.pancard_ocr.DTO;

import org.springframework.stereotype.Component;

@Component
public class AadharCardDetails {

	private String cardHolderName;
	private String yearOfBirth;
	private String gender;
	private String AadharNumber;
	private String imgUrl;
	
	public String getCardHolderName() {
		return cardHolderName;
	}
	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
	public String getYearOfBirth() {
		return yearOfBirth;
	}
	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAadharNumber() {
		return AadharNumber;
	}
	public void setAadharNumber(String aadharNumber) {
		AadharNumber = aadharNumber;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	@Override
	public String toString() {
		return "AadharCardDetails [cardHolderName=" + cardHolderName
				+ ", yearOfBirth=" + yearOfBirth + ", gender=" + gender
				+ ", AadharNumber=" + AadharNumber + ", imgUrl=" + imgUrl + "]";
	}
	
	public void reInitializeAadharObject()
	{
		cardHolderName = null;
		yearOfBirth = null;
		gender = null;
		AadharNumber = null;
		imgUrl = null;
	}

}
