package crawling;
import java.io.*;
import crawling.BasicCrawlController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import clustering.IncrementalDBSCAN;



public class Driver {
	private static HashSet<String> coreSites;
	private static String docsdir = "docs/";
	private static String pagedir ="pages/";									//relative path to documents (as bag of filtered words)
	private static String resultsdir = "results/";
	
	public static void main(String[] args) throws Exception 
	{
		Double epsilon = 0.6;
		Integer minPts = 2;
		int i,j=1,loops=4;
		createDirs();
		IncrementalDBSCAN dbscan = new IncrementalDBSCAN("results/", "mtree.db", "docsClusters.db", "docs/", epsilon, minPts);
		coreSites = loadCoreSites("results/coreSites.txt");
		FrontierDiscovery frontier;
		for(i=1;i<=loops;i++)
		{
			System.out.println("Starting crawler:: Cycle "+i);
			if(i!=1)
			{
				File inputFile = new File("src/crawling/outlinks.txt");
			    File outputFile = new File("src/crawling/seed_sites.txt");
			    FileReader in;
				try 
				{
						in = new FileReader(inputFile);
						FileWriter out = new FileWriter(outputFile);
					    int c;
					    while ((c = in.read()) != -1)
					      out.write(c);
					    in.close();
					    out.close();
				} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
			   
				//deletefile("src/crawling/outlinks.txt");
			}	           
			new BasicCrawlController();//blocking statement
			/*clustering n frontier discovery
			 * if(j%2==0)
			{
				//cluster
				System.out.println("Cycle " + i + ": Clustering documents");
				dbscan.loadFiles();
				dbscan.clusterNewDocuments();
				dbscan.shutdownAndSave();
				//frontier
				System.out.println("Cycle " + i + ": Discovering document frontier");
				frontier = new FrontierDiscovery(coreSites, dbscan.getClustering(), dbscan.getMtree(), 3);
				HashSet<String> newFrontier = frontier.getFrontier();
				System.out.println("Cycle " + i + ": Updating crawling frontier");
				saveFrontier("results/frontier", newFrontier);
				coreSites.addAll(newFrontier);
				saveCoreSites("results/coreSites.txt");
				feedback();
			}
			j++;*/
	   
		}

	}

	public static void feedback() throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/crawling/seed_sites.txt",true)) ;

		//feedback: FRONTIER 
        try{
            BufferedReader br = new BufferedReader(new FileReader("results/frontier"));
            String line = br.readLine();
            while(line != null)
            {
          	  line=line.replaceAll("%3A%2F%2F","://");
          	  line=line.replaceAll("%2E",".");
          	  line=line.replaceAll("%2F","");
          	  writer.write(line);
          	  System.out.println(line);
          	  line = br.readLine();            	  
            }
            br.close();
         }catch(Exception e){}
        
        //feedback: FRONTIER PHRASES
        /*
        try{
            BufferedReader br = new BufferedReader(new FileReader("results/frontierphrases"));
            String line = br.readLine();
            int c=0;
            while(line != null)
            {
          	  System.out.println(c+" "+line);
          	  c++;
          	  line=line.replaceAll(" ","+");
          	  writer.write("http://www.google.co.in/search?q="+line);
          	  line = br.readLine();            	  
            }
            br.close();
         }catch(Exception e){}
        */
        //http://www.google.co.in/search?q=Daman+and+Diu

        //feedback: FRONTIER WORDS
        
       /* try{
            BufferedReader br = new BufferedReader(new FileReader("results/frontierwords"));
            String line = br.readLine();
            while(line != null)
            {
          	  line=line.replaceAll(" ","+");
          	  writer.write("http://www.google.co.in/search?q="+line);
          	  System.out.println("http://www.google.co.in/search?q="+line);
          	  line = br.readLine();            	  
            }
            br.close();
         }catch(Exception e){}        
        */
		writer.close() ;
	}
	
	public static void createDirs()
	{
		File f2 = new File(docsdir);
    	if(!f2.exists())
    		f2.mkdir();
    	File f3 = new File(pagedir);
    	if(!f3.exists())
    		f3.mkdir();
    	File f4 = new File(resultsdir);
    	if(!f4.exists())
    		f4.mkdir();
	}
	
	public static void deletefile(String file)
	{
		File f1 = new File(file);
		@SuppressWarnings("unused")
		boolean success = f1.delete();
	}
	private static void saveFrontier(String frontierFileName, HashSet<String> newFrontier) {
		String frontierSites = "";
		Iterator<String> frontierIter = newFrontier.iterator();
		while(frontierIter.hasNext()) {
			String frontierSite = frontierIter.next();
			frontierSites += (frontierSite + "\n");
		}
		try {
			FileWriter frontierFile = new FileWriter(frontierFileName);
			frontierFile.write(frontierSites);
			frontierFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveCoreSites(String coreSitesFileName) {
		String allCoreSites = "";
		Iterator<String> coreSitesIter = coreSites.iterator();
		while(coreSitesIter.hasNext()) {
			String coreSite = coreSitesIter.next();
			allCoreSites += (coreSite + "\n");
		}
		try {
			FileWriter coreSitesFile = new FileWriter(coreSitesFileName);
			coreSitesFile.write(allCoreSites);
			coreSitesFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static HashSet<String> loadCoreSites(String coreSitesFileName) {
		HashSet<String> coreSites = new HashSet<String>();
		File coreSitesFile = new File(coreSitesFileName);
		if (coreSitesFile.exists()) {
			try {
				Scanner scanner = new Scanner(coreSitesFile);
				while(scanner.hasNextLine()) {
					coreSites.add(scanner.nextLine());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return coreSites;
	}


	
}
