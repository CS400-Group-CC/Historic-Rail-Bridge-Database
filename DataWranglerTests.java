// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class DataWranglerTests {
  String cnwString;
  String icString;
  BridgeDataReaderInterface cnwReader;
  BridgeDataReaderInterface icReader;

  @BeforeEach
  public void createInstance() {
    String cnwString =
        "Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\r\n" + 
        "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\r\n" + 
        "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1450 1/2,42.535809,-90.376592,C,W.B.C.,18,1.333,1.5,,,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1451,42.532987,-90.37749,B,P.B.,15.6,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1452,42.530087,-90.378563,B,I.B.,8.5,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1453,42.528865,-90.379038,B,P.B.,16,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1454,42.524838,-90.380649,B,P.B.,39.5,-1,,3,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1455,42.520434,-90.381838,B,P.R.S.,65.25,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1457,42.515294,-90.380762,B,P.B.,15.9,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1458,42.513591,-90.381276,C,W.B.C.,40,1.5,1.5,,,,,,,,,,,pre-1917,Removed,5-29-1934,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1459,42.51294,-90.381808,B,Unknown,-1,-1,,1,N,,,,,,,,,pre-1911,Removed,5-1911,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1435,42.576671,-90.395912,B,F.B.,72.7,-1,,5,N,,,,,,,,,pre-1917,Rebuilt,8-22-1917,B,P.B.,89.25,-1,,7,N,,,,,,,,,8-22-1917,Abandoned,1968,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1426,42.599641,-90.4159,B,P.B.,51.8,-1,,4,N,,,,,,,,,pre-1917,Rebuilt,1920,B,P.B.,65,-1,,5,N,,,,,,,,,1920,Rebuilt,2-1950,B,P.B.,51.8,-1,,4,N,,,,,,,,,2-1950,Abandoned,1968\r\n" + 
        "Bridge 1450 C,42.537143,-90.370554,B,P.R.S.,80,-1,,1,H,,,,,P.B.,125,-1,7,1917,Removed,1937,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1498,42.417696,-90.420749,B,T.R.S.,175,-1,,1,B,P.B.,8.1,-1,1,P.R.S.,75,-1,1,pre-1917,Removed,1939,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1496,42.419936,-90.41129,B,P.B.,15.7,-1,,1,N,,,,,,,,,pre-1917,Rebuilt,9-1932,B,P.B.,-1,-1,,2,N,,,,,,,,,9-1932,Abandoned,1939,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1950 B,42.537928,-90.374373,B,I.B.,80,-1,,1,N,,,,,,,,,1917,Removed,1937,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Bridge 1437,42.573425,-90.390835,B,P.B.,6.4,-1,,1,N,,,,,,,,,pre-1917,Rebuilt,7-1927,C,Conc. P.,32,4,4,,,,,,,,,,,7-1927,Abandoned,1968,,,,,,,,,,,,,,,,,,\r\n" + 
        "";
    String icString =
        "Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Illinois Central,Dodgeville-Jonesdale,V-,.,IC bridges on the Dodgeville-Red Oak branch are named with as ''V-[Number].[Suffix]'' where the number and suffix form the nearest tenth-mile mile post on the division.  Mile posts begin at Red Oak and increase towards Dodgeville.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\r\n" + 
        "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\r\n" + 
        "V-57.4,42.96424,-90.1307,B,D.P.G.,61.2,16,,2,B,O.D.F.T.,97,16,-1,O.D.F.T.,67,16,-1,1888,Rebuilt,6/27/1940,B,D.P.G. CREO. P.,61.2,61.2,16,2,B,O.D.F.T.,97,16,-1,O.D.F.T.,67,16,-1,6/27/1940,Removed,8/17/1942,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-56.7,42.96081,-90.11929,B,STO. Bx.,4,2,,-1,N,,,,,,,,,1888,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-56.5,42.9587,-90.11489,B,S.B.D.T. CREO.,40,12,,-1,N,,,,,,,,,1914,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-56.1,42.95392,-90.11043,B,O.D.F.T.,476,60,,-1,N,,,,,,,,,1898,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-55.7,42.95089,-90.10536,B,O.D. FRAME,336,58,,-1,N,,,,,,,,,1899,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-55.6,42.94959,-90.10225,B,B.D.R.T. CREO.,110,26,,-1,N,,,,,,,,,1916,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-55.2,42.94884,-90.09519,B,STD. AR.,4,12,,-1,N,,,,,,,,,1888,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-55.1,42.94832,-90.09384,B,D.P.G. PILE PIERS,30,30,,1,B,B.D.P.T.,82,30,-1,B.D.P.T.-CREO. P.,72,30,-1,1917,Removed,8/17/1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-53.8,42.93255,-90.08451,B,O.D.F.T.,479,38,,36,N,,,,,,,,,1887,Rebuilt,1920,B,P.D.F.T. CREO. P.,479,38,,36,N,,,,,,,,,1920,Removed,8/17/1942,,,,,,,,,,,,,,,,,,\r\n" + 
        "V-53.7,42.9307,-90.08477,B,O.H. HWY.,50,21.9,,-1,N,,,,,,,,,1902,Removed,Unknown,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\r\n" + 
        "";
    try {
      cnwReader = new BridgeDataReader(new Scanner(cnwString));
      assertEquals(true, cnwReader != null);
    } catch (DataFormatException e) {
      fail("Exception thrown with what should be a valid input: " + e.getMessage());
    }
    try {
      icReader = new BridgeDataReader(new Scanner(icString));
      assertEquals(true, icReader != null);
    } catch (DataFormatException e) {
      fail("Exception thrown with what should be a valid input: " + e.getMessage());
    }


  }

  /**
   * Tests both constructors to confirm exceptions are thrown when expected, and that no exceptions
   * are thrown by valid data sets.
   */
  @Test
  public void testConstructor() {
    try {
//      try {
//        Scanner test = null;
//        BridgeDataReaderInterface constructorTest = new BridgeDataReader(test);
//        fail("No exception thrown when DataFormatException was expected");
//      } catch (DataFormatException e) {
//      }
//      try {
//        BridgeDataReaderInterface constructorTest = new BridgeDataReader(new Scanner(""));
//        fail("No exception thrown when DataFormatException was expected");
//      } catch (DataFormatException e) {
//      }
//      try {
//        BridgeDataReaderInterface constructorTest = new BridgeDataReader(new String[1]);
//        fail("No exception thrown when DataFormatException was expected");
//      } catch (IllegalArgumentException e) {
//      }

      assertEquals(true, cnwReader != null);
      assertEquals(true, icReader != null);
    } catch (Exception e) {
      fail("Unexpected exception thrown: " + e.toString());
    }

  }

  /**
   * Tests getRailroad() on two valid data sets to confirm that the code returns these Strings from
   * the dataset correctly.
   */
  @Test
  public void testGetRailroad() {
    try {
      assertEquals(true, cnwReader.getRailroad().equals("Chicago & North Western"));
      assertEquals(true, icReader.getRailroad().equals("Illinois Central"));
    } catch (Exception e) {
      fail("This test should not throw any exceptions");
    }
  }

  /**
   * Tests getRegion() on two valid data sets to confirm that the code returns these Strings from
   * the dataset correctly.
   */
  @Test
  public void testGetRegion() {
    try {
      assertEquals(true, cnwReader.getRegion().equals("Galena-Cuba City"));
      assertEquals(true, icReader.getRegion().equals("Dodgeville-Jonesdale"));
    } catch (Exception e) {
      fail("This test should not throw any exceptions");
    }
  }

  /**
   * Tests getBridgePrefix() on two valid data sets to confirm that the code returns these Strings
   * from the dataset correctly.
   */
  @Test
  public void testGetBridgePrefix() {
    try {
      assertEquals(true, cnwReader.getBridgePrefix().equals("Bridge "));
      assertEquals(true, icReader.getBridgePrefix().equals("V-"));
    } catch (Exception e) {
      fail("This test should not throw any exceptions");
    }
  }

  /**
   * Tests getFormatHelp() on two valid data sets to confirm that the code returns these Strings
   * from the dataset correctly.
   * 
   */
  @Test
  public void testGetFormatHelp() {
    try {
      String cnwFormatHelp =
          "C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).";
      String icFormatHelp =
          "IC bridges on the Dodgeville-Red Oak branch are named with as ''V-[Number].[Suffix]'' where the number and suffix form the nearest tenth-mile mile post on the division.  Mile posts begin at Red Oak and increase towards Dodgeville.";
      assertEquals(true, cnwReader.getFormatHelp().equals(cnwFormatHelp));
      
      assertEquals(true, icReader.getFormatHelp().equals(icFormatHelp));
    } catch (Exception e) {
      fail("This test should not throw any exceptions");
    }
  }

  /**
   * Tests getBridges(), confirming that the list length and bridge order is as seen in the dataset.
   */
  @Test
  public void testGetBridges() {
    try {
      assertEquals(17, cnwReader.getBridges().size());
      assertEquals(10, icReader.getBridges().size());
      assertEquals(42.540064, cnwReader.getBridges().get(0).getLat());
      assertEquals(-90.08477, icReader.getBridges().get(9).getLon());
    } catch (Exception e) {
      fail("No exceptions should be thrown by this test");
    }

  }

  /**
   * Tests compareTo() to confirm that it is functioning correctly in a variety of expected
   * situations.
   */
  @Test
  public void testCompareTo() {
    try {
      // Chicago & North Western tests
      // Compare 1450 1/2 to 1450, 1450 1/2 should be bigger
      assertEquals(1, cnwReader.getBridges().get(1).compareTo(cnwReader.getBridges().get(0)));
      // Compare 1452 to 1450 1/2, 1452 should be bigger

      assertEquals(1, cnwReader.getBridges().get(3).compareTo(cnwReader.getBridges().get(0)));
      // Compare 1450 to 1450 C, 1450 C should be bigger
      assertEquals(1, cnwReader.getBridges().get(12).compareTo(cnwReader.getBridges().get(0)));
      // Compare 1450 B to 1450 C, 1450 B should be smaller
      assertEquals(1, cnwReader.getBridges().get(12).compareTo(new Bridge("Bridge 1450 B", cnwReader.getBridgePrefix(), cnwReader.getNumberDivider())));
      // Compare 1450 B to 1450 1/2, 1450 B should be bigger
      assertEquals(1, cnwReader.getBridges().get(15).compareTo(cnwReader.getBridges().get(1)));

      // Illinois Central tests
      // Compare 56.7 to 56.5, 56.7 should be bigger
      assertEquals(1, icReader.getBridges().get(1).compareTo(icReader.getBridges().get(2)));
      // Compare 56.1 to 56.5, 56.1 should be smaller
      assertEquals(-1, icReader.getBridges().get(3).compareTo(icReader.getBridges().get(2)));

      // General tests
      // Compare a bridge to itself, they should be equal
      assertEquals(0, cnwReader.getBridges().get(10).compareTo(cnwReader.getBridges().get(10)));
      // Compare a newly created bridge object to a bridge in a list, which will be done for
      // searches
      assertEquals(0, cnwReader.getBridges().get(0).compareTo(new Bridge("Bridge 1450", cnwReader.getBridgePrefix(), cnwReader.getNumberDivider())));
    } catch (Exception e) {
      fail("No exceptions should be thrown by compareTo method, but an exception was thrown: " + e.getClass() + " " + e.getMessage());
    }
  }

}
