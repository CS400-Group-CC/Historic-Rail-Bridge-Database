import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Backend implements BackendInterface {
  
  //BridgeDataObject
  private BridgeDataReaderDummy bridgeDataReader;
  
  private RedBlackTree<BridgeInterface> tree;
  
  
  /**
   * Constructor for backend
   * 
   * @param args
   */
  public Backend(String[] args ) {
    bridgeDataReader = new BridgeDataReaderDummy(args);
    
    tree = new RedBlackTree<BridgeInterface>();
    
    loadTree();
    
  }
  
  /**
   * Constructor for backend
   * 
   * @param input
   */
  public Backend(Scanner input) {
    bridgeDataReader = new BridgeDataReaderDummy(input);
    
    tree = new RedBlackTree<BridgeInterface>();
    
    loadTree();
    
  }
  
  /**
   * Loads tree with bridges
   */
  private void loadTree() {
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) bridgeDataReader.getBridges();
    
    for (BridgeInterface bridge : bridges) {
      tree.insert(bridge);
    }
    
  }

  
  /**
   * An accessor for the current dataset's railroad name
   * 
   * @return a String representation of the railroad name
   */
  @Override
  public String getRailroad() {
    // TODO Auto-generated method stub
    return bridgeDataReader.getRailroad();
  }

  
  /**
   * An accessor for the current dataset's region
   * 
   * @return a String representation of the dataset's region
   */
  @Override
  public String getRegion() {
    // TODO Auto-generated method stub
    return bridgeDataReader.getRegion();
  }

  
  /**
   * An accessor for the number of bridges in the current database
   * 
   * @return the number of bridges in the database
   */
  @Override
  public int getNumBridges() {
    // TODO Auto-generated method stub
    return tree.size();
  }

  
  /**
   * Gives information provided by the data table about bridge naming schemes
   * 
   * @return a String representation of a brief explanation of how bridges are named/numbered for
   *         search purposes
   */
  @Override
  public String getFormatHelp() {
    // TODO Auto-generated method stub
    return bridgeDataReader.getFormatHelp();
  }

  
  /**
   * Searches the database for the bridge matching the given input
   * 
   * @param input a String representation of the bridge name
   * @return the corresponding BridgeInterface object if such a bridge exists
   * @throws IllegalArgumentException if the bridge name is not in a valid format
   * @throws NoSuchElementException   if the bridge name was formatted correctly but is not present
   *                                  in the database. The message will contain the name of the
   *                                  bridge closest in number to the searched bridge
   */
  @Override
  public BridgeInterface getBridge(String input)
      throws IllegalArgumentException, NoSuchElementException {
    
    BridgeInterface inputBridge = new BridgeInterface(input, bridgeDataReader.getBridgePrefix(), 
        bridgeDataReader.getNumberDivider());
    
    BridgeInterface foundBridge;
    
    try {
      foundBridge = tree.findItem(inputBridge);
    } 
    catch (NoSuchElementException e) {
      throw e;
    }
    
    
    return foundBridge;
  }

  
  /**
   * Searches for the nearest bridge to the input location
   * 
   * @param latitude  a decimal representation of the latitude in degrees
   * @param longitude a decimal representation of the longitude in degrees
   * @return the BridgeInterface object for the nearest bridge
   * @throws IllegalArgumentException if the latitude is outside +/-90 or the longitude is outside
   *                                  +/- 180
   */
  @Override
  public BridgeInterface getNearestBridge(double latitude, double longitude)
      throws IllegalArgumentException {
    
    if ((latitude > 90 || latitude < -90) || (longitude > 180 || longitude < -180)) {
      throw new IllegalArgumentException("Invalid Latitude or Longitutde");
    }
    
    Iterator<BridgeInterface> iterator = tree.iterator();
    
    double smallestDistance = -1.0;
    BridgeInterface closestBridge = null;
    
    while (iterator.hasNext()) {
      
      BridgeInterface currentBridge = iterator.next();
      
      double distance = calculateDistance(latitude, currentBridge.getLat(), 
          longitude, currentBridge.getLon());
      
      if (smallestDistance < 0 || closestBridge == null) {
        smallestDistance = distance;
        closestBridge = currentBridge;
      } else if (distance < smallestDistance) {
        smallestDistance = distance;
        closestBridge = currentBridge;
      }
      
    }
    
    return closestBridge;
  }

  
  /**
   * Gets the distance between coordinates and a specified bridge, in miles
   * 
   * @param latitude  a decimal representation of the latitude in degrees
   * @param longitude a decimal representation of the longitude in degrees
   * @param bridge    the BridgeInterface object that the distance is being calculated to
   * @return the distance between the bridge and the input position, in miles
   * @throws IllegalArgumentException if the latitude is outside +/-90, if the longitude is outside
   *                                  +/- 180, or if the BridgeInterface is null
   */
  @Override
  public double getDistance(double latitude, double longitude, BridgeInterface bridge)
      throws IllegalArgumentException {
    
    if ((latitude > 90 || latitude < -90) || (longitude > 180 || longitude < -180)) {
      throw new IllegalArgumentException("Invalid Latitude or Longitutde");
    } else if (bridge == null) {
      throw new IllegalArgumentException("Bridge is empty");
    }
    
    double distance = calculateDistance(latitude, bridge.getLat(), longitude, bridge.getLon());
    
    return distance;
  }
  
  /**
   * 
   * Calculates the distance, in miles, between two points
   * 
   * @param lat1
   * @param lat2
   * @param lon1
   * @param lon2
   * @return distance between sets of latitudes and longitudes
   */
  private double calculateDistance(double lat1, double lat2, double lon1, double lon2) {
    
    double dLat = Math.toRadians(lat2 - lat1); 
    double dLon = Math.toRadians(lon2 - lon1); 

    // convert to radians 
    lat1 = Math.toRadians(lat1); 
    lat2 = Math.toRadians(lat2); 

    // apply formulae 
    double a = Math.pow(Math.sin(dLat / 2), 2) +  
               Math.pow(Math.sin(dLon / 2), 2) *  
               Math.cos(lat1) *  
               Math.cos(lat2); 
    double rad = 6371; 
    double c = 2 * Math.asin(Math.sqrt(a)); 
    return rad * c; 
  }

}
