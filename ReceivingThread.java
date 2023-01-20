import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class ReceivingThread extends Thread {
	Socket socket;
	MainObject object;

	public ReceivingThread(Socket clientSocket, MainObject object) {
		this.socket = clientSocket;
		this.object = object;
	}

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true){
			try {
				Message msg;
				msg = (Message) ois.readObject();
				synchronized(object){
					boolean isNotTerminated = true;
					if(msg instanceof MarkerMessage){
						int channelNo = ((MarkerMessage) msg).nodeId;
						ChandyLamportProtocol.sendMarkerMessage(object,channelNo);
					}	

					else if((msg instanceof ApplicationMessage) &&
							(object.isActive == false) &&
							(object.messageSentCount < object.maxNumber) &&
							(object.saveChannelMsg == 0))
					{
						object.isActive = true;
						new SendingMessageThread(object).start();
					}
					
					else if((msg instanceof ApplicationMessage) &&
							(object.isActive == false) &&
							(object.saveChannelMsg == 1))
					{
						int channelNo = ((ApplicationMessage) msg).nodeId;
						ChandyLamportProtocol.saveChannelMessages(channelNo,((ApplicationMessage) msg) , object);
					}
					
					else if(msg instanceof StateMessage){
						if(object.id == 0){
							object.stateMap.put(((StateMessage)msg).nodeId,((StateMessage)msg));
							object.stateIsRecieved[((StateMessage) msg).nodeId] = true;
							if(object.stateMap.size() == object.numofNodes){
								//Check for termination or take next snapshot
								isNotTerminated = ChandyLamportProtocol.detectTermination(object,((StateMessage)msg));
								if(isNotTerminated){
									object.initialize(object);
									//Call thread again to take new snapshot
									new ChandyLamportProtocolThread(object).start();
								}								
							}
						}
						else{
							ChandyLamportProtocol.sendToAncestor(object,((StateMessage)msg));
						}
					}
					
					else if(msg instanceof StopMessage){
						ChandyLamportProtocol.sendStopMessage(object);
					}

					if(msg instanceof ApplicationMessage){
						for(int i = 0; i< object.numofNodes; i++){
							object.vectorClock[i] = Math.max(object.vectorClock[i], ((ApplicationMessage) msg).vectorClock[i]);
						}
						object.vectorClock[object.id]++;
					}
				}
			}
			catch(StreamCorruptedException sce) {
				sce.printStackTrace();
				System.exit(2);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(2);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				System.exit(2);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				System.exit(2);
			}
		}
	}
}
