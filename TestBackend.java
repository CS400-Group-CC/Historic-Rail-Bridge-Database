import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TestBackend {

  
  /**
   * Tests the constructor of the backend
   */
  @Test
  void testConstructor() {
    Backend2 backend = new Backend2(new String[0]);
    int bridges = backend.getNumBridges();
    
    assertEquals(1, bridges);
  }
  
  /**
   * Tests the get railroad function of the backend
   */
  @Test
  void testGetRailroad() {
    Backend2 backend = new Backend2(new String[0]);
    String result = backend.getRailroad();
    
    assertEquals("Universal", result);
  }
  
  /**
   * Tests the get region function of the backend
   */
  @Test
  void testGetRegion() {
    Backend2 backend = new Backend2(new String[0]);
    String result = backend.getRegion();
    
    assertEquals("Dodgeville-Jonesdale", result);
  }
  
  /**
   * Tests the get root function of the backend.
   */
  @Test
  void testGetRoot() {
    Backend2 backend = new Backend2(new String[0]);
    BridgeInterface result = backend.getRoot();
    
    assertEquals(result.getName(), "45");
  }
  
  /**
   * Tests the get next bridge function of the backend
   */
  @Test
  void testGetNext() {
    Backend2 backend = new Backend2(new String[0]);
    BridgeInterface result = backend.getNext(backend.getRoot());
    
    assertEquals(result, null);
  }

}
