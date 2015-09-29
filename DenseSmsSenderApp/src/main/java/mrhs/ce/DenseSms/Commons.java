package mrhs.ce.DenseSms;

public interface Commons {
	public final Integer OPERATION_INSERT_FAILED = -333;
	
	// Database related 
	public final Integer DATABASE_VERSION=1;
	public final String DATABASE_NAME = "denseSMS";
	
	//Message condition
	public final Integer MESSAGE_PENDING=0;
	public final Integer MESSAGE_SENT=1;
	public final Integer MESSAGE_DELIVERED=2;
	public final Integer MESSAGE_FAILED=9;
	
	//Response condition when the users respond to the message
	public final Integer RESPONSE_NOT_ANSWERED=0;
	public final Integer RESPONSE_ACCEPTED=1;
	public final Integer RESPONSE_INVALID=8;
	public final Integer RESPONSE_REJECTED=9;
	
	// Interval between messages (ms)
	
	public final Integer MESSAGE_INTERVAL = 5000;
	
	// Use this for deactivate log througout the app
	public final boolean SHOW_LOG = true;
}
