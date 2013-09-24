package gov.eeoc.complainantportal.util;

import javax.inject.Named;

@Named
public interface Const {
	//To indicate the case is active
	public String ACTIVE_CASE_IND = "A";

	//To indicate the case is in-active
	public String INACTIVE_CASE_IND = "I";
	
	
	//To indicate Charge Status for the case
	public String ACTIVE_CHARGE = "Y";
	
	//To indicate Charge Status for the case
	public String INACTIVE_CHARGE = "N";
	
	public String SYSTEMIC_CASE = "SYSTEMIC_CASE";
	public String SYSTEMIC_CASE_ID = "SYSTEMIC_CASE_ID";
	
	public String  SYSTEMIC_EMAIL_RECIPIENT_ID = "SYSTEMIC_EMAIL_RECIPIENT_ID";
	public String  SYSTEMIC_EMAIL_OWNERSHIP =  "SYSTEMIC_EMAIL_OWNERSHIP";
	
	public String SYSTEMIC_CASE_CRITERIA = "SYSTEMIC_CASE_CRITERIA";
	
	public String SYSTEMIC_CASE_CRITERIA_ID = "SYSTEMIC_CASE_CRITERIA_ID";

	public String IS_OWNER = "Y";
	
	public String IS_NOT_OWNER = "N";
	
	public static String IS_SUBSCRIBER = "Y";
	
	public static String IS_NOT_SUBSCRIBER = "N";
	
	// Session Variable Names Used
	public String SHARED_STAFF_LIST = "SharedStaffList";
	public String SHARED_STATUTE_LIST = "SharedStatuteList";
	public String CASE_CATEGORY_LIST = "CaseCategoryList";

	// table name
	public String SYSTEMIC_CASE_CHARGE = "SYSTEMIC_CASE_CHARGE";
	// PK for SYSTEMIC_CASE_CHARGE
	public String SYSTEMIC_CASE_CHARGE_ID = "SYSTEMIC_CASE_CHARGE_ID";

	public final static Long IMS_STAFF_SEQ = 12900L;
	
	public static String STAFF_SEQ_ID = "staffid";
	
	public static String APPLICATION_ID = "appid";
	
	public static String SELECTED_TAB = "selectedtab";

		
	public final static String RECORD_STATUS_ADDED = "Added";
	
	public final static String RECORD_STATUS_REMOVED = "Removed";
	
	public final static String RECORD_STATUS_RETAINED = "Retained";
	
	//Java Email Related
	
	String FROM_EMAIL="complainantportal@eeoc.gov";
	
	String SMTP_SERVER="10.9.24.141";
	
	// The default user status
	public final static String USER_STATUS_DEFAULT = "Not Assigned";
	public final static String USER_STATUS_COORDINATING = "Coordinating";
	public final static String USER_STATUS_UNDER_CONSIDERATION = "Under Consideration";
	public final static String USER_STATUS_NOT_RELATED = "Considered - Not Related";
	
	public final static String ADDED_BY_SYSTEM = "SYSTEM";
	
	// When user explicitly adds charge to a case via IMS
	public final static String ADDED_BY_MANUAL = "MANUAL";
		
	/*
	 * The following group of variables indicate criteria_type flag used in SYSTEMIC_CASE_CRITERIA
	 * to differentiate one criteria from other.
	 */
	public final static String  CRITERIA_TYPE_RESP_NAME = "CTR";
	
	public final static String CRITERIA_TYPE__EEO_NUM = "CTN";
	
	public final static String CRITERIA_TYPE_EEO_UNIT_NUM = "CTU";
	
	public final static String CRITERIA_TYPE_STATUTE = "CTS";
	
	public final static String CRITERIA_TYPE_BASIS = "CTB";
	
	public final static String CRITERIA_TYPE_ISSUE = "CTI";
	
	public final static String CRITERIA_TYPE_RECORD = "CTQ";
	
	public final static String CRITERIA_TYPE_NUM_CLOSE_DAYS = "CTC";
	
	public final static String OPERATOR_TYPE_AND = "AND";
	
	public final static String OPERATOR_TYPE_OR = "OR";
	
	

	public final static String FLAG_FALSE = null;
	
	public final static String REC_QUERY_TYPE_CHARGE = "C";
	
	public final static String REC_QUERY_TYPE_OPEN_CHARGE = "O";
	
	public final static String REC_QUERY_TYPE_CHARGE_INQ = "CI";
	
	public final static String REC_QUERY_TYPE_INQ = "I";
	
		
	/*
	 * Delimiter used to separate criteria
	 */
	public final static String CRITERIA_SEPARATOR_CHAR = ",";
	public final static String CRITERIA_SPACE_CHAR = " ";
	
	/*
	 * Constants for Tab View in Charge Documents
	 */
	public final static String TabA = "TAB-A: Field Office Work Product";
	public final static String TabB = "TAB-B: Jurisdictional Items";
	public final static String TabC = "TAB-C: Charging Party's/Complainant's Evidence";
	public final static String TabD = "TAB-D: Respondent's Evidence";
	public final static String TabE = "TAB-E: Other Evidence/Miscellaneous Materials";
	public final static String TabF = "ORIP Info";
	public final static String TabG = "Witness Info";
	
	// added the following constants for reading the properties from config file
	public static final String ALFRESCO_SERVICE_URL = "alfrescoServiceUrl";
	public static final String ALFRESCO_SERVICE_ENDPOINT = "alfrescoSericeEndpoint";
	public static final String ALFRESCO_USER = "alfrescoAdminUser";
	public static final String ALFRESCO_ADMIN_PWD = "alfrescoAdminPassword";
	public static final String ALFRESCO_FOLDER_PATH = "alfrescoFolderPath";
	
	
}
