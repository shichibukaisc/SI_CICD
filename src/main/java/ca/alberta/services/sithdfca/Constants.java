package ca.alberta.services.sithdfca;

public final class Constants {
	//Date and Time Values
	public static final String ISO_8601_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static final String ISO_8601_DATETIME_REGEX = "\\d{4}-\\d{2}-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}([+-]\\d{2}\\:\\d{2}|Z)";
	public static final String ISO_8601_DATETIME_EXAMPLE = "2021-04-23T17:59:09-06:00";
	public static final String ISO_8601_DATETIME_ERROR_MESSAGE = "You need to match the following pattern: " + Constants.ISO_8601_DATETIME_FORMAT;
	public static final String BASIC_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String BASIC_DATETIME_REGEX = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";
	public static final String BASIC_DATETIME_ERROR_MESSAGE = "You need to match the following pattern: " + Constants.BASIC_DATETIME_FORMAT;
	public static final String BASIC_DATE_ONLY_FORMAT = "yyyy-MM-dd";
	public static final String BASIC_HOUR_STRING = "00:00:00";
	public static final String EMPTY_STRING_REGEX = "^$";
	
	public static final String ALPHA_NUMERIC_PATTERN_WITH_SPACES = "^[a-zA-Z0-9 ]+$";
	public static final String ALPHA_NUMERIC_PATTERN_WITHOUT_SPACES = "^[a-zA-Z0-9]+$";
	
	//Messages
	public static final String ALPHA_NUMERIC_PATTERN_WITH_SPACES_MESSAGE = "This string value must contain only characters a-z, A-Z and numbers 0-9";
	public static final String ALPHA_NUMERIC_PATTERN_WITHOUT_SPACES_MESSAGE = "This string value must contain only characters a-z, A-Z, numbers 0-9 and spaces";
	
	//Swagger Configuration Values
	public static final String TAG_1_NAME = "TAG_1 Name";
	public static final String TAG_1_DESCRIPTION = "By setting TAG values in Contsants file, you can reference them to describe sections of your APIs.. This attribute is called TAG_1_DESCRIPTION and can be edited in Constants.java";
	public static final String TAG_2_NAME = "Subscription Calls";
	public static final String TAG_2_DESCRIPTION = "This call is an example of how the JWT token is used to receive and leverage the Subscription Information";
	public static final String TAG_3_NAME = "Utilities";
	public static final String TAG_3_DESCRIPTION = "Some additional helper endpoints that demonstrate certain functions as well as help with troubleshooting while developing";
	public static final String SECURITY_SCHEME_TYPE = "bearerAuth";
	public static final String SECURITY_SCHEME_NAME = "bearerAuth";
	
	//Default security configurations
	public static final String AUTHORIZATION_STRATEGY_ACCESS_TOKEN="access-token";
	public static final String AUTHORIZATION_STRATEGY_IN_MEMORY="in-memory";
	public static final String DEFAULT_AUTHORIZATION_STRATEGY=AUTHORIZATION_STRATEGY_ACCESS_TOKEN;
	public static final String USER_ROLE="SBRAS_USER";
	public static final String ADMIN_ROLE="SBRAS_ADMIN";
	public static final String [] ALL_ROLES= {USER_ROLE,ADMIN_ROLE};
	
	//Environment names
	public static final String LOCAL = "LCL";
	public static final String DEVELOPMENT = "DEV";
	public static final String UAT = "UAT";
	public static final String PROD = "PROD";
	
	
}
