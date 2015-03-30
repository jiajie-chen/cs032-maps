package edu.brown.cs.jc124.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author jchen
 *
 *         A class for parsing and returning values form a CSV file.
 */
public final class CsvReader implements Closeable {
  private BufferedReader inFile;

  private String delim;
  private String[] header;
  private HashMap<String, String> current;

  /**
   * Makes a new reader that takes in a file and it's delimiter.
   *
   * @param file
   *          the path of the file to get
   * @param delim
   *          the delimiter to separate values by
   * @throws IOException
   *           if file is not a valid CSV file with header
   */
  public CsvReader(String file, String delim) throws IOException {
    inFile = new BufferedReader(new FileReader(file));
    this.delim = delim;
    if (!readHeader()) {
      throw new IOException("File is empty");
    }
  }

  /**
   * Makes a new reader that takes in a file and delimits by ','.
   *
   * @param file
   *          the path of the file to get
   * @throws IOException
   *           if file is not a valid CSv file with header
   */
  public CsvReader(String file) throws IOException {
    this(file, ",");
  }

  /**
   * Makes a new reader that takes in a file and it's delimiter.
   *
   * @param file
   *          the file object to get
   * @param delim
   *          the delimiter to separate values by
   * @throws IOException
   *           if file is not a valid CSV file with header
   */
  public CsvReader(File file, String delim) throws IOException {
    inFile = new BufferedReader(new FileReader(file));
    this.delim = delim;
    if (!readHeader()) {
      throw new IOException("File is empty");
    }
  }

  /**
   * Makes a new reader that takes in a file and delimits by ','.
   *
   * @param file
   *          the file object to get
   * @throws IOException
   *           if file is not a valid CSV file with header
   */
  public CsvReader(File file) throws IOException {
    this(file, ",");
  }

  private boolean readHeader() throws IOException {
    String record = inFile.readLine();

    if (record == null) {
      return false;
    }

    header = record.split("\\s*\\Q" + delim + "\\E\\s*");
    return true;
  }

  /**
   * Sets the header that CsvReader expects to map to.
   *
   * @param header
   *          the new header to use
   */
  public void setHeader(String[] header) {
    this.header = header;
  }

  /**
   * reads in the next line of the CSV file.
   *
   * @return true if file is not at EOF
   * @throws IOException
   *           is encounters issues reading the next line
   */
  public boolean readLine() throws IOException {
    String record = inFile.readLine();

    if (record == null) {
      return false;
    }

    String[] entries = record.split("\\s*\\Q" + delim + "\\E\\s*");
    if (entries.length != header.length) {
      throw new IOException("CSV record does not match the header");
    }

    current = new HashMap<String, String>();
    for (int i = 0; i < header.length; i++) {
      current.put(header[i], entries[i]);
    }
    return true;
  }

  /**
   * Gets the data in the column (mapped by the header) of the current line.
   *
   * @param key
   *          a column (as specified by the header)
   * @return the String value of that column
   */
  public String get(String key) {
    return current.get(key);
  }

  @Override
  public void close() throws IOException {
    inFile.close();
  }
}
