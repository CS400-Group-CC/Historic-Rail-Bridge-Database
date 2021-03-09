import java.util.ArrayList;

public class TestBackend {

  @Test
  public void TestConstructor() {
    BridgeDataReaderDummy dummyReader = new BridgeDataReaderDummy(new String[0]);
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) dummyReader.readDataSet();
    
    boolean complete = false;
    if (bridges != null)
      complete = true;
    
    assertEquals(complete, true);
    
  }
  
  @Test
  public void TestGetName() {
    BridgeDataReaderDummy dummyReader = new BridgeDataReaderDummy(new String[0]);
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) dummyReader.readDataSet();
    
    BridgeInterface bridge = bridges.get(0);
    String name = bridge.getName();
    
    assertEquals(name, "45");
    
  }
  
  @Test
  public void TestGetNumBridges() {
    BridgeDataReaderDummy dummyReader = new BridgeDataReaderDummy(new String[0]);
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) dummyReader.readDataSet();
    
    int size = bridges.size();
    
    assertEquals(size, 1);
    
  }
  
  @Test
  public void TestGetRegion() {
    BridgeDataReaderDummy dummyReader = new BridgeDataReaderDummy(new String[0]);
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) dummyReader.readDataSet();
    
    String name = dummyReader.getRegion();
    
    assertEquals(name, "Dodgeville-Jonesdale");
    
  }
  
  @Test
  public void TestGetRailroad() {
    BridgeDataReaderDummy dummyReader = new BridgeDataReaderDummy(new String[0]);
    ArrayList<BridgeInterface> bridges = (ArrayList<BridgeInterface>) dummyReader.readDataSet();
    
    String name = dummyReader.getRailroad();
    
    assertEquals(name, "Universal");
    
  }
  
}
