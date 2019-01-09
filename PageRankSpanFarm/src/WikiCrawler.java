import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiCrawler {
//	private static final boolean DEBUGGING = false;
	public static final String BASE_URL = "https://en.wikipedia.org";
	
	private String seedUrl;
	private String[] keywords;
	private int max;
	private String fileName;
	private Boolean isTopicSensitive;
	
	private HashSet<String> forbidden; //from robots.txt
	private WeightedQ<SearchNode, Double> fringe;   
	private ArrayList<String> expanded; 
	
	private int counter;//count times we send request to wiki
	
	private StringBuilder output;
	
	public WikiCrawler(String seedUrl, String[] keywords, int max, String fileName, Boolean isTopicSensitive){
		this.seedUrl = seedUrl;
		this.keywords = keywords;
		this.max = max;
		this.fileName = fileName;
		this.isTopicSensitive = isTopicSensitive;
		
		forbidden = new HashSet<String>();
		expanded = new ArrayList<String>();
		fringe = new WeightedQ<SearchNode, Double>(isTopicSensitive);

		counter = 0;

		getForbidden();
		output = new StringBuilder();
		output.append(max);
		output.append("\r\n");
	}
		
	private void getForbidden() {
		try {
			System.out.println("Crawling robots.txt");
			URL url = new URL(BASE_URL + "/robots.txt");  
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  
			httpUrlConn.setDoInput(true);  
			httpUrlConn.setRequestMethod("GET");  
			httpUrlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		
			InputStream input = httpUrlConn.getInputStream();
		
			InputStreamReader read = new InputStreamReader(input, "utf-8");
		
			BufferedReader br = new BufferedReader(read);  
		
			String line = br.readLine();
			
			
		    while ((line = br.readLine()) != null) {
	            if(line.contains("Disallow:")) {
	            	String link = line.replaceAll("Disallow: ", "");
	            	if(!link.contains(":") && !link.contains("#"))//disregard urls with #,: .We are not going to visit them anyway
	            		forbidden.add(link);
	            }
	        }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
		
	private String extractContents(String hyperlink) {
		try {  
			counter = (counter + 1) % 10;
			if(counter == 0) {
				try {
					System.out.println("waiting...");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			URL url = new URL(hyperlink);  
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  
			httpUrlConn.setDoInput(true);  
			httpUrlConn.setRequestMethod("GET");  
			httpUrlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		
			InputStream input = httpUrlConn.getInputStream();
		
			InputStreamReader read = new InputStreamReader(input, "utf-8");
		
			BufferedReader br = new BufferedReader(read);  
		
			String line = br.readLine();
		    while ((line = br.readLine()) != null) {//skip before <p>
	            if(line.contains("<p>")) {
	            	break;
	            }
	        }

			StringBuilder sb = new StringBuilder(1024000);//build the entire doc
		    sb.append(line);
		    sb.append(" ");
		    
		    while ((line = br.readLine()) != null) {
//		    	System.out.println(line);
	            sb.append(line);
	            sb.append(" ");
	        }
		    br.close();
		
			br.close();  
			read.close();  
			input.close();  	
			httpUrlConn.disconnect();  
			return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private boolean exploreChilden(SearchNode parent, String contents)
	{
		
		Pattern p = Pattern.compile("(<a href=\")(/wiki/.*?)(\")(.*?)(>)(.*?)(</a>)");
		Matcher m = p.matcher(contents);
		while(m.find()){
			String hyperlink = m.group(2);
			if(hyperlink.contains("#") || hyperlink.contains(":")) continue;
			if(forbidden.contains(hyperlink)) continue;
			if(hyperlink.equals(parent.getNodeName())) continue;//refuse self-loop
			String hypertext = m.group(6);
//			if (DEBUGGING) {
//				System.out.println(Arrays.toString(before));
//				System.out.println(hyperlink);
//				System.out.println(contents.);
//				System.out.println(hypertext);
//				System.out.println(Arrays.toString(after));
//				System.out.println(h_n);
//			}
			double h_n;
			
			if(isTopicSensitive) {
				int left = m.start();
				int right = m.end();
				
				int startOfLeft = left < 100 ? 0 : left - 100;
				int endOfRight = right > contents.length()-1 ? contents.length()-1 : right + 100;
				
				String beforeLowerCase = contents.substring(startOfLeft, left).toLowerCase();
				String afterLowerCase = contents.substring(right, endOfRight).toLowerCase();
				
				h_n = distanceHeuristic(hyperlink, hypertext, beforeLowerCase, afterLowerCase);
				
//				System.out.println(hyperlink + " " + hypertext + " " + h_n);
			}
			else
				h_n = 0.0;
			
			SearchNode child = new SearchNode(hyperlink, parent);
			
			GeneralTuple2D<SearchNode, Double> tuple = new GeneralTuple2D<SearchNode, Double>(child, h_n);
			
			fringe.add(tuple);

		}

//		if (DEBUGGING) System.out.println("Complete Node: " + parent.getNodeName());
		return false;
	}
	
	private double distanceHeuristic(String hyperlink, String hypertext, String beforeBuffer, String afterBuffer) {
		//larger the heuristic the better in this case
		if(containsKeywords(hyperlink) || containsKeywords(hypertext)) {
			return 1;
		}
		
		if(!containsKeywords(beforeBuffer) && !containsKeywords(afterBuffer)) {
			//some optimization
			return 0;
		}
		
		String[] before = beforeBuffer.split("\\W+");
		String[] after = afterBuffer.split("\\W+");
		int beforeDistance = Integer.MAX_VALUE;
		for(int i = before.length - 1; i >= Math.max(0, before.length - 17) ; i--) {
			if(keywordsAppear(before[i])) {
				beforeDistance = before.length - i;
				break;
			} 
		}
		int afterDistance = Integer.MAX_VALUE;
		for(int i = 0; i < Math.min(17, after.length) ; i++) {
			if(keywordsAppear(after[i])) {
				afterDistance = i + 1;
				break;
			}
		}
		int distance = Math.min(beforeDistance, afterDistance);
//		System.out.println(distance);
		return (distance > 17? 0 : 1/((double)distance + 2));
	}
	
	private boolean keywordsAppear(String str) {
		for(String keyword : keywords) {
			if (str != null && str.equalsIgnoreCase(keyword))
				return true;
		}
		return false;
	}
	private boolean containsKeywords(String str) {
		String tmp = str.toLowerCase();
		for(String keyword : keywords) {
			if (tmp != null && tmp.indexOf(keyword.toLowerCase()) >= 0)
				return true;
		}
		return false;
	}
	
	
	
	private void writeFile(String contents) {
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
	private void appendLine(SearchNode node) {
		output.append(node.get_parent().toString());
		output.append(" ");
		output.append(node.toString());
		output.append("\r\n");
	}
	public void crawl() {
		
		int count = 0;

		System.out.println("Searching, " + "keywords: " + Arrays.toString(keywords));
				
		fringe.add(new GeneralTuple2D<SearchNode, Double>(new SearchNode(seedUrl), 0.0));
		
		SearchNode currentNode = fringe.extract().getItem();
		String rootName = currentNode.getNodeName();
		expanded.add(rootName);
		count++;
		System.out.println(" Number of expanded sites: " + count);
		if (expanded.size() >= max)
			return;
		
		String currentURL = BASE_URL + rootName;
		exploreChilden(currentNode, extractContents(currentURL));


		while (currentNode != null){	
			
			currentNode = fringe.extract().getItem();
			String nodeName = currentNode.getNodeName();
			
			appendLine(currentNode);
			
			if(expanded.contains(nodeName)) continue;
			
			expanded.add(nodeName);
			
			count++;			
			System.out.println(" Number of expanded sites: " + count);

			currentURL = BASE_URL + nodeName;
			exploreChilden(currentNode, extractContents(currentURL));
			
			if (expanded.size() >= max) {
				GeneralTuple2D<SearchNode, Double> tuple = fringe.extract();
//				System.out.println(expanded.toString());
				while(tuple != null) {
					currentNode = tuple.getItem();
					if(expanded.contains(currentNode.getNodeName())) {
						appendLine(currentNode);
					}
					tuple = fringe.extract();
				}
				break;
			}
				

			// Provide a status report.
//			if (DEBUGGING) System.out.println("Nodes visited = " + nodesVisited
//					+ " |fringe| = " + fringe.size());
//			if (DEBUGGING) System.out.println("==========================================");
		}
	
		System.out.println("\nExpanded " + count + " nodes, starting @" +
				" " + BASE_URL + seedUrl + "\n");
		
		
		writeFile(output.toString());
		System.out.println("Writed to file: " + fileName);
	}
	
}
