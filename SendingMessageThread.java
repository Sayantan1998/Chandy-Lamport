import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class SendingMessageThread extends Thread{
	MainObject mappingOfObject;
	public SendingMessageThread(MainObject mappingOfObject) {
		this.mappingOfObject = mappingOfObject;
	}
	void sendMessages() throws InterruptedException{
		int randomMessages = 1;
		int minimumSendDelay = 0;
		synchronized(mappingOfObject){
			randomMessages = this.getRandomNumber(mappingOfObject.minPerActive, mappingOfObject.maxPerActive);
			if(randomMessages == 0){
				randomMessages = this.getRandomNumber(mappingOfObject.minPerActive + 1, mappingOfObject.maxPerActive);
			}
			minimumSendDelay = mappingOfObject.minSendDelay;
		}
		for(int i=0;i<randomMessages;i++){
			synchronized(mappingOfObject){
				int randNeighborNode = this.getRandomNumber(0, mappingOfObject.neighbors.size()-1);
				int currentNeighbor = mappingOfObject.neighbors.get(randNeighborNode);

				if(mappingOfObject.isActive == true){
					ApplicationMessage m = new ApplicationMessage();
					mappingOfObject.vectorClock[mappingOfObject.id]++;
					m.vectorClock = new int[mappingOfObject.vectorClock.length];
					System.arraycopy( mappingOfObject.vectorClock, 0, m.vectorClock, 0, mappingOfObject.vectorClock.length );
					m.nodeId = mappingOfObject.id;
					try {
						ObjectOutputStream oos = mappingOfObject.objectStream.get(currentNeighbor);
						oos.writeObject(m);	
						oos.flush();
					} catch (IOException exe) {
						exe.printStackTrace();
					}	
					mappingOfObject.messageSentCount++;
				}
			}
			try{
				Thread.sleep(minimumSendDelay);
			}
			catch (InterruptedException e) {
				System.out.println("Error in SendMessages");
				e.printStackTrace();
			}
		}
		synchronized(mappingOfObject){
			mappingOfObject.isActive = false;
		}
	}
	public void run(){
		try {
			this.sendMessages();
		} catch (InterruptedException e) {
			System.out.println("Error in SendMessages");
			e.printStackTrace();
		}
	}
		int getRandomNumber(int min,int max){
		Random rd = new Random();
		int randomNumber = rd.nextInt((max - min) + 1) + min;
		return randomNumber;
	}
}
