package com.psl.pancard_ocr.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;

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
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Component
public class JSONArrayGenerator {

	//get API path from config.json file
	static JSONObject jObj = getConfigJsonObject();
	static String OCR_API_URL = jObj.get("OCR_API_URL").toString();
	
	static Date date = new Date();
	static long time = date.getTime();	
	private final static Timestamp ts = new Timestamp(time);
	
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

        HttpPost request = new HttpPost(OCR_API_URL);
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
	
	public static void copyFileToFolder(File src , String dest)
	{
		Path result = null;
		

		try {
			// result = Files.move(Paths.get(src), Paths.get(dest));
			result = Files.copy(src.toPath(), Paths.get(dest));
		
		} catch (IOException e) {
			System.out.println("[" + ts + "] "
					+ "Exception while moving file: " + e.getMessage());
		}
		if (result != null) {
			System.out.println("[" + ts + "] " + "File moved successfully.");
		} else {
			System.out.println("[" + ts + "] " + "File movement failed.");
		}

	}
	
	public static JSONObject configValueJSON(String filepath)
			throws FileNotFoundException, IOException, ParseException {
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(filepath)) {

			Object obj = jsonParser.parse(reader);

			JSONObject jo = (JSONObject) obj;

			return jo;
		}

	}
	
	public static JSONObject getConfigJsonObject()
	{
		String filepath = System.getenv("AADHAR_PAN_OCR_CONFIG_PATH");
		JSONObject jObj = new JSONObject();
		try {
			jObj = configValueJSON(filepath);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObj;
	}
	
    	public String getFilePathFromFolder(MultipartFile file){
		
		//get Images Folder path from config.json file
		JSONObject jObj = getConfigJsonObject();
		String dest = jObj.get("AADHAR_PAN_IMAGES_FOLDER_PATH").toString();
		
		File convertedfile;
		String FilePath = null;
		try {
			convertedfile = JSONArrayGenerator.convert(file);
			System.out.println("[" + ts + "] " + "Destination file path: "+dest+"/"+convertedfile.getName());
			JSONArrayGenerator.copyFileToFolder(convertedfile, dest+"/"+convertedfile.getName());
			FilePath = dest+"/"+convertedfile.getName();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FilePath;
		
	}
}
