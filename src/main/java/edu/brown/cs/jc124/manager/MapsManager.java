package edu.brown.cs.jc124.manager;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import edu.brown.cs.is3.maps.Database;

/**
 * @author jchen
 *
 */
public class MapsManager {
  private Database db;
  private SuggestionHelper autocorrect;
  private KdTreeManager mapsKd;
  
  /**
   * @param db
   */
  public MapsManager(Database db) {
    this.db = db;
    
    autocorrect = new SuggestionHelper();
    autocorrect.fill(db);
    
    
    mapsKd.fill(db);
  }

}