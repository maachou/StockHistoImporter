package com.mehmaa.tools.stockhistoimporter;

import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.avaje.ebean.EbeanServer;

/**
 * The entry class
 * 
 * @author mehdimaachou
 * 
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class);
    private static final CommandLine commandLine = new CommandLine();
    private static CmdLineParser parser;
    private static EbeanServer server;

    /**
     * The main method
     * 
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
	SLF4JBridgeHandler.install();
	parseCommandLine(args);
    }

    // public static EbeanServer getEbeanServer() {
    // if (server == null) {
    // ServerConfig config = new ServerConfig();
    // config.setName("mysql");
    // DataSourceConfig mysqlDb = new DataSourceConfig();
    // mysqlDb.setDriver("com.mysql.jdbc.Driver");
    // mysqlDb.setUsername("root");
    // mysqlDb.setPassword("mehdiroot");
    // mysqlDb.setUrl("jdbc:mysql://192.168.1.40:3306/StockHistoDB");
    // mysqlDb.setHeartbeatSql("select count(*) from t_one");
    // config.setDataSourceConfig(mysqlDb);
    // config.setDdlGenerate(true);
    // config.setDdlRun(true);
    // config.setDefaultServer(false);
    // config.setRegister(false);
    // config.addClass(DailyQuote.class);
    // config.addClass(StockEntity.class);
    // server = EbeanServerFactory.create(config);
    // }
    // return server;
    // }

    /**
     * Parsing the command line args
     * 
     * @param args
     */
    private static void parseCommandLine(final String[] args) {
	parser = new CmdLineParser(commandLine);
	try {
	    parser.parseArgument(args);
	    if (commandLine.isHelp()) {
		printUsageAndExit();
	    }
	    if (commandLine.isSymbol() && !commandLine.getStockSymbols().isEmpty()) {
		processStocks(commandLine.getStockSymbols());
	    }

	} catch (final CmdLineException e) {
	    logger.error(e);
	    printUsageAndExit();
	}
    }

    /**
     * Printing Help
     */
    private static void printUsageAndExit() {
	System.out.println("usage: java -jar <name-of-jar> [options...]");
	parser.printUsage(System.out);
	System.exit(-1);
    }

    /**
     * Method that processes the xml source file
     */
    private static void processStocks(final List<String> stockSymbols) {
	StockQuoteImporter stockImporter = StockQuoteImporter.getInstance();
	for (String symbol : stockSymbols) {
	    stockImporter.importStockData(symbol);
	}
    }
}
