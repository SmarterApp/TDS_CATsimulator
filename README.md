# Welcome to the Computer Adaptive Test (CAT) Simulator Project #
The web simulator allows authorized users to configure and simulate tests for a specified number of times on specified number of opportunities. This software provides a web interface to configure and manage simulations and run simulations asynchronously by spawning worker threads. 

## License ##
This project is licensed under the [AIR Open Source License v1.0](http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf).

## Getting Involved ##
We would be happy to receive feedback on its capabilities, problems, or future enhancements:

* For general questions or discussions, please use the [Forum](http://forum.opentestsystem.org/viewforum.php?f=9).
* Feel free to **Fork** this project and develop your changes!

## Module Overview
Following are the modules which make up the simulator. 

### tds.websim.web.ui
This provides the web user interface using javascript, html and jsf 

### tds.websim.presentation
Allows transfer of data between UI and services using JSON format

### tds.websim.model
Model classes to pass around data 

### tds.websim.bll
Business layer which allows tasks to be performed on the service components or beans 

### dbsimulator
This is the work horse module of the simulator spawning necessary threads, iterating and doing the simulation for specified number of times on opportunities

### tds.websim.dal
This module provides abstractions around session and itembank databases to give necessary data services. 

## Configuration
- Do not install the Simulator at the root directory of your server
- Setup the necessary itembank, config and session MySQL databases for simulation
- Load test packages necessary for simulation into the itembank
- User need to have an account to run simulations. Accounts are created by authorized users in the session database used for simulation.
- Change the settings.xml in tds.websim.web.ui module to specify the itembank, config and session databases to be used with the simulator and the database credentials

## Build Order
Build order is already setup in the checked-in pom files

## Dependencies

### Compile Time Dependencies

* itemscoring
* itemselectionshell
* sharedmultijar
* tdsdll
* testscoring
* catsimulator
* mysql

### Runtime Dependencies