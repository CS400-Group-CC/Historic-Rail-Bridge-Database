// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * A class which contains information relevant to bridges
 * 
 * @author Joseph Peplinski
 *
 */
public class BridgeVersion implements BridgeVersionInterface {

  String specType, laType, haType, buildDate, fateDate, fate;
  double length, height, laLength, laHeight, haLength, haHeight;
  int spans, laSpans, haSpans;
  char approach;

  /**
   * A constructor which takes a section of a .csv and interprets its information to make it
   * accessible to other classes
   * 
   * @param input a Scanner which contains a section of a .csv relevant to one iteration of a bridge
   * @throws DataFormatException if the data found in any cell was not of the expected type
   */
  public BridgeVersion(Scanner input) throws DataFormatException {
    specType = input.next();
    length = input.nextDouble();
    height = input.nextDouble();
    input.next();
    spans = input.nextInt();
    approach = input.next().charAt(0);
    if (approach == 'N') {
      // No approaches, just skip over the next few lines
      for (int i = 0; i < 8; i++) {
        input.next();
      }
    } else if (approach == 'L') {
      // Only one approach, get information on it and skip over the other approach
      laType = input.next();
      laLength = input.nextDouble();
      laHeight = input.nextDouble();
      laSpans = input.nextInt();
      for (int i = 0; i < 4; i++) {
        input.next();
      }
    } else if (approach == 'H') {
      // Only one approach, skip over the low approach and get information on the high approach
      for (int i = 0; i < 4; i++) {
        input.next();
      }
      haType = input.next();
      haLength = input.nextDouble();
      haHeight = input.nextDouble();
      haSpans = input.nextInt();

    } else if (approach == 'B') {
      // Approaches on both sides, collect information on both of them
      laType = input.next();
      laLength = input.nextDouble();
      laHeight = input.nextDouble();
      laSpans = input.nextInt();
      haType = input.next();
      haLength = input.nextDouble();
      haHeight = input.nextDouble();
      haSpans = input.nextInt();
    } else {
      throw new DataFormatException("Unexpected approach type");
    }
    buildDate = input.next();
    fate = input.next();
    fateDate = input.next();
  }

  @Override
  public boolean isBridge() {
    return true;
  }

  @Override
  public boolean isCulvert() {
    return false;
  }

  @Override
  public String getSpecificType() {
    return specType;
  }

  @Override
  public double getLength() {
    return length;
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override
  public String getStartDate() {
    return buildDate;
  }

  @Override
  public String getEndDate() {
    return fateDate;
  }

  @Override
  public String getFate() {
    return fate;
  }

  @Override
  public int getSpans() {
    return spans;
  }

  @Override
  public char getApproachLoc() {
    return approach;
  }

  @Override
  public String getLAType() {
    return laType;
  }

  @Override
  public int getLASpans() {
    return laSpans;
  }

  @Override
  public double getLALength() {
    return laLength;
  }

  @Override
  public double getLAHeight() {
    return laHeight;
  }

  @Override
  public String getHAType() {
    return haType;
  }

  @Override
  public int getHASpans() {
    return haSpans;
  }

  @Override
  public double getHALength() {
    return haLength;
  }

  @Override
  public double getHAHeight() {
    return haHeight;
  }

}
