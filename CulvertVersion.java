// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.util.Scanner;

/**
 * A class which contains information relevant to culverts
 * 
 * @author Joseph Peplinski
 *
 */
public class CulvertVersion implements CulvertVersionInterface {

  String specType, buildDate, fateDate, fate;
  double length, height, width;
  
  /**
   * A constructor which takes a Scanner input and extracts the required data for use in the class
   * @param input a Scanner containing a line of a .csv
   */
  public CulvertVersion(Scanner input) {
    specType = input.next();
    length = input.nextDouble();
    height = input.nextDouble();
    width = input.nextDouble();
    // Skip over cells in the .csv used for bridge versions
    for (int i = 0; i < 10; i++) {
      input.next();
    }
    buildDate = input.next();
    fate = input.next();
    fateDate = input.next();
  }

  @Override
  public boolean isBridge() {
    return false;
  }

  @Override
  public boolean isCulvert() {
    return true;
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
  public double getWidth() {
    return width;
  }

}
