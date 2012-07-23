package com.mehmaa.tools.stockhistoimporter.json;

import java.util.List;

public class ResultSet {
    private String Query;
    private List<Stock> Result;

    public ResultSet() {
    }

    public String getQuery() {
	return Query;
    }

    public void setQuery(String query) {
	Query = query;
    }

    public List<Stock> getResult() {
	return Result;
    }

    public void setResult(List<Stock> result) {
	Result = result;
    }
}
