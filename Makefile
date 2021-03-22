# --== CS400 File Header Information ==--
# Name: Will Langas
# Email: wlangas@wisc.edu
# Team: CC-Red
# Role: Integration Manager
# TA: Xi Chen
# Lecturer: Gary Dahl
# Notes to Grader: None

# Main options that can be run with Makefile

runCNW: compile
	export COLUMNS
	java Frontend CNWBridges.csv

runIC: compile
	export COLUMNS
	java Frontend ICBridges.csv

compile: compileRedBlackTree compileSystems compileTests 

testAll: compile
	java -jar junit5.jar -cp . --scan-classpath

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
	javac -cp .:junit5.jar FrontEndDeveloperTests.java 

DataWranglerTests.class: DataWranglerTests.java
	javac -cp .:junit5.jar DataWranglerTests.java

TestBackend.class: TestBackend.java
	javac -cp .:junit5.jar TestBackend.java

Backend2.class: Backend2.java
	javac Backend2.java

BridgeDataReaderDummy.class: BridgeDataReaderDummy.class
	javac BridgeDataReaderDummy.java
