package com.psl.pancard_ocr.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class JSONArrayGenerator {

	public static String OCR_API_URL = "http://10.53.16.165:3500/upload";
	public static String getJSONArray(MultipartFile file)
	{
		String responseString = null;
		File convertedfile = null;
		
		try {
			convertedfile = convert(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        FileBody fileBody = new FileBody(convertedfile, ContentType.DEFAULT_BINARY);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        builder.addPart("image", fileBody);
        HttpEntity entity = builder.build();

        HttpPost request = new HttpPost("http://10.53.16.165:3500/upload");
        request.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(request);
            
            if(response != null)
            {
            	System.out.println(response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                responseString = EntityUtils.toString(resEntity);
                System.out.println("Response Entity: "+responseString);
            }
   
        } catch (IOException e) {
            e.printStackTrace();
        }
		return responseString;

	}
	
	public static File convert(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    convFile.createNewFile();
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
	

}
