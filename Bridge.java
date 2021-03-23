// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;


/**
 * A class which contains information about a bridge/culvert in a data set
 * 
 * @author Joseph Peplinski
 *
 */
public class Bridge implements BridgeInterface {

  String bridgePrefix;
  String numberDivider;
  String name;
  Double lat;
  Double lon;
  String type;
  List<BCVersionInterface> versions = new ArrayList<BCVersionInterface>();

  /**
   * A constructor which creates a bridge object for comparison purposes, only containing the
   * bare-bones information needed for comparison
   * 
   * @param name          the name of the bridge
   * @param bridgePrefix  the label before the number of the bridge
   * @param numberDivider a String of the characters between a bridge's main number and suffix, if
   *                      it has one
   */
  public Bridge(String name, String bridgePrefix, String numberDivider) {
    this.name = name;
    this.bridgePrefix = bridgePrefix;
    this.numberDivider = numberDivider;
  }

  /**
   * A constructor which creates a full-featured bridge object
   * 
   * @param input         a Scanner containing a line of a bridge data set
   * @param bridgePrefix  the prefix used by bridges within the data set
   * @param numberDivider the number divider used by bridges within the data set
   * @throws DataFormatException if the data is not formatted as expected
   */
  public Bridge(Scanner input, String bridgePrefix, String numberDivider)
      throws DataFormatException {
    this.bridgePrefix = bridgePrefix;
    this.numberDivider = numberDivider;
    // Get universal information
    name = input.next();
    lat = input.nextDouble();
    lon = input.nextDouble();
    // Generate bridge iterations until there are no more iterations
    while (input.hasNext()) {
      type = input.next();
      if (type.equals("")) {
        break;
      }
      boolean isBridge = type.equals("B");
      boolean isCulvert = type.equals("C");
      if (isBridge) {
        versions.add(new BridgeVersion(input));
      } else if (isCulvert) {
        versions.add(new CulvertVersion(input));
      } else {
        System.out.println(isBridge + " " + isCulvert);
        throw new DataFormatException(type + " was not of type bridge or culvert");
      }
    }
  }

  @Override
  public int compareTo(BridgeInterface other) {
    // Get the name of each of the bridges and get them into more useful formats
    String modifiedName = name.substring(bridgePrefix.length(), name.length());
    if (modifiedName.contains(numberDivider)) {
      modifiedName = modifiedName.substring(0, modifiedName.indexOf(numberDivider));
    }
    String otherModifiedName =
        other.getName().substring(bridgePrefix.length(), other.getName().length());
    if (otherModifiedName.contains(numberDivider)) {
      otherModifiedName = otherModifiedName.substring(0, otherModifiedName.indexOf(numberDivider));
    }
    // Compare the main numbers, if these don't match then we already know which bridge is bigger
    int mainNumber = Integer.valueOf(modifiedName);
    int otherMainNumber = Integer.valueOf(otherModifiedName);
    if (mainNumber > otherMainNumber) {
      return 1;
    }
    if (mainNumber < otherMainNumber) {
      return -1;
    }

    // Now we know that the main numbers are equal, so we need to parse the suffixes
    double suffix = 0.0;
    double otherSuffix = 0.0;

    if (name.substring(name.indexOf(numberDivider) + numberDivider.length()).contains("/")) {
      // This is a fraction, and needs to be converted to an integer
      String shortenedName = name.substring(bridgePrefix.length());
      String numeratorString =
          shortenedName.substring(shortenedName.indexOf(numberDivider) + numberDivider.length(),
              shortenedName.indexOf("/"));
      String denominatorString = shortenedName.substring(shortenedName.indexOf("/") + 1);
      suffix = Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString);
    } else {
      // This is either a letter or a decimal
      String shortenedName = name.substring(bridgePrefix.length());
      String suffixString = shortenedName
          .substring(shortenedName.indexOf(numberDivider) + numberDivider.length()).trim();
      // First check if it's a decimal, convert this into a double
      for (int i = 0; i < 10; i++) {
        if (suffixString.equals("" + i)) {
          suffix = Double.parseDouble(suffixString) / 10;
        }
      }
      char suffixChar = suffixString.charAt(0);
      // If the char is in one of these ranges, it's a letter, and should be converted to the
      // associated number (A = 1 or a = 1)
      if (suffixChar >= 65 && suffixChar <= 90) {
        suffix = (double) suffixChar - 64;
      }
      if (suffixChar >= 97 && suffixChar <= 122) {
        suffix = (double) suffixChar - 96;
      }
    }
    // Do the same thing for the other bridge
    if (other.getName().substring(other.getName().indexOf(numberDivider) + numberDivider.length())
        .contains("/")) {
      String shortenedName = other.getName().substring(bridgePrefix.length());
      String numeratorString =
          shortenedName.substring(shortenedName.indexOf(numberDivider) + numberDivider.length(),
              shortenedName.indexOf("/"));
      String denominatorString = shortenedName.substring(shortenedName.indexOf("/") + 1);
      otherSuffix = Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString);
    } else {
      String shortenedName = other.getName().substring(bridgePrefix.length());
      String suffixString = shortenedName
          .substring(shortenedName.indexOf(numberDivider) + numberDivider.length()).trim();
      for (int i = 0; i < 10; i++) {
        if (suffixString.equals("" + i)) {
          otherSuffix = Double.parseDouble(suffixString) / 10;
        }
      }
      char suffixChar = suffixString.charAt(0);
      if (suffixChar >= 65 && suffixChar <= 90) {
        otherSuffix = (double) suffixChar - 64;
      }
      if (suffixChar >= 97 && suffixChar <= 122) {
        otherSuffix = (double) suffixChar - 96;
      }
      // Now all of the suffixes have been converted to values, so they can be compared
    }
    if (suffix > otherSuffix) {
      return 1;
    }
    if (suffix < otherSuffix) {
      return -1;
    }
    return 0;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getLat() {
    return lat;
  }

  @Override
  public double getLon() {
    return lon;
  }

  @Override
  public int getNumVersions() {
    return versions.size();
  }

  @Override
  public BCVersionInterface getVersion(int index) {
    if (index < versions.size()) {
      return versions.get(index);
    }
    throw new IllegalArgumentException("Index was beyond the bounds of the bridge version list");
  }

  @Override
  public String toString() {
    return name;
  }
}
