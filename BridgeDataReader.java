// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * A class which takes a .csv of bridge information and converts it into a list of bridges
 * 
 * @author Joseph Peplinski
 *
 */
public class BridgeDataReader implements BridgeDataReaderInterface {

  List<BridgeInterface> bridges = new ArrayList<BridgeInterface>();
  String railroad, region, bridgePrefix, numberDivider, formatHelp;

  /**
   * A constructor which takes a scanner input, usually used for testing
   * 
   * @param input a Scanner containing a comma-separated data set
   * @throws DataFormatException if the data is not formatted as expected
   */
  public BridgeDataReader(Scanner input) throws DataFormatException {
    if (input == null)
      throw new DataFormatException("Input is null");
    unifiedDataInterpreter(input);
  }

  /**
   * A constructor which takes the arguments from the user and generates a list of bridges from the
   * input .csv filepath
   * 
   * @param args an array of Strings which contains a .csv location at its first index
   * @throws DataFormatException if the data is not formatted as expected
   * @throws FileNotFoundException if the .csv file could not be found
   */
  public BridgeDataReader(String[] args) throws DataFormatException, FileNotFoundException {
    if (args == null || args[0] == null || args[0].length() == 0)
      throw new DataFormatException();
    File file = new File(args[0]);
    Scanner input = new Scanner(file);
    unifiedDataInterpreter(input);
  }

  private void unifiedDataInterpreter(Scanner input) throws DataFormatException {
    input.useDelimiter(",");

    if (!input.hasNext()) {
      throw new DataFormatException(
          "Bridge Data Reader could not be generated with an empty data set");
    }
    // Don't check whether "Railroad" matches, as there are issues with the start of a csv that
    // do not show up with a Scanner input
    input.next();
    // Check all of the other headers in this line, just to confirm this is formatted as expected
    if (!input.next().equals("Region") || !input.next().equals("Bridge Prefix")
        || !input.next().equals("Number Divider") || !input.next().equals("Format Help")) {
      System.out.println(Arrays.toString(input.next().toCharArray()));

      throw new DataFormatException("File header was not as expected");
    }
    input.nextLine();
    railroad = input.next();
    region = input.next();
    bridgePrefix = input.next();
    numberDivider = input.next();
    formatHelp = input.next();
    // Skip over some more headers
    input.nextLine();
    input.nextLine();
    input.nextLine();
    // While there's more lines, there are more bridges which should be added to the list
    while (input.hasNextLine()) {
      String temp = input.nextLine();
      if (temp.length() == 0)
        break;
      Scanner bridgeInput = new Scanner(temp);
      bridgeInput.useDelimiter(",");
      bridges.add(new Bridge(bridgeInput, bridgePrefix, numberDivider));
    }
  }

  @Override
  public List<BridgeInterface> getBridges() {
    return bridges;
  }

  @Override
  public String getRailroad() {
    return railroad;
  }

  @Override
  public String getRegion() {
    return region;
  }

  @Override
  public String getBridgePrefix() {
    return bridgePrefix;
  }

  public String getNumberDivider() {
    return numberDivider;
  }

  @Override
  public String getFormatHelp() {
    return formatHelp;
  }

}
