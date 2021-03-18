import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Frontend {
  // Class variables
  Backend backend;
  Scanner inputs;
  boolean foundError;
  
  Double lat = null;
  Double lon = null;
  boolean coordsSet;
  boolean cityTreeGenerated;
  boolean showASCII;
  CitySearch cityList;
  Bridge currentBridge;
  
  public static void main(String[] args) {
    Frontend frontend = new Frontend(args);
    frontend.run(frontend.backend);
  }
  
  // no-arguments constructor for testing purposes
  public Frontend() {
    cityTreeGenerated = false;
    foundError = false;
    coordsSet = false;
    showASCII = false;
  }
  
  // more proper constructor for generation of variables, etc
  public Frontend(String[] args) {
    try {
      backend = new Backend(args);
    }
    catch (Exception error) {
      foundError = true;
      System.out.println("Error encountered in reading data set:\n" + error);
      System.out.println("Ensure that data file is correctly formatted.");
    }
    cityTreeGenerated = false;
    coordsSet = false;
    showASCII = false;
  }
  
  public void run(Backend backend) {
    // If an exception was hit in generating the backend, return without doing anything.
    if (foundError) {
      return;
    }
    // For testing purposes (because my tests were implemented before understanding the necessary logic)
    if (true) {
      this.backend = backend;
    }
    
    // Setup and introduction in the case of a correct dataset reading
    // NOTE: this relies on theoretical getRoot function
    currentBridge = backend.getRoot();
    // Welcome screen, only printed on startup.
    System.out.println("Welcome to the Historic Rail Bridge Database");
    // Enter main screen
    inputs = new Scanner(System.in);
    mainScreen();
  }
  
  private void mainScreen() {
    
    
    
    boolean quit = false;
    boolean reprint = true;
    String input;
    
    while (!quit) {
      if (reprint) {
        System.out.println("You are currently viewing data for:");
        System.out.println(backend.getRailroad() +" - " + backend.getRegion() +" Region.\n");
        // Top printouts, not reprinted if incorrect commands are entered.
        if (coordsSet) {
          System.out.print("Current reference location: ");
          if (lat.compareTo(0.0) >= 0) {
            System.out.printf("%.3f\u00B0 N, ", lat);
          }
          else {
            System.out.printf("%.3f\u00B0 S, ", Math.abs(lat));
          }
          // Longitude will be degrees E/W depending on sign
          if (lon.compareTo(0.0) >= 0) {
            System.out.printf("%.3f\u00B0 E%n", lon);
          }
          else {
            System.out.printf("%.3f\u00B0 W%n", Math.abs(lon));
          }
        }
        System.out.println("Enter your commands on the command line below.  \"x\" exits the program.");
        System.out.println("A full list of commands can be seen by typing \"?\" or \"help\".");
      }
      input = inputs.nextLine().strip().toLowerCase();
      
      // Input cases
      if (input.equals("?") || input.equals("help")) {
        helpScreen();
        reprint = true;
        continue;
      }
      if (input.equals("s")) {
        nameSearchScreen();
        reprint = true;
        continue;
      }
      if (input.equals("l")) {
        locScreen();
        reprint = true;
        continue;
      }
      if (input.equals("ls")) {
        reprint = locSearch();
        continue;
      }
      if (input.equals("v")) {
        viewScreen(currentBridge);
        reprint = true;
        continue;
      }
      if (input.equals("x")) {
        quit = true;
        continue;
      }
      // If we hit this point, input was invalid.
      System.out.println("Undefined command, enter \"?\" or \"help\" for a list of valid commands.");
      reprint = false;
    }    
    System.out.println("Thanks for using the Historic Rail Bridge Database, now get out there and find one!");
  }
  
  private void helpScreen() {
    
    // This variable controls whether we remain in the input-getting loop
    boolean exit = false;
    // This variable ensures that the main help screen is reprinted after viewing a sub-screen, but
    // not when an invalid command is entered.
    boolean reprint = true;
    String input;
    
    while (!exit) {
      if (reprint) {
        System.out.println("Dataset: " + backend.getRailroad() +" - " + backend.getRegion() +" Region.\n");
        // If implemented, current ASCII mode state displayed here
        
        System.out.print("On the main screen, the following are valid commands.");
        System.out.println("  Further information on each command (other than the help screen and exit)"
            + " can be found by entering the command.");
        System.out.println("To leave the help screen, press \"x\".\n");
        
        System.out.println("\"l\": Set or reset refernce location");
        System.out.println("\"s\": Search by bridge name");
        System.out.println("\"ls\": Search for the nearest bridge to the set reference location");
        System.out.println("\"v\": View information for the last bridge visited");
        System.out.println("\"?\" or \"help\": View help screen");
        System.out.println("\"x\": Exit the database program\n");
      }
      input = inputs.nextLine().strip().toLowerCase();
      
      // Input cases
      if (input.equals("s")) {
        helpS();
        reprint = true;
        continue;
      }
      if (input.equals("l")) {
        helpL();
        reprint = true;
        continue;
      }
      if (input.equals("ls")) {
        helpLS();
        reprint = true;
        continue;
      }
      if (input.equals("v")) {
        helpV();
        reprint = true;
        continue;
      }
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      // If we hit this point, input was invalid.
      System.out.println("Undefined command, try again.");
      reprint = false;
    }
    // Exit without printing anything
  }
  
  private void helpS() {
    System.out.println("This screen allows the database of bridges to be searched by bridge name.  "
        + "Names may be entered in upper or lower case, with or without the prefix to the bridge's number."
        + "If a searched name is incorrectly formatted, a new command will be prompted.  "
        + "  If a search is correctly formatted but returns no match, the closest match found in the search"
        + " is displayed for reference before allowing entry of a new command.  The search screen can "
        + "also be exited by entering \"x\".  "
        + "Information about the naming conventions in use in the current dataset is also shown.  "
        + "Available commands are listed below.\n");
    
    System.out.println("\"[bridge name/number]\": Search for the given bridge");
    System.out.println("\"x\": Return to main screen\n");
    
    System.out.println("Enter \"x\" to return to the main help screen.");
    
    boolean exit = false;
    String input;
    
    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();
      
      // Exit command is the only valid input, but keeping consistency in only allowing this for exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }
  private void helpLS() {
    System.out.println("This command searches the database for the bridge nearest to the reference "
        + "location currently set, and opens the viewing screen for this bridge.  "
        + "This location is not set by default, and can be reset, so if not set, this command prints "
        + "an error without doing anything.\n");
    
    System.out.println("Enter \"x\" to return to the main help screen.");
    
    boolean exit = false;
    String input;
    
    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();
      
      // Exit command is the only valid input, but keeping consistency in only allowing this for exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }
  private void helpL() {
    System.out.println("This screen allows a reference location to be set, which allows one to search "
        + "for the bridge nearest to this location, and to see the distance to any given bridge when "
        + "viewing them.  This also allows the existing location to be reset or replaced.  Command "
        + "formatting is shown below.\n");
    
    System.out.println("\"r\": Reset current reference location");
    System.out.println("\"[latitude] [longitude]\": Set reference location to the specified coordinates,"
        + " in decimal degrees");
    System.out.println("\"[city], [2-letter state abbreviation]\": Set reference location to the given"
        + " city, searched based on cities in the US with zip codes");
    System.out.println("\"x\": Return to main screen\n");
    
    System.out.println("Enter \"x\" to return to the main help screen.");
    
    boolean exit = false;
    String input;
    
    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();
      
      // Exit command is the only valid input, but keeping consistency in only allowing this for exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }
  private void helpV() {
    System.out.println("This command opens the bridge viewer screen to the last bridge viewed.  "
        + "On startup, without a last bridge viewed the  \"average\" bridge is shown.  Getting into the "
        + "details, this \"average\" bridge is the root of the binary search tree used to store the "
        + "bridges, and is thus close to the median bridge number.");
    System.out.println("The bridge viewer itself displays all known information on the first known version"
        + " of the bridge.  Commands are provided within the viewer to cycle through versions of a bridge "
        + "(generally being different rebuildings of the same bridge, retaining the original location and "
        + "number) and bridges in order of their numbering.  Commands are shown below.\n");
    
    System.out.println("\"a\"/\"d\": View previous/next bridge");
    System.out.println("\"q\"/\"e\": View previous/next version of the current bridge");
    System.out.println("\"x\": Return to main screen\n");
    
    System.out.println("Enter \"x\" to return to the main help screen.");
    
    boolean exit = false;
    String input;
    
    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();
      
      // Exit command is the only valid input, but keeping consistency in only allowing this for exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }
  
  private void nameSearchScreen() {
    System.out.println(backend.getFormatHelp() + "\n");
    System.out.println("Enter a bridge name/number to search for below,"
        + " or \"x\" to return to the main screen.");
    
    boolean exit = false;
    String input;
    while (!exit) {
      input = inputs.nextLine().strip();
      
      // Provide possibility of searching for the number without the prefix, case-insensitive
      if (input.toLowerCase().startsWith(backend.getPrefix().toLowerCase())) {
        input = backend.getPrefix() + input;
        try {
          Bridge searched = (Bridge)backend.getBridge(input);
          currentBridge = searched;
          viewScreen(currentBridge);
          exit = true;
          continue;
        }
        catch (IllegalArgumentException invalidFormat) {
          System.out.println("Incorrect bridge number format.");
          continue;
        }
        catch (NoSuchElementException nearest) {
          String nearestNum = nearest.toString().substring(backend.getPrefix().length());
          System.out.println("Bridge number not found, nearest was " + nearestNum);
          continue;
        }
      }
      // Leave screen
      if (input.toLowerCase().equals("x")) {
        exit = true;
        continue;
      }
      // Any other inputs will be treated as a full bridge name
      else {
        try {
          Bridge searched = (Bridge)backend.getBridge(input);
          currentBridge = searched;
          viewScreen(currentBridge);
          exit = true;
          continue;
        }
        catch (IllegalArgumentException invalidFormat) {
          System.out.println("Incorrect bridge number format.");
          continue;
        }
        catch (NoSuchElementException nearest) {
          System.out.println("Bridge name not found, nearest was " + nearest);
          continue;
        }
      }
    }
    // No output for exit, as bridge viewer will run within this method.
  }
  
  private void locScreen() {
    System.out.println("Enter a city and 2-letter state abbreviation (separated by a comma) or a pair"
        + " of coordinates (separated by a space) to set a new reference location, \"r\" to reset the"
        + " reference location, or \"x\" to return to the main screen.");
    
    boolean exit = false;
    String input;
    
    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();
      
      // Input cases
      if (input.equals("r")) {
        coordsSet = false;
        lat = null;
        lon = null;
        System.out.println("Reference location reset.");
        exit = true;
        continue;
      }
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      else {
        // If there is a comma in the input and there are two non-whitespace characters after it,
        // it's a search for a city.
        if (input.contains(",") && input.substring(input.indexOf(',') + 1).strip().length() == 2) {
          // if the city search tree hasn't been generated, print that it's being generated
          if (!cityTreeGenerated) {
            System.out.println("Generating city search system, this may take a moment.");
            try {
              cityList = new CitySearch("City-Lat-Long.csv");
              cityTreeGenerated = true;
              System.out.println("Search system generated successfully.");
              String searchString = input.substring(0, input.indexOf(',')).strip() + ", " +
                  input.substring(input.indexOf(',') + 1).strip();
              try{
                double[] coords = cityList.getCityPosition(searchString);
                lat = coords[0];
                lon = coords[1];
                coordsSet = true;
                System.out.println("City coordinates found and set to reference location.");
                exit = true;
                continue;
              } catch (NoSuchElementException notFound) {
                System.out.println("City not found, enter a new command.");
                continue;
              }
            } catch (FileNotFoundException noFile) {
              System.out.println("Search system generation failed, city data file not found.  "
                  + "Enter a new command.");
              continue;
            } catch (DataFormatException badFormat) {
              System.out.println("Search system generation failed, city data formatting incorrect.  "
                  + "Enter a new command.");
              continue;
            }
          }
          else {
            String searchString = input.substring(0, input.indexOf(',')).strip() + ", " +
                input.substring(input.indexOf(',') + 1).strip();
            try{
              double[] coords = cityList.getCityPosition(searchString);
              lat = coords[0];
              lon = coords[1];
              coordsSet = true;
              System.out.println("City coordinates found and set to reference location.");
              exit = true;
              continue;
            } catch (NoSuchElementException notFound) {
              System.out.println("City not found, enter a new command.");
              continue;
            }
          }
          
        }
        // If not a city, coordinates will have at least one space between words.
        // If not, it's an invalid input of some sort.
        if (input.contains(" ")) {
          String coord1 = input.substring(0, input.indexOf(' ')).strip();
          String coord2 = input.substring(input.indexOf(' ')).strip();
          try {
            // Convert to doubles, will throw NumberFormatException if values are not doubles
            Double trialLat = Double.valueOf(coord1);
            Double trialLon = Double.valueOf(coord2);
            // If out of bounds of how lat/long values work, throw same exception as valueOf would
            if (trialLat.compareTo(-90.0) < 0 || trialLat.compareTo(90.0) > 0 || 
                trialLon.compareTo(-180.0) < 0 || trialLon.compareTo(180.0) > 0) {
              throw new NumberFormatException("Coordinate out of bounds.");
            }
            lat = trialLat;
            lon = trialLon;
            coordsSet = true;
            System.out.println("Coordinates set.");
            exit = true;
            continue;
          }
          catch (NumberFormatException invalid) {
            System.out.println("Invalid coordinates, try again.");
            continue;
          }
        }
        
      }
      // If we hit this point, input was invalid.
      System.out.println("Undefined command, try again.");
    }
  }
  
  private boolean locSearch() {
    if (coordsSet) {
      viewScreen((Bridge)backend.getNearestBridge(lat, lon));
      return true;
    }
    else {
      System.out.println("Location search requires a reference location to be set.  Use \"l\" to set one.");
      return false;
    }
  }
  
  private void viewScreen(Bridge toView) {
    int versionIndex = 0;
    printBridgeVersion(toView, versionIndex);
    System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge versions,"
        + " or \"x\" to return to the main screen.");
    
    boolean exit = false;
    String input;
    
    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();
      
      // Input cases
      if (input.equals("q")) {
        if (versionIndex-1 >= 0) {
          versionIndex--;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        }
        else {
          System.out.println("No known earlier version.");
        }
        continue;
      }
      if (input.equals("e")) {
        if (versionIndex+1 < toView.getNumVersions()) {
          versionIndex++;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        }
        else {
          System.out.println("No known later version.");
        }
        continue;
      }
      if (input.equals("a")) {
        Bridge previous = backend.getPrevious(toView);
        if (previous == null) {
          System.out.println("No previous bridge to view.");
        }
        else {
          toView = previous;
          versionIndex = 0;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        }
        continue;
      }
      if (input.equals("d")) {
        Bridge next = backend.getNext(toView);
        if (next == null) {
          System.out.println("No next bridge to view.");
        }
        else {
          toView = next;
          versionIndex = 0;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        }
        continue;
      }
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      // If we hit this point, input was invalid.
      System.out.println("Undefined command, try again.");
    }
    // Ensure that movements in tree are saved to return to from the main screen
    currentBridge = toView;
  }
  private void printBridgeVersion(Bridge toView, int version) {
    System.out.println(toView.getName());
    // Print dividing line under bridge name.
    for (int i = 0; i < toView.getName().length(); i++) {
      System.out.print("-");
    }
    System.out.print("\n");
    // Print coordinates, fancily formatted
    // Latitude will be degrees N/S depending on sign
    if (Double.compare(toView.getLat(), 0.0) >= 0) {
      System.out.printf("%.5f\u00B0 N, ", toView.getLat());
    }
    else {
      System.out.printf("%.5f\u00B0 S, ", Math.abs(toView.getLat()));
    }
    // Longitude will be degrees E/W depending on sign
    if (Double.compare(toView.getLon(), 0.0) >= 0) {
      System.out.printf("%.5f\u00B0 E%n", toView.getLon());
    }
    else {
      System.out.printf("%.5f\u00B0 W%n", Math.abs(toView.getLon()));
    }
    // Printing link to location on Rail Map of Southwest Wisconsin
    System.out.print("google.com/maps/d/u/0/viewer?mid=1NvinLluYr01qEZKTxV4iTA4ehAvf2ih5&ll=");
    System.out.printf("%.4f", toView.getLat());
    System.out.print("%2C");
    System.out.printf("%.4f", toView.getLon());
    System.out.println("&z=19");
    // If reference location set, print distance to bridge (in feet if under 1 mile)
    if (coordsSet) {
      System.out.print("Distance from reference location: ");
      if (Double.compare(backend.getDistance(lat, lon, toView), 1.0) < 0) {
        System.out.printf("%.0f'%n", backend.getDistance(lat, lon, toView)*5280.0);
      }
      else {
        System.out.printf("%.2f miles%n", backend.getDistance(lat, lon, toView));
      }
    }
    // Gap between universal and version-specific information
    System.out.println("");
    // System to print out the version currently being viewed.  The grammar will break at the 21st
    // iteration, but no bridges are known that go beyond 3 iterations.
    switch (version) {
      case 0: System.out.println("1st Known Iteration (of " + toView.getNumVersions() + ")");
              break;
      case 1: System.out.println("2nd Known Iteration (of " + toView.getNumVersions() + ")");
              break;
      case 2: System.out.println("3rd Known Iteration (of " + toView.getNumVersions() + ")");
              break;
      default: System.out.println((version+1) + "th Known Iteration (of " + toView.getNumVersions() + ")");
              break;
    }
    System.out.println("Built: " + toView.getVersion(version).getStartDate());
    if (toView.getVersion(version).isBridge()) {
      System.out.println("Type: Bridge");
    }
    else {
      System.out.println("Type: Culvert");
    }
    System.out.println("Fate: " + toView.getVersion(version).getFate()
        + " ("+ toView.getVersion(version).getEndDate() + ")");
    if (toView.getVersion(version).isBridge()) {
      BridgeVersion currVersion = (BridgeVersion) toView.getVersion(version);
      if (currVersion.getApproachLoc() == 'N') {
        System.out.println("Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
        System.out.println("Length: " + unknownHelper(currVersion.getLength()));
        System.out.println("Height: " + unknownHelper(currVersion.getHeight()));
        System.out.println("Spans: " + unknownHelper(currVersion.getSpans()));
      }
      else if (currVersion.getApproachLoc() == 'U') {
        System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
        System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
        System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
        System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
        System.out.println("Approaches: Unknown");
      }
      else if (currVersion.getApproachLoc() == 'L') {
        System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
        System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
        System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
        System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
        System.out.println("Low-Side Approach Type: " + unknownHelper(currVersion.getLAType()));
        System.out.println("Low-Side Approach Length: " + unknownHelper(currVersion.getLALength()));
        System.out.println("Low-Side Approach Height: " + unknownHelper(currVersion.getLAHeight()));
        System.out.println("Low-Side Approach Spans: " + unknownHelper(currVersion.getLASpans()));
      }
      else if (currVersion.getApproachLoc() == 'H') {
        System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
        System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
        System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
        System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
        System.out.println("High-Side Approach Type: " + unknownHelper(currVersion.getHAType()));
        System.out.println("High-Side Approach Length: " + unknownHelper(currVersion.getHALength()));
        System.out.println("High-Side Approach Height: " + unknownHelper(currVersion.getHAHeight()));
        System.out.println("High-Side Approach Spans: " + unknownHelper(currVersion.getHASpans()));
      }
      else if (currVersion.getApproachLoc() == 'B') {
        System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
        System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
        System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
        System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
        System.out.println("Low-Side Approach Type: " + unknownHelper(currVersion.getLAType()));
        System.out.println("Low-Side Approach Length: " + unknownHelper(currVersion.getLALength()));
        System.out.println("Low-Side Approach Height: " + unknownHelper(currVersion.getLAHeight()));
        System.out.println("Low-Side Approach Spans: " + unknownHelper(currVersion.getLASpans()));
        System.out.println("High-Side Approach Type: " + unknownHelper(currVersion.getHAType()));
        System.out.println("High-Side Approach Length: " + unknownHelper(currVersion.getHALength()));
        System.out.println("High-Side Approach Height: " + unknownHelper(currVersion.getHAHeight()));
        System.out.println("High-Side Approach Spans: " + unknownHelper(currVersion.getHASpans()));
      }
    }
    else {
      CulvertVersion currVersion = (CulvertVersion) toView.getVersion(version);
      System.out.println("Culvert Type: " + unknownHelper(currVersion.getSpecificType()));
      System.out.println("Length: " + unknownHelper(currVersion.getLength()));
      if (Double.compare(currVersion.getHeight(), -1.0) == 0) {
        System.out.println("Height: Unknown");
      }
      else {
        System.out.printf("Height: %.0f\"%n", currVersion.getHeight()*12.0);
      }
      if (Double.compare(currVersion.getWidth(), -1.0) == 0) {
        System.out.println("Width: Unknown");
      }
      else {
        System.out.printf("Width: %.0f\"%n", currVersion.getWidth()*12.0);
      }
    }  
  }
  private String unknownHelper (int input) {
    if (input == -1) {
      return "Unknown";
    }
    else {
      return Integer.toString(input);
    }
  }
  private String unknownHelper (double input) {
    if (Double.compare(input, -1.0) == 0) {
      return "Unknown";
    }
    else {
      return Double.toString(input) + "'";
    }
  }
  private String unknownHelper (String input) {
    if (input == null) {
      return "Unknown";
    }
    else {
      return input;
    }
  }
  
}
