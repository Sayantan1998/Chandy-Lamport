import java.util.LinkedList;
import java.util.Queue;

public class ConvergeCast {
	
	static int[] parent;
	public static int getAncestor(int id) {
		return parent[id];
	}
	
	static void constructSpanningTree(int[][] adjMtx){
		boolean[] visited = new boolean[adjMtx.length];
		parent = new int[adjMtx.length];
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(0);
		parent[0] = 0;
		visited[0] = true;
		while(!queue.isEmpty()){
			int node = queue.remove();
			for(int i=0;i<adjMtx[node].length;i++){
				if(adjMtx[node][i] == 1 && visited[i] == false){
					queue.add(i);
					ConvergeCast.parent[i] = node;
					visited[i] = true;
				}
			}
		}
	}
}
