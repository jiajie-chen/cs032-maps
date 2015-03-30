package edu.brown.cs.jc124.data;

/**
 * @author jchen
 *
 *         Interface for wrapping an object into an Attribute for storage in
 *         some collection. Attribute can also contain meta-data or accessors
 *         for the object.
 *
 * @param <T>
 *          the object to wrap
 */
public interface Attribute<T> {
  /**
   * Gets the object wrapped by Attribute.
   *
   * @return the stored object
   */
  T getAttribute();
}
