package crawling;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import messif.algorithms.AlgorithmMethodException;
import messif.objects.AbstractObject;
import messif.operations.IncrementalNNQueryOperation;
import mtree.MTree;
import clustering.ClusterPoint;
import clustering.ClusterPointType;
import clustering.DocumentBagOfWords;

public class FrontierDiscovery {
	private String coreSitesFileName, documentClustersFileName, mtreeFileName;
	private Integer numNeighbours;
	private HashSet<String> coreSites;
	private Map<String,ClusterPoint> clustering;
	private MTree mtree;
	
	public FrontierDiscovery(String coreSitesFileName, String documentClustersFileName, String mtreeFileName, Integer numNeighbours) {
		this.coreSitesFileName = coreSitesFileName;
		this.documentClustersFileName = documentClustersFileName;
		this.mtreeFileName = mtreeFileName;
		this.numNeighbours = numNeighbours;
	}
	
	public FrontierDiscovery(HashSet<String> coreSites, Map<String, ClusterPoint> clustering, MTree mtree, Integer numNeighbours) {
		this.numNeighbours = numNeighbours;
		this.coreSites = coreSites;
		this.clustering = clustering;
		this.mtree = mtree;
	}

	private void loadCoreSites() {
		this.coreSites = new HashSet<String>();
		File coreSitesFile = new File(coreSitesFileName);
		if (coreSitesFile.exists()) {
			try {
				Scanner scanner = new Scanner(coreSitesFile);
				while(scanner.hasNextLine()) {
					this.coreSites.add(scanner.nextLine());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean loadClustering() {
		File clusterFile = new File(documentClustersFileName);
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
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean loadMTree() {
		File mtreefile = new File(mtreeFileName);
		if(mtreefile.exists()) {
			mtree = MTree.loadFromFile(mtreeFileName);
			return true;
		}
		else {
			return false;
		}
	}
	
	public HashSet<String> getFrontier() {
		HashSet<String> frontierSites = new HashSet<String>();
		if(this.coreSites.isEmpty()) {
			/* If this is first time discovery, add all COREPOINTs as Frontier Sites */
			Iterator<Entry<String, ClusterPoint>> clusterPointIter = clustering.entrySet().iterator();
			while(clusterPointIter.hasNext()) {
				Entry<String, ClusterPoint> clusterPoint = clusterPointIter.next();
				if(clusterPoint.getValue().pointType==ClusterPointType.COREPOINT) {
					frontierSites.add(clusterPoint.getKey());
				}
			}
		}
		else {
			/* Find the neighbours of core sites and add them */
			Iterator<String> coreSitesIter = coreSites.iterator();
			while(coreSitesIter.hasNext()) {
				String coreSite = coreSitesIter.next();
				ClusterPoint coreClusterPoint = clustering.get(coreSite);
				IncrementalNNQueryOperation incOper = new IncrementalNNQueryOperation(coreClusterPoint.document, this.numNeighbours);
				try {
					mtree.incrementalNN(incOper);
				} catch (AlgorithmMethodException e) {
					e.printStackTrace();
				}
				Iterator<AbstractObject> neighboursIter = incOper.getAnswer();
				while(neighboursIter.hasNext()) {
					DocumentBagOfWords neighbour = (DocumentBagOfWords) neighboursIter.next();
					frontierSites.add((String) neighbour.suppData);
				}
			}
		}
		return frontierSites;
	}
	
	public HashSet<String> loadAndGetFrontier() {
		if(!loadClustering()) {
			return null;
		}
		if(!loadMTree()) {
			return null;
		}
		loadCoreSites();
		return getFrontier();
	}
}
