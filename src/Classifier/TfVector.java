package Classifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.omg.CORBA.DoubleHolder;
import org.omg.CORBA.IntHolder;

/**
 * Creates a unified topic vector from a vector of
 * FocusedVectors, by averaging the values.
 *
 */
public class TfVector extends FocusedVector {
  /**
   * A Constructor for the TfVector. All the values that do not exist
   * in one of the FocusedVectors get a zero value at that vector.
   * @param posVec - Vector of FocusedVectors
   */
  public TfVector(Vector posVec) {
    super();
    HashMap hashTmp = new HashMap();
    for(int i = 0; i < posVec.size(); i++) { //iterate over the FocusedVectors.
      FocusedVector fv = (FocusedVector)posVec.get(i);
      Set s = fv.hash.entrySet();
      Iterator it = s.iterator();
      while(it.hasNext()) { //iterate over the values of the FocusedVector.
        Map.Entry m = (Map.Entry)it.next();
        String word = (String)m.getKey();
        double d = ((Double)m.getValue()).doubleValue();
        //if(word == null) continue;
        if(hashTmp.containsKey(word)) {
          Vector vec = (Vector)hashTmp.get(word);
          DoubleHolder dh = (DoubleHolder)vec.get(0);
          IntHolder ih = (IntHolder)vec.get(1);
          dh.value += d;
          ih.value++;
        }
        else {
          Vector vec = new Vector();
          vec.add(new DoubleHolder(d));
          vec.add(new IntHolder(1));
          hashTmp.put(word, vec);
        }
      }
    }

    //after averaging populate the topic vector.
    Set set = hashTmp.entrySet();
    Iterator it = set.iterator();
    while(it.hasNext()) {
      Map.Entry m = (Map.Entry)it.next();
      Vector vec = (Vector)m.getValue();
      DoubleHolder dh = (DoubleHolder)vec.get(0);
      IntHolder ih = (IntHolder)vec.get(1);
      hash.put(m.getKey(), new Double(dh.value / ih.value));
    }
  }
}
