package com.psl.pancard_ocr.Controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.psl.pancard_ocr.DTO.AadharCardDetails;
import com.psl.pancard_ocr.DTO.PanCardDetails;
import com.psl.pancard_ocr.Service.AadharOcrServiceImpl;
import com.psl.pancard_ocr.Service.PanOcrServiceImpl;

@RestController
public class PanOcrController {
	
	@Autowired
	PanOcrServiceImpl panOcrServiceImpl;
	
	@Autowired
	AadharOcrServiceImpl aadharCardServiceImpl;
	
	
	@PostMapping( value = "/getPanCardDetails" , produces = "application/json")
	public ResponseEntity<PanCardDetails> getPanCardDetails(@RequestParam("image") MultipartFile fileToBeParsed)
	{
		PanCardDetails panCardDetails =  panOcrServiceImpl.getPanCardDetails(fileToBeParsed);
		return new ResponseEntity<PanCardDetails>(panCardDetails, HttpStatus.OK);	
	}
	
	@PostMapping( value = "/getAadharCardDetails" , produces = "application/json")
	public ResponseEntity<AadharCardDetails> getAadharCardDetails(@RequestParam("image") MultipartFile fileToBeParsed)
	{
		AadharCardDetails aadharCardDetails = aadharCardServiceImpl.getAadharCardDetails(fileToBeParsed);
		return new ResponseEntity<AadharCardDetails>(aadharCardDetails, HttpStatus.OK);
	}
	
}
