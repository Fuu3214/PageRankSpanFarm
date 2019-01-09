
import java.util.ArrayList;

public class MyWiKiRanker {
	
	private String[] mapNames(int[] indexList, String[] realNames) {
        String[] ret = new String[indexList.length];
        for(int i = 0; i < indexList.length; i ++) {
        	ret[i] = realNames[indexList[i] - 1];
        }
        return ret;
	}

    public MyWiKiRanker(String seedUrl, String[] keywords, String output, int topN, int maxNode) {
        WikiCrawler crawler = new WikiCrawler(seedUrl, keywords,maxNode, output, true);
        crawler.crawl();
        
        Graph G = new Graph(output);
        String[] nodeNames = G.nodeNames();
        String newGraphFile = "integer_" + output;
        G.writeAsInteger(newGraphFile);

        PageRank prC = new PageRank(newGraphFile, 0.01, 0.85);
        PageRank prD = new PageRank(newGraphFile, 0.005, 0.85);
        PageRank prE = new PageRank(newGraphFile, 0.001, 0.85);
        
        String[] A = mapNames(prC.topKOutDegree(topN), nodeNames);//change to this
        String[] B = mapNames(prC.topKInDegree(topN), nodeNames);
        String[] C = mapNames(prC.topKPageRank(topN), nodeNames);
        String[] D = mapNames(prD.topKPageRank(topN), nodeNames);
        String[] E = mapNames(prE.topKPageRank(topN), nodeNames);
        // Get all the sets

        ArrayList<String[]> alist = new ArrayList<>();
        alist.add(A);
        alist.add(B);
        alist.add(C);
        alist.add(D);
        alist.add(E);



        for (int i = 65; i < 70; i ++) {
            System.out.println("Top " + topN +" set "+ (char) i + " contains: ");
            for (String s : alist.get(i-65)) {
                System.out.println(s);
            }

            for (int j = i+1; j < 70; j ++) {
                System.out.println("The exact Jaccard Similarities among " +
                        (char) i + " and " + (char) j + " is " +
                        exactJS(alist.get(i-65), alist.get(j-65)));
            }
        }
    }

    private double exactJS(String[] a1, String[] a2) {
        double total = a1.length + a2.length;
        double common = 0;

        for (int i = 0; i < a1.length; i++) {
            for (int j = 0; j < a2.length; j++) {
                if (a1[i].equals(a2[j])) {
                    common ++;
                    total --;
                }
            }
        }
        return common / total;
    }
}
