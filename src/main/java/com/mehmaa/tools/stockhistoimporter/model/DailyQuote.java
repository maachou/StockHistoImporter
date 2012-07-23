package com.mehmaa.tools.stockhistoimporter.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "STOCK_HISTO_QUOTES")
public class DailyQuote {

    @Id
    @Column(name = "ID")
    Short id;

    @Column(name = "Q_DATE")
    private Date date;

    @Column(name = "Q_OPEN")
    private BigDecimal open;

    @Column(name = "Q_HIGH")
    private BigDecimal high;

    @Column(name = "Q_LOW")
    private BigDecimal low;

    @Column(name = "Q_CLOSE")
    private BigDecimal close;

    @Column(name = "Q_VOLUME")
    private BigDecimal volume;

    @Column(name = "Q_ADJCLOSE")
    private BigDecimal adjClose;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "STOCK")
    StockEntity stock;

    public DailyQuote() {

    }

    public DailyQuote(final Date date, final BigDecimal open, final BigDecimal high, final BigDecimal low,
	    final BigDecimal close, final BigDecimal volume, final BigDecimal adjClose) {
	this.date = date;
	this.open = open;
	this.high = high;
	this.low = low;
	this.close = close;
	this.volume = volume;
	this.adjClose = adjClose;
    }

    public Short getId() {
	return id;
    }

    public void setId(Short id) {
	this.id = id;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public BigDecimal getOpen() {
	return open;
    }

    public void setOpen(BigDecimal open) {
	this.open = open;
    }

    public BigDecimal getHigh() {
	return high;
    }

    public void setHigh(BigDecimal high) {
	this.high = high;
    }

    public BigDecimal getLow() {
	return low;
    }

    public void setLow(BigDecimal low) {
	this.low = low;
    }

    public BigDecimal getClose() {
	return close;
    }

    public void setClose(BigDecimal close) {
	this.close = close;
    }

    public BigDecimal getVolume() {
	return volume;
    }

    public void setVolume(BigDecimal volume) {
	this.volume = volume;
    }

    public BigDecimal getAdjClose() {
	return adjClose;
    }

    public void setAdjClose(BigDecimal adjClose) {
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
