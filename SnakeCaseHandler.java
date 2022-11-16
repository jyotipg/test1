package com.hsbc.gbm.surveillance.sdf.trade.processor.utils;

import java.io.File;

import org.apache.commons.text.CaseUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SnakeCaseHandler {

	private static String rootJsonKey="";
	public static <T> T convertJsonStringJavaObject(String refereceFile, Class<T> classType) {

		T t = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			 
			t = mapper.readValue(removeSnakeJsonArray(refereceFile), classType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(refereceFile.concat(" file not found or invalid."));
		}

		return t;
	}
	
		private static String removeSnakeJsonArray(String refereceFile) {
		JSONParser parser = new JSONParser();
		
		String jsonObject = null;
		try {
			File file = new ClassPathResource(refereceFile).getFile();
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			Object jsonStringObject=mapper.readValue(file, Object.class);
			String jsonString=mapper.writeValueAsString(jsonStringObject);
			Object object = (Object) parser.parse(jsonString);

			 jsonObject= mapper.writeValueAsString(validJSONObject((JSONObject) object)) ;
			  
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = null;
		}
		return jsonObject;
	}
	@SuppressWarnings("unchecked")
	private static JSONObject validJSONObject(JSONObject jsonObject) {
		JSONArray output=new JSONArray();
		 jsonObject.keySet().forEach(key -> {
		        Object value = jsonObject.get(key);
		        String jsonKey= getcamelCaseString(String.valueOf(key));
		         		        
	        	rootJsonKey=jsonKey;
		        if (value instanceof JSONArray)
		        {
		        	 JSONArray urlArray = (JSONArray) value;
		        	 
		        	 urlArray.stream().forEach(j->{
		        		 JSONObject jobj=(JSONObject) j;
		        		 JSONObject validJSONObject=new JSONObject();
		        		 jobj.keySet().forEach(jkey -> {
		        			 String nJsonKey= getcamelCaseString(String.valueOf(jkey));
		        			 validJSONObject.put(nJsonKey, jobj.get(jkey));
		        			 
		        		 });
		        		 output.add(validJSONObject);
		        	 });
		        	
		        }
		       
		    });
		 JSONObject finalJSONObject=new JSONObject();
		 finalJSONObject.put(rootJsonKey, output);
		return finalJSONObject;
	}
 
	private static String getcamelCaseString(String key)
	{
        String jsonKey= String.valueOf(key);
        if (String.valueOf(key).contains(" "))
        {
        	 jsonKey= CaseUtils.toCamelCase(key, false, ' ');
        }
        if (String.valueOf(key).contains("-"))
        {
        	jsonKey= CaseUtils.toCamelCase(jsonKey, false, '-');
        }	
        if (String.valueOf(key).contains("_"))
        {
        	jsonKey= CaseUtils.toCamelCase(jsonKey, false, '_');
        }	
        return jsonKey;
	}
}
