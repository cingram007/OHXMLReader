package com.oh.datis.dbhandler;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/*
 * https://www.knpcode.com/2020/10/hikaricp-connection-pooling-java-example.html
 * zetcode.com/articles/hikaricp
 * https://www.baeldung.com/convert-file-to-input-stream
 */
public class DSCreator {
	private static final Logger log = Logger.getLogger(DSCreator.class);
	  private static HikariDataSource ds;
	  static {
	    try {
	      Properties properties = new Properties();
	      // Loading properties file from the resources folder	     
	      File initialFile = new File(".\\db.properties");
	      InputStream inputStream = new FileInputStream(initialFile);	      
	     	      
	      properties.load(inputStream);	      
	      HikariConfig cfg = new HikariConfig();
	       // This property is optional now 
	      cfg.setDriverClassName(properties.getProperty("dataSource.driver"));
	      cfg.setJdbcUrl(properties.getProperty("jdbcUrl"));
	      cfg.setUsername(properties.getProperty("dataSource.user"));
	      cfg.setPassword(properties.getProperty("dataSource.password"));
	      cfg.setMaximumPoolSize(Integer.parseInt(properties.getProperty("dataSource.maximumPoolSize")));	      
	      cfg.addDataSourceProperty("cachePrepStmts", "true");
	      cfg.addDataSourceProperty("prepStmtCacheSize", "250");
	      cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");	      
	      // Create DataSource
	      ds = new HikariDataSource(cfg);
	    }catch(IOException e) {
	    	 log.error("DSCreator threw : " + e.getMessage());
	    }
	  }
	  public static DataSource getDataSource() {
	    return ds;
	  }   
}
