package gov.eeoc.complainantportal.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtil {
	
	public final static String CONFIG_PROPERTY_FILE = "config.properties"; 	
	Logger log = LoggerFactory.getLogger(SystemUtil.class);
	
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
}

