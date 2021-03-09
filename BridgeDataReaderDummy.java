import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BridgeDataReaderDummy implements BridgeDataReaderInterface {

  public BridgeDataReaderDummy(String[] args) {
    
  }
  
  public BridgeDataReaderDummy(Scanner input) {
    
  }
  
  @Override
  public List<BridgeInterface> readDataSet() {
    
    ArrayList<BridgeInterface> bridges = new ArrayList<BridgeInterface>();
    
    bridges.add(new BridgeInterface() {

      @Override
      public int compareTo(BridgeInterface o) {
        // TODO Auto-generated method stub
        return 0;
      }

      @Override
      public String getName() {
        // TODO Auto-generated method stub
        return "45";
      }

      @Override
      public double getLat() {
        // TODO Auto-generated method stub
        return 45.0;
      }

      @Override
      public double getLon() {
        // TODO Auto-generated method stub
        return 50.0;
      }

      @Override
      public int getNumVersions() {
        // TODO Auto-generated method stub
        return 1;
      }

      @Override
      public BCVersionInterface getVersion(int index) {
        // TODO Auto-generated method stub
        return new BCVersionInterface() {

          @Override
          public boolean isBridge() {
            // TODO Auto-generated method stub
            return true;
          }

          @Override
          public boolean isCulvert() {
            // TODO Auto-generated method stub
            return false;
          }

          @Override
          public String getSpecificType() {
            // TODO Auto-generated method stub
            return "Bridge";
          }

          @Override
          public double getLength() {
            // TODO Auto-generated method stub
            return 60;
          }

          @Override
          public double getHeight() {
            // TODO Auto-generated method stub
            return 110;
          }

          @Override
          public String getStartDate() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public String getEndDate() {
            // TODO Auto-generated method stub
            return null;
          }

          @Override
          public String getFate() {
            // TODO Auto-generated method stub
            return null;
          }
          
        };
      }
      
    });
    
    return null;
  }

  @Override
  public String getRailroad() {
    // TODO Auto-generated method stub
    return "Universal";
  }

  @Override
  public String getRegion() {
    // TODO Auto-generated method stub
    return "Dodgeville-Jonesdale";
  }

  @Override
  public String getBridgePrefix() {
    // TODO Auto-generated method stub
    return "V-";
  }

  @Override
  public String getFormatHelp() {
    // TODO Auto-generated method stub
    return null;
  }

}