Jiaje Chen and Ian Stewart
CS 32
4/15/15

Maps

Jiaje Chen's (jc124) KDTree and Ian Stewart's (is3) Bacon and Autocorrect were used.

Our project has one known bug in which certain portions of the path do not appropriately highlight after it has been sent from the front end. There are also some problems with the street query from the front end.

Our project is run through main, which parses options from the command line and uses them to generate a database, a SuggestionHelper, a KDTree, and a MapsManager, which contain the functions to search the database and perform the various kinds of queries needed to run maps.

Main then calls either processQueries, which loops continously and generates ReplParsers and Commands to read and execute user input, or runSparkServer, which launches a traffic manager to query the traffic server and a spark server to host a gui for user interaction. The gui relies on a number of handlers which lever the objects above to serve up data which is rendered on the front end in JavaScript onto an HTML canvas. 

Queries rely on a MapsManager, which creates a graph object, with or without a traffic server, to run dijkstras and construct a path Graph relies on database to read from SQL and maintain a cache of ways and nodes. MapsManagers also generate BoundingBox-like objects called tiles, which send compact rendering information to the front end.

Our various code bases were fit together primarily through manager and helper objects, which gave other classes and projects a neat API for accessing the functionality of the various projects without relying too much on the internal details. A number of these projects, including Bacon and Autocorrect, were modified to accomodate the specific requirements of Maps.

We made a number of optimizations to our maps to meet the performance demands. First, we cached extensively on both the front and the back end to avoid unnecesarily making costly network and database calls. We also made a number of efforts to only pull in data as it was needed, including by only pulling edges from the database when we wanted to search them, rather than on object creation. We also created several compact object classes, including CompactWay and WeightedNode, to store and transmit data in a space and computation efficient manner.

Our code can be compiled and tested with:
	mvn clean package && cs032_system_tester_slow -t 20 ./run tests/*
And run from the command line with:
	./run [--gui] [--port <int>] [--tport <int>] <db>

As of now, we have five CheckStyle errors. Four of them relate to GSONs type analysis functions, which lead to empty brackets about which CheckStyle complains. The last is from a default method, which CheckSyle doesn't recognize because default methods are so new.