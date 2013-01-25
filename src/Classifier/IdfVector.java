package Classifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.omg.CORBA.IntHolder;

/**
 * A class that represents the IDF vector of the TFIDF classifier.
 */
public class IdfVector extends FocusedVector {

  /**
   * A constructor the gets a two vectors of FocusedVectors and calculates the
   * corresponding IDF vector.
   * @param posVec - vector of positive FocusedVectors
   * @param negVec - vector of negative FocusedVectors
   */
  public IdfVector(Vector posVec, Vector negVec) { //does not distinguish between the two
    super();
    HashMap hashTmp = new HashMap();

    getCount(hashTmp, posVec);
    getCount(hashTmp, negVec);

    Set s = hashTmp.entrySet();
    Iterator it = s.iterator();
    while(it.hasNext()) {
      Map.Entry m = (Map.Entry)it.next();
      double f = Math.log((double)(posVec.size() + negVec.size())
                          / ((IntHolder)m.getValue()).value);
      hash.put(m.getKey(), new Double(f));
    }
  }

  /**
   * Updates the a hash entry by the number of instances of a word
   * found in the vecotr of FocusedVecotrs
   * @param hashTmp - the hash to update
   * @param vec - vector of FocusedVectors.
   */
  private void getCount(HashMap hashTmp, Vector vec) {
    for(int i = 0; i < vec.size(); i++) {
      FocusedVector fv = (FocusedVector)vec.get(i);
      Set s = fv.hash.keySet();
      Iterator it = s.iterator();
      while(it.hasNext()) {
        String word = (String)it.next();
        //if(word == null) continue;
        if(hashTmp.containsKey(word)) {
          IntHolder ih = (IntHolder)hashTmp.get(word);
          ih.value++;
        }
        else {
          hashTmp.put(word, new IntHolder(1));
        }
      }
    }
  }
}
