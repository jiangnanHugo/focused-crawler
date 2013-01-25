package Classifier;

import java.util.*;
import org.omg.CORBA.IntHolder;

/**
 * Introduces the BaysianVector which is an extension to the
 * FocusedVector.  This vector counts the total number of instances of a token/word
 * int the set of documents and contains in each entry this value normalized by the
 * total number of words in the whole set of documents.
 */
public class BayesianVector extends FocusedVector {
  /**
   * Constructor that creates a new Baysian vector from  a vector of FocusedVectors.
   * @param vec - vector of FocusedVectors.
   */
  public BayesianVector(Vector vec) {
    super();
    int wordCount = 0;
    HashMap hashTmp = new HashMap();
    for(int i = 0; i < vec.size(); i++) {
      FocusedVector fv = (FocusedVector)vec.get(i); //get current FocusedVector.
      wordCount += fv.count; //continue counting the total number of words.
      Set s = fv.hash.entrySet();
      Iterator it = s.iterator();
      while(it.hasNext()) { //iterator over the FocusedVector.
        Map.Entry m = (Map.Entry)it.next();
        String word = (String)m.getKey();
        int count = (int)(((Double)m.getValue()).doubleValue() * fv.count); //number of instances of a word
        if(hashTmp.containsKey(word)) {
          IntHolder ih = (IntHolder)hashTmp.get(word);
          ih.value += count; //update the number of instances for the Baysian vector
        }
        else {
          hashTmp.put(word, new IntHolder(count));
        }
      }
    }

    Set set = hashTmp.entrySet();
    Iterator it = set.iterator();
    while(it.hasNext()) { //iterate over the hash of the Baysian vector
      Map.Entry m = (Map.Entry)it.next();
      int c = ((IntHolder)m.getValue()).value;
      hash.put(m.getKey(), new Double((double)c / wordCount)); //number of words normalized by the
      //total number of words
    }
  }
}
