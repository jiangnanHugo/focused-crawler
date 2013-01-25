package Classifier;

/**
 * Class for the TFIDF Classifier. It manages the two vectors idf and tf
 * and calculates grades for new documents.
 */
public class TfidfClassifier extends Classifier {
  private IdfVector idf;
  private TfVector tf;

  /**
   * create the idf and tf vectors from the positive and negative vectors
   * collected so far
   */
  public void learn() {
    idf = new IdfVector(posVec, negVec);
    tf = new TfVector(posVec);
    System.out.println("Using Tfidf Classifier");
  }

  /**
   * @param s the new documnet to be graded
   * @return the grade which is a double value from 0 to 1
   */
  public double getGrade(String s) {
    DocVector fv = new DocVector(s);
    return fv.getTfidfDistance(tf, idf);
  }

}
