import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//Print the globalSnapshots to the output File
public class Output {
	MainObject object;

	public Output(MainObject object) {
		this.object = object;
	}


	public void storeSnapshotsToFile() {
		String fileName = MainObject.outputFile + "-" + object.id + ".out";
		synchronized(object.globalSnapshots){
			try {
				File file = new File(fileName);
				FileWriter fW;
				if(file.exists()){
					fW = new FileWriter(file,true);
				}
				else
				{
					fW = new FileWriter(file);
				}
				BufferedWriter bW = new BufferedWriter(fW);

   
				for(int i = 0; i< object.globalSnapshots.size(); i++){
					for(int j: object.globalSnapshots.get(i)){
						bW.write(j + " ");
						
					}
					if(i<(object.globalSnapshots.size()-1)){
						bW.write("\n");
					}
				}			
				object.globalSnapshots.clear();
				bW.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
		}
	}

}

