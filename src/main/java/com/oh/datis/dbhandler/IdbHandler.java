package com.oh.datis.dbhandler;

import java.util.Map;

public interface IdbHandler {

	java.sql.Connection setUpDBConnection(String connStr);
	boolean testConnection();
	Integer insertRecord(Map<Integer, String> recordMap);
}
