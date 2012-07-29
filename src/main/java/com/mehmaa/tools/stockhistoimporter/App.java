package com.mehmaa.tools.stockhistoimporter;

import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.bridge.SLF4JBridgeHandler;

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

    /**
     * Parsing the command line args
     * 
     * @param args
     */
    private static void parseCommandLine(final String[] args) {
	parser = new CmdLineParser(commandLine);
	try {
	    /* Parsing input arguments */
	    parser.parseArgument(args);
	    /* process Help command */
	    if (commandLine.isHelp()) {
		printUsageAndExit();
	    }
	    /* process import symbol list command */
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
     * Method that processes a list of stock symbols
     * 
     * @param a
     *            list of stock symboles
     */
    private static void processStocks(final List<String> stockSymbols) {
	StockQuoteImporter stockImporter = StockQuoteImporter.getInstance();
	for (String symbol : stockSymbols) {
	    stockImporter.importStockData(symbol);
	}
    }
}
