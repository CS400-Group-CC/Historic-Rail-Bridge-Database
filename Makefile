# --== CS400 File Header Information ==--
# Name: Will Langas
# Email: wlangas@wisc.edu
# Team: CC-Red
# Role: Integration Manager
# TA: Xi Chen
# Lecturer: Gary Dahl
# Notes to Grader: None

# Main options that can be run with Makefile

run: compile
	export COLUMNS
	java Frontend CNWBridges.csv

compile: compileRedBlackTree compileSystems compileTests 

test: DataWranglerTests testBackend FrontEndDeveloperTests

testFrontend: compile
	java FrontEndDeveloperTests

testBackend: compile
	java testBackend

testData: compile
	java DataWranglerTests

clean:
	$(RM) *.class

# Compilation of component code systems: Red Black Tree, Bridge Database, Tests

compileRedBlackTree: SortedCollectionInterface.class RedBlackTree.class

compileSystems: BackendInterface.class Bridge.class BridgeVersion.class CulvertVersion.class BCVersionInterface.class BridgeDataReader.class Frontend.class CitySearch.class

compileTests: FrontEndDeveloperTests.class TestBackend.class DataWranglerTests.class

#RedBlackTree Functions

SortedCollectionInterface.class: SortedCollectionInterface.java
	javac SortedCollectionInterface.java
RedBlackTree.class: RedBlackTree.java
	javac RedBlackTree.java

# Railroad Database Functions

BackendInterface.class: BackendInterface.java
	javac BackendInterface.java
Backend.class: Backend.java
	javac Backend.java

Bridge.class: Bridge.java
	javac Bridge.java
BridgeInterface.class: BridgeInterface.java
	javac BridgeInterface.java

BridgeVersion.class: BridgeVersion.java
	javac BridgeVersion.java
BridgeVersionInterface.class: BridgeVersionInterface.java
	javac BridgeVersionInterface.java

CulvertVersion.class: CulvertVersion.java
	javac CulvertVersion.class
CulvertVersionInterface.class: CulvertVersionInterface.java
	javac CulvertVersionInterface.java

BCVersionInterface.class: BCVersionInterface.java
	javac BCVersionInterface.java

BridgeDataReader.class: BridgeDataReader.java
	javac BridgeDataReader.java
BridgeDataReaderInterface.class: BridgeDataReaderInterface.java
	javac BridgeDataReaderInterface.java

Frontend.class: Frontend.java
	javac Frontend.java

CitySearch.class: CitySearch.java
	javac CitySearch.java

#------------------------------------------------------------------

# Test functions

FrontEndDeveloperTests.class: FrontEndDeveloperTests.java
	javac FrontEndDeveloperTests.java

DataWranglerTests.class: DataWranglerTests.java
	javac  DataWranglerTests.java

TestBackend.class: TestBackend.java
	javac TestBackend.java
