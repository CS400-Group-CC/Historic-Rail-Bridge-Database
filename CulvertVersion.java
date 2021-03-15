import java.util.Scanner;

public class CulvertVersion implements CulvertVersionInterface {

  String specType, buildDate, fateDate, fate;
  double length, height, width;
  
  public CulvertVersion(Scanner input) {
    // TODO Auto-generated constructor stub
    specType = input.next();
    length = input.nextDouble();
    height = input.nextDouble();
    width = input.nextDouble();
    for (int i = 0; i < 10; i++) {
      input.next();
    }
    buildDate = input.next();
    fate = input.next();
    fateDate = input.next();
  }

  @Override
  public boolean isBridge() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isCulvert() {
    // TODO Auto-generated method stub
    return true;
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
  public double getWidth() {
    // TODO Auto-generated method stub
    return width;
  }

}
