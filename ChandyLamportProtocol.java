import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class ChandyLamportProtocol {
  
	public static void begin(MainObject object) {
		synchronized(object){
			object.stateIsRecieved[object.id] = true;
			sendMarkerMessage(object,object.id);
		}
	}
	public static void sendMarkerMessage(MainObject object, int channelId){
		synchronized(object){
			if(object.color == Color.BLUE){
				object.markerIsRecievedMap.put(channelId, true);
				object.color = Color.RED;
				object.currentState.isActive = object.isActive;
				object.currentState.vectorClock = object.vectorClock;
				object.currentState.nodeId = object.id;
				int[] tempVectorClock = new int[object.currentState.vectorClock.length];
				for(int i=0;i<tempVectorClock.length;i++){
					tempVectorClock[i] = object.currentState.vectorClock[i];  //Local Snapshot
				}
				object.globalSnapshots.add(tempVectorClock);

				object.saveChannelMsg = 1;
				for(int i : object.neighbors){
					MarkerMessage m = new MarkerMessage();
					m.nodeId = object.id;
					ObjectOutputStream oos = object.objectStream.get(i);
					try {
						oos.writeObject(m);
						oos.flush();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				if((object.neighbors.size() == 1) && (object.id!=0)){
					int parent = ConvergeCast.getAncestor(object.id);
					object.currentState.channelStates = object.applicationChannelStates;
					object.color = Color.BLUE;
					object.saveChannelMsg = 0;
					ObjectOutputStream output = object.objectStream.get(parent);
					try {
						output.writeObject(object.currentState);
						output.flush();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					object.initialize(object);
				}


			}
			else if(object.color == Color.RED){
				object.markerIsRecievedMap.put(channelId, true);
				int channel=0;
				while(channel<object.neighbors.size() && object.markerIsRecievedMap.get(object.neighbors.get(channel)) == true){
					channel++;
				}
				
				if(channel == object.neighbors.size() && object.id != 0){
					int parent = ConvergeCast.getAncestor(object.id);
					object.currentState.channelStates = object.applicationChannelStates;
					object.color = Color.BLUE;
					object.saveChannelMsg = 0;
					ObjectOutputStream output = object.objectStream.get(parent);
					try {
						output.writeObject(object.currentState);
						output.flush();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					object.initialize(object);
				}
				if(channel == object.neighbors.size() &&  object.id == 0){
					object.currentState.channelStates = object.applicationChannelStates;
					object.stateMap.put(object.id, object.currentState);
					object.color = Color.BLUE;
					object.saveChannelMsg = 0;
				}
			}
		}
	}
	public static boolean detectTermination(MainObject mapObject, StateMessage msg) throws InterruptedException {
		int channel=0,state=0,node=0;
		synchronized(mapObject){
			while(node < mapObject.stateIsRecieved.length && mapObject.stateIsRecieved[node] == true){
				node++;
			}
			if(node == mapObject.stateIsRecieved.length){
				for(state=0; state < mapObject.stateMap.size(); state++){
					if(mapObject.stateMap.get(state).isActive == true){
						return true;
					}
				}
				
				if(state == mapObject.numofNodes){
					for(channel=0; channel < mapObject.numofNodes; channel++){
						StateMessage value = mapObject.stateMap.get(channel);
						for(ArrayList<ApplicationMessage> cState : value.channelStates.values()){
							if(!cState.isEmpty()){
								return true;
							}
						}
					}
				}

				if(channel == mapObject.numofNodes){
					sendStopMessage(mapObject);
					return false;
				}
			}
		}
		return false;
	}


	public static void saveChannelMessages(int channelNo, ApplicationMessage applicationMsg, MainObject object) {
		synchronized(object){
			if(object.markerIsRecievedMap.get(channelNo) == false) {
				if((object.applicationChannelStates.get(channelNo).isEmpty())){
					ArrayList<ApplicationMessage> msgList = object.applicationChannelStates.get(channelNo);
					msgList.add(applicationMsg);
					object.applicationChannelStates.put(channelNo, msgList); // add to Hash map
				}
				else if(!(object.applicationChannelStates.get(channelNo).isEmpty())){
					object.applicationChannelStates.get(channelNo).add(applicationMsg);
				}
			}
		}
	}
	public static void sendToAncestor(MainObject mapObject, StateMessage stateMsg) {
		synchronized(mapObject){
			int parent = ConvergeCast.getAncestor(mapObject.id);
			// Send stateMsg to the parent
			ObjectOutputStream oos = mapObject.objectStream.get(parent);
			try {
				oos.writeObject(stateMsg);
				oos.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	public static void sendStopMessage(MainObject object) {
		synchronized(object){
			new Output(object).storeSnapshotsToFile();
			for(int s : object.neighbors){
				StopMessage m = new StopMessage();
				ObjectOutputStream oos = object.objectStream.get(s);
				try {
					oos.writeObject(m);
					oos.flush();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			System.out.println("Node : " + object.id + " - Success is yours to achieve and you have done it. Successfully written to output file");
			System.exit(0);
		}
	}
}

