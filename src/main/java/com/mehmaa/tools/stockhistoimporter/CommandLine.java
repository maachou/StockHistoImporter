package com.mehmaa.tools.stockhistoimporter;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Command line parser filled by args4J
 * 
 * @author mehdimaachou
 * 
 */
public class CommandLine {

    @Option(name = "--help", usage = "Print Help", aliases = { "--help", "-h" })
    private boolean help;

    @Option(name = "--symbol", usage = "Import historical data of a stock or a list of stocks", aliases = { "-s" })
    private boolean symbol = false;

    @Argument
    private List<String> stockSymbols = new ArrayList<String>();

    @Option(name = "--history", usage = "Total in years of historical data", aliases = { "-y" })
    private int yearsHistory;

    public boolean isSymbol() {
	return symbol;
    }

    public boolean isHelp() {
	return help;
    }

    public void setSymbol(final boolean symbol) {
	this.symbol = symbol;
    }

    public void setHelp(final boolean help) {
	this.help = help;
    }

    public List<String> getStockSymbols() {
	return this.stockSymbols;
    }

    public void setStockSymbols(List<String> stockSymbols) {
	this.stockSymbols = stockSymbols;
    }

    public int getYearsHistory() {
	return yearsHistory;
    }

    public void setYearsHistory(int yearsHistory) {
	this.yearsHistory = yearsHistory;
    }
}
