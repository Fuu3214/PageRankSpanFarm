import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;

public class Graph {
	private int N;
	private int M;
	private HashMap<String, Integer> nodes;
	private String[] names;
	private HashMap<Integer, LinkedList<Integer>> adjacentList;
	
	public Graph(String filename){
		buildFrom(filename);
	}
	private void buildFrom(String filename) {
		String filePath = filename;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {            
            String line;
            String[] v;
            N = new Integer(br.readLine());
            nodes = new HashMap<String, Integer>(N);
            names = new String[N];
    		adjacentList = new HashMap<>();
    		
            while ((line = br.readLine()) != null) {
            	v = split(line);
            	addToGraph(v[0], v[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	private void addEdge(int u, int v) {
		M++;
		if(adjacentList.get(u) == null)
			adjacentList.put(u, new LinkedList<Integer>());
		adjacentList.get(u).add(v);
	}
	private int addNode(String nodeName) {
		if(!nodes.containsKey(nodeName)) {
			int index = nodes.size();
			nodes.put(nodeName, index);
			names[index] = nodeName;
			return index;
		}
		int index = nodes.get(nodeName);
		return index;
	}
	private String[] split(String str) {
		String [] arr = str.split("\\s+");
		return arr;
	}
	
	public void addToGraph(String node1, String node2) {
		int u = addNode(node1);
		int v = addNode(node2);
		addEdge(u,v);
	}
	public int numNodes() {
		return N;
	}
	public int numEdges() {
		return M;
	}
	public String[] nodeNames() {
		return names;
	}
	public boolean containsNode(String nodeName) {
		return nodes.containsKey(nodeName);
	}
	public int indexofNode(String nodeName) {
		return nodes.get(nodeName);
	}
	public String nameOf(int index) {
		return names[index];
	}
	public HashMap<Integer, LinkedList<Integer>> adjacentList(){
		return adjacentList;
	}
	public void writeAsInteger(String filename) {
		StringBuilder sb = new StringBuilder();
		sb.append(N);
		sb.append("\r\n");
		for(int index = 0; index < N; index++) {
			LinkedList<Integer> Q = adjacentList.get(index);
			if(Q != null)
				for(int v : Q) {
					sb.append(index + 1);
					sb.append(" ");
					sb.append(v + 1);
					sb.append("\r\n");
				}
		}
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    writer.write(sb.toString());
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int index = 0; index < N; index++) {
			LinkedList<Integer> Q = adjacentList.get(index);
			if(Q != null)
				for(int v : Q) {
					sb.append(names[index]);
					sb.append(" ");
					sb.append(names[v]);
					sb.append("\r\n");
				}
		}
		return sb.toString();
	}
}
