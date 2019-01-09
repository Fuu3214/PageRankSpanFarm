import java.io.*;
import java.util.*;

public class test {
	public static void main(String[] args) {

//		WikiCrawler wikiCrawler = new WikiCrawler("/wiki/Tennis",
//				new String[] {"tennis", "grand slam"},
//				100, "wikiTennis.txt", true);
//		wikiCrawler.crawl();
//		
//
      MyWiKiRanker ranker = new MyWiKiRanker("/wiki/Tennis",
				new String[] {"tennis", "grand slam"}, "test.txt", 20, 100);
//		
//        PageRank pr = new PageRank("integer_wikiTennis.txt",  0.001, 0.85);
//        int target = pr.lowestPageRank();
//        System.out.print("Before update, the target is "+target);
//        System.out.println(" the current pagerank of the target is "+pr.pageRankOf(target));
//        SpamFarm sf = new SpamFarm("integer_wikiTennis.txt", (target));
//        sf.CreateSpam("./spam.txt");
//        PageRank pr1 = new PageRank("spam.txt",  0.001, 0.85);
//        System.out.println("Updated " + target + " to "+pr1.pageRankOf(target));

        
//        String[] topics = {"tennis", "grand slam"};
//		WikiCrawler crawler = new WikiCrawler("/wiki/Tennis", topics,100,
//							"WikiTennisGraph.txt", true);
//		crawler.crawl();
		
//        Graph G = new Graph("WikiTennisGraph.txt");
//        String newGraphFile = "integer_" + "WikiTennisGraph.txt";
//        G.writeAsInteger(newGraphFile);
        
		PageRank pr = new PageRank("integer_wikiTennis.txt",  0.001, 0.85);
		System.out.println(pr.numIteration());
		System.out.println("Page Rank of 1 " + pr.pageRankOf(1) + " number of iteration: " + pr.numIteration());
		System.out.println("outdegree of 2 " + pr.outDegreeOf(2));
		System.out.println("indegree of 1 " + pr.inDegreeOf(1));
		
//		System.out.print("topk: " + Arrays.toString(pr.topKOutDegree(3)));
//		
//		SpamFarm sf = new SpamFarm("test.txt", 1);
//		sf.CreateSpam("spam.txt");
//		PageRank pr1 = new PageRank("./testOut2.txt", 0.01, 0.85);
//		System.out.println("Page Rank of /wiki/Complexity_theory " + pr1.pageRankOf("/wiki/Complexity_theory"));
//		nodeEdgeInfor("Complexity_theory.txt");

		//		System.out.println("Page Rank of /wiki/Complexity_theory " + pr.pageRankOf("/wiki/Complexity_theory"));
//
	}
	
	public static void testQ() {
        long start = System.currentTimeMillis();       
		WeightedQ<Integer, Integer> wqu = new WeightedQ<Integer, Integer>();
		Vector<Integer> order = new Vector<Integer>();
		HashSet<Integer> showed = new HashSet<Integer>();
		System.out.println("begin");
		int lastAdd = 0;
//		Random generator = new Random(2333);
		for(int i = 0; i < 233333; i++) {
			int randKey = (int)(Math.random() * 2333);
			int randValue = (int)(Math.random() * 2333);

			if(!showed.contains(randKey)){
				order.add(randKey);
				showed.add(randKey);
				lastAdd = randKey;
			}
			GeneralTuple2D<Integer, Integer> tuple = new GeneralTuple2D<Integer, Integer>(randKey,randValue);
			System.out.println("Add order: " + tuple.toString());
			wqu.add(tuple);
		}
		wqu.add(new GeneralTuple2D<Integer, Integer>(lastAdd,2332));
		GeneralTuple2D<Integer, Integer> vv = wqu.extract();
		do {
			System.out.println(vv.toString());
			vv = wqu.extract();
		}while(vv != null);
		long time = System.currentTimeMillis() - start;
		System.out.println(time + "ms");
		System.out.println("lastAdd: " + lastAdd);
		System.out.println("order: " + order.toString());
	}
	public static void testSearchNode() {
		HashMap<SearchNode, Integer> visited = new HashMap<SearchNode, Integer>();
		SearchNode mockParent = new SearchNode("root");
		SearchNode mockParent2 = new SearchNode("root");
		SearchNode mockChild = new SearchNode("a");
		visited.put(new SearchNode("a", mockParent), 1);
		visited.put(new SearchNode("b", mockParent), 1);
		visited.put(new SearchNode("c", mockParent), 1);
		System.out.println(visited.containsKey(new SearchNode("a", mockParent)));
		System.out.println(visited.containsKey(new SearchNode("a", mockParent2)));
		System.out.println(visited.containsKey(new SearchNode("a", mockChild)));
		System.out.println(visited.containsKey(new SearchNode("root", mockChild)));
		System.out.println(visited.toString());
		SearchNode mockChild1 = new SearchNode("a", mockParent);
		SearchNode mockChild2 = new SearchNode("a", mockParent2);
		SearchNode mockChild3 = new SearchNode("root", mockChild);
		System.out.println(mockChild1.equals(mockChild2));
		System.out.println(mockChild1.equals(mockChild3));
		System.out.println(mockChild1.equals(mockChild1));
	}

	public static void nodeEdgeInfor(String filename) {
		HashSet<String> allNodes = new HashSet<>();
		HashSet<String[]> allEdges = new HashSet<>();
		int selfLoop = 0;
		int claimedEdges = 0;
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			System.out.println("Claimed Node number: " + sc.nextLine());
			while (sc.hasNextLine()) {
				StringTokenizer st = new StringTokenizer(sc.nextLine());
				claimedEdges ++;
				String s = st.nextToken();
				String t = st.nextToken();
				// Check Node
				if (!allNodes.contains(s)) {
					allNodes.add(s);
				}

				if (!allNodes.contains(t)) {
					allNodes.add(t);
				}

				// Check self loop
				if (s.equals(t)) {
					selfLoop ++;
				}

				// Check edges
				String[] newEdge = new String[]{s, t};
				if (!allEdges.contains(newEdge)) {
					allEdges.add(newEdge);
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("All Nodes Number: " + allNodes.size());
		System.out.println("All Edges Number: " + allEdges.size());
		System.out.println("Self loops: " + selfLoop + ", claimed edge numbers: " + claimedEdges);

	}
	
}
