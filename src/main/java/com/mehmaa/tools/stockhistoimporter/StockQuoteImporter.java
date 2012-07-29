package com.mehmaa.tools.stockhistoimporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.gson.Gson;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuote;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuotesAggregate;
import com.mehmaa.tools.stockhistoimporter.model.StockEntity;
import com.mehmaa.tools.stockhistoimporter.parse.json.QueryResult;
import com.mehmaa.tools.stockhistoimporter.parse.json.Stock;

import de.hdtconsulting.yahoo.finance.Yapi;
import de.hdtconsulting.yahoo.finance.core.YHistoric;
import de.hdtconsulting.yahoo.finance.core.YQuote;
import de.hdtconsulting.yahoo.finance.core.YSymbol;
import de.hdtconsulting.yahoo.finance.server.csv.connection.YConnectionManager;

/**
 * Singleton class that imports stock quotes into the database
 * 
 * @author mehdimaachou
 * 
 */

public class StockQuoteImporter {
    private static final Logger logger = Logger.getLogger(StockQuoteImporter.class);
    /* Yahoo finance API parameters */
    private YConnectionManager connectionManager;
    private Yapi yapi;
    private static int YAHOO_API_MAX_CONNECTION = 5;
    private static int TOTAL_YEARS_HISTORY_PERIOD = 10; // 10 years of
							// historical data
    /* Yahoo suggestion parameters */
    private static String YAHOO_STOCK_SUGGESTION_BASE_URL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc";
    private StringBuilder yahooServiceUrlBuilder;

    private static StockQuoteImporter instance;

    /**
     * Constructor
     */
    protected StockQuoteImporter() {
	/* Init Yahoo finance api connection */
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
     * Process a stock symbol
     * 
     * @param stockSymbol
     */
    public void importStockData(final String stockSymbol) {
	logger.debug("Cheking stock symbol " + stockSymbol + "...");
	Stock stockData = getSymbolDataFromYahoo(stockSymbol);
	if (stockData != null) {
	    logger.debug("processing symbol " + stockSymbol + "(" + stockData.getName() + ")");
	    StockEntity stockEntity = Ebean.find(StockEntity.class).where().eq("symbol", stockData.getSymbol())
		    .findUnique();
	    if (stockEntity == null) {
		logger.debug("Stock symbol doesn't exist in the DB. Creating the new Stock in db...");
		stockEntity = new StockEntity(stockData.getSymbol(), stockData.getName());
		logger.debug("Saving data...");
		Ebean.save(stockEntity);
		saveHistoQuotesFromBeginning(stockEntity, Yapi.HIST_DAYLY);
	    } else {
		logger.debug("Stock symbol exist in the DB.updating quotes...");
		/* Getting the last quote time */
		String sql = "select max(q.date) as lastQuoteDate from T_STOCK_HISTO_QUOTES q";
		RawSql rawSql = RawSqlBuilder.parse(sql).create();
		Query<DailyQuotesAggregate> query = Ebean.find(DailyQuotesAggregate.class);
		query.setRawSql(rawSql);
		DailyQuotesAggregate result = query.findUnique();
		Date lastDateDb = result.getLastQuoteDate();
		if (lastDateDb != null) {
		    updatingHistoQuotes(stockEntity, lastDateDb, Yapi.HIST_DAYLY);
		} else {
		    saveHistoQuotesFromBeginning(stockEntity, Yapi.HIST_DAYLY);
		}

	    }
	} else {
	    logger.warn(stockSymbol + " is not a Valid symbol!");
	}

    }

    /**
     * Converting a list of YHisto(yahoo finance api pojo representing a day
     * quote) to DailyQuote model model
     * 
     * @param yHistoList
     * @param currentStock
     * @return list of DailyQutes
     */
    private ArrayList<DailyQuote> convertYQuotesToQuotes(final ArrayList<YHistoric> yHistoList,
	    final StockEntity currentStock) {
	ArrayList<DailyQuote> result = new ArrayList<DailyQuote>();
	for (YHistoric yHisto : yHistoList) {
	    DailyQuote dailyQuote = new DailyQuote();
	    DateTime date = new DateTime(yHisto.getDate());
	    dailyQuote.setDate(date.toDate());
	    dailyQuote.setOpen(yHisto.getOpen().doubleValue());
	    dailyQuote.setHigh(yHisto.getHigh().doubleValue());
	    dailyQuote.setVolume(yHisto.getVolume().intValue());
	    dailyQuote.setLow(yHisto.getLow().doubleValue());
	    dailyQuote.setClose(yHisto.getClose().doubleValue());
	    dailyQuote.setAdjClose(yHisto.getAdjClose().doubleValue());
	    dailyQuote.setStock(currentStock);
	    result.add(dailyQuote);
	}
	return result;
    }

    /**
     * Saving all historical daily quotes for a given stock
     * 
     * @param stockId
     * @return
     */
    private void saveHistoQuotesFromBeginning(final StockEntity stockEntity, final String dataGranularity) {
	if (stockEntity != null && stockEntity.getSymbol() != null) {
	    YSymbol symbol = new YSymbol(stockEntity.getSymbol());
	    Date endDate = (Calendar.getInstance()).getTime();
	    Date startDate = (new DateTime(endDate)).minusYears(TOTAL_YEARS_HISTORY_PERIOD).toDate();
	    YQuote yquotes = yapi.getHistoric(symbol, startDate, endDate, dataGranularity);
	    ArrayList<DailyQuote> dailyQuotes = convertYQuotesToQuotes(yquotes.getHistorics(), stockEntity);
	    logger.debug(dailyQuotes.size() + " historical quotes retreived for " + stockEntity.getCompanyName() + "("
		    + stockEntity.getSymbol() + ")");
	    logger.debug("Saving data...");
	    Ebean.save(dailyQuotes);
	    logger.debug("Historical data for " + stockEntity.getSymbol() + "(" + stockEntity.getCompanyName()
		    + ") was saved successfully.");
	}
    }

    /**
     * Updating daily historical quotes for a given stock
     * 
     * @param stockEntity
     */
    private void updatingHistoQuotes(final StockEntity stockEntity, final Date lastDateDb, final String dataGranularity) {
	if (stockEntity != null && stockEntity.getSymbol() != null && lastDateDb != null) {
	    DateTime lastDateDbTime = new DateTime(lastDateDb);

	    DateTime endDateHisto = new DateTime(Calendar.getInstance().getTime());
	    DateTime lastdateDbPlus1Day = lastDateDbTime.plusDays(1);

	    YSymbol symbol = new YSymbol(stockEntity.getSymbol());

	    if (endDateHisto.compareTo(lastDateDbTime) > 0) {
		YQuote yquotes = null;
		ArrayList<YHistoric> yHistoList = null;
		if (Days.daysBetween(lastdateDbPlus1Day, endDateHisto).getDays() == 0) {
		    yquotes = yapi.getHistoric(symbol, lastDateDbTime.toDate(), endDateHisto.toDate(), dataGranularity);
		    // retreive current day quote
		    // FIXME : find a cleaner way to retreive the current day
		    // quote without too much turnarounds.
		    yHistoList.remove(yHistoList.size() - 1);
		} else {
		    yquotes = yapi.getHistoric(symbol, lastdateDbPlus1Day.toDate(), endDateHisto.toDate(),
			    dataGranularity);
		}
		yHistoList = yquotes.getHistorics();
		ArrayList<DailyQuote> dailyQuotes = convertYQuotesToQuotes(yHistoList, stockEntity);
		logger.debug(dailyQuotes.size() + " historical quotes retreived for " + stockEntity.getCompanyName()
			+ "(" + stockEntity.getSymbol() + ")");
		logger.debug("Saving data...");
		Ebean.save(dailyQuotes);
		logger.debug("Historical data for " + stockEntity.getSymbol() + "(" + stockEntity.getCompanyName()
			+ ") was saved successfully.");
	    } else {
		logger.warn("Warning : The last quote present in db has a time superior or equal to current time !!! No need to update.");
	    }
	}
    }

    /**
     * Check if a stock symbol exists and return details about it.
     * 
     * @param symbol
     * @return
     */
    public Stock getSymbolDataFromYahoo(final String symbol) {
	Stock result = null;
	if (yahooServiceUrlBuilder == null)
	    yahooServiceUrlBuilder = new StringBuilder();
	else
	    yahooServiceUrlBuilder.setLength(0);

	yahooServiceUrlBuilder.append(YAHOO_STOCK_SUGGESTION_BASE_URL);
	yahooServiceUrlBuilder.append("?query=");
	yahooServiceUrlBuilder.append(symbol);
	yahooServiceUrlBuilder.append("&callback=YAHOO.Finance.SymbolSuggest.ssCallback");
	/* Parsing Json result with gson */
	String yahooServiceUrlResponse = readUrl(yahooServiceUrlBuilder.toString());
	Gson gson = new Gson();
	logger.debug("retrieving symbol data from : " + yahooServiceUrlBuilder.toString());
	QueryResult query = gson.fromJson(yahooServiceUrlResponse, QueryResult.class);
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
	return result;
    }

    /**
     * Helper method to read the content of yahoo finance suggestion API
     * 
     * @param urlString
     * @return
     */
    public String readUrl(String urlString) {
	BufferedReader reader = null;
	String result = null;
	try {
	    URL url = new URL(urlString);
	    reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    StringBuffer buffer = new StringBuffer();
	    int read;
	    char[] chars = new char[1024];
	    while ((read = reader.read(chars)) != -1)
		buffer.append(chars, 0, read);
	    result = buffer.toString().replace("YAHOO.Finance.SymbolSuggest.ssCallback(", "").replace(")", "");
	    // FIXME: find a way to remove this nasty replace
	} catch (IOException e) {
	    logger.error("Error while parsing the url from yahoo finance suggestion");
	    e.printStackTrace();
	} finally {
	    if (reader != null)
		try {
		    reader.close();
		} catch (IOException e) {
		    logger.error("Error while closing the url stream from yahoo finance suggestion");
		    e.printStackTrace();
		}
	}
	return result;
    }
}
