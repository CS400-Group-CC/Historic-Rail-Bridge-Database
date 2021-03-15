import java.util.Scanner;
import java.util.zip.DataFormatException;

public class BridgeVersion implements BridgeVersionInterface {

  String specType, laType, haType, buildDate, fateDate, fate;
  double length, height, laLength, laHeight, haLength, haHeight;
  int spans, laSpans, haSpans;
  char approach;
  
  public BridgeVersion(Scanner input) throws DataFormatException {
    // TODO Auto-generated constructor stub
    specType = input.next();
    length = input.nextDouble();
    height = input.nextDouble();
    input.next();
    spans = input.nextInt();
    approach = input.next().charAt(0);
    if (approach == 'N') {
      for (int i = 0; i < 8; i++) {
        input.next();
      }
    } else if (approach == 'L') {
      laType = input.next();
      laLength = input.nextDouble();
      laHeight = input.nextDouble();
      laSpans = input.nextInt();
      for (int i = 0; i < 4; i++) {
        input.next();
      }
    } else if (approach == 'H') {
      for (int i = 0; i < 4; i++) {
        input.next();
      }
      haType = input.next();
      haLength = input.nextDouble();
      haHeight = input.nextDouble();
      haSpans = input.nextInt();
      
    } else if (approach == 'B') {
      laType = input.next();
      laLength = input.nextDouble();
      laHeight = input.nextDouble();
      laSpans = input.nextInt();
      haType = input.next();
      haLength = input.nextDouble();
      haHeight = input.nextDouble();
      haSpans = input.nextInt();
    } else {
      throw new DataFormatException("Unexpected approach type");
    }
    buildDate = input.next();
    fate = input.next();
    fateDate = input.next();
  }

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
    return specType;
  }

  @Override
  public double getLength() {
    // TODO Auto-generated method stub
    return length;
  }

  @Override
  public double getHeight() {
    // TODO Auto-generated method stub
    return height;
  }

  @Override
  public String getStartDate() {
    // TODO Auto-generated method stub
    return buildDate;
  }

  @Override
  public String getEndDate() {
    // TODO Auto-generated method stub
    return fateDate;
  }

  @Override
  public String getFate() {
    // TODO Auto-generated method stub
    return fate;
  }

  @Override
  public int getSpans() {
    // TODO Auto-generated method stub
    return spans;
  }

  @Override
  public char getApproachLoc() {
    // TODO Auto-generated method stub
    return approach;
  }

  @Override
  public String getLAType() {
    // TODO Auto-generated method stub
    return laType;
  }

  @Override
  public int getLASpans() {
    // TODO Auto-generated method stub
    return laSpans;
  }

  @Override
  public double getLALength() {
    // TODO Auto-generated method stub
    return laLength;
  }

  @Override
  public double getLAHeight() {
    // TODO Auto-generated method stub
    return laHeight;
  }

  @Override
  public String getHAType() {
    // TODO Auto-generated method stub
    return haType;
  }

  @Override
  public int getHASpans() {
    // TODO Auto-generated method stub
    return haSpans;
  }

  @Override
  public double getHALength() {
    // TODO Auto-generated method stub
    return haLength;
  }

  @Override
  public double getHAHeight() {
    // TODO Auto-generated method stub
    return haHeight;
  }

}
