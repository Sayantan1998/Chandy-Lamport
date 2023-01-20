import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public Client(MainObject object, int curNode) {
		for(int i = 0; i<object.numofNodes; i++){
			if(object.adjMtx[curNode][i] == 1){
				String hostName = object.nodeInfo.get(i).host;
				int port = object.nodeInfo.get(i).port;
				InetAddress address = null;
				try {
					address = InetAddress.getByName(hostName);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}
				Socket client = null;
				try {
					client = new Socket(address,port);
				} catch (IOException e) {
					System.out.println("Connection ERROR : Check Connection");
					e.printStackTrace();
					System.exit(1);
				}
				object.channels.put(i, client);
				object.neighbors.add(i);
				ObjectOutputStream output = null;
				try {
					output = new ObjectOutputStream(client.getOutputStream());
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				object.objectStream.put(i, output);
			}
		}
	}
}
