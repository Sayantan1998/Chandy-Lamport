import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	ServerSocket listener = null;
	Socket socket = null;
	int serverPort;
	private MainObject object;
	
	public Server(MainObject object) {
		
		this.object = object;
		serverPort = object.nodes.get(object.id).port;
		try {
			listener = new ServerSocket(serverPort);
		} 
		catch(BindException e) {
			System.out.println("Node " + object.id + " : " + e.getMessage() + ", Port : " + serverPort);
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void listen(){
		try {
			while (true) {
				try {
					socket = listener.accept();
				} catch (IOException e1) {
					System.out.println("Connection Broken");
					System.exit(1);
				}
				new ReceivingThread(socket, object).start();
			}
		}
		finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}