#TFTP Iteration One


## Setup Instructions
Set your eclipse workspace to the top level, which is the project root directory.  Eclipse should automatically recognize and import the project. 

If that did not work, follow these steps:
Ensure the Java Perspective is open in Eclipse.

Create the Core project:
-In the package explorer in eclipse, right click and select 'New' -> 'Java Project'
-In the Project name text box, type in 'core'
-Click Finish
This project will now be recognized and imported.  Repeat this process with the project names: 'client', 'server', and 'errorSimulator'.

The client, server, and errorSimulator projects will now show errors.  The 'core' project is required to be on the buildpath of the other three projects.  To add the 'core' project to the buildpath or 'client':
-Right click on the 'client' project
-Click 'properties'
-Click 'java build path'
-Click the 'projects' tab
-Click the 'add' button
-Select the checkbox next to the 'core' project
-Click OK
Repeat this process for the 'server' project and the 'errorSimulator' project.

Eclipse should now be configured properly.


## Running Instructions

### Server
To run the server, run the server project from eclipse.
To shutdown the server, type 'shutdown' in the servers command line interface.

### Error Simulator
To run the Error Simulator, run the error simulator from eclipse.
To shutdown the Error Simulator, type 'shutdown' in the error simulators command line interface.

### Client
By default, client will connect directly to the ser when run through eclipse.  To have the client run through the error simulator, run the client with the -t command line argument.
To see the usage information, type 'help' in the cli.  
To perform a read operation from the server to the client, type 'read' followed by a space and the filename.
To perform a write operation from the client to the server, type 'write' followed by a space and the filename.

#### Command Line Arguments
To enter a command line argument in eclipse:
-Right click on the client project and select 'Run as' -> 'Run Configurations'
-Ensure that the client project is selected in the tree view in the left hand side of the popup window
-Select the 'Arguments' tab on the right hand side
-In the 'Program Arguments' text box, add -t

## Project Structure
The file TeamResponsibilities.txt describes the responsibilities of each team member for this iteration.  The document is split into a different section for each member, and lists their contributions. 
The file TestPlan.pdf describes the test procedure followed to ensure correct functionality of the program.  It also describes the command line arguments and cli commands available for the client, server, and error simulator.
The javadoc for each project is located in that projects doc folder.
There is a top level folder called ucms where the ucm diagrams are located.

The source code for this deliverable is split up into four main projects: core, client, errorSimulator, and server.  
Core - The core project contains all of the common core functionality shared between projects.  The core project contains all code relating to the TFTP standard.  This project is a dependency for all other projects.
Client - The client project contains the code specific to the client application.
errorSimulator - The errorSimulator project contains the code specific to the Error Simulator application.
Server - The server project contains the code specific to the server application.


##UCM Diagrams

###Request
This diagram demonstrates the flow of a request from the client to the server and its response.  This is generic, as the same logic is used for a read and a write request.

###Read Transfer
This shows the steady state transfer when reading from a file on the server to the client.

###Write Transfer
This shows the steady state transfer when writing to a file on the server from the client.


## Source Code Structure

### Client
The client project contains only the client class.  This class is responsible for parsing command line arguments, starting the clients command line interface, and starting the client.  This contains the main method for the client application.

### Error Simulator
The error simulator project is responsible for relaying datagram packets between the client and the server.  The main method for the error simulator application is contained in the 'ErrorSimulator.java' class.

### Server
The server project contains only the server class.  This class is responsible for starting the servers command line interface, and starting the server.  This class contains the main method for the server application

### Core
The core project contains functionality needed by the client, server, and errorSimulator projects.  It has several packages: core.cli, core.ctrl, core.log, core.net, core.req, and core.util

#### core.cli
This package contains the generic implementation of the command line interface.

#### core.ctrl
This package contains the parent classes of the client, server, and errorSimulator.  Its purpose is to abstract client and server logic to allow it to be reused by the error simulator.

#### core.log
This package is responsible for setting up a logger that allows a global logging severity level to be defined.

#### core.net
This package contains the core file transfer logic and network operations.  This includes actions such as writing to sockets, reading from sockets, etc.

#### core.req
This package contains the logic for encoding and decoding TFTP protocol messages.  

#### core.util
This package contains two utility classes: ByteUtils.java and Worker.java

##### ByteUtils.java
This class contains static methods to find the index of a value in a byte array, and to convert a byte array to a String.

##### Worker.java
This is an abstract base class for long running asynchronous jobs.

For further details on any specific class from a core package, refer to the provided javadoc.  


