package crawling;


import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.*;
import java.io.*;
/**
 * 
 */
@SuppressWarnings("unused")
public class BasicCrawler extends WebCrawler {

        private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
                        + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
        private Map<Integer,URLInfo> URLIndex;
       
        //This function decides whether the given URL should be crawled or not.
        public boolean shouldVisit(WebURL url) {
        	System.out.println("In shouldVisit...url: "+url.toString());
                String href = url.getURL().toLowerCase();
                boolean bool=false;
                if(!FILTERS.matcher(href).matches() && href.startsWith("http://")) 
                {	bool=true; System.out.println("YES...");}
                else {bool=false;System.out.println("NO...");}
                
               //now if bool=1 that means that we have to crawl this site; so we need to set the flag isCrawled=true inthe Index
               loadIndex();
               if (bool==true)
               {
            	   int hashCode=href.hashCode();
            	   URLInfo ui = URLIndex.get(hashCode());
            	   ui.setCrawled(true);
               }
        	   URLInfo  ui = new URLInfo(url,true,false,false,false,false,false,false);
        	   URLIndex.put(url.hashCode(),ui);
                
                if(bool==true) return true;
                else return false;
        }

        /**
         * This function is called when a page is fetched and ready 
         * to be processed by your program.
         * This function is called after the content of a URL is downloaded successfully. 
         * You can easily get the url, text, links, html, and unique id of the downloaded page. 
         */
        @Override
        
        public void visit(Page page) {
        		System.out.println("In visit");
                int docid = page.getWebURL().getDocid();
                String url = page.getWebURL().getURL();
                int parentDocid = page.getWebURL().getParentDocid();

                System.out.print("Docid: " + docid);
                //System.out.println("URL: " + url);
                //System.out.println("Docid of parent page: " + parentDocid);

                if (page.getParseData() instanceof HtmlParseData) {
                        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                        //String text = htmlParseData.
						//String html = htmlParseData.getHtml();
                        List<WebURL> links = htmlParseData.getOutgoingUrls();
                        loadIndex();
                        //deletefile("outlinks.txt");
                        BufferedWriter bw=null;
						try {
							bw = new BufferedWriter(new FileWriter(new File("src/crawling/outlinks.txt"),true));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                          
                        for(WebURL wurl:links)
                        {
                        	URLInfo  ui = new URLInfo(wurl,false,false,false,false,false,false,false);
                    		URLIndex.put(wurl.getURL().hashCode(),ui);
                    		try {
                    			//System.out.println("Writing to OUTLINKS.TXT");
								bw.write(wurl.getURL());
								bw.write("\n"); 
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                    		
                        }
                        try {bw.close();} catch (IOException e) {e.printStackTrace();}
                        
                       // System.out.println("Text: "+text);
                        //if(!URLIndex.containsKey(url.hashCode()))
                        //{
                        	String text = htmlParseData.getText();
                        	doVisitedPage(text,url);
                        //}
                        //String tagged="";
                       /* try {
							MaxentTagger tagger = new MaxentTagger("tagger/english-bidirectional-distsim.tagger");
							tagged = tagger.tagString(text);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						*/
						//System.out.println(tagged);
						
                       // System.out.println("Text length: " + text.length());
                        //System.out.println("Html length: " + html.length());
                        //System.out.println("Number of outgoing links: " + links.size());
                }

               // System.out.println("=============");
        }
        //to save bagOfWords
        public void doVisitedPage(String text, String url)
        {
        	System.out.println("In doVisitPage");
        	try{
        		//  System.out.println("--------------------DOVISITED_PAGE-----------------------------");
        		  FileWriter fstream = new FileWriter("pages/"+url.hashCode());
        		  BufferedWriter out = new BufferedWriter(fstream);
        		  out.write(text);
        		  
        		  FileWriter fstream1 = new FileWriter("docs/"+url.hashCode());
        	      BufferedWriter out1 = new BufferedWriter(fstream1);
        	         
        	         try{
        	        	 	BufferedReader br = new BufferedReader(new FileReader("src/crawling/stopwords.txt"));
        	                String line = br.readLine();
        	                while(line != null){
        	                	 line=line.trim();
        	                     if(text.contains(line)) text.replaceAll(line,"");
        	                     line = br.readLine();              
        	                 }
        	                      br.close();
        	                   }catch(Exception e){}
        	         out1.write(text);
        	         out1.close();
        		  
        		  //System.out.println("----------------------WRITE_OVER-------------------------------");
        		  out.close();
        		  }catch (Exception e){
        		  System.err.println("Error: " + e.getMessage());
        		  }
        }
        
        @SuppressWarnings("unchecked")
		public void loadIndex()
        {
        	File URLIndexFile = new File("results/URLIndex.dat");
     		if (URLIndexFile.exists()) 
     		{
     			try {
     				FileInputStream f_in = new FileInputStream (URLIndexFile);
     				ObjectInputStream obj_in = new ObjectInputStream (f_in);
     				Object obj = obj_in.readObject();
     				URLIndex = (Map<Integer,URLInfo>) obj;
     				obj_in.close();
     				f_in.close();
     			}
     			catch (Exception e) {
     				e.printStackTrace();
     			}
     		}
     		else 
     		{
     			URLIndex= (Map<Integer,URLInfo>) Collections.synchronizedMap(new HashMap<Integer,URLInfo>()); 
     			File file = new File("results/URLIndex.dat");  
     			FileOutputStream f;
					try {
						f = new FileOutputStream(file);
						ObjectOutputStream s = new ObjectOutputStream(f);          
	        			s.writeObject(URLIndex);
	        			s.flush();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
     		}
        }
        
    	public static void deletefile(String file)
    	{
    		File f1 = new File(file);
    		boolean success = f1.delete();
    	    if (!success)
    	    {
    		  System.out.println(file+" Deletion failed.");
    		  //System.exit(0);
    		 }
    		else
    		{
    		System.out.println("File "+file+" deleted.");
    		}
    	}
}
