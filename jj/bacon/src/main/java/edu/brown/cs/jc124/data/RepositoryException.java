package edu.brown.cs.jc124.data;

/**
 * @author jchen
 *
 *         Exception when a repository runs into an error when retrieving
 *         information.
 */
public class RepositoryException extends RuntimeException {

  /**
   * Auto-generated UID.
   */
  private static final long serialVersionUID = -6656447076068286022L;

  /**
   * Makes default exception.
   */
  public RepositoryException() {
    super();
  }

  /**
   * Makes exception with a message.
   *
   * @param msg
   *          message to use
   */
  public RepositoryException(String msg) {
    super(msg);
  }
}
