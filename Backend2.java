import java.awt.List;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Backend2 implements BackendInterface {
  
  //BridgeDataObject
  private BridgeDataReaderDummy bridgeDataReader;
  
  private RedBlackTree<BridgeInterface> tree;
  
  
  /**
   * Constructor for backend
   * 
   * @param args
   */
  public Backend2(String[] args ) throws DataFormatException, FileNotFoundException {
    try {
    bridgeDataReader = new BridgeDataReaderDummy(args);
    } catch (DataFormatException e) {
      throw e;
    } catch (FileNotFoundException e) {
      throw e;
    }
    
    tree = new RedBlackTree<BridgeInterface>();
    
    loadTree();
    
  }
  
  /**
   * Constructor for backend
   * 
   * @param input
   */
  public Backend2(Scanner input) throws DataFormatException {
    try {
    bridgeDataReader = new BridgeDataReaderDummy(input);
    } catch (DataFormatException e) {
      throw e;
    }
    
    tree = new RedBlackTree<BridgeInterface>();
    
    loadTree();
    
  }
  
  /**
   * Loads tree with bridges
   */
  private void loadTree() {
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) bridgeDataReader.getBridges();
        
    if (bridges != null) {
      for (BridgeInterface bridge : bridges) {
        tree.insert(bridge);
      }
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
   * An accessor for the bridge prefix
   * 
   * @return bridge prefix
   */
  public String getPrefix() {
    return bridgeDataReader.getBridgePrefix();
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
   * Gets next bridge in tree order
   * 
   * @param current bridge
   * @return next bridge
   */
   public BridgeInterface getNext(BridgeInterface current) throws NoSuchElementException {
    
    Iterator<BridgeInterface> iterator = tree.iterator();
    
    boolean found = false;
    BridgeInterface foundBridge = null;
    
    while (iterator.hasNext() && found == false) {
      BridgeInterface currentBridge = iterator.next();
      if (currentBridge.compareTo(current) == 0) {
        if (iterator.hasNext()) {
          foundBridge = iterator.next();
        }
        found = true;
      }
    }
    
    if (foundBridge == null)
      throw new NoSuchElementException("No next bridge");
    
    return foundBridge;
  }
  
  
  /**
   * Gets previous bridge
   * 
   * @param current bridge
   * @return previous bridge
   */
   public BridgeInterface getPrevious(BridgeInterface current) throws NoSuchElementException {
     Iterator<BridgeInterface> iterator = tree.iterator();
     
     boolean found = false;
     BridgeInterface foundBridge = null;
     BridgeInterface previous = null;
     
     while (iterator.hasNext() && found == false) {
       BridgeInterface currentBridge = iterator.next();
       if (currentBridge.compareTo(current) == 0) {
         foundBridge = previous;
         found = true;
       } else {
         previous = currentBridge;
       }
     }
     
     if (foundBridge == null)
       throw new NoSuchElementException("No Previous Bridges");
     
     return foundBridge;
     
   }
  
  /**
   * Gets root bridge
   * 
   * @return root bridge
   */
  public BridgeInterface getRoot() {
    return tree.getRoot();
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
    
    BridgeInterface inputBridge = new Bridge(input, bridgeDataReader.getBridgePrefix(), 
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
   * Used https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
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
    double rad = 3958.8; 
    double c = 2 * Math.asin(Math.sqrt(a)); 
    return rad * c; 
  }

}
