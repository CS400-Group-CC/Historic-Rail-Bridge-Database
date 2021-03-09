import java.util.List;

public interface BridgeDataReaderInterface {
    public List<BridgeInterface> readDataSet();
    public String getRailroad();
    public String getRegion();
    public String getBridgePrefix();
    public String getFormatHelp();
}

