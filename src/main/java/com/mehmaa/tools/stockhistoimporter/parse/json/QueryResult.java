package com.mehmaa.tools.stockhistoimporter.parse.json;

/**
 * Pojo representing a query result from yahoo finance suggestion
 * 
 * @author mehdimaachou
 * 
 */
public class QueryResult {

    private ResultSet ResultSet;

    public QueryResult() {

    }

    public ResultSet getResultSet() {
	return ResultSet;
    }

    public void setResultSet(ResultSet resultSet) {
	ResultSet = resultSet;
    }

}
