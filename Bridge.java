import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

public class Bridge implements BridgeInterface {

  String bridgePrefix;
  String numberDivider;
  String name;
  Double lat;
  Double lon;
  String type;
  List<BCVersionInterface> versions = new ArrayList<BCVersionInterface>();
  
  public Bridge(String name, String bridgePrefix, String numberDivider) {
    this.name = name;
    this.bridgePrefix = bridgePrefix;
    this.numberDivider = numberDivider;
  }
  public Bridge(Scanner input, String bridgePrefix, String numberDivider) throws DataFormatException {
    this.bridgePrefix = bridgePrefix;
    this.numberDivider = numberDivider;
    name = input.next();
    //System.out.println(name);
    lat = input.nextDouble();
    //System.out.println(lat);
    lon = input.nextDouble();
    //System.out.println(lon);
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
    // TODO Confirm that the bridge is formatted right, make everything case-insensitive
    
    String modifiedName = name.substring(bridgePrefix.length(), name.length());
    if (modifiedName.contains(numberDivider)) {
      modifiedName = modifiedName.substring(0, modifiedName.indexOf(numberDivider));
    }
    String otherModifiedName = other.getName().substring(bridgePrefix.length(), other.getName().length());
    if (otherModifiedName.contains(numberDivider)) {
      otherModifiedName = otherModifiedName.substring(0, otherModifiedName.indexOf(numberDivider));
    }
    
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
      String shortenedName = name.substring(bridgePrefix.length());
      String numeratorString = shortenedName.substring(shortenedName.indexOf(numberDivider) + numberDivider.length(), shortenedName.indexOf("/"));
      String denominatorString = shortenedName.substring(shortenedName.indexOf("/") + 1);
      suffix = Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString);
    } else {
      String shortenedName = name.substring(bridgePrefix.length());
      String suffixString = shortenedName.substring(shortenedName.indexOf(numberDivider) + numberDivider.length()).trim();
      for (int i = 0; i < 10; i++) {
        if (suffixString.equals("" + i)) {
          suffix = Double.parseDouble(suffixString) / 10;
        }
      }
      char suffixChar = suffixString.charAt(0);
      if (suffixChar >= 65 && suffixChar <= 90) {
        suffix = (double)suffixChar - 64;
      }
      if (suffixChar >= 97 && suffixChar <= 122) {
        suffix = (double)suffixChar - 96;
      }
    }
    
    if (other.getName().substring(other.getName().indexOf(numberDivider) + numberDivider.length()).contains("/")) {
      String shortenedName = other.getName().substring(bridgePrefix.length());
      String numeratorString = shortenedName.substring(shortenedName.indexOf(numberDivider) + numberDivider.length(), shortenedName.indexOf("/"));
      String denominatorString = shortenedName.substring(shortenedName.indexOf("/") + 1);
      otherSuffix = Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString);
    } else {
      String shortenedName = other.getName().substring(bridgePrefix.length());
      String suffixString = shortenedName.substring(shortenedName.indexOf(numberDivider) + numberDivider.length()).trim();
      for (int i = 0; i < 10; i++) {
        if (suffixString.equals("" + i)) {
          otherSuffix = Double.parseDouble(suffixString) / 10;
        }
      }
      char suffixChar = suffixString.charAt(0);
      if (suffixChar >= 65 && suffixChar <= 90) {
        otherSuffix = (double)suffixChar - 64;
      }
      if (suffixChar >= 97 && suffixChar <= 122) {
        otherSuffix = (double)suffixChar - 96;
      }
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
    // TODO Auto-generated method stub
    return name;
  }

  @Override
  public double getLat() {
    // TODO Auto-generated method stub
    return lat;
  }

  @Override
  public double getLon() {
    // TODO Auto-generated method stub
    return lon;
  }

  @Override
  public int getNumVersions() {
    // TODO Auto-generated method stub
    return versions.size();
  }

  @Override
  public BCVersionInterface getVersion(int index) {
    // TODO Auto-generated method stub
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
