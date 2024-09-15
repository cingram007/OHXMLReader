package com.oh.datis.xml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oh.datis.util.XMLUtil;


public class XmlConfig { 	
	private static final Logger log = Logger.getLogger(XmlConfig.class);
	  public XmlConfig(){ }	   
	  private static Map<String, String> hashMap = null;
	  
	   protected static Map<String, String> readCred(String persistenceName){	       	             
	       String xmlPersistenceFile= "/META-INF/persistence.xml";        
	       InputStream inStream = XmlConfig.class.getResourceAsStream(xmlPersistenceFile);
	       Document doc = null;	       
	        try{
	        	hashMap = new HashMap<String, String>();
	             DocumentBuilderFactory docFact= DocumentBuilderFactory.newInstance();
	             DocumentBuilder db = docFact.newDocumentBuilder();
	             doc = db.parse(inStream);
	             if (doc != null){
	                   NodeList nodes = doc.getElementsByTagName("persistence");
	                   if (nodes != null && nodes.getLength()>0){
	                        Node persistNode= XMLUtil.getNodebyAttributeValue(nodes.item(0).getChildNodes(), "persistence-unit","name" ,persistenceName);
	                        if (persistNode !=null){
	                            Node properties = XMLUtil.getNode(persistNode.getChildNodes(), "properties");
	                            if (properties != null){
	                               Node jdbcUrlNode = XMLUtil.getNodebyAttributeValue(properties.getChildNodes(), "property", "name" , "javax.persistence.jdbc.url");
	                               if (jdbcUrlNode !=null && jdbcUrlNode.hasAttributes()){
	                                   Node attrName = jdbcUrlNode.getAttributes().getNamedItem("value");
	                                   if (attrName!=null){	                                	   
	                                	   hashMap.put("jdbcUrl",  attrName.getTextContent());
	                                   }
	                               }
	                               Node jdbcDriverNode = XMLUtil.getNodebyAttributeValue(properties.getChildNodes(), "property", "name" , "javax.persistence.jdbc.driver");
	                               if (jdbcDriverNode !=null && jdbcDriverNode.hasAttributes()){
	                                   Node attrName = jdbcUrlNode.getAttributes().getNamedItem("value");
	                                   if (attrName!=null){	                                	   
	                                	   hashMap.put("jdbcDriver",  attrName.getTextContent());
	                                   }
	                               }
	                               
	                               Node userNode = XMLUtil.getNodebyAttributeValue(properties.getChildNodes(), "property", "name" , "javax.persistence.jdbc.user");
	                               Node passNode = XMLUtil.getNodebyAttributeValue(properties.getChildNodes(), "property", "name" , "javax.persistence.jdbc.password");
	                               if (userNode !=null && userNode.hasAttributes()){
	                                   Node attrName = userNode.getAttributes().getNamedItem("value");
	                                   if (attrName!=null){	                                      
	                                      hashMap.put("user",  attrName.getTextContent());
	                                   }
	                               }
	                               if (passNode !=null && passNode.hasAttributes()){
	                                   Node attrName = passNode.getAttributes().getNamedItem("value");
	                                   if (attrName!=null){	                                      
	                                      hashMap.put("pass",  attrName.getTextContent());
	                                   }
	                               }
	                            }
	                        }
	                   }
	           }
	        }catch(Exception ex){
	            ex.printStackTrace();
	        }	       
	       return hashMap;
	   }	

	public static Connection  createConnection (String persistenceName){
		   Connection conn = null ;
			try {
				Class.forName(hashMap.get("jdbcDriver")); //"oracle.jdbc.OracleDriver"				
				conn = DriverManager.getConnection(hashMap.get("jdbcUrl"), hashMap.get("user"), hashMap.get("pass"));
				return conn;
			} catch (ClassNotFoundException | SQLException  e) {								
				log.error("SuppValidator() consrtuctor: " + e.getMessage());
			}				
           return null;
   }

}
