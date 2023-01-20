import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

enum Color {RED,BLUE};
@SuppressWarnings("serial")
public class MainObject implements Serializable  {
	int id;
	int[][] adjMtx;
	ArrayList<Integer> neighbors;
	int[] vectorClock;
	boolean isActive;
	int messageSentCount;
	String config;
	int numofNodes;
	int minPerActive;
	int maxPerActive;
	int minSendDelay;
	int snapshotDelay;
	int maxNumber;
	
	Color color;
	int saveChannelMsg;
	boolean isFirstSnapshot;
	static String outputFile;
	HashMap<Integer,Node> nodeInfo;
	ArrayList<Node> nodes;
	HashMap<Integer,Socket> channels;
	HashMap<Integer,ArrayList<ApplicationMessage>> applicationChannelStates;
	HashMap<Integer,ObjectOutputStream> objectStream;
	HashMap<Integer,StateMessage> stateMap;
	HashMap<Integer,Boolean> markerIsRecievedMap;
	StateMessage currentState;
	boolean[] stateIsRecieved;
	ArrayList<int[]> globalSnapshots;
	
	public MainObject() {
		messageSentCount = 0;
		isActive =false;
		neighbors = new ArrayList<>();
		color = Color.BLUE;
		saveChannelMsg=0;
		isFirstSnapshot = true;
		nodes = new ArrayList<Node>();
		nodeInfo = new HashMap<Integer,Node>();
		channels = new HashMap<Integer,Socket>();
		objectStream = new HashMap<Integer,ObjectOutputStream>();
		globalSnapshots = new ArrayList<int[]>();
	}
	
	void initialize(MainObject mapObject){
		mapObject.applicationChannelStates = new HashMap<Integer,ArrayList<ApplicationMessage>>();
		mapObject.markerIsRecievedMap = new HashMap<Integer,Boolean>();
		mapObject.stateMap = new HashMap<Integer,StateMessage>();

		Set<Integer> keys = mapObject.channels.keySet();
		
		for(Integer e : keys){
			ArrayList<ApplicationMessage> arrList = new ArrayList<ApplicationMessage>();
			mapObject.applicationChannelStates.put(e, arrList);
		}
		
		for(Integer e: mapObject.neighbors){
			mapObject.markerIsRecievedMap.put(e,false);
		}
		mapObject.stateIsRecieved = new boolean[mapObject.numofNodes];
		mapObject.currentState = new StateMessage();
		mapObject.currentState.vectorClock = new int[mapObject.numofNodes];
	}	
}
