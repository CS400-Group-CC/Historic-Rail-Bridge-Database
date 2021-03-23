// --== CS400 File Header Information ==--
// Name: Joseph Peplinski
// Email: jnpeplinski@wisc.edu
// Team: CC Red
// Role: Data Wrangler
// TA: Xi Chen
// Lecturer: Gary Dahl
// Notes to Grader: None

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * A class which allows the coordinates of US cities and towns with zip codes to be found by
 * searching by name for a city/town. This class was designed around zip code data compiled by
 * opendatasoft
 * 
 * @author Joseph Peplinski
 *
 */
public class CitySearch {
  /**
   * A private inner class which contains information about cities
   * 
   * @author Joseph Peplinski
   *
   */
  private static class City implements Comparable<City> {
    String city;
    String state;
    double lat;
    double lon;

    /**
     * A constructor for use with a bridge dataset
     * 
     * @param input a scanner containing one line of the .csv
     */
    private City(Scanner input) {
      input.useDelimiter(",");
      city = input.next();
      state = input.next();
      lat = input.nextDouble();
      lon = input.nextDouble();
      input.close();
    }

    /**
     * A constructor for use with the search function, where the coordinates are unknown
     * 
     * @param input a string of the format "[placename], [2 letter state name]"
     */
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

  /**
   * A constructor which takes a string input of the file path for the csv data set
   * 
   * @param fileLocation the location of the .csv file
   * @throws DataFormatException   if the file is not formatted as expected
   * @throws FileNotFoundException if the .csv file could not be found
   */
  public CitySearch(String fileLocation) throws DataFormatException, FileNotFoundException {
    // Confirm that the file location is a valid string
    if (fileLocation == null)
      throw new DataFormatException();
    File file = new File(fileLocation);
    Scanner input = new Scanner(file);
    input.useDelimiter(",");
    // Check if the .csv is empty, and throw an exception if it is
    if (!input.hasNext()) {
      input.close();
      throw new DataFormatException("City list could not be generated with an empty data set");
    }
    // Ignore the header
    input.nextLine();
    // Insert cities to the tree
    while (input.hasNextLine()) {
      try {
        tree.insert(new City(new Scanner(input.nextLine())));
      } catch (IllegalArgumentException e) {
        // This would be thrown if there are repeat cities in the dataset, which we will ignore
      }
    }
    input.close();
  }

  /**
   * A search method which will find the coordinates of the city searched for
   * 
   * @param city a string of the format "[placename], [2 letter state name]"
   * @return an array of doubles containing the latitude and longitude
   * @throws NoSuchElementException if the city was not found in the data set
   */
  public double[] getCityPosition(String city) throws NoSuchElementException {
    // Create a city object for comparison
    City searchedCity = new City(city);
    RedBlackTree.Node<City> current = tree.root;
    double[] output = new double[2];
    boolean found = false;
    // Begin searching through the tree to find the city
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
          // If this case is reached, the city is not contained in the data set
          throw new NoSuchElementException();
        }

      }
      if (searchedCity.compareTo(current.data) < 0) {
        if (current.leftChild != null) {
          current = current.leftChild;
          continue;
        } else {
          // If this case is reached, the city is not contained in the data set
          throw new NoSuchElementException();
        }
      }
    }
    return output;
  }
}
