package gov.eeoc.complainantportal.util;

import javax.inject.Named;

@Named
public interface Const {
	
	// table name
	public String COMPLAINANT_DATA = "COMPLAINANT_DATA";
	
	public static String STAFF_SEQ_ID = "staffid";
	
	public static String APPLICATION_ID = "appid";

	//Java Email Related
	
	String FROM_EMAIL="ComplainantPortal@eeoc.gov";
	
	String SMTP_SERVER="10.9.24.141";
	
	// added the following constants for reading the properties from config file
	public static final String ALFRESCO_SERVICE_URL = "alfrescoServiceUrl";
	public static final String ALFRESCO_SERVICE_ENDPOINT = "alfrescoSericeEndpoint";
	public static final String ALFRESCO_USER = "alfrescoAdminUser";
	public static final String ALFRESCO_ADMIN_PWD = "alfrescoAdminPassword";
	public static final String ALFRESCO_FOLDER_PATH = "alfrescoFolderPath";
	
	
}
