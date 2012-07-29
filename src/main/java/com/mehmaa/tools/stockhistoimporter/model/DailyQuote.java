package com.mehmaa.tools.stockhistoimporter.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

/**
 * Model representing daily stock quotes
 * 
 * @author mehdimaachou
 * 
 */
@Entity
@Table(name = "T_STOCK_HISTO_QUOTES")
public class DailyQuote {

    @Id
    Long id;

    private Date date;

    private double open;

    private double high;

    private double low;

    private double close;

    private int volume;

    private double adjClose;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stock_id")
    StockEntity stock;

    public DailyQuote() {

    }

    public DailyQuote(final Date date, final double open, final double high, final double low, final double close,
	    final int volume, final double adjClose) {
	this.date = date;
	this.open = open;
	this.high = high;
	this.low = low;
	this.close = close;
	this.volume = volume;
	this.adjClose = adjClose;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public double getOpen() {
	return open;
    }

    public void setOpen(double open) {
	this.open = open;
    }

    public double getHigh() {
	return high;
    }

    public void setHigh(double high) {
	this.high = high;
    }

    public double getLow() {
	return low;
    }

    public void setLow(double low) {
	this.low = low;
    }

    public double getClose() {
	return close;
    }

    public void setClose(double close) {
	this.close = close;
    }

    public int getVolume() {
	return volume;
    }

    public void setVolume(int volume) {
	this.volume = volume;
    }

    public double getAdjClose() {
	return adjClose;
    }

    public void setAdjClose(double adjClose) {
	this.adjClose = adjClose;
    }

    public StockEntity getStock() {
	return stock;
    }

    public void setStock(StockEntity stock) {
	this.stock = stock;
    }

    @Override
    public String toString() {
	return "DailyQuote [id=" + id + ", date=" + date + ", open=" + open + ", high=" + high + ", low=" + low
		+ ", close=" + close + ", volume=" + volume + ", adjClose=" + adjClose + ", stock=" + stock + "]";
    }
}
