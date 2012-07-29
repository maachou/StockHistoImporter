package com.mehmaa.tools.stockhistoimporter.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.avaje.ebean.annotation.Sql;

/**
 * Ebean aggregate object used to retrieve some calculated date from
 * DailyQuotes.
 * 
 * @author mehdimaachou
 * 
 */
@Entity
@Sql
public class DailyQuotesAggregate {

    @OneToOne
    DailyQuote quote;

    private Date lastQuoteDate;

    public Date getLastQuoteDate() {
	return lastQuoteDate;
    }

    public void setLastQuoteDate(Date lastQuoteDate) {
	this.lastQuoteDate = lastQuoteDate;
    }
}
