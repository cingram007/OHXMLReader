package com.oh.datis.implement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.oh.datis.dbhandler.IdbHandler;

public class SuppImpl implements IdbHandler{
	
	private StringBuilder strbDart;
	 private Connection conn ;

	final Map<Integer, String> recordMap = new HashMap<Integer, String>();
	
public SuppImpl(){}
    
    public SuppImpl(String inStr1, String strConn){
    	strbDart = new StringBuilder(inStr1);
    	setUpDBConnection( strConn);
    }

	public Connection setUpDBConnection(String strConn) {		 
		  String driver = "oracle.jdbc.OracleDriver"; 
			try {
				Class.forName(driver);
				//String url = "jdbc:oracle:thin:@datisodatest-scan:1521/testd";		
				String url = strConn;
				conn = DriverManager.getConnection(url, "DATIS2CBI_INTERFACE", "cbitransfertest");
				return conn;
			} catch (ClassNotFoundException | SQLException  e) {			
				//e.printStackTrace();
				System.err.println(e.getMessage());
				return conn;
			}				
	}

	@Override
	public boolean testConnection() {

		final Map<String, String> testMap;
		boolean bFlag = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// INSERT_RAW_OH_RECORD

		try {
			String strSql = "SELECT Dart, CBI_ORG_ID, ORG_NAME FROM CMHA_PILOT  WHERE dart=:1";
			pstmt = conn.prepareStatement(strSql);
			pstmt.setString(1, strbDart.toString());

			rs = pstmt.executeQuery();

			testMap = new HashMap<String, String>();

			if (rs.next()) {
				testMap.put("DART", rs.getString("DART"));
				testMap.put("CBI_ORG_ID", rs.getString("CBI_ORG_ID"));
				testMap.put("ORG_NAME", rs.getString("ORG_NAME"));
			}

			System.out.println(testMap);

			rs.close();
			pstmt.close();
			return bFlag;
		} catch (SQLException e) {
			System.err.println("testConnection.java -> testConnection(): " + e.getMessage());
			try {
				rs.close();
			} catch (SQLException e1) {
				System.err.println("testConnection.java -> testConnection(): " + e1.getMessage());
				return bFlag;
			}
			return bFlag;
		} finally {
			pstmt = null;			
		}
	}

	@Override
	public Integer insertRecord(Map<Integer, String> recordMap)     {		
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {			
			String sql = "{call INSERT_RAW_SUPP_OH_RECORD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n"   
								                                                                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n" 
								                                                                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n" //150
								                                                                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?\r\n"          //172
								                                                                        + ")}";					                                                                        
			pstmt = conn.prepareCall(sql);
			pstmt.clearParameters();
			          
	        for (Map.Entry<Integer, String> entry : recordMap.entrySet())  {              //recordMap only has 124          
	            //logger.log(Level.INFO, "Key: {0}: Value: {1}", new Object[]{entry.getKey(), entry.getValue()});            
					pstmt.setString(entry.getKey(), entry.getValue());			
	        }	      
	        rs = pstmt.executeQuery();	        
	        return 1;
		} catch (SQLException e) {			
			System.err.println(e.getMessage());
			return 0;
		}
	}	

}
