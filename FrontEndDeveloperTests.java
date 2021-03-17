// --== CS400 File Header Information ==--
// Name: Jeremy Peplinski
// Email: japeplinski@wisc.edu
// Team: Red
// Group: CC
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: <optional extra notes>
///////////////////////////////////////////////////////////////////////////////

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A class for basic tests of the front end for the Historic Rail Bridge Database
 * @author Jeremy Peplinski
 *
 */
class FrontEndDeveloperTests {

  PrintStream standardOut;
  InputStream standardIn;
  Frontend frontend;
  Backend backend;
  String inputData;
  String enter;
  
  /**
   * Basic setup of common variables
   */
  @BeforeEach
  public void setup() {
    standardOut = System.out;
    standardIn = System.in;
    // Frontend will be instantiated properly once implemented.
    frontend = new Frontend();
    inputData = "Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\r\n" + 
        "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\r\n" + 
        "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
    Scanner readInput = new Scanner(inputData);
    readInput.useDelimiter(",");
    backend = new Backend(readInput);
    enter = System.lineSeparator();
  }
  
  /**
   * Tests of the help screen, checking if major keybindings are displayed.
   * Currently minimal due to uncertainty in exact phrasing.
   */
  @Test
  public void testHelp() {
    try {
      // Setting input stream to required inputs for entering and leaving help screen and program
      String input = "?" + enter + "x" + enter + "x" + enter;
      InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
      System.setIn(inputStreamSimulator);
      ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
      // set the output to the stream captor to read the output of the front end
      System.setOut(new PrintStream(outputStreamCaptor));
      
      // Running program itself
      frontend.run(backend);
      // set the output back to standard out for running the test
      System.setOut(standardOut);
      // same for standard in
      System.setIn(standardIn);
      String printedToScreen = outputStreamCaptor.toString();
      
      // Tests
      boolean contains = printedToScreen.contains("\"x\": Exit the database program") &&
          printedToScreen.contains("\"ls\": Search for the nearest bridge to the set reference location")
          && printedToScreen.contains("\"l\": Set or reset refernce location") && 
          printedToScreen.contains("\"v\": View information for the last bridge visited");
      assertNotNull(frontend);
      assertTrue(contains);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Tests of entering a location, ensuring that the set location is correctly displayed on the
   * home screen.  Currently minimal due to uncertainty in exact phrasing.
   */
  @Test
  public void testLocEntry() {
    try {
      // Setting input stream to required inputs for entering and leaving help screen and program
      String input = "l" + enter + "42.3 -90.1" + enter + "x" + enter;
      InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
      System.setIn(inputStreamSimulator);
      ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
      // set the output to the stream captor to read the output of the front end
      System.setOut(new PrintStream(outputStreamCaptor));

      // Running program itself
      frontend.run(backend);
      // set the output back to standard out for running the test
      System.setOut(standardOut);
      // same for standard in
      System.setIn(standardIn);
      String printedToScreen = outputStreamCaptor.toString();
      
      // Tests
      assertNotNull(frontend);
      // Screen should display set coordinates in some way
      assertTrue(printedToScreen.contains("42.3") && printedToScreen.contains("90.1"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Tests of the viewer screen as accessed by default, checking that the bridge at the head of the list
   * is correctly displayed (testing name and some key specifications).
   * Currently minimal due to uncertainty in exact phrasing.
   */
  @Test
  public void testViewer() {
    try {
      // Setting input stream to required inputs for entering and leaving help screen and program
      String input = "v" + enter + "x" + enter + "x" + enter;
      InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
      System.setIn(inputStreamSimulator);
      ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
      // set the output to the stream captor to read the output of the front end
      System.setOut(new PrintStream(outputStreamCaptor));

      // Running program itself
      frontend.run(backend);
      // set the output back to standard out for running the test
      System.setOut(standardOut);
      // same for standard in
      System.setIn(standardIn);
      String printedToScreen = outputStreamCaptor.toString();
      
      // Tests
      boolean contains = printedToScreen.contains("Bridge 1450") && printedToScreen.contains("P.R.S.")
          && printedToScreen.contains("64.0'") && printedToScreen.contains("Unknown");
      assertNotNull(frontend);
      assertTrue(contains);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Tests of a basic search for a bridge by name, checking that the searched-for bridge is displayed
   * with some key specifications.  Currently minimal due to uncertainty in exact phrasing.
   */
  @Test
  public void testNameSearch() {
    try {
      // Setting input stream to required inputs for entering and leaving help screen and program
      String input = "s" + enter + "Bridge 1450" + enter + "x" + enter + "x" + enter;
      InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
      System.setIn(inputStreamSimulator);
      ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
      // set the output to the stream captor to read the output of the front end
      System.setOut(new PrintStream(outputStreamCaptor));

      // Running program itself
      frontend.run(backend);
      // set the output back to standard out for running the test
      System.setOut(standardOut);
      // same for standard in
      System.setIn(standardIn);
      String printedToScreen = outputStreamCaptor.toString();
      
      // Tests
      boolean contains = printedToScreen.contains("Bridge 1450") && printedToScreen.contains("P.R.S.")
          && printedToScreen.contains("64.0'") && printedToScreen.contains("Unknown");
      assertNotNull(frontend);
      assertTrue(contains);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Tests of the setting and resetting the current reference location, checking that once reset, the
   * location is no longer shown on the home screen.
   * Currently minimal due to uncertainty in exact phrasing and limited dataset size.
   */
  @Test
  public void testLocReset() {
    // Enter location, return to home, return to location entry and reset location, confirm that
    // a location is no longer displayed.
    try {
      // Setting input stream to inputs, which will set the location and reset it.
      String input = "l" + enter + "42.3 -90.1" + enter + "l" + enter + "r" + enter + "x" + enter;
      InputStream inputStreamSimulator = new ByteArrayInputStream(input.getBytes());
      System.setIn(inputStreamSimulator);
      ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
      // set the output to the stream captor to read the output of the front end
      System.setOut(new PrintStream(outputStreamCaptor));

      // Running program itself
      frontend.run(backend);
      // set the output back to standard out for running the test
      System.setOut(standardOut);
      // same for standard in
      System.setIn(standardIn);
      String printedToScreen = outputStreamCaptor.toString();
      
      String afterReset = printedToScreen.substring(printedToScreen.indexOf("Reference location reset."));
      // Tests
      assertNotNull(frontend);
      // Screen should not have displayed coordinates after reset.
      assert(!afterReset.contains("42.3") && !afterReset.contains("90.1"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

}
