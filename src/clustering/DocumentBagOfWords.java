package clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import messif.objects.LocalAbstractObject;
import messif.objects.ObjectString;

public class DocumentBagOfWords extends ObjectString {

	private static final long serialVersionUID = -2939195467176984953L;

	public DocumentBagOfWords(String bagofwords) {
		super(bagofwords);
	}
	
	private List<String> bagToList(String words) {
		List<String> alist = Arrays.asList(words.split(" "));
		return alist;
	}

	@Override
	public double getDistance(LocalAbstractObject obj) {
		// Jacard Distance as distance between two bag of words
		// (Jacard follows triangle inequality)
		List<String> otherdata = bagToList(((ObjectString)obj).getStringData());
		List<String> thisdata = bagToList(this.text);
		List<String> intersection = new ArrayList<String>(thisdata);
		intersection.retainAll(otherdata);
		if(intersection.size()==0)
			return 1.0;
		else {
			TreeSet<String> union = new TreeSet<String>(thisdata);
			union.addAll(otherdata);
			return 1.0 - intersection.size()/(1.0*union.size());
		}
	}

	@Override
	public double getDistance(LocalAbstractObject arg0, double arg1) {
		return getDistance(arg0);
	}
}