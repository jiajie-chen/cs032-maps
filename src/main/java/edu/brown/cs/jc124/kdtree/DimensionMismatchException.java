package edu.brown.cs.jc124.kdtree;

/**
 * @author jchen
 *
 *         Runtime exception class for mismatches of dimension in Coordinates.
 */
public class DimensionMismatchException extends IndexOutOfBoundsException {

  /**
   * Makes a new exception with the given message.
   *
   * @param s
   *          the message
   */
  public DimensionMismatchException(String s) {
    super(s);
  }

  /**
   * Makes a new exception.
   */
  public DimensionMismatchException() {
    super();
  }

  /**
   * auto-generated id.
   */
  private static final long serialVersionUID = -7759535654962211582L;

}
