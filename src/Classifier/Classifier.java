package Classifier;

import java.util.Vector;

/**
 * Abstract class for the Classifier. Eeach class that extends it must know how to
 * add positive/negative pages and return grades to new documents.
 */

abstract public class Classifier {
  @SuppressWarnings("rawtypes")
protected Vector posVec;
  @SuppressWarnings("rawtypes")
protected Vector negVec;

  /**
   * Default Constructor
   *
   */
  @SuppressWarnings("rawtypes")
public Classifier() {
    posVec = new Vector();
    negVec = new Vector();
  }

  /**
   * Add a positive page to the learing set
   * @param s Positive page
   */
  @SuppressWarnings("unchecked")
public void addPositive(String s) {
    posVec.add(new DocVector(s));
  }

  /**
   * Add a negaitive page to the learing set
   * @param s
   */
  @SuppressWarnings("unchecked")
public void addNegative(String s) {
    negVec.add(new DocVector(s));
  }

  /**
   * Create the classifier from the positive and negative example set
   *
   */
  abstract public void learn();

  /**
   * Grades a text page
   * @param s Text page
   * @return Grade between 0 and 1
   */
  abstract public double getGrade(String s);

}
