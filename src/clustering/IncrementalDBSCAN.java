package clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import messif.buckets.LocalBucket;
import messif.objects.AbstractObject;
import messif.operations.InsertOperation;
import messif.operations.RangeQueryOperation;
import mtree.MTree;

public class IncrementalDBSCAN {
	
	private MTree mtree;
	Map<String,ClusterPoint> clustering;
	private String docsDir, clusterDir, mtreename, clusterfile;
	private Double eps;
	private Integer minpoints;
	
	public IncrementalDBSCAN(String clusterDir, String mtreename, String clusterfile, String docsDir, Double eps, Integer minpoints) {
		this.docsDir = docsDir;
		this.eps = eps;
		this.minpoints = minpoints;
		this.clusterDir = clusterDir;
		this.mtreename = mtreename;
		this.clusterfile = clusterfile;
	}
	
	public MTree getMtree() {
		return mtree;
	}

	public Map<String, ClusterPoint> getClustering() {
		return clustering;
	}
	
	public void loadFiles() {
		loadMTree(clusterDir,mtreename);
		loadClustering(clusterDir, clusterfile);
	}

	public void shutdownAndSave() {
		saveMTree(clusterDir,mtreename);
		saveClustering(clusterDir,clusterfile);
	}
	
	private void loadMTree(String clusterDir, String mtreename) {
		File mtreefile = new File(clusterDir+mtreename);
		if(mtreefile.exists()) {
			mtree = MTree.loadFromFile(clusterDir+mtreename);
		}
		else {
			try {
				mtree = new MTree();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadClustering(String clusterDir, String clusterfile) {
		File clusterFile = new File(clusterDir+clusterfile);
		if (clusterFile.exists()) {
			try {
				FileInputStream f_in = new FileInputStream (clusterFile);
				ObjectInputStream obj_in = new ObjectInputStream (f_in);
				Object obj = obj_in.readObject();
				this.clustering = (Map<String,ClusterPoint>) obj;
				obj_in.close();
				f_in.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			this.clustering = new HashMap<String,ClusterPoint>();
		}
	}
	
	private void saveMTree(String clusterDir, String mtreename) {
		mtree.saveToFile(clusterDir+mtreename);
	}
	
	private void saveClustering(String clusterDir, String clusterfile) {
		try {
			FileOutputStream f_out = new FileOutputStream (clusterDir+clusterfile);
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(this.clustering);
			obj_out.close();
			f_out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DocumentBagOfWords addtoMTree(String docName, String bagofwords) {
		
		System.out.println("Docname::"+docName);
		bagofwords = ""; 
		DocumentBagOfWords document = new DocumentBagOfWords(bagofwords);
		document.suppData = docName;
		InsertOperation insOper = new InsertOperation(document);
			mtree.insert(insOper);
	
		return document;
	}
	
	public void addtoClustering(DocumentBagOfWords document) {
		String docName = (String) document.suppData;
		HashSet<DocumentBagOfWords> newNeighbourhoodCores = new HashSet<DocumentBagOfWords>();
		HashSet<String> epsilonNeighbourhood = getEpsilonNeighbourhood(document);
		Iterator<String> epsilonNeighbourhoodIter = epsilonNeighbourhood.iterator();
		/* Find out list of new core points in the vicinity of inserted document */
		while(epsilonNeighbourhoodIter.hasNext()) {
			String neighbourDocName = epsilonNeighbourhoodIter.next();
			ClusterPoint neighbour = clustering.get(neighbourDocName);
			neighbour.neighbours++;
			if(neighbour.neighbours==this.minpoints) {
				newNeighbourhoodCores.add(neighbour.document);
			}
		}
		/* Find out updateSeed - List of core points around the new core points */
		HashSet<String> updateSeed = new HashSet<String>();
		Iterator<DocumentBagOfWords> newCoresIter = newNeighbourhoodCores.iterator();
		while(newCoresIter.hasNext()) {
			DocumentBagOfWords newCoreDocument = newCoresIter.next();
			HashSet<String> newCoreNeighbours = getEpsilonNeighbourhood(newCoreDocument);
			newCoreNeighbours.remove(docName);
			Iterator<String> newCoreNeighboursIter = newCoreNeighbours.iterator();
			while(newCoreNeighboursIter.hasNext()) {
				String newCoreNeighbour = newCoreNeighboursIter.next();
				ClusterPoint newCoreNeighbourPoint = clustering.get(newCoreNeighbour);
				if(newCoreNeighbourPoint.neighbours>=this.minpoints) {
					updateSeed.add(newCoreNeighbour);
				}
			}
		}
		/* Decide ClusterPoint Parameters based on updateSeed */
		Integer numNeighbours = epsilonNeighbourhood.size();
		ClusterPointType clusterType;
		String clusterName = docName;
		if(numNeighbours>=this.minpoints) {
			clusterType = ClusterPointType.COREPOINT;
		}
		else {
			clusterType = ClusterPointType.BORDERPOINT;
		}
		if(updateSeed.size() == 0) {
			/* New Point is a Noise Point */
			clusterType = ClusterPointType.NOISE;
		}
		else {
			boolean updateSeedAllNoise = true;
			Iterator<String> updateSeedIter = updateSeed.iterator();
			while(updateSeedAllNoise == true && updateSeedIter.hasNext()) {
				String updateSeedDoc = updateSeedIter.next();
				ClusterPoint updateSeedPoint = clustering.get(updateSeedDoc);
				clusterName = updateSeedPoint.clusterName;
				if(updateSeedPoint.pointType != ClusterPointType.NOISE) {
					updateSeedAllNoise = false;
				}
			}
			if(updateSeedAllNoise) {
				/* New Point leads to creation of a new Cluster */
				clusterName = docName;
				updateSeedIter = updateSeed.iterator();
				while(updateSeedIter.hasNext()) {
					String updateSeedDoc = updateSeedIter.next();
					ClusterPoint updateSeedPoint = clustering.get(updateSeedDoc);
					updateSeedPoint.clusterName = clusterName;
				}
			}
			else {
				/* New Point is absorbed to existing cluster/clusters */
				clusterName = docName;
				updateSeedIter = updateSeed.iterator();
				while(updateSeedIter.hasNext()) {
					String updateSeedDoc = updateSeedIter.next();
					ClusterPoint updateSeedPoint = clustering.get(updateSeedDoc);
					if(updateSeedPoint.clusterName.compareTo(clusterName)!=0) {
						absorbCluster(updateSeedPoint.clusterName, clusterName);
					}
				}
			}
		}
		
		/* Change status of all updateSeed points to COREPOINTS */
		Iterator<String> updateSeedIter = updateSeed.iterator();
		while(updateSeedIter.hasNext()) {
			String updateSeedDoc = updateSeedIter.next();
			ClusterPoint updateSeedPoint = clustering.get(updateSeedDoc);
			updateSeedPoint.pointType = ClusterPointType.COREPOINT;
		}
		
		/* Make the ClusterPoint and add it to the clustering */
		ClusterPoint newPoint = new ClusterPoint(clusterType, document, numNeighbours, clusterName);
		clustering.put(docName, newPoint);
	}
	
	public HashSet<String> getEpsilonNeighbourhood(DocumentBagOfWords document) {
		HashSet<String> epsilonNeighbourhood = new HashSet<String>();
		Map<LocalBucket, RangeQueryOperation> neighbourhood = mtree.rangeSearch(new RangeQueryOperation(document, eps));
		Collection<RangeQueryOperation> neighbourgroups = neighbourhood.values();
		Iterator<RangeQueryOperation> groupsiter = neighbourgroups.iterator();
		while(groupsiter.hasNext()) {
			RangeQueryOperation rqo = groupsiter.next();
			Iterator<AbstractObject> groupiter = rqo.getAnswer();
			while(groupiter.hasNext()) {
				DocumentBagOfWords ndoc = (DocumentBagOfWords) groupiter.next();
				epsilonNeighbourhood.add((String) ndoc.suppData);
			}
		}
		epsilonNeighbourhood.remove(document.suppData);
		return epsilonNeighbourhood;
	}

	private void absorbCluster(String oldClusterName, String newClusterName) {
		Collection<ClusterPoint> points = clustering.values();
		Iterator<ClusterPoint> pointsIter = points.iterator();
		while(pointsIter.hasNext()) {
			ClusterPoint point = pointsIter.next();
			if(point.clusterName.compareTo(oldClusterName)==0) {
				point.clusterName = newClusterName;
			}
		}
	}
	
	public HashSet<String> getNewDocumentNames() {
		/* Find list of files in documents directory */
		File docsdir = new File(this.docsDir);
		List<String> newsnapshot = Arrays.asList(docsdir.list());		
		/* Find out files which are not already indexed */
		HashSet<String> newpages = new HashSet<String>();
		newpages.addAll(newsnapshot);
		newpages.removeAll(clustering.keySet());
		return newpages;
	}
	
	public void clusterDocument(String docName) {
		/* Read the document (bag of words) and add to Mtree and Clustering */
		try {
			System.out.println("Clustering: "+docName);
			BufferedReader br = new BufferedReader(new FileReader(docsDir+docName));
			String bagofwords = br.readLine();
			addtoClustering(addtoMTree(docName, bagofwords));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clusterNewDocuments() {
		HashSet<String> newDocumentNames = getNewDocumentNames();
		Iterator<String> newDocumentsIter = newDocumentNames.iterator();
		while(newDocumentsIter.hasNext()) {
			String newDocument = newDocumentsIter.next();
			clusterDocument(newDocument);
		}
	}
	
	public void generateClusters(String clustersFile) {
		class clusterPointCompare implements Comparator<Entry<String,ClusterPoint>> {
			@Override
			public int compare(Entry<String, ClusterPoint> arg0, Entry<String, ClusterPoint> arg1) {
				ClusterPoint point1 = arg0.getValue();
				ClusterPoint point2 = arg1.getValue();
				return point1.clusterName.compareTo(point2.clusterName);
			}
		}
		List<Entry<String, ClusterPoint>> clusterPoints = new ArrayList<Entry<String, ClusterPoint>>(clustering.entrySet());
		Collections.sort(clusterPoints, new clusterPointCompare());
		
		ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
		String clusteroutput = "", clusterhead;
		int corecount=0,bordercount=0,noisecount=0;
		ListIterator<Entry<String,ClusterPoint>> clusterPointsIter = clusterPoints.listIterator();
		clusterhead = clusterPointsIter.next().getValue().clusterName;
		clusterPointsIter.previous();
		while(clusterPointsIter.hasNext()) {
			ArrayList<String> clusterwords = new ArrayList<String>();
			while(clusterPointsIter.hasNext()) {
				Entry<String,ClusterPoint> pointEntry = clusterPointsIter.next();
				ClusterPoint point = pointEntry.getValue();
				if(clusterhead.compareTo(point.clusterName)==0) {
					if(point.pointType==ClusterPointType.COREPOINT) {
						corecount++;
					}
					else if(point.pointType==ClusterPointType.NOISE) {
						noisecount++;
					}
					else if(point.pointType==ClusterPointType.BORDERPOINT){
						bordercount++;
					}
					clusteroutput += (pointEntry.getKey() + " " + point.pointType + "\n");
					clusterwords.add(pointEntry.getKey());
				}
				else {
					if(clusterwords.size()>1) {
						System.out.println("Generated cluster of size: "+clusterwords.size());
						clusteroutput += ("Members:"+clusterwords.size()+"\n\n");
					}
					else {
						clusteroutput += "\n\n";
					}
					clusterhead = point.clusterName;
					clusterPointsIter.previous();
					break;
				}
			}
			clusters.add(clusterwords);
		}
		clusteroutput += ("Core: " + corecount + "\nBorder: " + bordercount + "\nNoise: " + noisecount);
		
		try {
			FileWriter clusterfile = new FileWriter(clustersFile);
			clusterfile.write(clusteroutput);
			clusterfile.close();
			FileOutputStream f_out = new FileOutputStream (clustersFile+".obj");
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(clusters);
			obj_out.close();
			f_out.close();
		} catch (IOException e) {
			System.out.println("I/O Exception");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		IncrementalDBSCAN dbscan = new IncrementalDBSCAN("results/62/", "mtree.db", "docsClusters.db", "docs/", 0.6, 2);
		dbscan.loadFiles();
		HashSet<String> newDocumentNames = dbscan.getNewDocumentNames();
		Iterator<String> newDocumentsIter = newDocumentNames.iterator();
		while(newDocumentsIter.hasNext()) {
			String newDocument = newDocumentsIter.next();
			dbscan.clusterDocument(newDocument);
		}
		dbscan.shutdownAndSave();
		dbscan.generateClusters("results/62/docsClusters.txt");
		System.out.println("Saved. Exiting..");
	}

}