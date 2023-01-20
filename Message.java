import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Message implements Serializable {
}

class ApplicationMessage extends Message implements Serializable{
	int nodeId;
	int[] vectorClock;
	String message = "Application Message";
}
class MarkerMessage extends Message implements Serializable{
	int nodeId;
	String message = "Marker Message";
}

class StateMessage extends Message implements Serializable{
	boolean isActive;
	int nodeId;
	HashMap<Integer,ArrayList<ApplicationMessage>> channelStates;
	int[] vectorClock;
}

class StopMessage extends Message implements Serializable{
	String msg = "Stop Message";
}
