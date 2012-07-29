package com.mehmaa.tools.stockhistoimporter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuote;
import com.mehmaa.tools.stockhistoimporter.model.DailyQuotesAggregate;
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
    public void SaveDailyStockHistoGoogle() throws Exception {
	StockQuoteImporter.getInstance().importStockData("GOOG");
	StockEntity googleStockEntity = Ebean.find(StockEntity.class).where().eq("symbol", "GOOG").findUnique();
	Assert.assertNotNull(googleStockEntity);
	Assert.assertTrue(googleStockEntity.getDailyQuotes().size() > 0);
	System.out.println("total quotes: " + googleStockEntity.getDailyQuotes().size());
    }

    @Test
    public void updatingHistoQuotesTest() {
	/* Creating a test symbol */
	StockEntity aStock = new StockEntity("GOOG", "Google Inc.");
	Ebean.save(aStock);
	DateTime dt = new DateTime(2012, 6, 1, 20, 0);
	Date dateDailyQuote01 = (Date) dt.toDate();
	/* Creating quote 1 */
	DailyQuote dailyQuote01 = new DailyQuote(dateDailyQuote01, new BigDecimal(34.5).doubleValue(), new BigDecimal(
		56.5).doubleValue(), new BigDecimal(20.9).doubleValue(), new BigDecimal(40).doubleValue(), 20000,
		new BigDecimal(340.5).doubleValue());
	dailyQuote01.setStock(aStock);
	Ebean.save(dailyQuote01);
	/* Creating quote 2 */
	dt = new DateTime(2012, 7, 21, 20, 0);
	Date dateDailyQuote02 = (Date) dt.toDate();
	DailyQuote dailyQuote02 = new DailyQuote(dateDailyQuote02, new BigDecimal(20.5).doubleValue(), new BigDecimal(
		100.5).doubleValue(), new BigDecimal(20.9).doubleValue(), new BigDecimal(40).doubleValue(), 20000,
		new BigDecimal(340.5).doubleValue());
	dailyQuote02.setStock(aStock);
	Ebean.save(dailyQuote02);

	String sql = "select max(q.date) as lastQuoteDate from T_STOCK_HISTO_QUOTES q";
	RawSql rawSql = RawSqlBuilder.parse(sql).create();
	Query<DailyQuotesAggregate> query = Ebean.find(DailyQuotesAggregate.class);
	query.setRawSql(rawSql);
	DailyQuotesAggregate result = query.findUnique();
	Date lastDate = result.getLastQuoteDate();

	Assert.assertTrue(lastDate.compareTo(dateDailyQuote02) == 0);

    }

    @Test
    public void getSymbolDataFromYahooTest() {
	String expectedSymbol = "GOOG";
	Stock resultParsing = StockQuoteImporter.getInstance().getSymbolDataFromYahoo(expectedSymbol);
	Assert.assertTrue(resultParsing.getSymbol().equals(expectedSymbol));
    }
}
