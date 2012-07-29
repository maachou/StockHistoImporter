package com.mehmaa.tools.stockhistoimporter;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuote;
import com.mehmaa.tools.stockhistoimporter.model.StockEntity;
import com.mehmaa.tools.stockhistoimporter.parse.json.Stock;

public class StockQuoteImporterTest {

    @Before
    public void setUp() throws Exception {
	Ebean.createSqlUpdate("delete from T_STOCK_HISTO_QUOTES").execute();
	Ebean.createSqlUpdate("delete from T_STOCKS").execute();
    }

    @Test
    public void SaveStockTest() throws Exception {
	StockEntity aStock = new StockEntity("GOOG", "Google Inc.");
	Ebean.save(aStock);
	StockEntity aStock2 = Ebean.find(StockEntity.class, aStock.getId());
	Assert.assertEquals(aStock.getId(), aStock2.getId());
    }

    @Test
    public void SaveDailyQuoteTest() throws Exception {

	StockEntity aStock = new StockEntity("GOOG", "Google Inc.");
	Ebean.save(aStock);

	Calendar calendar = Calendar.getInstance();
	DailyQuote aDailyQuote = new DailyQuote(calendar.getTime(), new BigDecimal(34.5).doubleValue(), new BigDecimal(
		56.5).doubleValue(), new BigDecimal(20.9).doubleValue(), new BigDecimal(40).doubleValue(), 20000,
		new BigDecimal(340.5).doubleValue());
	aDailyQuote.setStock(aStock);
	Ebean.save(aDailyQuote);

	Assert.assertNotNull(aDailyQuote.getId());

	Assert.assertTrue(aDailyQuote.getOpen() == 34.5);
	Assert.assertTrue(aDailyQuote.getHigh() == 56.5);
	Assert.assertTrue(aDailyQuote.getLow() == 20.9);
	Assert.assertTrue(aDailyQuote.getClose() == 40);
	Assert.assertTrue(aDailyQuote.getVolume() == 20000);
	Assert.assertTrue(aDailyQuote.getAdjClose() == 340.5);
    }

    @Test
    public void SaveDailyStockHistoGoogleTest() throws Exception {
	StockQuoteImporter.getInstance().importStockData("GOOG");
	StockEntity googleStockEntity = Ebean.find(StockEntity.class).where().eq("symbol", "GOOG").findUnique();
	Assert.assertNotNull(googleStockEntity);
	Assert.assertTrue(googleStockEntity.getDailyQuotes().size() > 0);
	System.out.println("total quotes: " + googleStockEntity.getDailyQuotes().size());
    }

    @Test
    public void getSymbolDataFromYahooTest() {
	String expectedSymbol = "GOOG";
	Stock resultParsing = StockQuoteImporter.getInstance().getSymbolDataFromYahoo(expectedSymbol);
	Assert.assertTrue(resultParsing.getSymbol().equals(expectedSymbol));
    }

    @Test
    public void readUrlTest() {
	Properties prop = null;
	try {
	    prop = new Properties();
	    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("unittest.properties");
	    prop.load(in);
	    in.close();
	} catch (Exception e) {
	    fail("Failed to load properties: " + e.getMessage());
	}

	String expectedResult = prop.getProperty("yahoo.finance.suggest.response");
	String yahooFinanceSuggUrl = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=GOOG&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
	String result = StockQuoteImporter.getInstance().readUrl(yahooFinanceSuggUrl);
	System.out.println(expectedResult);
	Assert.assertTrue(expectedResult.equals(result));
    }
}
