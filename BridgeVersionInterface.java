
public interface BridgeVersionInterface extends BCVersionInterface {
  /**
   * An accessor for the number of spans the bridge has
   * 
   * @return an int representation of the number of spans a bridge has, or -1 if unknown
   */
  public int getSpans();

  /**
   * An accessor for the position of approaches for the bridge
   * 
   * @return 'N' if no approaches, 'L' if only an approach on the low-numbered side, 'H' if only an
   *         approach on the high-numbered side, 'B' if approaches on both sides, or 'U' if approach
   *         information is unknown
   */
  public char getApproachLoc();

  /**
   * An accessor for the specific type of the low-numbered side approach
   * 
   * @return a String representation of the specific type specified in the database, or null if
   *         unknown
   */
  public String getLAType();

  /**
   * An accessor for the number of spans on the low-numbered side approach
   * @return an int representation of the number of spans, or -1 if unknown
   */
  public int getLASpans();

  /**
   * An accessor for the length of the low-numbered side approach
   * @return a double representation of the approach length, in feet, or -1 if unknown
   */
  public double getLALength();

  /**
   * An accessor for the height of the low-numbered side approach
   * @return a double representation of the approach height, in feet, or -1 if unknown
   */
  public double getLAHeight();

  /**
   * An accessor for the specific type of the high-numbered side approach
   * 
   * @return a String representation of the specific type specified in the database, or null if
   *         unknown
   */
  public String getHAType();

  /**
   * An accessor for the number of spans on the high-numbered side approach
   * @return an int representation of the number of spans, or -1 if unknown
   */
  public int getHASpans();

  /**
   * An accessor for the length of the high-numbered side approach
   * @return a double representation of the approach length, in feet, or -1 if unknown
   */
  public double getHALength();

  /**
   * An accessor for the height of the high-numbered side approach
   * @return a double representation of the approach height, in feet, or -1 if unknown
   */
  public double getHAHeight();

}
