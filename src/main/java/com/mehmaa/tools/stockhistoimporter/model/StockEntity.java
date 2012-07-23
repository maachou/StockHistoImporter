package com.mehmaa.tools.stockhistoimporter.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;

/**
 * TODO
 * 
 * @author mehdimaachou
 * 
 */

@Entity
@Table(name = "T_STOCKS")
public class StockEntity {

    @Id
    @Column(name = "ID")
    Short id;

    @Column(name = "SYMBOL")
    @NotNull
    @Length(max = 20)
    String symbol;

    @Column(name = "COMPANY_NAME")
    @NotNull
    String companyName;

    @Column(name = "ADDED_TIME")
    @CreatedTimestamp
    Timestamp addedTime;

    @OneToMany(mappedBy = "stock")
    List<DailyQuote> dailyQuotes;

    public StockEntity() {

    }

    public StockEntity(final String symbol, final String companyName) {
	this.symbol = symbol;
	this.companyName = companyName;
    }

    public Short getId() {
	return id;
    }

    public void setId(Short id) {
	this.id = id;
    }

    public String getSymbol() {
	return symbol;
    }

    public void setSymbol(String symbol) {
	this.symbol = symbol;
    }

    public String getCompanyName() {
	return companyName;
    }

    public void setCompanyName(String companyName) {
	this.companyName = companyName;
    }

    public Timestamp getAddedTime() {
	return addedTime;
    }

    public void setAddedTime(Timestamp addedTime) {
	this.addedTime = addedTime;
    }

    public List<DailyQuote> getDailyQuotes() {
	return dailyQuotes;
    }

    public void setDailyQuotes(List<DailyQuote> dailyQuotes) {
	this.dailyQuotes = dailyQuotes;
    }

    @Override
    public String toString() {
	return "StockEntity [id=" + id + ", symbol=" + symbol + ", companyName=" + companyName + ", addedTime="
		+ addedTime + ", dailyQuotes=" + dailyQuotes + "]";
    }
}
