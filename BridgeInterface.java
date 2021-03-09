
public interface BridgeInterface extends Comparable<BridgeInterface> {
  public String getName();

  public double getLat();

  public double getLon();

  public int getNumVersions();

  public BCVersionInterface getVersion(int index);
}
