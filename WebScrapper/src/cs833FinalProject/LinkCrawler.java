package cs833FinalProject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
//import org.apache.commons.validator.UrlValidator;
public class LinkCrawler {
	//UrlValidator vl = new UrlValidator();
	private HashSet<String> links;
	BufferedReader buf = null;
	static int count=0;
	static int limit=0;
	public LinkCrawler() {
        links = new HashSet<String>();
    }
	PageRank pg = new PageRank();
	public void getPageLinks(String URL) {
		String strText="";
		String storeString="";
		Whitelist whitelist = Whitelist.simpleText();
		//UrlValidator urlValidator = new UrlValidator();
        //4. Check if you have already crawled the URLs
        //(we are intentionally not checking for duplicate content in this example)
        if (!links.contains(URL)) {
            try {
                //4. (i) If not add it to the index
                /*if (links.add(URL)) {
                    System.out.println(URL);
                }*/
            	links.add(URL);
            	count++;
               System.out.println("PRINT"+ count);
            	
                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();
                document.select("script,style,.hidden").remove();
                //System.out.println("document " + document);
                
                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");
                
                //System.out.println(linksOnPage);
                InputStream is = new ByteArrayInputStream(document.toString().getBytes());
                buf = new BufferedReader(new InputStreamReader(is));
                while((strText=buf.readLine()) != null)
				{
		    	Document doc = Jsoup.parseBodyFragment(strText);
		    	doc.select("script,style,.hidden").remove();
		    	String text = Jsoup.clean(doc.body().text(), whitelist);
		    	storeString=storeString+text+ " ";
		    	//System.out.println(text);
				}
                PageRank.textProcessor(storeString, URL);
                //System.out.println(storeString);
                storeString="";
                //5. For each extracted URL... go back to Step 4.
                for (Element page : linksOnPage) {
                	//System.out.println("test "+ page.absUrl("href"));
                		if(count>limit)
                			break;
                		if (!links.contains(page.absUrl("abs:href")) && !page.absUrl("abs:href").contains("#")) {
                			if(page.absUrl("href").contains("k-state.edu")|| page.absUrl("href").contains("cs.ksu.edu")) {
                			//System.out.println("test "+ page.absUrl("abs:href"));
                			/*if(page.absUrl("abs:href").toString().charAt(page.absUrl("abs:href").toString().length()-1)=='\\') {
                				String traverseLink=page.absUrl("abs:href").toString().replace(page.absUrl("abs:href").toString().substring(page.absUrl("abs:href").toString().length()-1), "");
                				}*/
                			getPageLinks(page.absUrl("abs:href").toString());
                			//links.add(page.absUrl("abs:href"));
                		
                		}
                	}
                    //getPageLinks(page.absUrl("abs:href"));
                	//System.out.println(page.attr("abs:href"));
                }
                
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
            catch (IllegalArgumentException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
//        else {
//        	System.out.println("Not URL " + URL);
//        }
        
    }
	public static void main(String[] args) {
		//1. Pick a URL from the frontier
		System.out.println("Enter the limit of Number of Pages to be found");
		Scanner scan = new Scanner(System.in);
		limit = Integer.parseInt(scan.nextLine());
		new LinkCrawler().getPageLinks("http://www.cs.ksu.edu");
		PageRank.queryProcess();
		PageRank.pageRankmain();
		scan.close();
	}

}
