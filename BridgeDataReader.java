import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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

public class BridgeDataReader implements BridgeDataReaderInterface {

  List<BridgeInterface> bridges = new ArrayList<BridgeInterface>();
  String railroad, region, bridgePrefix, numberDivider, formatHelp;
  
  public BridgeDataReader(Scanner input) throws DataFormatException {
    if (input == null) throw new DataFormatException("Input is null");
    unifiedDataInterpreter(input);
  }

  public BridgeDataReader(String[] args) throws DataFormatException, FileNotFoundException {
    if (args == null || args[0].length() == 0) throw new DataFormatException();
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
    if (!input.next().equals("Railroad") || !input.next().equals("Region") || !input.next().equals("Bridge Prefix") || !input.next().equals("Number Divider") || !input.next().equals("Format Help")) {
      throw new DataFormatException("File header was not as expected");
    }
    input.nextLine();
    railroad = input.next();
    region = input.next();
    bridgePrefix = input.next();
    numberDivider = input.next();
    formatHelp = input.next();
    input.nextLine();
    input.nextLine();
    input.nextLine();
    while (input.hasNextLine()) {
      String temp = input.nextLine();
      if (temp.length() == 0) break;
      Scanner bridgeInput = new Scanner(temp);
      bridgeInput.useDelimiter(",");
      bridges.add(new Bridge(bridgeInput, bridgePrefix, numberDivider));
    }
  }
  @Override
  public List<BridgeInterface> getBridges() {
    // TODO Auto-generated method stub
    return bridges;
  }

  @Override
  public String getRailroad() {
    // TODO Auto-generated method stub
    return railroad;
  }

  @Override
  public String getRegion() {
    // TODO Auto-generated method stub
    return region;
  }

  @Override
  public String getBridgePrefix() {
    // TODO Auto-generated method stub
    return bridgePrefix;
  }

  public String getNumberDivider() {
    return numberDivider;
  }
  @Override
  public String getFormatHelp() {
    // TODO Auto-generated method stub
    return formatHelp;
  }

}
