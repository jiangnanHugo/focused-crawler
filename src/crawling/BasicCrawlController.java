package crawling;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * 
 */
public class BasicCrawlController {

        //public static void main(String[] args) throws Exception {
    public BasicCrawlController(){      
	/*  if (args.length != 2) {
                        System.out.println("Needed parameters: ");
                        System.out.println("\t rootFolder (it will contain intermediate crawl data)");
                        System.out.println("\t numberOfCralwers (number of concurrent threads)");
                        return;
                }*/

                /*
                 * crawlStorageFolder is a folder where intermediate crawl data is
                 * stored.
                 */
                String crawlStorageFolder = "db11/";

                /*
                 * numberOfCrawlers shows the number of concurrent threads that should
                 * be initiated for crawling.
                 */
                //int numberOfCrawlers = Integer.parseInt(args[1]);
                int numberOfCrawlers = Integer.parseInt("5");
                CrawlConfig config = new CrawlConfig();

                config.setCrawlStorageFolder(crawlStorageFolder);

                /*
                 * Be polite: Make sure that we don't send more than 1 request per
                 * second (1000 milliseconds between requests).
                 */
                config.setPolitenessDelay(1000);

                /*
                 * You can set the maximum crawl depth here. The default value is -1 for
                 * unlimited depth
                 */
                config.setMaxDepthOfCrawling(0);

                /*
                 * You can set the maximum number of pages to crawl. The default value
                 * is -1 for unlimited number of pages
                 */
                config.setMaxPagesToFetch(1000);

                /*
                 * Do you need to set a proxy? If so, you can use:
                 * config.setProxyHost("proxyserver.example.com");
                 * config.setProxyPort(8080);
                 * 
                 * If your proxy also needs authentication:
                 * config.setProxyUsername(username); config.getProxyPassword(password);
                 */

                
                config.setProxyHost("172.30.3.3");
                config.setProxyPort(8080);
                //config.setProxyUsername("f2008533"); 
                //config.setProxyPassword("rishabhm");
                
                /*
                 * This config parameter can be used to set your crawl to be resumable
                 * (meaning that you can resume the crawl from a previously
                 * interrupted/crashed crawl). Note: if you enable resuming feature and
                 * want to start a fresh crawl, you need to delete the contents of
                 * rootFolder manually.
                 */
                config.setResumableCrawling(false);

                /*
                 * Instantiate the controller for this crawl.
                 */
                PageFetcher pageFetcher = new PageFetcher(config);
                RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
                RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
                CrawlController controller=null;
				try {
					controller = new CrawlController(config, pageFetcher, robotstxtServer);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

                /*
                 * For each crawl, you need to add some seed urls. These are the first
                 * URLs that are fetched and then the crawler starts following links
                 * which are found in these pages
                 */
               //controller.addSeed("http://www.rishabhmehrotra.com/research/courses.htm");
               try{
                   BufferedReader br = new BufferedReader(new FileReader(new File("src/crawling/seed_sites")));
                   String line = br.readLine();
                   while(line != null)
                   {
                	  System.out.println("HERE....."); 
                 	  line=line.replaceAll("%3A%2F%2F","://");
                 	  line=line.replaceAll("%2E",".");
                 	  line=line.replaceAll("%2F","");
                 	  controller.addSeed(line);
                 	  System.out.println(line);
                 	  line = br.readLine();            	  
                   }
                   br.close();
                }catch(Exception e){System.out.println("Error::"+e);}
               /* if(!exist(frontier))
                {
                	while
                		{
                	controller.addSeed("seeddocs");}
                	controller.start(BasicCrawler.class, numberOfCrawlers);
                }
                else
                {
                	controller.addSeed("frontierdocs");
                	controller.start(BasicCrawler.class, numberOfCrawlers);
                }*/
                //controller.addSeed("http://www.ics.uci.edu/~yganjisa/");
               // controller.addSeed("http://www.ics.uci.edu/~lopes/");
              //  controller.addSeed("http://www.ics.uci.edu/");

                /*
                 * Start the crawl. This is a blocking operation, meaning that your code
                 * will reach the line after this only when crawling is finished.
                 */
                System.out.println("Controller started...");
                controller.start(BasicCrawler.class, numberOfCrawlers);
        }
}
