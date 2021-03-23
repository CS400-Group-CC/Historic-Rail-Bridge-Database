import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import org.junit.jupiter.api.Test;

//--== CS400 File Header Information ==--
//Name: Colby Brown
//Email: csbrown7@wisc.edu
//Team: CC Red
//Role: Backend Developer
//TA: Xi Chen
//Lecturer: Gary Dahl
//Notes to Grader:

class TestBackend {

  
  /**
   * Tests the constructor of the backend
   */
  @Test
  void testConstructor() {
    Backend backend = null;
    try {
      backend = new Backend(new Scanner("Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\n" + 
          "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\n" + 
          "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1450 1/2,42.535809,-90.376592,C,W.B.C.,18,1.333,1.5,,,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1451,42.532987,-90.37749,B,P.B.,15.6,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"));
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
    
    
    int bridges = backend.getNumBridges();
    
    
    assertEquals(3, bridges);
  }
  
  /**
   * Tests the get railroad function of the backend
   */
  @Test
  void testGetRailroad() {
    Backend backend = null;
    try {
      backend = new Backend(new Scanner("Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\n" + 
          "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\n" + 
          "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1450 1/2,42.535809,-90.376592,C,W.B.C.,18,1.333,1.5,,,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1451,42.532987,-90.37749,B,P.B.,15.6,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"));
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
    
    
    String result = backend.getRailroad();
    
    assertEquals("Chicago & North Western", result);
  }
  
  /**
   * Tests the get region function of the backend
   */
  @Test
  void testGetRegion() {
    Backend backend = null;
    try {
      backend = new Backend(new Scanner("Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\n" + 
          "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\n" + 
          "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1450 1/2,42.535809,-90.376592,C,W.B.C.,18,1.333,1.5,,,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1451,42.532987,-90.37749,B,P.B.,15.6,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"));
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
    
    String result = backend.getRegion();
        
    assertEquals("Galena-Cuba City", result);
  }
  
  /**
   * Tests the get root function of the backend.
   */
  @Test
  void testGetRoot() {
    Backend backend = null;
    try {
      backend = new Backend(new Scanner("Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\n" + 
          "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\n" + 
          "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1450 1/2,42.535809,-90.376592,C,W.B.C.,18,1.333,1.5,,,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1451,42.532987,-90.37749,B,P.B.,15.6,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"));
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
    
    BridgeInterface result = backend.getRoot();
        
    assertEquals(result.getName(), "Bridge 1450 1/2");
  }
  
  /**
   * Tests the get next bridge function of the backend
   */
  @Test
  void testGetNext() {
    Backend backend = null;
    try {
      backend = new Backend(new Scanner("Railroad,Region,Bridge Prefix,Number Divider,Format Help,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Chicago & North Western,Galena-Cuba City,Bridge , ,C&NW bridges are named with as ''Bridge [Number] [Suffix]'' where the number is assigned incrementally along the trackage.  Numbers are initially assigned integers with spurs being given a letter suffix appended to the number of the next-lowest bridge number before the junction.  Bridges added after the initial assignment are given a fractional suffix denoting their placement between two bridges.  An example is Bridge 1450 1/2 being added between Bridge 1450 and Bridge 1451.  Further additions work by placing another fraction halfway between the relevant bridge numbers (such as 1450 3/4 between 1450 1/2 and 1451).,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Universal,,,Original,(shared),,,(culvert),(bridge),,,,,,,,,,(universal),,,First Rebuild,,,,,,,,,,,,,,,,,,Further Rebuilds continue cycle,,,,,,,,,,,,,,,,,\n" + 
          "Number,Lat,Long,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date,Basic Type,Spec. Type,Length,Height,Width/Dia,Spans,Approach,LA Type,LA Length,LA Height,LA Spans,HA Type,HA Length,HA Height,HA Spans,Built,Fate,Fate Date\n" + 
          "Bridge 1450,42.540064,-90.375658,B,P.R.S.,64,-1,,1,N,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1450 1/2,42.535809,-90.376592,C,W.B.C.,18,1.333,1.5,,,,,,,,,,,pre-1917,Removed,11-30-1942,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" + 
          "Bridge 1451,42.532987,-90.37749,B,P.B.,15.6,-1,,1,N,,,,,,,,,pre-1917,Removed,5-30-1941,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"));
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
    
    BridgeInterface result = backend.getNext(backend.getRoot());
        
    assertEquals("Bridge 1451", result.toString());
  }

}
