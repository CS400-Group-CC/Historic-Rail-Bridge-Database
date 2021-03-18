import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class CitySearch {
  private static class City implements Comparable<City> {
    String city;
    String state;
    double lat;
    double lon;

    private City(Scanner input) {
      input.useDelimiter(",");
      city = input.next();
      state = input.next();
      lat = input.nextDouble();
      lon = input.nextDouble();
      input.close();
    }

    private City(String input) {
      city = input.substring(0, input.indexOf(", "));
      state = input.substring(input.indexOf(", ") + 2);
    }

    public double[] getPos() {
      double[] output = {lat, lon};
      return output;
    }

    @Override
    public int compareTo(City o) {
      return ((city + ", " + state).toLowerCase())
          .compareTo((o.city + ", " + o.state).toLowerCase());
    }

  }

  RedBlackTree<City> tree = new RedBlackTree<City>();

  public CitySearch(String fileLocation) throws DataFormatException, FileNotFoundException {
    if (fileLocation == null)
      throw new DataFormatException();
    File file = new File(fileLocation);
    Scanner input = new Scanner(file);
    input.useDelimiter(",");
    if (!input.hasNext()) {
      input.close();
      throw new DataFormatException("City list could not be generated with an empty data set");
    }
    if (!input.nextLine().equals("City,State,Latitude,Longitude")) {
      input.close();
      throw new DataFormatException();
    }
    while (input.hasNextLine()) {
      try {
        tree.insert(new City(new Scanner(input.nextLine())));
      } catch (IllegalArgumentException e) {

      } catch (Exception unexpected) {
        input.close();
        throw new DataFormatException();
      }
    }
    input.close();
  }

  public double[] getCityPosition(String city) {
    City searchedCity = new City(city);
    RedBlackTree.Node<City> current = tree.root;
    double[] output = new double[2];
    boolean found = false;
    while (!found) {
      if (searchedCity.compareTo(current.data) == 0) {
        output = current.data.getPos();
        found = true;
        continue;
      }
      if (searchedCity.compareTo(current.data) > 0) {
        if (current.rightChild != null) {
          current = current.rightChild;
          continue;
        } else {
          throw new NoSuchElementException();
        }

      }
      if (searchedCity.compareTo(current.data) < 0) {
        if (current.leftChild != null) {
          current = current.leftChild;
          continue;
        } else {
          throw new NoSuchElementException();
        }
      }
    }
    return output;
  }
}
