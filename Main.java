import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final int NODE_ZERO = 0;
		MainObject object = ReadConfigFile.readConfigFile(args[1]);
		object.id = Integer.parseInt(args[0]);
		int curNode = object.id;
		object.config = args[1];
		MainObject.outputFile = object.config.substring(0, object.config.lastIndexOf('.'));
		ConvergeCast.constructSpanningTree(object.adjMtx);
		for(int i=0;i<object.nodes.size();i++){
			object.nodeInfo.put(object.nodes.get(i).nodeId, object.nodes.get(i));
		}
		Server server = new Server(object);
		new Client(object, curNode);
		object.vectorClock = new int[object.numofNodes];
		object.initialize(object);
		if(curNode == NODE_ZERO) {
			object.isActive = true;
			new ChandyLamportProtocolThread(object).start();
			new SendingMessageThread(object).start();
		}
		server.listen();
		
	}
}
