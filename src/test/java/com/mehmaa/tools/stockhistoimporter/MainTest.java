package com.mehmaa.tools.stockhistoimporter;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuote;
import com.mehmaa.tools.stockhistoimporter.model.StockEntity;

public class MainTest {

    @Before
    public void setUp() throws Exception {
	Ebean.createSqlUpdate("delete from STOCK_HISTO_QUOTES").execute();
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
	DailyQuote aDailyQuote = new DailyQuote(calendar.getTime(), new BigDecimal(34.5), new BigDecimal(56.5),
		new BigDecimal(20.9), new BigDecimal(40), new BigDecimal(200000), new BigDecimal(340.5));
	aDailyQuote.setStock(aStock);
	Ebean.save(aDailyQuote);

	Assert.assertNotNull(aDailyQuote.getId());
	System.out.println(aDailyQuote.toString());
    }

    @Test
    public void SaveDailyStockHistoGoogle() throws Exception {
	StockQuoteImporter.getInstance().importStockData("GOOG");
	StockEntity googleStockEntity = Ebean.find(StockEntity.class).where().eq("symbol", "GOOG").findUnique();
	Assert.assertNotNull(googleStockEntity);
	Assert.assertTrue(googleStockEntity.getDailyQuotes().size() > 0);
	System.out.println("total quotes: " + googleStockEntity.getDailyQuotes().size());
    }
}