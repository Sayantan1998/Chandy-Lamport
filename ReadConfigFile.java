import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadConfigFile {

	public static MainObject readConfigFile(String name) throws IOException{
		MainObject object = new MainObject();
		int node_count = 0,next = 0;
		int curNode = 0;
		String fileName = System.getProperty("user.dir") + "/" + name;
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			while((line = br.readLine()) != null) {
				if(line.length() == 0 || line.startsWith("#"))
					continue;
				String[] config_input;
				if(line.contains("#")){
					String[] config_input_comment = line.split("#.*$"); //Ignore text after # symbol
					config_input = config_input_comment[0].split("\\s+");
				}
				else {
					config_input = line.split("\\s+");
				}

				if(next == 0 && config_input.length == 6){
					object.numofNodes = Integer.parseInt(config_input[0]);
					object.minPerActive = Integer.parseInt(config_input[1]);
					object.maxPerActive = Integer.parseInt(config_input[2]);
					object.minSendDelay = Integer.parseInt(config_input[3]);
					object.snapshotDelay = Integer.parseInt(config_input[4]);
					object.maxNumber = Integer.parseInt(config_input[5]);
					object.adjMtx = new int[object.numofNodes][object.numofNodes];
					next++;
				}
				else if(next == 1 && node_count < object.numofNodes)
				{							
					object.nodes.add(new Node(Integer.parseInt(config_input[0]),config_input[1],Integer.parseInt(config_input[2])));
					node_count++;
					if(node_count == object.numofNodes){
						next = 2;
					}
				}
				else if(next == 2) {
					for(String i : config_input){
						if(curNode != Integer.parseInt(i)) {
							object.adjMtx[curNode][Integer.parseInt(i)] = 1;
							object.adjMtx[Integer.parseInt(i)][curNode] = 1;
						}
					}
					curNode++;
				}
			}
			br.close();  
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");                  
		}
		return object;
	}
}

