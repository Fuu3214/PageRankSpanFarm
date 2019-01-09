import java.util.Arrays;

public class SortTest {
    public static void main(String[] args) {
        PageRank pr = new PageRank("integer_complex.txt",  0.001, 0.85);
        int target = pr.lowestPageRank();
        System.out.print("Before update, the target is "+target);
        System.out.println(" the current pagerank of the target is "+pr.pageRankOf(target));
        SpamFarm sf = new SpamFarm("integer_complex.txt", (target));
        sf.CreateSpam("./spam.txt");
        PageRank pr1 = new PageRank("spam.txt",  0.001, 0.85);
        System.out.println("Updated " + target + " to "+pr1.pageRankOf(target));
//
//        int target1 = pr1.lowestPageRank();
//        System.out.print("Before update, the target is "+target1);
//        System.out.println(" the current pagerank of the target1 is "+pr1.pageRankOf(target1));
//        SpamFarm sf1 = new SpamFarm("spam.txt", (target1));
//        sf1.CreateSpam("./spam1.txt");
//        PageRank pr2 = new PageRank("spam1.txt",  0.001, 0.85);
//        System.out.println("Updated " + target1 + " to "+pr2.pageRankOf(target1));
        String[] topics = {"tennis", "grand slam"};
//        WikiCrawler crawler = new WikiCrawler("/wiki/Tennis", topics,100,
//                "WikiTennisGraph.txt", true);
//        crawler.crawl();

//        PageRank pr = new PageRank("integer_WikiTennisGraph.txt",  0.01, 0.85);
//        System.out.println("Itr when epsilon is 0.01 " + pr.numIteration());
//        PageRank pr1 = new PageRank("integer_WikiTennisGraph.txt",  0.001, 0.85);
//        System.out.println("Itr when epsilon is 0.001 " + pr1.numIteration());
//        PageRank pr2 = new PageRank("integer_WikiTennisGraph.txt",  0.005, 0.85);
//        System.out.println("Itr when epsilon is 0.005 " + pr2.numIteration());

//        MyWiKiRanker ranker = new MyWiKiRanker("/wiki/Complexity",
//				new String[] {"Complexity", "complex"}, "complex.txt", 20, 500);
    }
}
