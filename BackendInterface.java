import java.util.NoSuchElementException;

public interface BackendInterface {
  public String getRailroad();

  public String getRegion();

  public int getNumBridges();

  public String getFormatHelp();

  public BridgeInterface getBridge(String input)
      throws IllegalArgumentException, NoSuchElementException;

  public BridgeInterface getNearestBridge(double latitude, double longitude)
      throws IllegalArgumentException;

  public double getDistance(double latitude, double longitude, BridgeInterface bridge)
      throws IllegalArgumentException;
}


