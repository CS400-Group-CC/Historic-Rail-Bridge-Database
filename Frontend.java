// --== CS400 File Header Information ==--
// Name: Jeremy Peplinski
// Email: japeplinski@wisc.edu
// Team: CC (red)
// Role: Frontend Developer
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * A class to organize and run the UI for the rail bridge database, including extensive code for ASCII
 * art generation.
 * @author Jeremy Peplinski
 */
public class Frontend {
  // Class variables
  Backend backend;
  Scanner inputs;
  boolean foundError;

  int cols;
  boolean initASCII = false;
  TerminalPrinting linePrinter;
  BridgePrinting asciiGen;

  Double lat = null;
  Double lon = null;
  boolean coordsSet;
  boolean cityTreeGenerated;
  boolean showASCII;
  CitySearch cityList;
  Bridge currentBridge;

  /**
   * The main method to instantiate and run the frontend.
   * @param args Command-line arguments containing the bridge data file path
   */
  public static void main(String[] args) {
    Frontend frontend = new Frontend(args);
    frontend.run(frontend.backend);
  }

  /**
   * A no-arguments constructor for testing purposes, which does not set up ASCII mode.
   */
  public Frontend() {
    cityTreeGenerated = false;
    foundError = false;
    coordsSet = false;
    showASCII = false;
  }

  /**
   * A constructor for the Frontend, which uses the command line arguments to parse the provided data file
   * and tries to get the terminal dimensions for ASCII mode.
   * @param args Command-line arguments containing the bridge data file path
   */
  public Frontend(String[] args) {
    try {
      backend = new Backend(args);
    } catch (Exception error) {
      foundError = true;
      System.out.println("Error encountered in reading data set:\n" + error);
      System.out.println("Ensure that data file is correctly formatted.");
    }
    cityTreeGenerated = false;
    coordsSet = false;
    showASCII = false;

    try {
      String columnString;
      columnString = System.getenv("COLUMNS");
      cols = Integer.parseInt(columnString);
      linePrinter = new TerminalPrinting();
      asciiGen = new BridgePrinting();
      initASCII = true;
      if (cols < 20) {
        showASCII = false;
        System.out.println("Terminal width insufficient for ASCII mode to be enabled.\n");
      }
      else {
        showASCII = true;
      }
    } catch (Exception genFailed) {
      System.out.println("Terminal width data inaccessible, ASCII bridge representations are disabled.");
      System.out.println("Terminal width may be set manually with \"a\" on the home screen.\n");
      cols = 0;
      linePrinter = new TerminalPrinting();
      asciiGen = new BridgePrinting();
      initASCII = true;
      showASCII = false;
    }
  }

  /**
   * The system to actually begin running the UI, which is called from main.
   * @param backend The Backend object containing the data used for the database
   */
  public void run(Backend backend) {
    // If an exception was hit in generating the backend, return without doing anything.
    if (foundError) {
      return;
    }
    // For testing purposes (because my tests were implemented before understanding the necessary
    // logic)
    if (true) {
      this.backend = backend;
    }

    // Setup and introduction in the case of a correct dataset reading
    // NOTE: this relies on theoretical getRoot function
    currentBridge = (Bridge) backend.getRoot();
    // Welcome screen, only printed on startup.
    System.out.println("Welcome to the Historic Rail Bridge Database\n");
    // Enter main screen
    inputs = new Scanner(System.in);
    mainScreen();
  }

  /**
   * A method controlling the user interface of the program's main screen, where main screen information 
   * is printed and the core commands (such as search, location search, exit program) are entered.
   */
  private void mainScreen() {
    boolean quit = false;
    boolean reprint = true;
    String input;

    while (!quit) {
      if (reprint) {
        System.out.println("You are currently viewing data for:");
        System.out.println(backend.getRailroad() + " - " + backend.getRegion() + " Region.\n");
        // Top printouts, not reprinted if incorrect commands are entered.
        if (coordsSet) {
          System.out.print("Current reference location: ");
          if (lat.compareTo(0.0) >= 0) {
            System.out.printf("%.3f\u00B0 N, ", lat);
          } else {
            System.out.printf("%.3f\u00B0 S, ", Math.abs(lat));
          }
          // Longitude will be degrees E/W depending on sign
          if (lon.compareTo(0.0) >= 0) {
            System.out.printf("%.3f\u00B0 E%n", lon);
          } else {
            System.out.printf("%.3f\u00B0 W%n", Math.abs(lon));
          }
        }
        System.out
            .println("Enter your commands on the command line below.  \"x\" exits the program.");
        System.out.println("A full list of commands can be seen by typing \"?\" or \"help\".");
      }
      input = inputs.nextLine().strip().toLowerCase();

      // Input cases
      if (input.equals("?") || input.equals("help")) {
        helpScreen();
        reprint = true;
        continue;
      }
      if (input.equals("a")) {
        asciiScreen();
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
      System.out
          .println("Undefined command, enter \"?\" or \"help\" for a list of valid commands.");
      reprint = false;
    }
    System.out.println(
        "Thanks for using the Historic Rail Bridge Database, now get out there and find one!");
  }

  /**
   * A method controlling the user interface of the program's help screen, where help information about 
   * the main screen is printed, and where commands to return to the main screen or to view help 
   * information for specific commands are entered.
   */
  private void helpScreen() {

    // This variable controls whether we remain in the input-getting loop
    boolean exit = false;
    // This variable ensures that the main help screen is reprinted after viewing a sub-screen, but
    // not when an invalid command is entered.
    boolean reprint = true;
    String input;

    while (!exit) {
      if (reprint) {
        System.out.println(
            "Dataset: " + backend.getRailroad() + " - " + backend.getRegion() + " Region.\n");
        // If implemented, current ASCII mode state displayed here

        System.out.print("On the main screen, the following are valid commands.");
        System.out
            .println("  Further information on each command (other than the help screen and exit)"
                + " can be found by entering the command.\n");

        System.out.println("\"l\": Set or reset reference location");
        System.out.println("\"s\": Search by bridge name");
        System.out.println("\"ls\": Search for the nearest bridge to the set reference location");
        System.out.println("\"v\": View information for the last bridge visited");
        System.out.println("\"a\": View ASCII mode settings");
        System.out.println("\"?\" or \"help\": View help screen");
        System.out.println("\"x\": Exit the database program\n");
        
        System.out.println("To leave the help screen, press \"x\".");
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
      if (input.equals("a")) {
        helpA();
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

  /**
   * A method controlling the user interface of the program's search help screen, where help information 
   * about the search screen is printed, and where commands to return to the main help screen are entered.
   */
  private void helpS() {
    System.out.println("This screen allows the database of bridges to be searched by bridge name.  "
        + "Names may be entered in upper or lower case, with or without the prefix to the bridge's number."
        + "  If a searched name is incorrectly formatted, a new command will be prompted."
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

      // Exit command is the only valid input, but keeping consistency in only allowing this for
      // exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }

  /**
   * A method controlling the user interface of the program's location search help screen, where help 
   * information about the location search screen is printed, and where commands to return to the main 
   * help screen are entered.
   */
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

      // Exit command is the only valid input, but keeping consistency in only allowing this for
      // exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }

  /**
   * A method controlling the user interface of the program's location help screen, where help 
   * information about the location screen is printed, and where commands to return to the main help 
   * screen are entered.
   */
  private void helpL() {
    System.out
        .println("This screen allows a reference location to be set, which allows one to search "
            + "for the bridge nearest to this location, and to see the distance to any given bridge when "
            + "viewing them.  This also allows the existing location to be reset or replaced.  Command "
            + "formatting is shown below.\n");

    System.out.println("\"r\": Reset current reference location");
    System.out
        .println("\"[latitude] [longitude]\": Set reference location to the specified coordinates,"
            + " in decimal degrees");
    System.out
        .println("\"[city], [2-letter state abbreviation]\": Set reference location to the given"
            + " city, searched based on cities in the US with zip codes");
    System.out.println("\"x\": Return to main screen\n");

    System.out.println("Enter \"x\" to return to the main help screen.");

    boolean exit = false;
    String input;

    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();

      // Exit command is the only valid input, but keeping consistency in only allowing this for
      // exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }

  /**
   * A method controlling the user interface of the program's bridge viewer help screen, where help 
   * information about the bridge viewer screen is printed, and where commands to return to the main 
   * help screen are entered.
   */
  private void helpV() {
    System.out.println("This command opens the bridge viewer screen to the last bridge viewed.  "
        + "On startup, without a last bridge viewed the  \"average\" bridge is shown.  Getting into the "
        + "details, this \"average\" bridge is the root of the binary search tree used to store the "
        + "bridges, and is thus close to the median bridge number.");
    System.out.println(
        "The bridge viewer itself displays all known information on the first known version"
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

      // Exit command is the only valid input, but keeping consistency in only allowing this for
      // exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }

  /**
   * A method controlling the user interface of the program's ASCII mode help screen, where help 
   * information about the ASCII mode screen is printed, and where commands to return to the main help 
   * screen are entered.
   */
  private void helpA() {
    System.out.println("This screen allows the ASCII mode settings for the database to be modified."
        + "  ASCII mode allows basic representations of bridges to be generated from available data and"
        + " printed in the bridge viewer.  In order to do this accurately, the width of the terminal "
        + "must be known.  The program attempts to identify the terminal width on startup, but this is not"
        + " possible on all systems, and only reflects the width of the terminal at startup.  Thus, if "
        + "this cannot be identified or the terminal is resized, the printing of these ASCII "
        + "representations may break down.  This screen allows ASCII mode to be enabled and disabled, "
        + "and for the terminal width to be set manually.  Commands are shown below.\n");

    System.out.println("\"[terminal width]\": Set the width of the terminal");
    System.out.println("\"on\": Enable ASCII mode");
    System.out.println("\"off\": Disable ASCII mode");
    System.out.println("\"x\": Return to main screen\n");

    System.out.println("Enter \"x\" to return to the main help screen.");

    boolean exit = false;
    String input;

    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();

      // Exit command is the only valid input, but keeping consistency in only allowing this for
      // exit
      if (input.equals("x")) {
        exit = true;
        continue;
      }
      System.out.println("Undefined command, try again.");
    }
  }

  /**
   * A method controlling the ASCII mode screen, where information about the terminal width can be entered 
   * and ASCII mode can be enabled or disabled.
   */
  private void asciiScreen() {
    System.out.println("Enter \"on\" or \"off\" to change the ASCII mode, a terminal width to set "
        + "(in columns), or \"x\" to return to the main screen.");

    boolean exit = false;
    String input;

    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();

      // Input cases
      if (input.equals("on")) {
        if (cols == 0) {
          cols = 80;
          System.out.println("Number of columns not initialized, setting to default of 80.");
        }
        if (cols >= 20) {
          showASCII = true;
          System.out.println("ASCII mode enabled.");
        } else {
          System.out.println("Terminal size of " + cols + " columns does not support ASCII mode.");
        }
        continue;
      }
      if (input.equals("off")) {
        showASCII = false;
        System.out.println("ASCII mode disabled.");
        continue;
      }
      if (input.equals("x")) {
        exit = true;
        continue;

      }
      // Assume any other input is a terminal size
      else {
        try {
          int colsCandidate;
          colsCandidate = Integer.parseInt(input);
          if (colsCandidate > 0) {
            cols = colsCandidate;
            if (cols < 20) {
              showASCII = false;
              System.out.println("Terminal size set, insufficient for ASCII mode.");
            }
            else {
              System.out.println("Terminal size set.");
            }
            continue;
          } else {
            System.out.println("Invalid terminal size, try again.");
            continue;
          }
        } catch (NumberFormatException e) {
          // If not a valid integer, it was some other command
          System.out.println("Undefined command, try again.");
        }
      }
    }
  }

  /**
   * A method controlling the name search screen, where bridges in the database can be searched for by 
   * name or number.  A successful search jumps the user directly to the bridge viewer screen.
   */
  private void nameSearchScreen() {
    System.out.println(backend.getFormatHelp() + "\n");
    System.out.println("Enter a bridge name/number to search for below,"
        + " or \"x\" to return to the main screen.");

    boolean exit = false;
    String input;
    while (!exit) {
      input = inputs.nextLine().strip();

      // Leave screen
      if (input.toLowerCase().equals("x")) {
        exit = true;
        continue;
      }
      // Provide possibility of searching for the number without the prefix, case-insensitive
      if (!input.toLowerCase().startsWith(backend.getPrefix().toLowerCase())) {
        input = backend.getPrefix() + input;
        try {
          Bridge searched = (Bridge) backend.getBridge(input);
          currentBridge = searched;
          viewScreen(currentBridge);
          exit = true;
          continue;
        } catch (IllegalArgumentException invalidFormat) {
          System.out.println("Incorrect bridge number format.");
          continue;
        } catch (NoSuchElementException nearest) {
          String nearestNum = nearest.getMessage().substring(backend.getPrefix().length());
          System.out.println("Bridge number not found, nearest was " + nearestNum + ".");
          continue;
        }
      }
      // Any other inputs will be treated as a full bridge name
      else {
        try {
          Bridge searched = (Bridge) backend.getBridge(input);
          currentBridge = searched;
          viewScreen(currentBridge);
          exit = true;
          continue;
        } catch (IllegalArgumentException invalidFormat) {
          System.out.println("Incorrect bridge number format.");
          continue;
        } catch (NoSuchElementException nearest) {
          System.out.println("Bridge name not found, nearest was " + nearest.getMessage() + ".");
          continue;
        }
      }
    }
    // No output for exit, as bridge viewer will run within this method.
  }

  /**
   * A method controlling the location screen, where the reference location can be reset or a new 
   * reference location set (either as a pair of coordinates or by searching for a town name from known 
   * ZIP code coordinates).
   */
  private void locScreen() {
    System.out
        .println("Enter a city and 2-letter state abbreviation (separated by a comma) or a pair"
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
      } else {
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
              String searchString = input.substring(0, input.indexOf(',')).strip() + ", "
                  + input.substring(input.indexOf(',') + 1).strip();
              try {
                double[] coords = cityList.getCityPosition(searchString);
                lat = coords[0];
                lon = coords[1];
                coordsSet = true;
                System.out.println("City coordinates found and set to reference location.\n");
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
              System.out
                  .println("Search system generation failed, city data formatting incorrect.  "
                      + "Enter a new command.");
              continue;
            }
          } else {
            String searchString = input.substring(0, input.indexOf(',')).strip() + ", "
                + input.substring(input.indexOf(',') + 1).strip();
            try {
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
            if (trialLat.compareTo(-90.0) < 0 || trialLat.compareTo(90.0) > 0
                || trialLon.compareTo(-180.0) < 0 || trialLon.compareTo(180.0) > 0) {
              throw new NumberFormatException("Coordinate out of bounds.");
            }
            lat = trialLat;
            lon = trialLon;
            coordsSet = true;
            System.out.println("Coordinates set.");
            exit = true;
            continue;
          } catch (NumberFormatException invalid) {
            System.out.println("Invalid coordinates, try again.");
            continue;
          }
        }

      }
      // If we hit this point, input was invalid.
      System.out.println("Undefined command, try again.");
    }
  }

  /**
   * A method controlling location searches, which jumps to the bridge viewer screen for the nearest 
   * bridge found if a reference location is set, or does prints an error if no location is set.
   * @return true if a reference location is set, false otherwise
   */
  private boolean locSearch() {
    if (coordsSet) {
      viewScreen((Bridge) backend.getNearestBridge(lat, lon));
      return true;
    } else {
      System.out.println(
          "Location search requires a reference location to be set.  Use \"l\" to set one.");
      return false;
    }
  }

  /**
   * A method controlling the viewer screen, printing the initial version of the bridge and allowing the 
   * user to cycle through bridges or bridge versions.
   * @param toView the Bridge object to be viewed
   */
  private void viewScreen(Bridge toView) {
    int versionIndex = 0;
    printBridgeVersion(toView, versionIndex);
    System.out
        .println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge versions,"
            + " or \"x\" to return to the main screen.");

    boolean exit = false;
    String input;

    while (!exit) {
      input = inputs.nextLine().strip().toLowerCase();

      // Input cases
      if (input.equals("q")) {
        if (versionIndex - 1 >= 0) {
          versionIndex--;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        } else {
          System.out.println("No known earlier version.");
        }
        continue;
      }
      if (input.equals("e")) {
        if (versionIndex + 1 < toView.getNumVersions()) {
          versionIndex++;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        } else {
          System.out.println("No known later version.");
        }
        continue;
      }
      if (input.equals("a")) {
        Bridge previous = (Bridge) backend.getPrevious(toView);
        if (previous == null) {
          System.out.println("No previous bridge to view.");
        } else {
          toView = previous;
          versionIndex = 0;
          printBridgeVersion(toView, versionIndex);
          System.out.println("\nEnter \"a\"/\"d\" or \"q\"/\"e\" to cycle through bridges or bridge"
              + " versions, or \"x\" to return to the main screen.");
        }
        continue;
      }
      if (input.equals("d")) {
        Bridge next = (Bridge) backend.getNext(toView);
        if (next == null) {
          System.out.println("No next bridge to view.");
        } else {
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

  /**
   * A helper method for the viewer screen, which handles the printing of specified bridge versions.  The 
   * style of printing is dependent on whether ASCII mode is enabled (based on the showASCII variable).  
   * Handling of null Bridge objects or out-of-bounds version indexes is not explicitly provided.
   * @param toView the Bridge object to generate a representation for
   * @param version the version of toView to generate a representaiton for
   */
  private void printBridgeVersion(Bridge toView, int version) {
    if (showASCII) {
      // Universal information
      linePrinter.printCentered(toView.getName(), '/', "| ", " |", '\\', '=');
      linePrinter.printCentered("", '/', '\\', ' ');
      
      String toPrint = "";
      // Print coordinates, fancily formatted
      // Latitude will be degrees N/S depending on sign
      if (Double.compare(toView.getLat(), 0.0) >= 0) {
        toPrint += String.format("%.5f\u00B0 N, ", toView.getLat());
      } else {
        toPrint += String.format("%.5f\u00B0 S, ", Math.abs(toView.getLat()));
      }
      // Longitude will be degrees E/W depending on sign
      if (Double.compare(toView.getLon(), 0.0) >= 0) {
        toPrint += String.format("%.5f\u00B0 E", toView.getLon());
      } else {
        toPrint += String.format("%.5f\u00B0 W", Math.abs(toView.getLon()));
      }
      linePrinter.printIndented(toPrint, '/', '\\', false);
      linePrinter.printCentered("", '/', '\\', ' ');
      toPrint = "";

      toPrint +=
          String.format("google.com/maps/d/u/0/viewer?mid=1NvinLluYr01qEZKTxV4iTA4ehAvf2ih5&ll=");
      toPrint += String.format("%.4f", toView.getLat());
      toPrint += ("%2C");
      toPrint += String.format("%.4f", toView.getLon());
      toPrint += ("&z=19");
      linePrinter.printIndented(toPrint, '/', '\\', false);
      linePrinter.printCentered("", '/', '\\', ' ');
      toPrint = "";

      // If reference location set, print distance to bridge (in feet if under 1 mile)
      if (coordsSet) {
        toPrint += String.format("Distance from reference location: ");
        if (Double.compare(backend.getDistance(lat, lon, toView), 1.0) < 0) {
          toPrint += String.format("%.0f'", backend.getDistance(lat, lon, toView) * 5280.0);
        } else {
          toPrint += String.format("%.2f miles", backend.getDistance(lat, lon, toView));
        }
        linePrinter.printIndented(toPrint, '/', '\\', false);
        toPrint = "";
        linePrinter.printCentered("", '/', '\\', ' ');
      }

      BCVersionInterface bc = toView.getVersion(version);
      int ATHeight = asciiGen.getBridgeATHeight(bc, true, false, false);
      int BTHeight = asciiGen.getBridgeBTHeight(bc, true, false, false);

      // Printing divider with version information
      switch (version) {
        case 0:
          toPrint = ("1st Known Iteration (of " + toView.getNumVersions() + ")");
          break;
        case 1:
          toPrint = ("2nd Known Iteration (of " + toView.getNumVersions() + ")");
          break;
        case 2:
          toPrint = ("3rd Known Iteration (of " + toView.getNumVersions() + ")");
          break;
        default:
          toPrint = ((version + 1) + "th Known Iteration (of " + toView.getNumVersions() + ")");
          break;
      }
      linePrinter.printCentered(toPrint, '/', "| ", " |", '\\', '-');
      linePrinter.printCentered("", '/', '\\', ' ');
      toPrint = "";

      if (asciiGen.getBridgeCols(bc, true, false, false) + 2 > cols) {
        linePrinter.printCentered(
            "Window too small to view bridge, "
                + (asciiGen.getBridgeCols(bc, true, false, false) + 2) + " columns required.",
            '/', '\\', ' ');
      }
      else if (asciiGen.getBridgeCols(bc, true, false, false) <= 0) {
        linePrinter.printCentered("Not enough data to generate representation.", '/', '\\', ' ');
      }
      else {
        for (int i = ATHeight; i >= -BTHeight; i--) {
          linePrinter.printCentered(asciiGen.getBridgeRep(bc, i), '/', '\\', ' ');
        }
      }
      linePrinter.printCentered("", '/', '\\', ' ');

      if (bc.isBridge()) {
        toPrint = "Bridge";
      } else {
        toPrint = "Culvert";
      }
      linePrinter.printCentered(toPrint, '/', "| ", " |", '\\', '-');
      toPrint = "";

      toPrint = ("Built: " + toView.getVersion(version).getStartDate());
      linePrinter.printIndented(toPrint, '/', '\\', false);

      toPrint = ("Fate: " + toView.getVersion(version).getFate() + " ("
          + toView.getVersion(version).getEndDate() + ")");
      linePrinter.printIndented(toPrint, '/', '\\', false);

      if (toView.getVersion(version).isBridge()) {
        BridgeVersion currVersion = (BridgeVersion) toView.getVersion(version);
        if (currVersion.getApproachLoc() == 'N') {
          toPrint = ("Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Length: " + unknownHelper(currVersion.getLength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Height: " + unknownHelper(currVersion.getHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Spans: " + unknownHelper(currVersion.getSpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
        } else if (currVersion.getApproachLoc() == 'U') {
          toPrint = ("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Length: " + unknownHelper(currVersion.getLength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Height: " + unknownHelper(currVersion.getHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Spans: " + unknownHelper(currVersion.getSpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Approaches: Unknown");
          linePrinter.printIndented(toPrint, '/', '\\', false);
        } else if (currVersion.getApproachLoc() == 'L') {
          toPrint = ("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Length: " + unknownHelper(currVersion.getLength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Height: " + unknownHelper(currVersion.getHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Spans: " + unknownHelper(currVersion.getSpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Type: " + unknownHelper(currVersion.getLAType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Length: " + unknownHelper(currVersion.getLALength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Height: " + unknownHelper(currVersion.getLAHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Spans: " + unknownHelper(currVersion.getLASpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
        } else if (currVersion.getApproachLoc() == 'H') {
          toPrint = ("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Length: " + unknownHelper(currVersion.getLength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Height: " + unknownHelper(currVersion.getHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Spans: " + unknownHelper(currVersion.getSpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Type: " + unknownHelper(currVersion.getHAType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Length: " + unknownHelper(currVersion.getHALength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Height: " + unknownHelper(currVersion.getHAHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Spans: " + unknownHelper(currVersion.getHASpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
        } else if (currVersion.getApproachLoc() == 'B') {
          toPrint = ("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Length: " + unknownHelper(currVersion.getLength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Height: " + unknownHelper(currVersion.getHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Main Spans: " + unknownHelper(currVersion.getSpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Type: " + unknownHelper(currVersion.getLAType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Length: " + unknownHelper(currVersion.getLALength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Height: " + unknownHelper(currVersion.getLAHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("Low-Side Approach Spans: " + unknownHelper(currVersion.getLASpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Type: " + unknownHelper(currVersion.getHAType()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Length: " + unknownHelper(currVersion.getHALength()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Height: " + unknownHelper(currVersion.getHAHeight()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
          toPrint = ("High-Side Approach Spans: " + unknownHelper(currVersion.getHASpans()));
          linePrinter.printIndented(toPrint, '/', '\\', false);
        }
      } else {
        CulvertVersion currVersion = (CulvertVersion) toView.getVersion(version);
        toPrint = ("Culvert Type: " + unknownHelper(currVersion.getSpecificType()));
        linePrinter.printIndented(toPrint, '/', '\\', false);
        toPrint = ("Length: " + unknownHelper(currVersion.getLength()));
        linePrinter.printIndented(toPrint, '/', '\\', false);
        if (Double.compare(currVersion.getHeight(), -1.0) == 0) {
          toPrint = ("Height: Unknown");
          linePrinter.printIndented(toPrint, '/', '\\', false);
        } else {
          toPrint = String.format("Height: %.0f\"", currVersion.getHeight() * 12.0);
          linePrinter.printIndented(toPrint, '/', '\\', false);
        }
        if (Double.compare(currVersion.getWidth(), -1.0) == 0) {
          toPrint = ("Width: Unknown");
          linePrinter.printIndented(toPrint, '/', '\\', false);
        } else {
          toPrint = String.format("Width: %.0f\"", currVersion.getWidth() * 12.0);
          linePrinter.printIndented(toPrint, '/', '\\', false);
        }
      }
      linePrinter.printCentered("", '/', '\\', '=');
    }

    else {
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
      } else {
        System.out.printf("%.5f\u00B0 S, ", Math.abs(toView.getLat()));
      }
      // Longitude will be degrees E/W depending on sign
      if (Double.compare(toView.getLon(), 0.0) >= 0) {
        System.out.printf("%.5f\u00B0 E%n", toView.getLon());
      } else {
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
          System.out.printf("%.0f'%n", backend.getDistance(lat, lon, toView) * 5280.0);
        } else {
          System.out.printf("%.2f miles%n", backend.getDistance(lat, lon, toView));
        }
      }
      // Gap between universal and version-specific information
      System.out.println("");

      // System to print out the version currently being viewed. The grammar will break at the 21st
      // iteration, but no bridges are known that go beyond 3 iterations.
      switch (version) {
        case 0:
          System.out.println("1st Known Iteration (of " + toView.getNumVersions() + ")");
          break;
        case 1:
          System.out.println("2nd Known Iteration (of " + toView.getNumVersions() + ")");
          break;
        case 2:
          System.out.println("3rd Known Iteration (of " + toView.getNumVersions() + ")");
          break;
        default:
          System.out
              .println((version + 1) + "th Known Iteration (of " + toView.getNumVersions() + ")");
          break;
      }
      System.out.println("Built: " + toView.getVersion(version).getStartDate());
      if (toView.getVersion(version).isBridge()) {
        System.out.println("Type: Bridge");
      } else {
        System.out.println("Type: Culvert");
      }
      System.out.println("Fate: " + toView.getVersion(version).getFate() + " ("
          + toView.getVersion(version).getEndDate() + ")");
      if (toView.getVersion(version).isBridge()) {
        BridgeVersion currVersion = (BridgeVersion) toView.getVersion(version);
        if (currVersion.getApproachLoc() == 'N') {
          System.out.println("Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          System.out.println("Length: " + unknownHelper(currVersion.getLength()));
          System.out.println("Height: " + unknownHelper(currVersion.getHeight()));
          System.out.println("Spans: " + unknownHelper(currVersion.getSpans()));
        } else if (currVersion.getApproachLoc() == 'U') {
          System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
          System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
          System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
          System.out.println("Approaches: Unknown");
        } else if (currVersion.getApproachLoc() == 'L') {
          System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
          System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
          System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
          System.out.println("Low-Side Approach Type: " + unknownHelper(currVersion.getLAType()));
          System.out
              .println("Low-Side Approach Length: " + unknownHelper(currVersion.getLALength()));
          System.out
              .println("Low-Side Approach Height: " + unknownHelper(currVersion.getLAHeight()));
          System.out.println("Low-Side Approach Spans: " + unknownHelper(currVersion.getLASpans()));
        } else if (currVersion.getApproachLoc() == 'H') {
          System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
          System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
          System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
          System.out.println("High-Side Approach Type: " + unknownHelper(currVersion.getHAType()));
          System.out
              .println("High-Side Approach Length: " + unknownHelper(currVersion.getHALength()));
          System.out
              .println("High-Side Approach Height: " + unknownHelper(currVersion.getHAHeight()));
          System.out
              .println("High-Side Approach Spans: " + unknownHelper(currVersion.getHASpans()));
        } else if (currVersion.getApproachLoc() == 'B') {
          System.out.println("Main Bridge Type: " + unknownHelper(currVersion.getSpecificType()));
          System.out.println("Main Length: " + unknownHelper(currVersion.getLength()));
          System.out.println("Main Height: " + unknownHelper(currVersion.getHeight()));
          System.out.println("Main Spans: " + unknownHelper(currVersion.getSpans()));
          System.out.println("Low-Side Approach Type: " + unknownHelper(currVersion.getLAType()));
          System.out
              .println("Low-Side Approach Length: " + unknownHelper(currVersion.getLALength()));
          System.out
              .println("Low-Side Approach Height: " + unknownHelper(currVersion.getLAHeight()));
          System.out.println("Low-Side Approach Spans: " + unknownHelper(currVersion.getLASpans()));
          System.out.println("High-Side Approach Type: " + unknownHelper(currVersion.getHAType()));
          System.out
              .println("High-Side Approach Length: " + unknownHelper(currVersion.getHALength()));
          System.out
              .println("High-Side Approach Height: " + unknownHelper(currVersion.getHAHeight()));
          System.out
              .println("High-Side Approach Spans: " + unknownHelper(currVersion.getHASpans()));
        }
      } else {
        CulvertVersion currVersion = (CulvertVersion) toView.getVersion(version);
        System.out.println("Culvert Type: " + unknownHelper(currVersion.getSpecificType()));
        System.out.println("Length: " + unknownHelper(currVersion.getLength()));
        if (Double.compare(currVersion.getHeight(), -1.0) == 0) {
          System.out.println("Height: Unknown");
        } else {
          System.out.printf("Height: %.0f\"%n", currVersion.getHeight() * 12.0);
        }
        if (Double.compare(currVersion.getWidth(), -1.0) == 0) {
          System.out.println("Width: Unknown");
        } else {
          System.out.printf("Width: %.0f\"%n", currVersion.getWidth() * 12.0);
        }
      }
    }
  }

  /**
   * A helper method for systems of noting unknown information, converting an input value of -1 to a String 
   * containing "Unknown" or otherwise passing the toString version of the input along.
   * @param input an int-representation piece of data to be printed
   * @return a String containing the appropriate toString version of this input
   */
  private String unknownHelper(int input) {
    if (input == -1) {
      return "Unknown";
    } else {
      return Integer.toString(input);
    }
  }

  /**
   * A helper method for systems of noting unknown information, converting an input value of -1.0 to a 
   * String containing "Unknown" or otherwise passing the toString version of the input along.
   * @param input a double-representation piece of data to be printed
   * @return a String containing the appropriate toString version of this input
   */
  private String unknownHelper(double input) {
    if (Double.compare(input, -1.0) == 0) {
      return "Unknown";
    } else {
      return Double.toString(input) + "'";
    }
  }

  /**
   * A helper method for systems of noting unknown information, converting a null input to a String 
   * containing "Unknown" or otherwise passing the input along.
   * @param input a String-representation piece of data to be printed
   * @return a String containing the appropriate text for this input
   */
  private String unknownHelper(String input) {
    if (input == null) {
      return "Unknown";
    } else {
      return input;
    }
  }

  /**
   * An inner class designed to handle ASCII-art representations of bridges, used in the viewer screen 
   * when ASCII mode is enabled.
   * @author Jeremy Peplinski
   */
  private class BridgePrinting {
    // Universal variables for referencing scaling, in case I want to change this.
    double charWidth = 5.0;
    double charHeight = 10.0;


    /**
     * A method to get the number of columns necessary for printing the given BCVersion object.  This can 
     * be calculated for the full length of the bridge, or just an approach or the main span(s).
     * @param input the BCVersion to have a length calculated
     * @param getFullLen whether or not the full length of the bridge should be calculated
     * @param getMainSpan whether or not the main span's length should be calculated (if getFullLen is 
     * false)
     * @param getLowApp whether or not the low-side approach's length should be calculated (if getFullLen 
     * and getMainSpan are false), calculating the high-side approach's length otherwise.
     * @return the number of columns the given part of the bridge representation will require
     */
    public int getBridgeCols(BCVersionInterface input, boolean getFullLen, boolean getMainSpan,
        boolean getLowApp) {
      // return number of columns necessary, or -1 if not able to be computed
      if (input.isCulvert()) {
        return 15;
      } else {
        BridgeVersion b = (BridgeVersion) input;
        int mainSpan = (int) (Math.ceil(b.getLength() / charWidth));
        int LASpan = (int) (Math.ceil(b.getLALength() / charWidth));
        int HASpan = (int) (Math.ceil(b.getHALength() / charWidth));
        if (mainSpan == 0) {
          return -1;
        } else if (getFullLen) {
          return mainSpan + LASpan + HASpan + 10;
        } else if (getMainSpan) {
          return mainSpan;
        } else if (getLowApp) {
          return LASpan;
        } else {
          return HASpan;
        }
      }
    }

    /**
     * A method to get the number of rows necessary (above track height) for printing the given BCVersion 
     * object.  This can be calculated for the full length of the bridge, or just an approach or the 
     * main span(s).
     * @param input the BCVersion to have a height calculated
     * @param getFullLen whether or not the height of the full bridge should be calculated
     * @param getMainSpan whether or not the main span's height should be calculated (if getFullLen is 
     * false)
     * @param getLowApp whether or not the low-side approach's height should be calculated (if getFullLen 
     * and getMainSpan are false), calculating the high-side approach's height otherwise.
     * @return the number of rows above track level the given part of the bridge representation will 
     * require
     */
    public int getBridgeATHeight(BCVersionInterface input, boolean getFullLen, boolean getMainSpan,
        boolean getLowApp) {
      // return number of rows necessary for printing above track, or -1 if not able to be computed
      if (input.isCulvert()) {
        return 0;
      } else {
        int reqHeight = 0;
        int reqLAHeight = 0;
        int reqHAHeight = 0;
        BridgeVersion b = (BridgeVersion) input;
        String t = b.getSpecificType();

        // Main span calculations
        if (getFullLen || getMainSpan) {
          // Unknown case is where we don't know bridge length, which means we likely don't the info
          // to check how tall the representation will need to be
          if (b.getLength() == -1.0) {
            return -1;
          }
          // Special cases are small girders, large girders, square girders
          // Small giders first
          if ((t.contains("P.R.S.") && !t.contains("T.P. P.R.S.")) || t.contains("T.P.G.")) {
            // under 5', we only get one character and can't generate any reasonable top bit
            if (((Double) b.getLength()).compareTo(charWidth) <= 0) {
              reqHeight = 0;
            }
            // over 5', we get at least two chars and can print a 1-tall top bit
            else {
              reqHeight = 1;
            }
            // over 10', we get at least 3 chars and can print a 2-tall top bit
            if (((Double) b.getLength()).compareTo(2 * charWidth) > 0) {
              reqHeight = 2;
            }
          }
          // Large girders
          else if (t.contains("T.R.S.")) {
            // under 5', we only get one character and can't generate any reasonable top bit
            if (((Double) b.getLength()).compareTo(charWidth) <= 0) {
              reqHeight = 0;
            }
            // over 5', we print a very short small girder
            else {
              reqHeight = 1;
            }
            // over 10', this now gets a top line
            if (((Double) b.getLength()).compareTo(3 * charWidth) > 0) {
              reqHeight = 2;
            }
            // over 15', we get at least 4 chars and can print a proper large girder
            if (((Double) b.getLength()).compareTo(3 * charWidth) > 0) {
              reqHeight = 3;
            }
          }
          // Box girders
          else if (t.contains("T.P. P.R.S.") || t.contains("T.PIN.")) {
            // under 10', we only get two characters and can't generate any reasonable top bit
            if (((Double) b.getLength()).compareTo(2 * charWidth) <= 0) {
              reqHeight = 0;
            }
            // over this, we can print a full-height box girder
            else {
              reqHeight = 3;
            }
          }
          // There are other bridge types, but we can't identify literally every one. The rest of
          // the
          // identified types don't need above-track lines, or are unidentified and going to be
          // printed
          // as pile bridges
          else {
            reqHeight = 0;
          }
        }

        // Low approach calculations
        if (getFullLen || (!getMainSpan && getLowApp)) {
          // Now it's time for approaches, which will be very similar.
          if (b.getApproachLoc() == 'L' || b.getApproachLoc() == 'B') {
            t = b.getLAType();
            // Small girders first
            if ((t.contains("P.R.S.") && !t.contains("T.P. P.R.S.")) || t.contains("T.P.G.")) {
              // under 5', we only get one character and can't generate any reasonable top bit
              if (((Double) b.getLALength()).compareTo(charWidth) <= 0) {
                reqLAHeight = 0;
              }
              // over 5', we get at least two chars and can print a 1-tall top bit
              else {
                reqLAHeight = 1;
              }
              // over 10', we get at least 3 chars and can print a 2-tall top bit
              if (((Double) b.getLALength()).compareTo(2 * charWidth) > 0) {
                reqLAHeight = 2;
              }
            }
            // Large girders
            else if (t.contains("T.R.S.")) {
              // under 5', we only get one character and can't generate any reasonable top bit
              if (((Double) b.getLALength()).compareTo(charWidth) <= 0) {
                reqLAHeight = 0;
              }
              // over 5', we print a very short small girder
              else {
                reqLAHeight = 1;
              }
              // over 10', this now gets a top line
              if (((Double) b.getLALength()).compareTo(3 * charWidth) > 0) {
                reqLAHeight = 2;
              }
              // over 15', we get at least 4 chars and can print a proper large girder
              if (((Double) b.getLALength()).compareTo(3 * charWidth) > 0) {
                reqLAHeight = 3;
              }
            }
            // Box girders
            else if (t.contains("T.P. P.R.S.") || t.contains("T.PIN.")) {
              // under 10', we only get two characters and can't generate any reasonable top bit
              if (((Double) b.getLALength()).compareTo(2 * charWidth) <= 0) {
                reqLAHeight = 0;
              }
              // over this, we can print a full-height box girder
              else {
                reqLAHeight = 3;
              }
            }
            // There are other bridge types, but we can't identify literally every one. The rest of
            // the
            // identified types don't need above-track lines, or are unidentified and going to be
            // printed
            // as pile bridges
            else {
              reqLAHeight = 0;
            }
          }
        }

        if (getFullLen || (!getMainSpan && !getLowApp)) {
          if (b.getApproachLoc() == 'H' || b.getApproachLoc() == 'B') {
            t = b.getHAType();
            // Small giders first
            if ((t.contains("P.R.S.") && !t.contains("T.P. P.R.S.")) || t.contains("T.P.G.")) {
              // under 5', we only get one character and can't generate any reasonable top bit
              if (((Double) b.getHALength()).compareTo(charWidth) <= 0) {
                reqHAHeight = 0;
              }
              // over 5', we get at least two chars and can print a 1-tall top bit
              else {
                reqHAHeight = 1;
              }
              // over 10', we get at least 3 chars and can print a 2-tall top bit
              if (((Double) b.getHALength()).compareTo(2 * charWidth) > 0) {
                reqHAHeight = 2;
              }
            }
            // Large girders
            else if (t.contains("T.R.S.")) {
              // under 5', we only get one character and can't generate any reasonable top bit
              if (((Double) b.getHALength()).compareTo(charWidth) <= 0) {
                reqHAHeight = 0;
              }
              // over 5', we print a very short small girder
              else {
                reqHAHeight = 1;
              }
              // over 10', this now gets a top line
              if (((Double) b.getHALength()).compareTo(3 * charWidth) > 0) {
                reqHAHeight = 2;
              }
              // over 15', we get at least 4 chars and can print a proper large girder
              if (((Double) b.getHALength()).compareTo(3 * charWidth) > 0) {
                reqHAHeight = 3;
              }
            }
            // Box girders
            else if (t.contains("T.P. P.R.S.") || t.contains("T.PIN.")) {
              // under 10', we only get two characters and can't generate any reasonable top bit
              if (((Double) b.getHALength()).compareTo(2 * charWidth) <= 0) {
                reqHAHeight = 0;
              }
              // over this, we can print a full-height box girder
              else {
                reqHAHeight = 3;
              }
            }
            // There are other bridge types, but we can't identify literally every one. The rest of
            // the
            // identified types don't need above-track lines, or are unidentified and going to be
            // printed
            // as pile bridges
            else {
              reqHAHeight = 0;
            }
          }
        }

        if (getFullLen) {
          reqHeight = Math.max(reqHeight, reqLAHeight);
          reqHeight = Math.max(reqHeight, reqHAHeight);
          return reqHeight;
        } else if (getMainSpan) {
          return reqHeight;
        } else if (getLowApp) {
          return reqLAHeight;
        } else {
          return reqHAHeight;
        }
      }
    }

    /**
     * A method to get the number of rows necessary (below track height) for printing the given BCVersion 
     * object.  This can be calculated for the full length of the bridge, or just an approach or the 
     * main span(s).
     * @param input the BCVersion to have a height calculated
     * @param getFullLen whether or not the height of the full bridge should be calculated
     * @param getMainSpan whether or not the main span's height should be calculated (if getFullLen is 
     * false)
     * @param getLowApp whether or not the low-side approach's height should be calculated (if getFullLen 
     * and getMainSpan are false), calculating the high-side approach's height otherwise.
     * @return the number of rows below track level the given part of the bridge representation will 
     * require
     */
    public int getBridgeBTHeight(BCVersionInterface input, boolean getFullHeight,
        boolean getMainSpan, boolean getLowApp) {
      // return number of rows necessary for printing below track, or -1 if not able to be computed
      // Culverts have fixed ASCII representations, both of which only need one line below track
      // level
      if (input.isCulvert()) {
        return 1;
      }
      // Bridges require a known height or (if unavailable) a calculated height based on specific
      // type
      else {
        BridgeVersion b = (BridgeVersion) input;

        int reqMainHeight = 0;
        int reqLAHeight = 0;
        int reqHAHeight = 0;

        // Determine required height for main span
        if (getFullHeight || getMainSpan) {
          // If height is known, this is simple
          if (Double.compare(b.getHeight(), -1.0) != 0) {
            reqMainHeight = (int) Math.ceil(b.getHeight() / charHeight);
          } else {
            // Unknown case is where we don't know bridge length, which means we likely don't the
            // info
            // to generate a reasonable approximation
            if (b.getLength() == -1.0) {
              return -1;
            }
            // Otherwise, we should be able to guess heights of bridges from lengths and types.
            Double refHeight;
            // In all cases, a main span will set the baseline required height
            // General idea is that a bridge <=20' long is only roughly 10' tall, otherwise assume
            // 20'
            if (((Double) b.getLength()).compareTo(10.0) <= 0) {
              refHeight = 10.0;
            } else {
              refHeight = 20.0;
            }
            // Some special types of bridges tend to be taller than this for a proper representation
            String t = b.getSpecificType();

            // Overhead bridges require clearance for the trains, roughly 22' at the shortest, so 3
            // chars
            if (t.contains("O.H.")) {
              refHeight = 22.0;
            }

            // under-track girder bridges need at least 2 chars (20')
            if (t.contains("D.P.G.") || t.contains("D.R.L.")) {
              // and if large under-track, need at least 3 and look much better at 4 (40')
              if (((Double) b.getLength()).compareTo(40.0) > 0) {
                refHeight = 40.0;
              } else {
                refHeight = Math.max(20.0, refHeight);
              }
            }
            reqMainHeight = (int) Math.ceil(refHeight / charHeight);
          }
        }

        // determine required height for low approach
        if (getFullHeight || (!getMainSpan && getLowApp)) {
          if (Double.compare(b.getLAHeight(), -1.0) != 0) {
            reqLAHeight = (int) Math.ceil(b.getLAHeight() / charHeight);
          } else {
            Double refLAHeight = 0.0;
            if (b.getApproachLoc() == 'L' || b.getApproachLoc() == 'B') {
              if (((Double) b.getLALength()).compareTo(10.0) <= 0) {
                refLAHeight = 10.0;
              } else {
                refLAHeight = 20.0;
              }
              // Some special types of bridges tend to be taller than this for a proper
              // representation
              String t = b.getLAType();

              // Overhead bridges require clearance for the trains, roughly 22' at the shortest, so
              // 3 chars
              if (t.contains("O.H.")) {
                refLAHeight = 22.0;
              }

              // under-track girder bridges need at least 2 chars (20')
              if (t.contains("D.P.G.") || t.contains("D.R.L.")) {
                // and if large under-track, need at least 3 and look much better at 4 (40')
                if (((Double) b.getLALength()).compareTo(40.0) > 0) {
                  refLAHeight = 40.0;
                } else {
                  refLAHeight = Math.max(20.0, refLAHeight);
                }
              }
            }
            reqLAHeight = (int) Math.ceil(refLAHeight / charHeight);
          }
        }

        // same for approach
        if (getFullHeight || (!getMainSpan && !getLowApp)) {
          if (Double.compare(b.getHAHeight(), -1.0) != 0) {
            reqHAHeight = (int) Math.ceil(b.getHAHeight() / charHeight);
          } else {
            Double refHAHeight = 0.0;
            if (b.getApproachLoc() == 'H' || b.getApproachLoc() == 'B') {
              if (((Double) b.getHALength()).compareTo(10.0) <= 0) {
                refHAHeight = 10.0;
              } else {
                refHAHeight = 20.0;
              }
              // Some special types of bridges tend to be taller than this for a proper
              // representation
              String t = b.getHAType();

              // Overhead bridges require clearance for the trains, roughly 22' at the shortest, so
              // 3 chars
              if (t.contains("O.H.")) {
                refHAHeight = 22.0;
              }

              // under-track girder bridges need at least 2 chars (20')
              if (t.contains("D.P.G.") || t.contains("D.R.L.")) {
                // and if large under-track, need at least 3 and look much better at 4 (40')
                if (((Double) b.getHALength()).compareTo(40.0) > 0) {
                  refHAHeight = 40.0;
                } else {
                  refHAHeight = Math.max(20.0, refHAHeight);
                }
              }
            }
            reqHAHeight = (int) Math.ceil(refHAHeight / charHeight);
          }
        }
        if (getFullHeight) {
          return Math.max(reqMainHeight, Math.max(reqLAHeight, reqHAHeight));
        } else if (getMainSpan) {
          return reqMainHeight;
        } else if (getLowApp) {
          return reqLAHeight;
        } else {
          return reqHAHeight;
        }
      }
    }

    /**
     * The central method for getting the ASCII representation of a bridge, which returns the necessary 
     * String for the representation at the given row number.  Row 0 is defined as track/road level, with 
     * positive numbers being above the track/road and negative below.
     * @param input the BCVersion to get an ASCII representation of
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation for the given bridge at the given row number.  If 
     * the row number is outside of what is required, the returned string will contain the appropriate 
     * number of spaces to match the length of other rows.
     */
    public String getBridgeRep(BCVersionInterface input, int rowNum) {
      // return a string for the given bridge's ascii rep at the given row (0 being track height)
      // Note that this won't handle non-printable cases - that's the viewer's job

      // if we're given a row beyond the bounds of what will have text, return an appropriately-long
      // string of spaces
      if (rowNum > getBridgeATHeight(input, true, false, false)
          || rowNum < -getBridgeBTHeight(input, true, false, false)) {
        String returnVal = "";
        for (int i = 0; i < getBridgeCols(input, true, false, false); i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      // Starting with culverts, which are of fixed design
      if (input.isCulvert()) {
        if (rowNum == 0) {
          return "---------------";
        } else {
          CulvertVersion c = (CulvertVersion) input;
          // Box culverts are going to be treated as the default here, so we'll make an array of all
          // known
          // ways to refer to a pipe and check if the specific type contains that. If so, we'll
          // return
          // the pipe representation, otherwise we'll return the box culvert representation
          String[] pipeNames = {"Con. Pipe", "conc. Pipe", "Conc. P.", "Conc. Pipe", "C.I.P.",
              "V.P.C.", "Vit. Pipe", "Concr. P.", "V.S.P.", "W.I.P."};
          for (int i = 0; i < pipeNames.length; i++) {
            if (c.getSpecificType().contains(pipeNames[i])) {
              return "       o       ";
            }
          }
          return "      [ ]      ";
        }

      }

      // Otherwise, we've got a bridge.
      else {
        BridgeVersion b = (BridgeVersion) input;
        String returnVal = "";
        boolean hasApproaches = false;

        // Check if the bridge has approaches
        switch (b.getApproachLoc()) {
          case 'L':
          case 'H':
          case 'B':
            hasApproaches = true;
            break;
          default:
            break;
        }
        boolean typeFound = false;
        int length;
        int height;

        if (rowNum > 0) {
          returnVal += "     ";
        } else if (rowNum < 0) {
          returnVal += "    ]";
        } else {
          returnVal += "-----";
        }

        if (hasApproaches && (b.getApproachLoc() == 'L' || b.getApproachLoc() == 'B')) {

          // Do same as for main span (except for OH)
          typeFound = false;
          length = getBridgeCols(b, false, false, true);
          height = getBridgeBTHeight(b, false, false, true);

          // We need to search through bridge types from known names
          // Starting with square trusses, which use a phrase from other bridge types
          String[] squareTrussNames = {"T.P. P.R.S.", "T.PIN."};
          for (int i = 0; i < squareTrussNames.length; i++) {
            if (b.getLAType().contains(squareTrussNames[i])) {
              typeFound = true;
              returnVal += genSquareTruss(length, height, b.getHASpans(), false, true,
                  b.getApproachLoc(), rowNum);
              break;
            }
          }
          // Large trusses
          if (!typeFound) {
            String[] largeTrussNames = {"T.R.S."};
            for (int i = 0; i < largeTrussNames.length; i++) {
              if (b.getLAType().contains(largeTrussNames[i])) {
                typeFound = true;
                returnVal += genLargeTruss(length, height, b.getLASpans(), false, true,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Small trusses
          if (!typeFound) {
            String[] smallTrussNames = {"P.R.S.", "T.P.G."};
            for (int i = 0; i < smallTrussNames.length; i++) {
              if (b.getLAType().contains(smallTrussNames[i])) {
                typeFound = true;
                returnVal += genSmallTruss(length, height, b.getLASpans(), false, true,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Inverted square trusses
          if (!typeFound) {
            String[] inverseSquareTrussNames = {"D.P.G.", "D.R.L."};
            for (int i = 0; i < inverseSquareTrussNames.length; i++) {
              if (b.getLAType().contains(inverseSquareTrussNames[i])) {
                typeFound = true;
                returnVal += genInverseSquareTruss(length, height, b.getLASpans(), false, true,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Inverted standard trusses
          if (!typeFound) {
            String[] inverseTrussNames = {"D.PIN."};
            for (int i = 0; i < inverseTrussNames.length; i++) {
              if (b.getLAType().contains(inverseTrussNames[i])) {
                typeFound = true;
                returnVal += genInverseTruss(length, height, b.getLASpans(), false, true,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // I-beams
          if (!typeFound) {
            String[] IBNames = {"I.B."};
            for (int i = 0; i < IBNames.length; i++) {
              if (b.getLAType().contains(IBNames[i])) {
                typeFound = true;
                returnVal +=
                    genIB(length, height, b.getLASpans(), false, true, b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Else, we'll assume it's something pile bridge-like
          if (!typeFound) {
            returnVal +=
                genPB(length, height, b.getHASpans(), false, true, b.getApproachLoc(), rowNum);
          }
        }
        // Main spans will always be present
        // We need to search through bridge types from known names
        // Starting with square trusses, which use a phrase from other bridge types

        typeFound = false;
        length = getBridgeCols(b, false, true, false);
        height = getBridgeBTHeight(b, false, true, false);

        String[] squareTrussNames = {"T.P. P.R.S.", "T.PIN."};
        for (int i = 0; i < squareTrussNames.length; i++) {
          if (b.getSpecificType().contains(squareTrussNames[i])) {
            typeFound = true;
            returnVal += genSquareTruss(length, height, b.getSpans(), true, false,
                b.getApproachLoc(), rowNum);
            break;
          }
        }
        // Large trusses
        if (!typeFound) {
          String[] largeTrussNames = {"T.R.S."};
          for (int i = 0; i < largeTrussNames.length; i++) {
            if (b.getSpecificType().contains(largeTrussNames[i])) {
              typeFound = true;
              returnVal += genLargeTruss(length, height, b.getSpans(), true, false,
                  b.getApproachLoc(), rowNum);
              break;
            }
          }
        }
        // Small trusses
        if (!typeFound) {
          String[] smallTrussNames = {"P.R.S.", "T.P.G."};
          for (int i = 0; i < smallTrussNames.length; i++) {
            if (b.getSpecificType().contains(smallTrussNames[i])) {
              typeFound = true;
              returnVal += genSmallTruss(length, height, b.getSpans(), true, false,
                  b.getApproachLoc(), rowNum);
              break;
            }
          }
        }
        // Inverted square trusses
        if (!typeFound) {
          String[] inverseSquareTrussNames = {"D.P.G.", "D.R.L."};
          for (int i = 0; i < inverseSquareTrussNames.length; i++) {
            if (b.getSpecificType().contains(inverseSquareTrussNames[i])) {
              typeFound = true;
              returnVal += genInverseSquareTruss(length, height, b.getSpans(), true, false,
                  b.getApproachLoc(), rowNum);
              break;
            }
          }
        }
        // Inverted standard trusses
        if (!typeFound) {
          String[] inverseTrussNames = {"D.PIN."};
          for (int i = 0; i < inverseTrussNames.length; i++) {
            if (b.getSpecificType().contains(inverseTrussNames[i])) {
              typeFound = true;
              returnVal += genInverseTruss(length, height, b.getSpans(), true, false,
                  b.getApproachLoc(), rowNum);
              break;
            }
          }
        }
        // I-beams
        if (!typeFound) {
          String[] IBNames = {"I.B."};
          for (int i = 0; i < IBNames.length; i++) {
            if (b.getSpecificType().contains(IBNames[i])) {
              typeFound = true;
              returnVal +=
                  genIB(length, height, b.getSpans(), true, false, b.getApproachLoc(), rowNum);
              break;
            }
          }
        }
        // Overhead
        if (!typeFound) {
          if (b.getSpecificType().contains("O.H.")) {
            String[] HwyNames = {"HWY", "Highway"};
            for (int i = 0; i < HwyNames.length; i++) {
              if (b.getSpecificType().contains(HwyNames[i])) {
                typeFound = true;
                if (rowNum == 0) {
                  // This is the one case where our leading/trailing road bed isn't track, so
                  // explicitly
                  // set the returnVal to the full thing and return directly
                  returnVal =
                      "_____" + genOH(length, height, b.getSpans(), false, rowNum) + "_____";
                  return returnVal;
                } else {
                  returnVal += genOH(length, height, b.getSpans(), false, rowNum);
                }
                break;
              }
            }
            // If it didn't get caught by highway terms, we'll assume it's an overhead rail bridge
            if (!typeFound) {
              returnVal += genOH(length, height, b.getSpans(), true, rowNum);
            }
          }
        }
        // Else, we'll assume it's something pile bridge-like
        if (!typeFound) {
          returnVal += genPB(length, height, b.getSpans(), true, false, b.getApproachLoc(), rowNum);
        }
        if (hasApproaches && (b.getApproachLoc() == 'H' || b.getApproachLoc() == 'B')) {
          // Do same as for main span (except for OH)
          typeFound = false;
          length = getBridgeCols(b, false, false, false);
          height = getBridgeBTHeight(b, false, false, false);

          // We need to search through bridge types from known names
          // Starting with square trusses, which use a phrase from other bridge types (note that
          // this
          // is still in-scope because it was the first type checked before)
          for (int i = 0; i < squareTrussNames.length; i++) {
            if (b.getHAType().contains(squareTrussNames[i])) {
              typeFound = true;
              returnVal += genSquareTruss(length, height, b.getHASpans(), false, false,
                  b.getApproachLoc(), rowNum);
              break;
            }
          }
          // Large trusses
          if (!typeFound) {
            String[] largeTrussNames = {"T.R.S."};
            for (int i = 0; i < largeTrussNames.length; i++) {
              if (b.getHAType().contains(largeTrussNames[i])) {
                typeFound = true;
                returnVal += genLargeTruss(length, height, b.getHASpans(), false, false,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Small trusses
          if (!typeFound) {
            String[] smallTrussNames = {"P.R.S.", "T.P.G."};
            for (int i = 0; i < smallTrussNames.length; i++) {
              if (b.getHAType().contains(smallTrussNames[i])) {
                typeFound = true;
                returnVal += genSmallTruss(length, height, b.getHASpans(), false, false,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Inverted square trusses
          if (!typeFound) {
            String[] inverseSquareTrussNames = {"D.P.G.", "D.R.L."};
            for (int i = 0; i < inverseSquareTrussNames.length; i++) {
              if (b.getHAType().contains(inverseSquareTrussNames[i])) {
                typeFound = true;
                returnVal += genInverseSquareTruss(length, height, b.getHASpans(), false, false,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Inverted standard trusses
          if (!typeFound) {
            String[] inverseTrussNames = {"D.PIN."};
            for (int i = 0; i < inverseTrussNames.length; i++) {
              if (b.getHAType().contains(inverseTrussNames[i])) {
                typeFound = true;
                returnVal += genInverseTruss(length, height, b.getHASpans(), false, false,
                    b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // I-beams
          if (!typeFound) {
            String[] IBNames = {"I.B."};
            for (int i = 0; i < IBNames.length; i++) {
              if (b.getHAType().contains(IBNames[i])) {
                typeFound = true;
                returnVal +=
                    genIB(length, height, b.getHASpans(), false, false, b.getApproachLoc(), rowNum);
                break;
              }
            }
          }
          // Else, we'll assume it's something pile bridge-like
          if (!typeFound) {
            returnVal +=
                genPB(length, height, b.getHASpans(), false, false, b.getApproachLoc(), rowNum);
          }

        }
        if (rowNum > 0) {
          returnVal += "     ";
        } else if (rowNum < 0) {
          returnVal += "[    ";
        } else {
          returnVal += "-----";
        }
        return returnVal;
      }
    }

    /**
     * A method to generate the ASCII representation of a small truss bridge with the given specifications 
     * as is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of small truss bridge at the 
     * specified row number.  If the row number is outside of what is required, the returned string will 
     * contain the appropriate number of spaces to match the length of other rows.
     */
    private String genSmallTruss(int length, int height, int spans, boolean isMainSpan,
        boolean isLowApp, char approachLoc, int rowNum) {
      String returnVal = "";
      // Provide handling for unknown number of spans - max per span will be 14 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 15) + 1;
      }
      // Have to determine the required above-track height to figure out when to just return spaces
      int reqATHeight;
      if (length < 2) {
        reqATHeight = 0;
      } else if (length < 3) {
        reqATHeight = 1;
      } else {
        reqATHeight = 2;
      }

      if (rowNum > reqATHeight || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      }

      int effLength;

      // Below track height, piling logic remains the same as for pile bridges, except all center
      // supports
      // become two-wide.
      if (rowNum < 0) {
        effLength = length - (spans - 1);
        returnVal = genPB(effLength, 1, spans, isMainSpan, isLowApp, approachLoc, -1);

        // if there's a line at the beginning or end, ignore those in our replacement
        // both ends
        if (returnVal.charAt(0) == '|' && returnVal.charAt(returnVal.length() - 1) == '|') {
          returnVal = "|" + returnVal.substring(1, returnVal.length() - 1).replace("|", "||") + "|";
        }
        // end
        else if (returnVal.charAt(returnVal.length() - 1) == '|') {
          returnVal = returnVal.substring(0, returnVal.length() - 1).replace("|", "||") + "|";
        }
        // beginning
        else if (returnVal.charAt(0) == '|') {
          returnVal = "|" + returnVal.substring(1, returnVal.length()).replace("|", "||");
        } else {
          returnVal = returnVal.replace("|", "||");
        }

        return returnVal;
      }
      // Above-track, the base case is always 1 span
      else {
        if (spans == 1) {
          // Top layer first
          if (rowNum == 2) {
            // Note that we've already handled extremely short bridges where this doesn't have
            // anything
            returnVal += " ";
            for (int i = 0; i < length - 2; i++) {
              returnVal += "_";
            }
            returnVal += " ";
          }
          // rowNum == 1
          else {
            boolean isForward = true;
            // even length will be filled with slashes, odd will have a space in the middle
            if (length % 2 == 0) {
              for (int i = 0; i < length; i++) {
                if (isForward) {
                  returnVal += "/";
                } else {
                  returnVal += "\\";
                }
                isForward = !isForward;
              }

            } else {
              for (int i = 0; i < length; i++) {
                if (i == length / 2) {
                  returnVal += " ";
                } else {
                  if (isForward) {
                    returnVal += "/";
                  } else {
                    returnVal += "\\";
                  }
                  isForward = !isForward;
                }
              }
            }
          }
        } else {
          if (isMainSpan) {
            // If there are multiple spans, we'll reuse the code generating the base to ensure that
            // our
            // top spans match
            String spanReference = genSmallTruss(length, 1, spans, true, false, approachLoc, -1);
            int[] spanLengths = new int[spans];
            int currIndex = 0;

            // Thought this would have to be done with mods for each approach location, but
            // approaches
            // just mean a single |, which won't get caught by these (unless the bridge is
            // unreasonably
            // small, which we'll treat as a practical impossibility with actual data).
            for (int i = 0; i < spans; i++) {
              if (i != spans - 1) {
                spanLengths[i] = spanReference.indexOf("||", currIndex) + 1 - currIndex;
                currIndex = spanReference.indexOf("||", currIndex) + 1;
              } else {
                spanLengths[i] = spanReference.length() - (currIndex);
              }
            }
            // Now our returnVal is just all of these 1-span bridges put together
            for (int i = 0; i < spans; i++) {
              returnVal +=
                  genSmallTruss(spanLengths[i], height, 1, true, false, approachLoc, rowNum);
            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genSmallTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genSmallTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
      }
      return returnVal;
    }

    /**
     * A method to generate the ASCII representation of a large truss bridge with the given specifications 
     * as is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of large truss bridge at the 
     * specified row number.  If the row number is outside of what is required, the returned string will 
     * contain the appropriate number of spaces to match the length of other rows.
     */
    private String genLargeTruss(int length, int height, int spans, boolean isMainSpan,
        boolean isLowApp, char approachLoc, int rowNum) {
      String returnVal = "";

      // Provide handling for unknown number of spans - max per span will be 34 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 35) + 1;
      }

      // Determine required height to check if anything needs to be printed
      int reqATHeight;
      if (length < 2) {
        reqATHeight = 0;
      } else if (length < 3) {
        reqATHeight = 1;
      } else if (length < 5) {
        reqATHeight = 2;
      } else {
        reqATHeight = 3;
      }

      if (rowNum > reqATHeight || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      }

      // Underside logic is shared with that of a small truss, so reuse that here
      if (rowNum < 0) {
        returnVal +=
            genSmallTruss(length, height, spans, isMainSpan, isLowApp, approachLoc, rowNum);
        return returnVal;
      }
      // Above-track calculations are different, but once again use the base case of 1 span
      else {
        if (spans == 1) {
          // Under 4 long, we represent with a small tress
          if (length < 4) {
            returnVal +=
                genSmallTruss(length, height, spans, isMainSpan, isLowApp, approachLoc, rowNum);
          }
          // Lengths of 4 - 7 have custom generations
          else if (length == 4) {
            if (rowNum == 2) {
              returnVal += " /\\ ";
            } else if (rowNum == 1) {
              returnVal += "/\\/\\";
            }
          } else if (length == 5) {
            if (rowNum == 3) {
              returnVal += "  _  ";
            }
            if (rowNum == 2) {
              returnVal += " / \\ ";
            } else if (rowNum == 1) {
              returnVal += "/\\ /\\";
            }
          } else if (length == 6) {
            if (rowNum == 3) {
              returnVal += "  __  ";
            }
            if (rowNum == 2) {
              returnVal += " /\\/\\ ";
            } else if (rowNum == 1) {
              returnVal += "/ /\\ \\";
            }
          } else if (length == 7) {
            if (rowNum == 3) {
              returnVal += "  ___  ";
            }
            if (rowNum == 2) {
              returnVal += " /\\_/\\ ";
            } else if (rowNum == 1) {
              returnVal += "/ / \\ \\";
            }
          }
          // Now we can get to standardized generation
          else {
            // top row will be underscores with two spaces on either side
            if (rowNum == 3) {
              returnVal += "  ";
              for (int i = 0; i < length - 4; i++) {
                returnVal += "_";
              }
              returnVal += "  ";
            }
            // middle and bottom rows get more complicated
            else if (rowNum == 2) {
              String lSupport = " / ";
              String rSupport = " \\ ";
              String center = "";

              int numStdPerSide = (length - 6) / 6;
              for (int i = 0; i < numStdPerSide; i++) {
                center += "\\/ ";
              }

              int centerFillLen = (length - 6) % 6;
              switch (centerFillLen) {
                case 1:
                  center += " ";
                  break;
                case 2:
                  center += "\\/";
                  break;
                case 3:
                  center += "\\_/";
                  break;
                case 4:
                  center += "\\/\\/";
                  break;
                case 5:
                  center += "\\/ \\/";
                  break;
                default:
                  break;
              }
              for (int i = 0; i < numStdPerSide; i++) {
                center += " \\/";
              }
              returnVal += lSupport + center + rSupport;
            } else if (rowNum == 1) {
              String lSupport = "/  ";
              String rSupport = "  \\";
              String center = "";

              int numStdPerSide = (length - 6) / 6;
              for (int i = 0; i < numStdPerSide; i++) {
                center += "/\\ ";
              }

              int centerFillLen = (length - 6) % 6;
              switch (centerFillLen) {
                case 1:
                  center += " ";
                  break;
                case 2:
                  center += "/\\";
                  break;
                case 3:
                  center += "/ \\";
                  break;
                case 4:
                  center += "/\\/\\";
                  break;
                case 5:
                  center += "/\\ /\\";
                  break;
                default:
                  break;
              }
              for (int i = 0; i < numStdPerSide; i++) {
                center += " /\\";
              }
              returnVal += lSupport + center + rSupport;
            }
          }
        }
        // If more than one span, use the same recursive logic as in the small truss
        else {
          if (isMainSpan) {
            // If there are multiple spans, we'll reuse the code generating the base to ensure that
            // our
            // top spans match
            String spanReference = genLargeTruss(length, 1, spans, true, false, approachLoc, -1);
            int[] spanLengths = new int[spans];
            int currIndex = 0;

            // Thought this would have to be done with mods for each approach location, but
            // approaches
            // just mean a single |, which won't get caught by these (unless the bridge is
            // unreasonably
            // small, which we'll treat as a practical impossibility with actual data).
            for (int i = 0; i < spans; i++) {
              if (i != spans - 1) {
                spanLengths[i] = spanReference.indexOf("||", currIndex) + 1 - currIndex;
                currIndex = spanReference.indexOf("||", currIndex) + 1;
              } else {
                spanLengths[i] = spanReference.length() - (currIndex);
              }
            }
            // Now our returnVal is just all of these 1-span bridges put together
            for (int i = 0; i < spans; i++) {
              returnVal +=
                  genLargeTruss(spanLengths[i], height, 1, true, false, approachLoc, rowNum);
            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genLargeTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genLargeTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
      }
      return returnVal;
    }

    /**
     * A method to generate the ASCII representation of an inverse square truss bridge with the given 
     * specifications as is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of inverse square truss bridge at the 
     * specified row number.  If the row number is outside of what is required, the returned string will 
     * contain the appropriate number of spaces to match the length of other rows.
     */
    private String genInverseSquareTruss(int length, int height, int spans, boolean isMainSpan,
        boolean isLowApp, char approachLoc, int rowNum) {
      String returnVal = "";

      // Provide handling for unknown number of spans - max per span will be 19 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 20) + 1;
      }

      if (rowNum > 0 || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      else if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      }
      // Only stuff going on is below-track
      else {
        // Same logic as always, 1-span base case and recurse for multiple spans
        if (spans == 1) {
          if (isMainSpan) {
            // a couple of cases going on here, if height < 3 we can only print a short design, and
            // the same
            // for a length under 8 chars. Each of these will be affected by approach locations
            if (height < 3 || length < 8) {
              if (rowNum == -1) {
                // All of these aren't going to be designed to handle ridiculously short spans
                // right,
                // because those don't actually show up in real data sets.
                switch (approachLoc) {
                  case 'L':
                    returnVal += "|";
                    for (int i = 0; i < length - 1; i++) {
                      returnVal += "-";
                    }
                    break;
                  case 'H':
                    for (int i = 0; i < length - 1; i++) {
                      returnVal += "-";
                    }
                    returnVal += "|";
                    break;
                  case 'B':
                    returnVal += "|";
                    for (int i = 0; i < length - 2; i++) {
                      returnVal += "-";
                    }
                    returnVal += "|";
                    break;
                  default:
                    for (int i = 0; i < length; i++) {
                      returnVal += "-";
                    }
                    break;
                }
              }
              // Outside of here, these generate like an I beam
              else {
                returnVal +=
                    genIB(length, height, spans, isMainSpan, isLowApp, approachLoc, rowNum);
              }

            }
            // Otherwise, these generate like a flipped square truss, but having to handle
            // approaches
            else {
              // First two layers will be reversed square trusses, cropped to remove ends
              if (rowNum > -3) {
                switch (approachLoc) {
                  case 'L':
                    returnVal += genSquareTruss(length + 1, height, spans, isMainSpan, isLowApp,
                        approachLoc, rowNum + 3).substring(0, length);
                    break;
                  case 'H':
                    returnVal += genSquareTruss(length + 1, height, spans, isMainSpan, isLowApp,
                        approachLoc, rowNum + 3).substring(1, length + 1);
                    break;
                  case 'B':
                    returnVal += genSquareTruss(length, height, spans, isMainSpan, isLowApp,
                        approachLoc, rowNum + 3);
                    break;
                  default:
                    returnVal += genSquareTruss(length + 2, height, spans, isMainSpan, isLowApp,
                        approachLoc, rowNum + 3).substring(1, length + 1);
                    break;
                }
              }
              // Third row will be like the short cases, but with unicode overlines
              else if (rowNum == -3) {
                switch (approachLoc) {
                  case 'L':
                    returnVal += "|";
                    for (int i = 0; i < length - 1; i++) {
                      returnVal += "\u203E";
                    }
                    break;
                  case 'H':
                    for (int i = 0; i < length - 1; i++) {
                      returnVal += "\u203E";
                    }
                    returnVal += "|";
                    break;
                  case 'B':
                    returnVal += "|";
                    for (int i = 0; i < length - 2; i++) {
                      returnVal += "\u203E";
                    }
                    returnVal += "|";
                    break;
                  default:
                    for (int i = 0; i < length; i++) {
                      returnVal += "\u203E";
                    }
                    break;
                }
              }
              // Further rows will be like an I beam
              else {
                returnVal +=
                    genIB(length, height, spans, isMainSpan, isLowApp, approachLoc, rowNum);
              }
            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genInverseSquareTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genInverseSquareTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
        // Multiple spans means recursion as always
        else {
          if (isMainSpan) {
            // If there are multiple spans, we'll reuse the code generating the base to ensure that
            // our
            // top spans match
            String spanReference = genSquareTruss(length, 1, spans, true, false, approachLoc, -1);
            int[] spanLengths = new int[spans];
            int currIndex = 0;

            // Thought this would have to be done with mods for each approach location, but
            // approaches
            // just mean a single |, which won't get caught by these (unless the bridge is
            // unreasonably
            // small, which we'll treat as a practical impossibility with actual data).
            for (int i = 0; i < spans; i++) {
              if (i != spans - 1) {
                spanLengths[i] = spanReference.indexOf("||", currIndex) + 1 - currIndex;
                currIndex = spanReference.indexOf("||", currIndex) + 1;
              } else {
                spanLengths[i] = spanReference.length() - (currIndex);
              }
            }
            // Now our returnVal is just all of these 1-span bridges put together
            for (int i = 0; i < spans; i++) {
              switch (approachLoc) {
                case 'L':
                  if (i == spans - 1) {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'L', rowNum);
                  } else {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  }
                  break;
                case 'H':
                  if (i == 0) {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'H', rowNum);
                  } else {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  }
                  break;
                case 'B':
                  returnVal +=
                      genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  break;
                default:
                  if (i == 0) {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'H', rowNum);
                  } else if (i == spans - 1) {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'L', rowNum);
                  } else {
                    returnVal +=
                        genInverseSquareTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  }
                  break;
              }
            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genInverseSquareTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genInverseSquareTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
      }

      return returnVal;
    }

    /**
     * A method to generate the ASCII representation of a square truss bridge with the given specifications 
     * as is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of square truss bridge at the 
     * specified row number.  If the row number is outside of what is required, the returned string will 
     * contain the appropriate number of spaces to match the length of other rows.
     */
    private String genSquareTruss(int length, int height, int spans, boolean isMainSpan,
        boolean isLowApp, char approachLoc, int rowNum) {
      String returnVal = "";

      // Provide handling for unknown number of spans - max per span will be 34 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 35) + 1;
      }

      // Determine required height to check if anything needs to be printed
      int reqATHeight;
      if (length < 3) {
        reqATHeight = 0;
      } else {
        reqATHeight = 3;
      }

      if (rowNum > reqATHeight || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      else if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      } else if (rowNum < 0) {
        returnVal +=
            genSmallTruss(length, height, spans, isMainSpan, isLowApp, approachLoc, rowNum);
        return returnVal;
      }
      // else we're dealing with top section
      else {
        if (spans == 1) {
          // All cases can be handled with modified large trusses except for length of 3
          if (length == 3) {
            if (rowNum == 3) {
              returnVal += " _ ";
            } else {
              returnVal += "| |";
            }
          } else {
            if (rowNum == 3) {
              returnVal += " ";
              for (int i = 0; i < length - 2; i++) {
                returnVal += "_";
              }
              returnVal += " ";
            } else {
              returnVal += "|" + genLargeTruss(length + 2, height, spans, isMainSpan, isLowApp,
                  approachLoc, rowNum).substring(2, length) + "|";
            }
          }
        }
        // If more than one span, use the same recursive logic as in the large truss
        else {
          if (isMainSpan) {
            // If there are multiple spans, we'll reuse the code generating the base to ensure that
            // our
            // top spans match
            String spanReference = genSquareTruss(length, 1, spans, true, false, approachLoc, -1);
            int[] spanLengths = new int[spans];
            int currIndex = 0;

            // Thought this would have to be done with mods for each approach location, but
            // approaches
            // just mean a single |, which won't get caught by these (unless the bridge is
            // unreasonably
            // small, which we'll treat as a practical impossibility with actual data).
            for (int i = 0; i < spans; i++) {
              if (i != spans - 1) {
                spanLengths[i] = spanReference.indexOf("||", currIndex) + 1 - currIndex;
                currIndex = spanReference.indexOf("||", currIndex) + 1;
              } else {
                spanLengths[i] = spanReference.length() - (currIndex);
              }
            }
            // Now our returnVal is just all of these 1-span bridges put together
            for (int i = 0; i < spans; i++) {
              returnVal +=
                  genSquareTruss(spanLengths[i], height, 1, true, false, approachLoc, rowNum);
            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genSquareTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genSquareTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
      }
      return returnVal;
    }

    /**
     * A method to generate the ASCII representation of an inverse standard truss bridge with the given 
     * specifications as is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of inverse standard truss bridge at 
     * the specified row number.  If the row number is outside of what is required, the returned string 
     * will contain the appropriate number of spaces to match the length of other rows.
     */
    private String genInverseTruss(int length, int height, int spans, boolean isMainSpan,
        boolean isLowApp, char approachLoc, int rowNum) {
      // A good amount of overlap with genInverseSquareTruss, so confirm issues aren't there as well
      String returnVal = "";

      // Provide handling for unknown number of spans - max per span will be 19 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 20) + 1;
      }

      if (rowNum > 0 || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      else if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      }
      // Only stuff going on is below-track
      else {
        // Same logic as always, 1-span base case and recurse for multiple spans
        if (spans == 1) {
          if (isMainSpan) {
            // This design requires consideration of approach locations for generating support areas
            String interiorSpan = "";
            int appChars = 0;
            if (approachLoc == 'L' || approachLoc == 'H') {
              appChars = 1;
            } else if (approachLoc == 'B') {
              appChars = 2;
            }
            int effLength = length - appChars;

            // Extremely short effective lengths or short heights get an I beam-like representation
            if (effLength < 2 || height < 2) {
              if (rowNum == -1) {
                for (int i = 0; i < effLength; i++) {
                  interiorSpan += "-";
                }
              } else {
                for (int i = 0; i < effLength; i++) {
                  interiorSpan += " ";
                }
              }
            }
            // Effective lengths under 8 characters get represented with an inverted small truss
            // bridge
            // We also do this if the height is under 3 chars, where the large truss wouldn't fit
            else if (effLength < 8 || height == 2) {
              if (rowNum == -1) {
                // This set of replaces is terrible, but allows me to flip the representation by
                // switching
                // "/"s and "\"s
                interiorSpan =
                    genSmallTruss(effLength, height, spans, isMainSpan, isLowApp, approachLoc, 1)
                        .replace('/', 's').replace('\\', '/').replace('s', '\\');
              } else if (rowNum == -2) {
                interiorSpan += " ";
                for (int i = 0; i < effLength - 2; i++) {
                  interiorSpan += "\u203E";
                }
                interiorSpan += " ";
              } else {
                for (int i = 0; i < effLength; i++) {
                  interiorSpan += " ";
                }
              }
            }
            // Otherwise, we use a large truss
            else {
              if (rowNum == -1) {
                // This set of replaces is terrible, but allows me to flip the representation by
                // switching
                // "/"s and "\"s
                interiorSpan =
                    genLargeTruss(effLength, height, spans, isMainSpan, isLowApp, approachLoc, 1)
                        .replace('/', 's').replace('\\', '/').replace('s', '\\');
                // If this size originally had an underscore in the center on the other half, add
                // this
                if ((effLength - 6) % 6 == 3) {
                  String lHalf = interiorSpan.substring(0, effLength / 2);
                  String rHalf = interiorSpan.substring((effLength / 2) + 1);
                  interiorSpan = lHalf + "_" + rHalf;
                }
              } else if (rowNum == -2) {
                // This set of replaces is terrible, but allows me to flip the representation by
                // switching
                // "/"s and "\"s
                interiorSpan =
                    genLargeTruss(effLength, height, spans, isMainSpan, isLowApp, approachLoc, 2)
                        .replace('/', 's').replace('\\', '/').replace('s', '\\');

                // This part may have an underscore in the center, which we'd want to replace with a
                // space
                interiorSpan = interiorSpan.replace('_', ' ');
              } else if (rowNum == -3) {
                interiorSpan += "  ";
                for (int i = 0; i < effLength - 4; i++) {
                  interiorSpan += "\u203E";
                }
                interiorSpan += "  ";
              } else {
                for (int i = 0; i < effLength; i++) {
                  interiorSpan += " ";
                }
              }
            }

            switch (approachLoc) {
              case 'L':
                returnVal += "|";
                returnVal += interiorSpan;
                break;
              case 'H':
                returnVal += interiorSpan;
                returnVal += "|";
                break;
              case 'B':
                returnVal += "|";
                returnVal += interiorSpan;
                returnVal += "|";
                break;
              default:
                returnVal += interiorSpan;
                break;
            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genInverseSquareTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genInverseSquareTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
        // Multiple spans means recursion as always
        else {
          if (isMainSpan) {
            // If there are multiple spans, we'll reuse the code generating the base to ensure that
            // our
            // top spans match
            String spanReference = genSquareTruss(length, 1, spans, true, false, approachLoc, -1);
            int[] spanLengths = new int[spans];
            int currIndex = 0;

            // Thought this would have to be done with mods for each approach location, but
            // approaches
            // just mean a single |, which won't get caught by these (unless the bridge is
            // unreasonably
            // small, which we'll treat as a practical impossibility with actual data).
            for (int i = 0; i < spans; i++) {
              if (i != spans - 1) {
                spanLengths[i] = spanReference.indexOf("||", currIndex) + 1 - currIndex;
                currIndex = spanReference.indexOf("||", currIndex) + 1;
              } else {
                spanLengths[i] = spanReference.length() - (currIndex);
              }
            }
            // Now our returnVal is just all of these 1-span bridges put together
            for (int i = 0; i < spans; i++) {
              switch (approachLoc) {
                case 'L':
                  if (i == spans - 1) {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'L', rowNum);
                  } else {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  }
                  break;
                case 'H':
                  if (i == 0) {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'H', rowNum);
                  } else {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  }
                  break;
                case 'B':
                  returnVal += genInverseTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  break;
                default:
                  if (i == 0) {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'H', rowNum);
                  } else if (i == spans - 1) {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'L', rowNum);
                  } else {
                    returnVal +=
                        genInverseTruss(spanLengths[i], height, 1, true, false, 'B', rowNum);
                  }
                  break;
              }

            }
          }
          // Same logic as elsewhere: If not a main span, it's an approach of some sort - check if
          // it's
          // low-side approach, and can be done as a main section with an approach on the opposite
          // side
          else if (isLowApp) {
            returnVal += genInverseTruss(length, height, spans, true, false, 'H', rowNum);
          }
          // Otherwise, it's a high-side approach
          else {
            returnVal += genInverseTruss(length, height, spans, true, false, 'L', rowNum);
          }
        }
      }
      return returnVal;
    }

    /**
     * A method to generate the ASCII representation of an I-beam bridge with the given specifications as 
     * is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of I-beam bridge at the specified 
     * row number.  If the row number is outside of what is required, the returned string will contain the 
     * appropriate number of spaces to match the length of other rows.
     */
    private String genIB(int length, int height, int spans, boolean isMainSpan, boolean isLowApp,
        char approachLoc, int rowNum) {
      String returnVal = "";

      // Provide handling for unknown number of spans - max per span will be 14 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 15) + 1;
      }

      if (rowNum > 0 || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      }

      // Logic for this is extremely similar to pile and OH bridges, avoid cross-braces and replace
      // spaces of row -1 with overlines or dashes
      if (length < 9) {
        // for "short" I-Beam bridges, we use unicode overline characters to represent beams
        returnVal = genPB(length, 1, spans, isMainSpan, isLowApp, approachLoc, -1);
        if (rowNum == -1) {
          returnVal = returnVal.replace(' ', '\u203E');
        }
      } else {
        // for longer I-Beam bridges, we use dashes to represent beams
        returnVal = genPB(length, 1, spans, isMainSpan, isLowApp, approachLoc, -1);
        if (rowNum == -1) {
          returnVal = returnVal.replace(' ', '-');
        }
      }
      return returnVal;
    }

    /**
     * A method to generate the ASCII representation of a pile/frame bridge with the given specifications 
     * as is necessary in the given row number.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isMainSpan whether or not this bridge is the main span
     * @param isLowApp whether or not this bridge is the low-side approach (only considered if isMainSpan 
     * is false)
     * @param approachLoc the location of the bridge's approaches, as defined in the Bridge class
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of pile/frame bridge at the specified 
     * row number.  If the row number is outside of what is required, the returned string will contain the 
     * appropriate number of spaces to match the length of other rows.
     */
    private String genPB(int length, int height, int spans, boolean isMainSpan, boolean isLowApp,
        char approachLoc, int rowNum) {
      String returnVal = "";
      String lAppend = "";
      String rAppend = "";
      int effLength;

      // Provide handling for unknown number of spans - max per span will be 4 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 5) + 1;
      }

      if (rowNum > 0 || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      if (rowNum == 0) {
        for (int i = 0; i < length; i++) {
          returnVal += "=";
        }
        return returnVal;
      }

      if (isMainSpan) {
        if (approachLoc == 'N' || approachLoc == 'U') {
          effLength = length;
        } else if (approachLoc == 'L') {
          effLength = length - 1;
          lAppend = "|";
        } else if (approachLoc == 'H') {
          effLength = length - 1;
          rAppend = "|";
        }
        // Otherwise, approaches on both sides
        else {
          effLength = length - 2;
          lAppend = "|";
          rAppend = "|";
        }
        // The similarity to OH bridge representations means I can just reuse this code, but cut off
        // the bottom layer with the rep. of tracks running under it
        returnVal += genOH(effLength, height + 1, spans, true, rowNum);
        if (height > 2 && rowNum != -height) {
          returnVal = returnVal.replace(' ', '_');
        }
      }

      // If not a main span, it's an approach of some sort - check if it's low-side approach
      // Approaches can be done as a main section with an approach on the opposite side
      else if (isLowApp) {
        returnVal += genPB(length, height, spans, true, false, 'H', rowNum);
      }
      // Otherwise, it's a high-side approach
      else {
        returnVal += genPB(length, height, spans, true, false, 'L', rowNum);
      }
      return lAppend + returnVal + rAppend;
    }

    /**
     * A method to generate the ASCII representation of an overhead bridge with the given specifications 
     * as is necessary in the given row number.  All overhead bridges are assumed to be of a pile bridge-
     * like form.
     * @param length the length of the bridge, in characters
     * @param height the below-track height of the bridge, in characters
     * @param spans the number of spans the bridge should have
     * @param isRail whether or not the bridge running over the track is a rail bridge
     * @param rowNum the row of the representation being generated
     * @return a String of the ASCII representation of the given type of pile/frame bridge at the specified 
     * row number.  If the row number is outside of what is required, the returned string will contain the 
     * appropriate number of spaces to match the length of other rows.
     */
    private String genOH(int length, int height, int spans, boolean isRail, int rowNum) {
      String returnVal = "";

      // Provide handling for unknown number of spans - max per span will be 5 chars in this case
      if (spans == -1 || spans == 0) {
        spans = (length / 6) + 1;
      }

      // Incorrect call will be a row number out of bounds, >0 or <-height, return proper-length
      // line
      // full of spaces.
      if (rowNum > 0 || rowNum < -height) {
        for (int i = 0; i < length; i++) {
          returnVal += " ";
        }
        return returnVal;
      }

      // Actual bridge spans will be printed as underscores if a road bridge, or equals if a rail
      // bridge
      if (rowNum == 0) {
        if (isRail) {

          for (int i = 0; i < length; i++) {
            returnVal += "=";
          }
          return returnVal;
        } else {
          for (int i = 0; i < length; i++) {
            returnVal += "_";
          }
          return returnVal;
        }
      }

      // Bottom row will include a pair of periods to represent tracks under the bridge
      if (rowNum == -height) {
        int baseSpacing = (length - (spans - 1)) / spans;
        int addSpacing = (length - (spans - 1)) % spans;
        int normSpansL, normSpansR;
        // Simplest case, print things out consistently
        if (addSpacing == 0) {
          // Under a base spacing of 2, can't print track representation
          if (baseSpacing < 2) {
            for (int i = 0; i < spans; i++) {
              for (int j = 0; j < baseSpacing; j++) {
                returnVal += " ";
              }
              if (i != spans - 1) {
                returnVal += "|";
              }
            }
          }
          // Otherwise, we need tracks as close to the center as possible (right-justified when not
          // exact)
          else {
            // Odd number of spans will have the tracks in the center, to the right side of a span
            // if
            // an odd number of spaces
            if (spans % 2 == 1) {
              for (int i = 0; i < spans / 2; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                returnVal += "|";
              }
              int rSpaces = (baseSpacing - 2) / 2;
              for (int i = 0; i < (baseSpacing - 2 - rSpaces); i++) {
                returnVal += " ";
              }
              returnVal += "..";
              for (int i = 0; i < rSpaces; i++) {
                returnVal += " ";
              }
              if (spans != 1) {
                returnVal += "|";
              }
              for (int i = 0; i < spans / 2; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                if (i != spans / 2 - 1) {
                  returnVal += "|";
                }
              }
            }
            // Even number of spans means put tracks at left edge of right-center span
            else {
              for (int i = 0; i < spans / 2; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                returnVal += "|";
              }
              returnVal += "..";
              for (int i = 0; i < (baseSpacing - 2); i++) {
                returnVal += " ";
              }
              if (spans - (spans / 2 + 1) != 0) {
                returnVal += "|";
              }
              for (int i = 0; i < (spans / 2) - 1; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                if (i != spans / 2 - 2) {
                  returnVal += "|";
                }
              }
            }
          }
        }

        // If the oddness of spaces to be added matches the oddness of spans, add them from the
        // center
        // radiating out
        else if (spans % 2 == addSpacing % 2) {
          normSpansL = (spans - addSpacing) / 2;
          normSpansR = spans - normSpansL - addSpacing;

          for (int i = 0; i < normSpansL; i++) {
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // recursive call for center section, length becomes a bit of a mess.
          // each added space means a span of original length+1, with a piling between each span (no
          // ends)
          returnVal +=
              genOH(addSpacing * (baseSpacing + 2) - 1, height, addSpacing, isRail, rowNum);
          for (int i = 0; i < normSpansR; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
          }
        }
        // Odd spaces, even spans means add spaces radiating out from center-right span
        else if (spans % 2 == 0 && addSpacing % 2 == 1) {
          normSpansL = ((spans - addSpacing) / 2 + 1);
          normSpansR = (spans - addSpacing - normSpansL);

          // Normal-width left spans
          for (int i = 0; i < normSpansL; i++) {
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // Extra-width left spans
          for (int i = 0; i < addSpacing / 2; i++) {
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // Left-justified tracks under extra length span
          returnVal += "..";
          for (int i = 0; i < (baseSpacing + 1 - 2); i++) {
            returnVal += " ";
          }
          // Extra-width right spans
          for (int i = 0; i < addSpacing / 2; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
          }
          // Normal-width right spans
          for (int i = 0; i < normSpansR; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
          }
        }
        // Finally, odd spans and even spaces to add means we put them in on the end spans
        else {
          for (int i = 0; i < addSpacing / 2; i++) {
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // Recursive call for center, length calculation is kinda cursed but makes sense
          returnVal += genOH(length - (addSpacing * (baseSpacing + 2)), height, spans - addSpacing,
              isRail, rowNum);
          for (int i = 0; i < addSpacing / 2; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
          }
        }
      }
      // Rows between the lowest and actual spans (rowNum = 0) will print like a pile bridge, no
      // tracks
      // Again this is bad practice, but the logic here is just the same as above but with all cases
      // of a ".." string replaced with " ". This means inefficient code, but less new bugs showing
      // up
      // in modified sections. If I have time, I'll improve this.
      else {
        int baseSpacing = (length - (spans - 1)) / spans;
        int addSpacing = (length - (spans - 1)) % spans;
        int normSpansL, normSpansR;
        // Simplest case, print things out consistently
        if (addSpacing == 0) {
          // Under a base spacing of 2, can't print track representation
          if (baseSpacing < 2) {
            for (int i = 0; i < spans; i++) {
              for (int j = 0; j < baseSpacing; j++) {
                returnVal += " ";
              }
              if (i != spans - 1) {
                returnVal += "|";
              }
            }
          }
          // Otherwise, we need tracks as close to the center as possible (right-justified when not
          // exact)
          else {
            // Odd number of spans will have the tracks in the center, to the right side of a span
            // if
            // an odd number of spaces
            if (spans % 2 == 1) {
              for (int i = 0; i < spans / 2; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                returnVal += "|";
              }
              int rSpaces = (baseSpacing - 2) / 2;
              for (int i = 0; i < (baseSpacing - 2 - rSpaces); i++) {
                returnVal += " ";
              }
              returnVal += "  ";
              for (int i = 0; i < rSpaces; i++) {
                returnVal += " ";
              }
              if (spans != 1) {
                returnVal += "|";
              }
              for (int i = 0; i < spans / 2; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                if (i != spans / 2 - 1) {
                  returnVal += "|";
                }
              }
            }
            // Even number of spans means put tracks at left edge of right-center span
            else {
              for (int i = 0; i < spans / 2; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                returnVal += "|";
              }
              returnVal += "  ";
              for (int i = 0; i < (baseSpacing - 2); i++) {
                returnVal += " ";
              }
              if (spans - (spans / 2 + 1) != 0) {
                returnVal += "|";
              }
              for (int i = 0; i < (spans / 2) - 1; i++) {
                for (int j = 0; j < baseSpacing; j++) {
                  returnVal += " ";
                }
                if (i != spans / 2 - 2) {
                  returnVal += "|";
                }
              }
            }
          }
        }

        // If the oddness of spaces to be added matches the oddness of spans, add them from the
        // center
        // radiating out
        else if (spans % 2 == addSpacing % 2) {
          normSpansL = (spans - addSpacing) / 2;
          normSpansR = spans - normSpansL - addSpacing;

          for (int i = 0; i < normSpansL; i++) {
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // recursive call for center section, length becomes a bit of a mess.
          // each added space means a span of original length+1, with a piling between each span (no
          // ends)
          returnVal +=
              genOH(addSpacing * (baseSpacing + 2) - 1, height, addSpacing, isRail, rowNum);
          for (int i = 0; i < normSpansR; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
          }
        }
        // Odd spaces, even spans means add spaces radiating out from center-right span
        else if (spans % 2 == 0 && addSpacing % 2 == 1) {
          normSpansL = ((spans - addSpacing) / 2 + 1);
          normSpansR = (spans - addSpacing - normSpansL);

          // Normal-width left spans
          for (int i = 0; i < normSpansL; i++) {
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // Extra-width left spans
          for (int i = 0; i < addSpacing / 2; i++) {
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // Left-justified tracks under extra length span
          returnVal += "  ";
          for (int i = 0; i < (baseSpacing + 1 - 2); i++) {
            returnVal += " ";
          }
          // Extra-width right spans
          for (int i = 0; i < addSpacing / 2; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
          }
          // Normal-width right spans
          for (int i = 0; i < normSpansR; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing; j++) {
              returnVal += " ";
            }
          }
        }
        // Finally, odd spans and even spaces to add means we put them in on the end spans
        else {
          for (int i = 0; i < addSpacing / 2; i++) {
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
            returnVal += "|";
          }
          // Recursive call for center, length calculation is kinda cursed but makes sense
          returnVal += genOH(length - (addSpacing * (baseSpacing + 2)), height, spans - addSpacing,
              isRail, rowNum);
          for (int i = 0; i < addSpacing / 2; i++) {
            returnVal += "|";
            for (int j = 0; j < baseSpacing + 1; j++) {
              returnVal += " ";
            }
          }
        }
      }

      return returnVal;
    }
  }

  /**
   * An inner class designed to provide fancier forms of printing in the terminal, including centered 
   * text, indented text, and more nuanced line-wrapping.
   * @author Jeremy Peplinski
   */
  private class TerminalPrinting {
    /**
     * A method to print a piece of text with the given characters at the edges of the screen, with 
     * intervening space filled with the specified character.
     * @param text a String to be printed
     * @param lEdge the character to place at the left edge of the screen
     * @param rEdge the character to place at the right edge of the screen
     * @param fill the character to fill other space in the line with
     */
    public void printCentered(String text, char lEdge, char rEdge, char fill) {
      if (cols <= 2) {
        return;
      }
      if (text.length() + 2 > cols) {
        // need to break up print into two recursive calls
        // if too large, jump to end of printable area and search for ' ', '-', ',' probably?
        // if not found, break at max length, else break at first index found of one of those chars
        // for space, print stuff before the space and after the space (removing it)
        // otherwise, print stuff including the char first, then stuff after the char
        String printable = text.substring(0, cols - 2);
        int breakIndex = cols - 2;
        int foundIndex;
        boolean isSpace = false;
        String breakChars = " ,-=/\\";
        for (int i = 0; i < breakChars.length(); i++) {
          foundIndex = printable.lastIndexOf(breakChars.charAt(i));
          if (foundIndex >= 0 && foundIndex < breakIndex) {
            breakIndex = foundIndex + 1;
            if (breakChars.charAt(i) == ' ') {
              isSpace = true;
            }
          }
        }
        if (breakIndex < cols - 2) {
          String firstPrint = printable.substring(0, breakIndex);
          String secondPrint = text.substring(breakIndex).stripLeading();
          if (isSpace) {
            firstPrint = firstPrint.stripTrailing();
          }
          printCentered(firstPrint, lEdge, rEdge, fill);
          printCentered(secondPrint, lEdge, rEdge, fill);
        } else {
          printCentered(text.substring(0, breakIndex), lEdge, rEdge, fill);
          printCentered(text.substring(breakIndex).stripLeading(), lEdge, rEdge, fill);
        }
      } else {
        int toFill = cols - 2 - text.length();
        int toFillL = toFill / 2;
        int toFillR = toFill - toFillL;
        String toPrint = "" + lEdge;
        for (int i = 0; i < toFillL; i++) {
          toPrint += "" + fill;
        }
        toPrint += text;
        for (int i = 0; i < toFillR; i++) {
          toPrint += "" + fill;
        }
        toPrint += "" + rEdge;
        System.out.println(toPrint);
      }
    }

    /**
     * A method to print a piece of text, with the given characters at the edges of the screen and the 
     * specified Strings on either side of the text, with intervening space filled with the specified 
     * character.
     * @param text a String to be printed
     * @param lEdge the character to place at the left edge of the screen
     * @param lTextEdge the String to be placed on the left side of the printed text
     * @param rTextEdge the String to be placed on the right side of the printed text
     * @param the character to place at the right edge of the screen
     * @param fill the character to fill other space in the line with
     */
    public void printCentered(String text, char lEdge, String lTextEdge, String rTextEdge,
        char rEdge, char fill) {
      if (text.length() + lTextEdge.length() + rTextEdge.length() + 2 > cols) {
        // need to break up print into two recursive calls
        // if too large, jump to end of printable area and search for ' ', '-', ',' probably?
        // if not found, break at max length, else break at first index found of one of those chars
        // for space, print stuff before the space and after the space (removing it)
        // otherwise, print stuff including the char first, then stuff after the char
        if (cols <= 2 + lTextEdge.length() + rTextEdge.length()) {
          return;
        }
        String printable = text.substring(0, cols - (2 + lTextEdge.length() + rTextEdge.length()));
        int breakIndex = cols - (2 + lTextEdge.length() + rTextEdge.length());
        int foundIndex;
        boolean isSpace = false;
        String breakChars = " ,-=/\\";
        for (int i = 0; i < breakChars.length(); i++) {
          foundIndex = printable.lastIndexOf(breakChars.charAt(i));
          if (foundIndex >= 0 && foundIndex < breakIndex) {
            breakIndex = foundIndex + 1;
            if (breakChars.charAt(i) == ' ') {
              isSpace = true;
            }
          }
        }
        if (breakIndex < cols - (2 + lTextEdge.length() + rTextEdge.length())) {
          String firstPrint = printable.substring(0, breakIndex);
          String secondPrint = text.substring(breakIndex).stripLeading();
          if (isSpace) {
            firstPrint = firstPrint.stripTrailing();
          }
          printCentered(firstPrint, lEdge, lTextEdge, rTextEdge, rEdge, fill);
          printCentered(secondPrint, lEdge, lTextEdge, rTextEdge, rEdge, fill);
        } else {
          printCentered(text.substring(0, breakIndex), lEdge, lTextEdge, rTextEdge, rEdge, fill);
          printCentered(text.substring(breakIndex).stripLeading(), lEdge, lTextEdge, rTextEdge,
              rEdge, fill);
        }
      } else {
        int toFill = cols - (2 + lTextEdge.length() + rTextEdge.length()) - text.length();
        int toFillL = toFill / 2;
        int toFillR = toFill - toFillL;
        String toPrint = "" + lEdge;
        for (int i = 0; i < toFillL; i++) {
          toPrint += "" + fill;
        }
        toPrint += lTextEdge + text + rTextEdge;
        for (int i = 0; i < toFillR; i++) {
          toPrint += "" + fill;
        }
        toPrint += "" + rEdge;
        System.out.println(toPrint);
      }
    }

    /**
     * A method to print a piece of text with the given characters at the edges of the screen, indented 
     * from the left edge by roughly 1/7 of the screen width.  An extra indent can be added, largely for 
     * the recursive use of this function when text must wrap.
     * @param text a String to be printed
     * @param lEdge the character to place at the left edge of the screen
     * @param rEdge the character to place at the right edge of the screen
     * @param hasExtraIndent whether or not the text should be indented further (intended for line 
     * wrapping)
     */
    public void printIndented(String text, char lEdge, char rEdge, boolean hasExtraIndent) {
      int indent;
      // basic indent amount
      indent = (cols / 7) + 1;
      // if extra indent necessary (for recursive calls when text length exceeds available space)
      // add this
      if (hasExtraIndent) {
        if (cols >= 40) {
          indent += 4;
        } else {
          indent += (cols / 10) + 1;
        }
      }
      // if indent and added side chars are over available columns, do nothing
      if (cols <= indent + 2) {
        return;
      }

      if (text.length() + indent + 2 > cols) {
        // recursive case, where we'd need more than one line, with additional indent on successive
        // lines
        int addedIndent = 0;
        if (!hasExtraIndent) {
          if (cols >= 40) {
            addedIndent = 4;
          } else {
            addedIndent = (cols / 10) + 1;
          }
        }
        // if added indent will not allow successive lines to be printed, return without doing
        // anything
        if (cols <= indent + addedIndent + 2) {
          return;
        }

        String printable = text.substring(0, cols - (2 + indent));
        int breakIndex = cols - (2 + indent);
        int foundIndex;
        boolean isSpace = false;
        String breakChars = " ,-=/\\";
        for (int i = 0; i < breakChars.length(); i++) {
          foundIndex = printable.lastIndexOf(breakChars.charAt(i));
          if (foundIndex >= 0 && foundIndex < breakIndex) {
            breakIndex = foundIndex + 1;
            if (breakChars.charAt(i) == ' ') {
              isSpace = true;
            }
          }
        }
        if (breakIndex < cols - (2 + indent)) {
          String firstPrint = printable.substring(0, breakIndex);
          String secondPrint = text.substring(breakIndex).stripLeading();
          if (isSpace) {
            firstPrint = firstPrint.stripTrailing();
          }
          printIndented(firstPrint, lEdge, rEdge, hasExtraIndent);
          printIndented(secondPrint, lEdge, rEdge, true);
        } else {
          printIndented(text.substring(0, breakIndex), lEdge, rEdge, hasExtraIndent);
          printIndented(text.substring(breakIndex).stripLeading(), lEdge, rEdge, true);
        }
      }
      // Base case
      else {
        int toFill = cols - 2 - indent - text.length();
        String toPrint = "" + lEdge;
        for (int i = 0; i < indent; i++) {
          toPrint += " ";
        }
        toPrint += text;
        for (int i = 0; i < toFill; i++) {
          toPrint += " ";
        }
        toPrint += "" + rEdge;
        System.out.println(toPrint);
      }
    }
  }
}
