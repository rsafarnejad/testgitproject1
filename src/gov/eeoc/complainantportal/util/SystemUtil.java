package gov.eeoc.complainantportal.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtil {
	
	public final static String CONFIG_PROPERTY_FILE = "config.properties"; 
	public final static String EMAIL_BODY_CONTENT_FILE = "emailText.properties"; 
//	public final static String ALFRESCO_URL_PROP ="alfresco.service.url"; commented and will be removed
	
	
	private final static String REPORT_URL_PROP = "report.url";
	
	
	Logger log = LoggerFactory.getLogger(SystemUtil.class);
	
	/*
	 * Map contains "Case Name" as the key and a list containing charge/inquiry number
	 */
	public boolean senEmail(String emailTo, Set<String> ccList, Map<String, List<String>> dataMap){
		boolean isSucessful = false;
		try{
			String caseName = this.getCaseName(dataMap);
			isSucessful = sendEmailWithoutAttachment(emailTo, ccList, buildStringFromMap(dataMap), caseName);
		}catch (Exception e){
			log.error(e.toString());
		}
		
		
		return isSucessful;
	}
	
	
	public boolean sendEmailWithoutAttachment(String to, Set<String> ccList, String content, String caseName) {
		
		boolean isSucessful = false;
		Email email = new SimpleEmail();
		Properties configProperties = null;
		try {
			configProperties = getPropertiesFromClasspath(CONFIG_PROPERTY_FILE);
		} catch (Exception e1) {
			log.error("Unable to load property file" + CONFIG_PROPERTY_FILE, e1.toString());
			return false;
		}
		String emailHostName = configProperties.getProperty("email.hostname");
		String subject = configProperties.getProperty("email.subject.line");
		subject = subject + " " +  caseName;
		email.setHostName(emailHostName);
		String emailPortStr = configProperties.getProperty("email.smtp.port");
		if(emailPortStr != null){
			emailPortStr = emailPortStr.trim();
		}
		email.setSmtpPort(Integer.valueOf(emailPortStr));
		String emailAuthReq = configProperties.getProperty("email.auth.required");
		if(emailAuthReq != null && emailAuthReq.equalsIgnoreCase("true")){
			email.setAuthentication(configProperties.getProperty("email.smtp.username"),
					configProperties.getProperty("email.smtp.password"));
		}
		email.setTLS(true);
		try {
			email.setFrom(configProperties.getProperty("email.from"));
			email.setSubject(subject);
			email.setMsg(content);
			email.addTo(to);
			if(ccList != null){
				Iterator i = ccList.iterator();
				while(i.hasNext()){
					String cc = (String) i.next();
					if(cc != null && !cc.isEmpty()){
						email.addCc(cc);
					}
				}
			}
			email.send();
			isSucessful =  true;
		} catch (EmailException e) {
			log.error("Unable to send email : {}", e);
		}
		return isSucessful;
	}
	
	public String getFileContent(String fileName) {
		String content = null;
		try{
			//fileName = "/" + fileName;
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
			content = IOUtils.toString(is);
		}catch (Exception e){
			log.error(e.toString());
			// ignore
		}
		return content;
	}


	public Properties getPropertiesFromClasspath(String fileName) throws RuntimeException {
		if(fileName == null || fileName.isEmpty()) return null;
		Properties props = new Properties();
		InputStream inputStream;
		inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			props.load(inputStream);
			if (inputStream == null) {
				throw new FileNotFoundException("property file '" + fileName
						+ "' not found in the classpath");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		

		return props;
	}
	
	public String buildReportServerUrl(String webServer, Long chargeSequenceNumber, Long userSequenceNumber, String token){
		if(webServer == null || chargeSequenceNumber == null || userSequenceNumber == null || token == null) return null;
		String url = null;
		Properties prop;
		try {
			prop = getPropertiesFromClasspath(CONFIG_PROPERTY_FILE);
			String partialUrl = prop.getProperty(REPORT_URL_PROP);
			
			Map <String, String>valuesMap = new HashMap <String, String>();
			valuesMap.put("webserver", webServer);
			valuesMap.put("chargeSequenceNumber", String.valueOf(chargeSequenceNumber.longValue()));
			valuesMap.put("userSequenceNumber", String.valueOf(userSequenceNumber.longValue()));
			valuesMap.put("token", token);
			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			url = sub.replace(partialUrl);
		} catch (Exception e) {
			log.error("Error building report url  for webserver {}, chargeNumber {}, userSequenceNumber {}, token {}", webServer, chargeSequenceNumber);
			log.error("Error building url {}", e.toString());
		}
		return url;
	}
	
	private String getCaseName(Map<String, List<String>> dataMap){
		if(dataMap == null || dataMap.isEmpty()) return null;
		Set caseSet = dataMap.keySet();
		Iterator <String> i = caseSet.iterator();
		String caseName = null;
		while(i.hasNext()){
			caseName = (String) i.next();
			if(caseName != null){
				break;
			}
		}
		return caseName;
	}
	
	private String buildStringFromMap(Map<String, List<String>> dataMap){
		StringBuilder builder = new StringBuilder();
		if(dataMap == null || dataMap.isEmpty()) return null;
		Set caseSet = dataMap.keySet();
		Iterator <String> i = caseSet.iterator();
		String caseName =  null;
		while(i.hasNext()){
			caseName = (String) i.next();
			List<String> charges = dataMap.get(caseName);
			if(charges != null && !charges.isEmpty()){
				int index = 1;
				for(String charge : charges){
					builder.append("\t");
					builder.append("(");
					builder.append(index++);
					builder.append(")");
					builder.append("\t\t\t");
					builder.append(charge);
					builder.append("\n");
				}
			}
		}
		String dataListStr = builder.toString();
		Map <String, String>valuesMap = new HashMap <String, String>();
		valuesMap.put("caseName", caseName);
		valuesMap.put("resultData", dataListStr);
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String emailText = this.getFileContent(EMAIL_BODY_CONTENT_FILE);
		String resolvedStr = sub.replace(emailText);
		return resolvedStr;
	}
	
	/**
	 * @param key which is the property defined in the properties file.
	 * Caller does not need to provide ".prod" or ".test" extension
	 * @return value stored in the config.properties or null
	 */
	public String getValueByProperyKey(String key){
		Properties prop = getPropertiesFromClasspath(CONFIG_PROPERTY_FILE);
		Enumeration enm = prop.keys();
		while(enm.hasMoreElements()) {
			String entry = (String) enm.nextElement();
			if(entry.equalsIgnoreCase(key)){
				return prop.getProperty(key);
			} else if( entry.contains(key)){
				if(System.getProperty("PROD") != null){
					key = key + ".prod";
				} else {
					key = key + ".test";
				}
				return prop.getProperty(key);
			}
		}
		return null;
	}
	
	public static void main (String [] args){
		
		SystemUtil util = new SystemUtil();
		Map <String, List<String>> map = new HashMap <String, List<String>> ();
		List list = new ArrayList<String> ();
		list.add("1234");
		list.add("3453");
		list.add("3454");
		map.put("Test-1", list);
		Set ccList = new HashSet <String>();
		ccList.add("vinit.r.patel@gmail.com");
		util.senEmail("vinit.patel@eeoc.gov", ccList, map);
		
	}

}

