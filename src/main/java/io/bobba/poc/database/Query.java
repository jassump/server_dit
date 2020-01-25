package io.bobba.poc.database;

public class Query {
	private String query = "";
	public final String EQUAL = "=";
	public final String DIFFERENT = "<>";
	
	public Query SELECT(String param) {
		query = getQuery() + "SELECT "+param+" ";
		return this; 
	}
	
	public Query FROM(String params) {
		query = getQuery() + "FROM "+params+" ";
		return this;
	}
	
	public Query WHERE(String params) {
		query = getQuery() + "WHERE "+params+" ";
		return this;
	}
	
	public Query UPDATE(String table){
		this.query += "UPDATE "+table+" ";
		return this;
	}
	
	public Query SET(String params) {
		this.query += "SET "+params+" ";
		return this;
	}
	
	public Query INSERT_INTO(String params) {
		this.query += "INSERT INTO "+params+" ";
		return this;
	}
	
	public Query VALUES(String values) {
		this.query +="VALUES "+values+" ";
		return this;
	}
	

	public String getQuery() {
		return this.query;
	}
}
