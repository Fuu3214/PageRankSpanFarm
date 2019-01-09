import java.io.*;

public class SpamFarm {

	private int target;

	private Graph G;
	private int N;

	public SpamFarm(String filename, int target){

		this.target = target;
		G = new Graph(filename);
		N = G.numNodes();

	}
	private StringBuilder addEdgeLine(StringBuilder output, String node1, String node2) {
		output.append(node1);
		output.append(" ");
		output.append(node2);
		output.append("\r\n");
		return output;
	}

	private void writeFile(String contents,  String fileName) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
			writer.write(contents);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}

	public void CreateSpam(String fileName){
		int numNew = N/10;
		if(target > N || target == 0) return;
		String targetName = Integer.toString(target);//convert your "nice" integer input to string node name
		if(!G.containsNode(targetName)) return;

		StringBuilder output = new StringBuilder();//original nodes
		output.append(N + numNew);
		output.append("\r\n");
		output.append(G.toString());

		for(int i = 1; i <= numNew; i++) {//append new
			String newNodeName = Integer.toString(i + N);
			output = addEdgeLine(output, newNodeName, targetName);
			output = addEdgeLine(output, targetName, newNodeName);
		}

		writeFile(output.toString(), fileName);
	}

}
