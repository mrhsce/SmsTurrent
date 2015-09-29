package gcm;

public interface Config {		
	
	// give your server registration url here
    static final String SERVER_URL = "http://androtech.ir/messaging/register.php"; 
    
    // Google project id
    static final String SENDER_ID = "198836429662"; 
    
 // application information
    static final String APP_NAME = "DenseSms";
        
    // Server login information
    static final String SERVER_PASSWORD = "password";
    static final String SERVER_USERNAME = "test";
    
    // Notification type
    final Integer PLAIN_MESSAGE_ID = 1373;
	final Integer REPORT_MESSAGE_ID = 1374;
	final Integer BUY_MESSAGE_ID = 1375;
	final Integer COMMENT_MESSAGE_ID = 1376;
	final Integer UPDATE_MESSAGE_ID = 1377;
       
    // Notification behaviour
    static final Integer NOTIFICATION_REMOVABLE = 0;
    static final Integer NOTIFICATION_NON_REMOVABLE = 5;

    // Update notification type
    static final Integer UPDATE_SELECTIVE = 0;
    static final Integer UPDATE_CRITICAL = 5;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.mrhs.gcm.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";
    
}