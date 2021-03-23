DataWrangler README for Project Two (CS400 @ UW Madison)
========================================================

Name of DataWrangler: Joseph Peplinski
@wisc.edu Email of DataWrangler: jnpeplinski@wisc.edu
Group: CC
Team: Red

Files Written by Me:
--------------------
BridgeDataReader.java - Parses a csv file and generates a list of Bridge objects
Bridge.java - Contains information about a bridge/culvert, and a list of bridge and culvert versions
BridgeVersion.java - Contains information specific to a bridge
CulvertVersion.java - Contains information specific to a culvert
CitySearch.java - Parses a csv of US cities, and creates a red black tree of cities which can be searched, with the purpose of getting the city's coordinates
DataWranglerTests.java - Tests the basic functionality of classes related to the BridgeDataReader

(Interfaces were written with input from Jeremy Peplinski and other members of Group CC)
BackendInterface.java - Defines functions required for the Backend
BCVersionInterface.java - Defines functions common to BridgeVersions and CulvertVersions
BridgeDataReaderInterface.java - Defines functions required for the BridgeDataReader
BridgeInterface.java - Defines functions required for Bridge objects
BridgeVersionInterface.java - Defines specific functions required for iterations of a bridge/culvert which are a bridge
CulvertVersionInterface.java - Defines specific functions required for iterations of a bridge/culvert which are a culvert

ICBridges.csv - Contains information about Illinois Central rail bridges between Dodgeville and Jonesdale, written in collaboration with Jeremy Peplinski
CNWBridges.csv - Contains information about Chicago & Northwestern rail bridges between Montfort and Galena, written in collaboration with Jeremy Peplinski

Additional Contributions:
-------------------------
Helped solve problems with the Backend, helped write the project proposal, and helped communicate any issues found in the interfaces to the Blue team.  Also gave input on Frontend UI design.

Signature:
----------
Joseph Peplinski
(City-Lat-Long.csv data intended to be used with the CitySearch class was found here, with some information removed to reduce file size: https://public.opendatasoft.com/explore/dataset/us-zip-code-latitude-and-longitude/information/)
 
