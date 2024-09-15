package com.oh.datis.implement;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.oh.datis.dbhandler.DSCreator;
import com.oh.datis.exceptionhandlers.XSDValidationErrorHandler;
import com.oh.datis.interfaces.IValidator;

public class PDSValidator implements IValidator {	
	private static final Logger log = Logger.getLogger(PDSValidator.class);	
	PDSValidator(){}

	@Override
	public ArrayList<String> validateXMLFile( Document document, String inputFile) {		
		ArrayList<String> arrayListErrors = new ArrayList<String>();
	    try {	       
	      SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	      schemaFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
	      schemaFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
	      schemaFactory.setFeature("http://apache.org/xml/features/validate-annotations", false);
	      schemaFactory.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", false);
	    //  StreamSource streamSource = new StreamSource(new File("./config/pds.xsd")); 
	     // Schema schema = schemaFactory.newSchema(streamSource);
	     // Validator validator = schema.newValidator();
	    //  XSDValidationErrorHandler xSDValidationErrorHandler = new XSDValidationErrorHandler();
	    //  validator.setErrorHandler((ErrorHandler)xSDValidationErrorHandler);
	    //  validator.validate(new DOMSource(document));
	    //  arrayListErrors = xSDValidationErrorHandler.getEncounteredErrors();
	      if ( arrayListErrors.size() == 0){
	    	  log.info("PDSValidator.validateXMLFile() Root Element :" + document.getDocumentElement().getNodeName());
	      }
	    } catch (SAXException saxe) {
	    	log.error("PDSValidator.validateXMLFile() threw :  " + saxe.getMessage());
	        arrayListErrors.add("PDSValidator.validateXMLFile() threw :  " + saxe.getMessage());
	    }/*catch (IOException ioe) {	      
	      log.error("PDSValidator.validateXMLFile() threw :  " + ioe.getMessage());
	        arrayListErrors.add("PDSValidator.validateXMLFile() threw :  " + ioe.getMessage());
	    } */
	    return arrayListErrors;
	}

	@Override	
    public Integer parseXML(Document document) {
		  final Map<Integer, String> recordMap = new HashMap<Integer, String>();		  
		  Integer recsInserted = new Integer(0);
		  int mapCount = 0;
		  
		  try {		  
	          // recommended : see http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	          document.getDocumentElement().normalize();	                 
	          
	          // get <SupplementaryData>
	          NodeList suppList = document.getElementsByTagName("PDSData");

	          for (Integer temp = 0; temp < suppList.getLength(); temp++) {
	              Node firstSuppNode = suppList.item(temp);
	              if (firstSuppNode.getNodeType() == Node.ELEMENT_NODE) {
	                  Element element = (Element) firstSuppNode;                       

	                  // get elements	                 	     
	                  recordMap.put(++mapCount, element.getElementsByTagName("RecordID").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("dart").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("client_mrn").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("episode_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("referral_no").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("referred_day").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("referred_month").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("referred_year").item(0).getTextContent());	                    
	                  recordMap.put(++mapCount, element.getElementsByTagName("referral_source_type").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("agency_name").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("city").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("postal_code").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("DOB_day").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("DOB_month").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("DOB_year").item(0).getTextContent());	 
	                  recordMap.put(++mapCount, element.getElementsByTagName("employment_status").item(0).getTextContent());	               
	                  recordMap.put(++mapCount, element.getElementsByTagName("gender_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("income_source").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("official_language").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("mother_tongue1").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("mother_tongue2").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("mother_tongue3").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("mother_tongue4").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("mother_tongue5").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("legal_status1").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("legal_status2").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("legal_status3").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("legal_status4").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("legal_status5").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("education_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_termination_d").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_termination_hh").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_termination_m").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_termination_mm").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_termination_y").item(0).getTextContent());	                                    
	                  recordMap.put(++mapCount, element.getElementsByTagName("program_name").item(0).getTextContent());                 	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("program_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_initiation_d").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_initiation_hh").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_initiation_m").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_initiation_mm").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_initiation_y").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("fc_code").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("program_discharge_reason").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("marital_status").item(0).getTextContent());	                                    
	                  recordMap.put(++mapCount, element.getElementsByTagName("activity_id").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_modality").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("service_modality_type").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("indirect_minutes").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("direct_minutes").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("encounter_d").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("encounter_hh").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("encounter_m").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("encounter_mm").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("encounter_y").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("agency_group_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("encounter_status").item(0).getTextContent());	                      
	              }
	              
	              if(log.getLevel() == Level.DEBUG) {
		              for (Map.Entry<Integer, String> entry : recordMap.entrySet()) {                        
		            	  log.debug("PDSValidator.parseXML()  Key: " + new Object[]{entry.getKey() +  "Value: {1} " +  entry.getValue()});		                                
		              }
	              }
	              
	              recsInserted = recsInserted + insertRecord(recordMap);
	              mapCount = 0;
	              log.debug("PDSValidator.parseXML(): Record inserted " + "\r \n" ) ;
	              recordMap.clear();	              
	              
	          } //end for loop, get list length	          
	          return recsInserted;

	      } catch (Exception  e) {
	    	  log.error("PDSValidator.parseXML(): threw exception : " + e.getMessage()) ;
	          return recsInserted;
	      }
	}	
	
	@Override
	public Integer parseXMLDebug(Document document) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	static Integer visitChildNodes(NodeList nList, Integer mapCount, Map<Integer, String> recordMap){
		  
		  Integer insertCount = 0;

		    for (int temp = 0; temp < nList.getLength(); temp++) {
		      
		      Node node = nList.item(temp);
		      if (node.getNodeType() == Node.ELEMENT_NODE) {
		        
		        //System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
	            recordMap.put(++mapCount,node.getTextContent());
	            insertCount++;
		        //Check all attributes
		        if (node.hasAttributes()) {
		          
		          // get attributes names and values
		          NamedNodeMap nodeMap = node.getAttributes();
		          for (int i = 0; i < nodeMap.getLength(); i++) {
		            
		            Node tempNode = nodeMap.item(i);
		            //System.out.println("Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue());
		          }
		          if (node.hasChildNodes()) {
		            //We got more children; Let's visit them as well
		            visitChildNodes(node.getChildNodes(), mapCount, recordMap);
		          }
		        }
		      }
		    }
		    return insertCount;
		}
	
	public Integer insertRecord(Map<Integer, String> recordMap)  {		
		Connection conn = null;
		
		try {
			DataSource ds = DSCreator.getDataSource();
			conn = ds.getConnection();			
			PreparedStatement pstmt = null;		
					
			String sql = "{call INSERT_RAW_PDS_OH_RECORD(?,?,?,?,?,?,?,?,?,?, "
					                                                                                 + "?,?,?,?,?,?,?,?,?,?,"
					                                                                                 + "?,?,?,?,?,?,?,?,?,?,"
					                                                                                 + "?,?,?,?,?,?,?,?,?,?,"
					                                                                                 + "?,?,?,?,?,?,?,?,?,?,"
					                                                                                 + "?,?,?,?,?,?,?)}"; //57 20230922   
			pstmt = conn.prepareCall(sql);
			pstmt.clearParameters();
			
			if(log.getLevel() == Level.DEBUG) {
				for (Map.Entry<Integer, String> entry : recordMap.entrySet())  {             
		        		 log.info("PDSValidator.insertRecord(): Key: " + new Object[]{entry.getKey() +  "Value: {1} " +  entry.getValue()});
	        	 }							
	        }			          
			 
	        for (Map.Entry<Integer, String> entry : recordMap.entrySet())  {         			 
					pstmt.setString(entry.getKey(), entry.getValue());			
	        }		
			 
	        pstmt.executeQuery();	        
	        return 1;
		} catch (SQLException e) {			
			log.error("PDSValidator.insertRecord() threw : " + e.getMessage());
			return 0;
		 } catch (Exception e) {			
				log.error("PDSValidator.insertRecord() threw : " + e.getMessage());
				return 0;
		  }finally {
			  if(conn != null){
			        try {
			          conn.close();
			        } catch (SQLException e) {
			           log.error("PDSValidator.insertRecord() threw : " + e.getMessage());
			        }
			      }
	        }		
	}	

}
