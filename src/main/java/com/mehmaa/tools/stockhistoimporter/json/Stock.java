package com.mehmaa.tools.stockhistoimporter.json;

public class Stock {

    private String exch;
    private String exchDisp;
    private String name;
    private String symbol;
    private String type;
    private String typeDisp;

    public Stock() {

    }

    public String getExch() {
	return exch;
    }

    public void setExch(String exch) {
	this.exch = exch;
    }

    public String getExchDisp() {
	return exchDisp;
    }

    public void setExchDisp(String exchDisp) {
	this.exchDisp = exchDisp;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSymbol() {
	return symbol;
    }

    public void setSymbol(String symbol) {
	this.symbol = symbol;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getTypeDisp() {
	return typeDisp;
    }

    public void setTypeDisp(String typeDisp) {
	this.typeDisp = typeDisp;
    }

}
