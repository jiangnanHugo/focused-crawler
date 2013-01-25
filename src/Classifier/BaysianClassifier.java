package Classifier;

/**
 * Implementation of the Baysian classifier.
 */
public class BaysianClassifier extends Classifier {
  BayesianVector posBv;
  BayesianVector negBv;

  /**
   * Creates new BaysianVectors for the set of positive and negative
   * FocusedVectors.
   */
  public void learn() {
    posBv = new BayesianVector(posVec);
    negBv = new BayesianVector(negVec);
    System.out.println("Using Baysian Classifier");
  }

  /**
   * Calculates the Baysian distance for the specified document.
   * @param s the document represented by a string.
   * @return the grade for the document.
   **/
  public double getGrade(String s) {
    DocVector fv = new DocVector(s);
    return fv.getBayesianDistance(posBv, negBv);
  }
}
