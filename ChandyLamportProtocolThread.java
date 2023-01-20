public class ChandyLamportProtocolThread extends Thread{

	MainObject object;
	public ChandyLamportProtocolThread(MainObject object){
		this.object = object;
	}
	public void run(){
		if(object.isFirstSnapshot){
			object.isFirstSnapshot = false;
		}
		else{
			try {
				Thread.sleep(object.snapshotDelay);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		ChandyLamportProtocol.begin(object);
	}
}
