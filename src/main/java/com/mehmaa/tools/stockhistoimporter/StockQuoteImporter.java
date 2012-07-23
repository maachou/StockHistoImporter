package com.mehmaa.tools.stockhistoimporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.avaje.ebean.Ebean;
import com.google.gson.Gson;
import com.mehmaa.tools.stockhistoimporter.json.QueryResult;
import com.mehmaa.tools.stockhistoimporter.json.Stock;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuote;
import com.mehmaa.tools.stockhistoimporter.model.StockEntity;

import de.hdtconsulting.yahoo.finance.Yapi;
import de.hdtconsulting.yahoo.finance.core.YHistoric;
import de.hdtconsulting.yahoo.finance.core.YQuote;
import de.hdtconsulting.yahoo.finance.core.YSymbol;
import de.hdtconsulting.yahoo.finance.server.csv.connection.YConnectionManager;

public class StockQuoteImporter {
    private static final Logger logger = Logger.getLogger(StockQuoteImporter.class);
    private YConnectionManager connectionManager;
    private Yapi yapi;
    private static int YAHOO_API_MAX_CONNECTION = 5;
    private static int INIT_DAY = 1;
    private static int INIT_MONTH = 0;
    private static int INIT_YEAR = 2000;
    private static String DATA_GRANULARITY = Yapi.HIST_DAYLY;

    private static StockQuoteImporter instance;

    /**
     * Constructor
     */
    protected StockQuoteImporter() {
	connectionManager = new YConnectionManager();
	connectionManager.setMaxConnections(YAHOO_API_MAX_CONNECTION);
	yapi = new Yapi();
	yapi.setConnectionManager(connectionManager);
    }

    /**
     * Getting the instance of StockQuoteImporter
     * 
     * @return StockQuoteImporter
     */
    public static StockQuoteImporter getInstance() {
	if (instance == null) {
	    instance = new StockQuoteImporter();
	}
	return instance;
    }

    /**
     * Process a stock
     * 
     * @param stockSymbol
     */
    public void importStockData(final String stockSymbol) {
	logger.debug("Cheking stock symbol " + stockSymbol + "...");
	Stock stockData = getSymbolDataFromYahoo(stockSymbol);
	if (stockData != null) {
	    StockEntity currentStock = getStockEntity(stockData);
	    ArrayList<DailyQuote> dailyQuotes = convertYQuotesToQuotes(getYhistoQuotes(stockSymbol), currentStock);
	    Ebean.save(dailyQuotes);
	} else {
	    logger.warn(stockSymbol + " is not a Valid symbol!");
	}

    }

    /**
     * TODO
     * 
     * @param yHistoList
     * @param currentStock
     * @return
     */
    private ArrayList<DailyQuote> convertYQuotesToQuotes(final ArrayList<YHistoric> yHistoList,
	    final StockEntity currentStock) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
	ArrayList<DailyQuote> result = new ArrayList<DailyQuote>();
	try {
	    for (YHistoric yHisto : yHistoList) {
		DailyQuote dailyQuote = new DailyQuote();
		dailyQuote.setDate((Date) dateFormat.parse(yHisto.getDate()));
		dailyQuote.setOpen(yHisto.getOpen());
		dailyQuote.setHigh(yHisto.getHigh());
		dailyQuote.setVolume(yHisto.getVolume());
		dailyQuote.setLow(yHisto.getLow());
		dailyQuote.setClose(yHisto.getClose());
		dailyQuote.setAdjClose(yHisto.getAdjClose());
		dailyQuote.setStock(currentStock);
		result.add(dailyQuote);
	    }
	} catch (ParseException e) {
	    logger.error("Error while converting a yHisto to a DailyQuote");
	    e.printStackTrace();
	}
	return result;
    }

    /**
     * TODO
     * 
     * @param stockData
     * @return
     */
    private StockEntity getStockEntity(final Stock stockData) {
	StockEntity stockEntity = Ebean.find(StockEntity.class).where().eq("symbol", stockData.getSymbol())
		.findUnique();
	if (stockEntity == null) {
	    stockEntity = new StockEntity(stockData.getSymbol(), stockData.getName());
	    Ebean.save(stockEntity);
	}
	return stockEntity;
    }

    /**
     * Get all stock daily quotes
     * 
     * @param stockId
     * @return
     */
    private ArrayList<YHistoric> getYhistoQuotes(final String stockId) {
	YSymbol symbol = new YSymbol(stockId);
	Calendar cal = Calendar.getInstance();
	Date endDate = cal.getTime();
	cal.clear();
	cal.set(INIT_YEAR, INIT_MONTH, INIT_DAY);
	Date startDate = cal.getTime();

	YQuote quote = yapi.getHistoric(symbol, startDate, endDate, DATA_GRANULARITY);

	return quote.getHistorics();
    }

    /**
     * Check if a stock symbol is valid
     * 
     * @param symbol
     * @return
     */
    private Stock getSymbolDataFromYahoo(final String symbol) {
	Stock result = null;
	try {
	    String yahooServiceUrl = readUrl("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + symbol
		    + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback");
	    Gson gson = new Gson();
	    QueryResult query = gson.fromJson(yahooServiceUrl, QueryResult.class);
	    List<Stock> stockResults = new ArrayList<Stock>();
	    for (Stock item : query.getResultSet().getResult()) {
		stockResults.add(item);
	    }
	    for (Stock stock : stockResults) {
		if (stock.getSymbol().equals(symbol)) {
		    result = stock;
		    break;
		}
	    }
	} catch (IOException e) {
	    logger.error("Error while trying to retrieve json content from yahoo finance suggestion service");
	    e.getMessage();
	}
	return result;
    }

    private static String readUrl(String urlString) throws IOException {
	BufferedReader reader = null;
	try {
	    URL url = new URL(urlString);
	    reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1)
		buffer.append(chars, 0, read);
	    String result = buffer.toString();
	    result = result.replace("YAHOO.Finance.SymbolSuggest.ssCallback(", "").replace(")", "");
	    return result;
	} finally {
	    if (reader != null)
		reader.close();
	}
    }
}
