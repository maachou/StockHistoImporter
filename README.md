## Description:
StockHistoImporter is a command line tool that imports historical stock quotes into a database for a given stock symbol.
## Application workflow:
This tool takes as an input a list of stock symbols and perform those following operations in order:

1. Check the validity of a symbol.
2. If the symbol is valid and does not exist in the database (T_STOCKS) he create the associated stock from the info retrieved from yahoo suggestion API and then store it in the database.
3. Retrieve and parse the last X years of historical quote data using Yahoo finance API (I found an implementation developed by [Hans-Dietel Thiel Consulting limited](http://www.hans-dieter-thiel.de/YahooFinance.96.0.html?&L=1)).
4. Store the results into the database(T_STOCK_HISTO_QUOTES).

If the stock already exists in the database he update it with fresh data.

## Configurations:
### ebeans.properties:
* Database parameters : In my case I used a MySQL database but you are free to choose between an H2, Oracle or PostgreSQL database.
Depending on your choice you have to setup your default datasource:

```properties
datasource.default=mysql  # MYSQL database (possible values: h2,ora,pg,mysql).
```

Then setup the database name , server hostname, port, user and password:

```properties
datasource.mysql.username=DB_USERNAME
datasource.mysql.password=DB_PASSWORD
datasource.mysql.databaseUrl=jdbc:mysql://DB_IP:DB_PORT/DB_NAME
```

* Database model generation: In order to generate properly the tables, we have to activate this lines:

```properties
ebean.ddl.generate=true
ebean.ddl.run=true
```
After running for the first time the generated Jar:  2 tables will be created T_STOCKS and T_STOCKS_HISTO_QUOTES.
We should now disable the above 2 options and regenerate our definitive jar.

* Model classes location : You should specify where Ebean will find the model classes

```properties
ebean.search.jars=StockHistoImporter-1.0-SNAPSHOT.jar
```
If you decide to change the name of the generated jar don't forget to update this parameter.

### Yahoo Finance API maven dependency:
I used in this project The Yahoo finance API implementation developed by  Hans-Dietel Thiel Consulting limited. 
Since  the project is not present in maven central repository.
You will have to checkout the project and compile it on your local machine so the project can compile successfully. 

To checkout the project execute the command :

```bash
svn co https://yahoofinanceapi.svn.sourceforge.net/svnroot/yahoofinanceapi yahoofinanceapi
```

Then execute a mvn install in the trunk folder.

## Build&Run:

* To build the application just run the script build.sh
* The application take as input 2 parameters: 
  * Stock symbols list with the option -s : ex.  -s GOOG YHOO
  * Total years of historical data with the option -y : ex. -y 10

An example of running the application would be:
```bash
java -jar target/StockHistoImporter-1.0-SNAPSHOT.jar -s YHOO GOOG -y 10
```
## Technologies used:

* Java.
* [Avaje Ebean ORM persistence Layer](http://www.avaje.org/).
* [Yahoo Finance API](http://finance.yahoo.com/).
* [Gson](http://code.google.com/p/google-gson/).
* [Joda time](http://joda-time.sourceforge.net/).
* [Junit](http://www.junit.org/).

