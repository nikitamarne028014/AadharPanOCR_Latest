package com.psl.pancard_ocr.DTO;

import org.springframework.stereotype.Component;

@Component
public class PanCardDetails {
	
	private String panHolderName;
	private String fathersName;
	private String panNumber;
	private String DOB;
	

	public String getPanHolderName() {
		return panHolderName;
	}
	public void setPanHolderName(String panHolderName) {
		this.panHolderName = panHolderName;
	}
	public String getFathersName() {
		return fathersName;
	}
	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}
	public String getPanNumber() {
		return panNumber;
	}
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	public String getDOB() {
		return DOB;
	}
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	@Override
	public String toString() {
		return "PanCardDetails [panHolderName=" + panHolderName
				+ ", fathersName=" + fathersName + ", panNumber=" + panNumber
				+ ", DOB=" + DOB + "]";
	}

}
