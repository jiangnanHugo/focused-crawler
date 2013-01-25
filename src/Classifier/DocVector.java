package Classifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class extracts different infromation from a document given by a string.
 */
public class DocVector extends FocusedVector {

  /**
   * Constructor that gets a string and creates a hash that maps between a word
   * and the number of instances of that word in the text.
   * @param s The document to be parsed.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
public DocVector(String s) {
    int wordsNum = 0;
    hash = new HashMap();
    HashMap thash = new HashMap();
    //s = s.toLowerCase();
    s = s + " ";
    //s = s.replaceAll("[^a-zA-Z]"," ");//to remove non-word characters
    //s = s.replaceAll("(\\s+)"," ");
    s = s.replaceAll(" \\w{1,2} ", " ");
    while(s.length() > 1) {
      Pattern p = Pattern.compile("\\s*(\\S+).*");
      Matcher m = p.matcher(s);
      m.matches(); //execute match
      String currString;
      try {
        currString = m.group(1); //returns the first word
      } catch(Exception e) {
        break;
      }
      int counter = 0; //count how many times the word appears in the text
      int index = 0, ret = 0;
      do {
        counter++;
        ret = s.indexOf(" " + currString + " ", index);
        index = ret + currString.length() + 2;
      } while(ret != -1);
      s = s.replaceAll(" " + currString + "( )", " ");
      wordsNum += counter;
      s = s.replaceFirst("\\s*", " "); //remove initial whitespace
      if(currString.length() < 3) {
        continue;
      }
      thash.put(currString, new Integer(counter));
    }
    count = wordsNum;

    Set set = thash.entrySet();
    Iterator it = set.iterator();
    while(it.hasNext()) {
      Map.Entry m = (Map.Entry)it.next();
      double counter = ((Integer)m.getValue()).intValue();
      String currString = (String)m.getKey();
      hash.put(currString, new Double((double)counter / count));
    }
  }

  /**
   * Calculates by using the TFIDF classifier methods the distance of the document
   * from the topic
   * @param tf The tf vector of the TFIDF classifier
   * @param idf The idf vecotr of the TFIDF classifier
   * @return The distance/grade of the documnet in realtion to the topic
   */
  @SuppressWarnings("rawtypes")
public double getTfidfDistance(TfVector tf, IdfVector idf) {
    Set setTf = tf.hash.entrySet();
    double upperMult = 0;
    Iterator itTf = setTf.iterator();
    while(itTf.hasNext()) {
      Map.Entry m = (Map.Entry)itTf.next();
      if(idf.hash.containsKey(m.getKey()) &&
         tf.hash.containsKey(m.getKey()) &&
         hash.containsKey(m.getKey())) {
        upperMult += ((Double)m.getValue()).doubleValue() *
            ((Double)hash.get(m.getKey())).doubleValue() *
            Math.pow(((Double)idf.hash.get(m.getKey())).doubleValue(), 2);
      }
    }
    return upperMult / (getNorm(tf, idf) * getNorm(this, idf));

  }

  /**
   * finds the product of two vector norms: one of which is FocusedVector and the other
   * IdfVector.
   * @param tf The first vector
   * @param idf The second vector
   * @return Product of two vector norms
   */
  @SuppressWarnings("rawtypes")
private double getNorm(FocusedVector tf, IdfVector idf) {
    Set setTf = tf.hash.entrySet();
    Iterator itTf = setTf.iterator();
    double norm = 0;
    while(itTf.hasNext()) {
      Map.Entry m = (Map.Entry)itTf.next();
      if(idf.hash.containsKey(m.getKey())) {
        norm += Math.pow(((Double)idf.hash.get(m.getKey())).doubleValue() *
                         ((Double)m.getValue()).doubleValue(), 2);
      }
    }
    return Math.sqrt(norm);
  }

  /**
   * Calculate the grade of a document by finding the Bayesian grade to it
   * in realtion to the set of "good" vectors and "bad" vectors.
   * @param goodVec Vector of "good" FocusedVectors
   * @param badVec Vector of "bas" FocusedVectors
   * @return The grade/distance of the document.
   */
  @SuppressWarnings("rawtypes")
public double getBayesianDistance(BayesianVector goodVec, BayesianVector badVec) {
    Set s = hash.entrySet();
    Iterator it = s.iterator();
    double prod = 1;
    double negProd = 1;
    double pi = 0;
    double b_bad = 1e-9, g_good = 1e-9;
    while(it.hasNext()) {
      Map.Entry m = (Map.Entry)it.next();
      int occurs = (int)(((Double)m.getValue()).doubleValue() * count);
      String word = (String)m.getKey();
      if(word == null) {
        continue; // BUG!!! TODO
      }
      if(badVec.hash.containsKey(word)) {
        b_bad = ((Double)(badVec.hash.get(word))).doubleValue();
      }
      if(goodVec.hash.containsKey(word)) {
        g_good = ((Double)(goodVec.hash.get(word))).doubleValue();
      }
      pi = b_bad / (g_good + b_bad);
      System.out.print("pi = " + pi + " word = " + word + " b_bad = " + b_bad +
                       " g_good = " + g_good);
      prod *= Math.pow(pi, occurs);
      negProd *= Math.pow(1 - pi, occurs);
      System.out.println(" prod = " + prod + " negProd = " + negProd);
    }
    return 1 - (prod / (prod + negProd));
  }
}
