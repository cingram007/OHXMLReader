package com.oh.datis.implement;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class SuppValidator implements IValidator {
	
	   private static Logger log = Logger.getLogger(SuppValidator.class);	   
	   public static final Integer MAX_SUBSTANCE_CHILDREN = new Integer(25);
	   public static final Integer MAX_GAMBLING_CHILDREN = new Integer(25);
	   	   
	   SuppValidator(){}

	@Override
	public ArrayList<String> validateXMLFile(Document document, String inputFile) {			
			ArrayList<String> arrayListErrors = new ArrayList<String>();
		    try {				
				SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
				schemaFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
				schemaFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
				schemaFactory.setFeature("http://apache.org/xml/features/validate-annotations", false);
				schemaFactory.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", false);
				StreamSource streamSource = new StreamSource(new File("./config/supp.xsd"));
				Schema schema = schemaFactory.newSchema(streamSource);
				Validator validator = schema.newValidator();
				XSDValidationErrorHandler xSDValidationErrorHandler = new XSDValidationErrorHandler();
				validator.setErrorHandler((ErrorHandler) xSDValidationErrorHandler);
				log.debug("SuppValidator.validateXMLFile() Root Element :" + inputFile);			
				validator.validate(new DOMSource(document));
				arrayListErrors = xSDValidationErrorHandler.getEncounteredErrors();
				if (arrayListErrors.size() == 0) {
					log.info("SuppValidator.validateXMLFile() Root Element :" + document.getDocumentElement().getNodeName());					
				}
		    } catch (SAXException saxe) {
		    	log.error("SuppValidator.validateXMLFile() threw :  " + saxe.getMessage());
		        arrayListErrors.add(saxe.getMessage());
		    } catch (IOException ioe) {		      
		    	 log.error("SuppValidator.validateXMLFile() threw :  " + ioe.getMessage());
		        arrayListErrors.add( ioe.getMessage());
		    }		    
		    return arrayListErrors;
	}

	@Override
	public Integer parseXML(Document document) {
		 final Map<Integer, String> recordMap = new HashMap<Integer, String>();
		  int mapCount = 0;
		  Integer recsInserted = 0;
		  Integer insertCount =0;
		  boolean skip_record = false;
		  
		  try {			
	          document.getDocumentElement().normalize();
	          
	          // get <SupplementaryData>
	          NodeList suppList = document.getElementsByTagName("SupplementaryData");

	          for (Integer temp = 0; temp < suppList.getLength(); temp++) {
	              Node firstSuppNode = suppList.item(temp);
	              if (firstSuppNode.getNodeType() == Node.ELEMENT_NODE) {
	                  Element element = (Element) firstSuppNode;                
	                  
	                  log.debug("SuppValidator: Getting first element." );

	                  // get elements	         
	                  recordMap.put(++mapCount, element.getElementsByTagName("RecordID").item(0).getTextContent());	                	  
	                  recordMap.put(++mapCount, element.getElementsByTagName("client_mrn").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("dart").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("agency_name").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("agency_client_id").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("admission_no").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("admission_datetime_d").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("admission_datetime_hh").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("admission_datetime_m").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("admission_datetime_mm").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("admission_datetime_y").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("Site").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("episode_id").item(0).getTextContent());	                  
	                  
	                  if( element.getElementsByTagName("referred_day").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referred_day").item(0).getTextContent());
	                  }else{
		                	  log.debug("referred_day element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("referred_month").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referred_month").item(0).getTextContent());
	                  }else{
		                	  log.debug("referred_month element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                                    
	                  if( element.getElementsByTagName("referred_year").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referred_year").item(0).getTextContent());
	                  }else{
		                	  log.debug("referred_year element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                 
	                  if( element.getElementsByTagName("referral_in_no").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referral_in_no").item(0).getTextContent());
	                  }else{
		                	  log.debug("referral_in_no element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	               
	                  if( element.getElementsByTagName("referral_agency").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referral_agency").item(0).getTextContent());
	                  }else{
		                	  log.debug("referral_agency element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("referral_source").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referral_source").item(0).getTextContent());
	                  }else{
		                	  log.debug("referral_source element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("cond_surr_contact_id").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("program_name").item(0).getTextContent());
	                  
	                  //<!-- The unique client-program-number identifier -->	                       
	                  recordMap.put(++mapCount, element.getElementsByTagName("program_id").item(0).getTextContent());
	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("client_type").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("county_id").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("ethnicity_id").item(0).getTextContent());            
	                  recordMap.put(++mapCount, element.getElementsByTagName("language").item(0).getTextContent());	                                                   
	                  recordMap.put(++mapCount, element.getElementsByTagName("pregnant").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("young_offender").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("diag_mh_12_months").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("diag_mh_lifetime").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("med_mh_current").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("med_mh_12_months").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("med_mh_lifetime").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("rcv_mh_current").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("rcv_mh_12_months").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("rcv_mh_lifetime").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("hosp_mh_12_months").item(0).getTextContent());                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("hosp_mh_lifetime").item(0).getTextContent());	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("num_overnight_hosp").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("methadone").item(0).getTextContent());
	                  
	                  NodeList firstChildList = element.getElementsByTagName("Substance12mos");
	                  Element firstSubElement = (Element)firstChildList.item(0);
	                  
	                  if(firstSubElement !=  null) {
		                  NodeList children = firstSubElement.getChildNodes();          
		                  insertCount = visitChildNodes(children, mapCount, recordMap);                     
		                  mapCount = recordMap.size();
		                  
		                  for(;insertCount < MAX_SUBSTANCE_CHILDREN;insertCount++){
		                	  recordMap.put(++mapCount, null);
		                  }      
	                  }else{
	                	  log.debug("Substance12mos element not found in record:  " + recsInserted + 1);
	                	  skip_record = true;
	                  }
	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("non_medical_idu_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("prim_substance_id").item(0).getTextContent());
	                  recordMap.put(++mapCount, element.getElementsByTagName("substance_freq_id").item(0).getTextContent());	                  
	                	     
	                  if( element.getElementsByTagName("pps1_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps1_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps1_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("pps1_freq_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps1_freq_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps1_freq_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  

	                  if( element.getElementsByTagName("pps2_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps2_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps2_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }                  
	                 
	                  if( element.getElementsByTagName("pps2_freq_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps2_freq_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps2_freq_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  
	                  if( element.getElementsByTagName("pps3_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps3_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps3_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		              }	                  
	                  
	                  if( element.getElementsByTagName("pps3_freq_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps3_freq_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps3_freq_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  	                  
	                  if( element.getElementsByTagName("pps4_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps4_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps4_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("pps4_freq_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps4_freq_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps4_freq_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  
	                  if( element.getElementsByTagName("pps5_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps5_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps5_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  	       
	                  if( element.getElementsByTagName("pps5_freq_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("pps5_freq_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("pps5_freq_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  NodeList secondChildList = element.getElementsByTagName("GamblingActs");
	                  Element secondSubElement = (Element)secondChildList.item(0);
	                  
	                  if(secondSubElement !=  null) {
		                  NodeList gambChildren = secondSubElement.getChildNodes();
		                  insertCount = visitChildNodes(gambChildren, mapCount, recordMap);
		                  mapCount = recordMap.size();
		                  
		                  for(;insertCount < MAX_GAMBLING_CHILDREN;insertCount++){
		                	  recordMap.put(++mapCount,null);
		                  }                  
	                  }else{
	                	  log.debug("GamblingActs element not found in record:  " + recsInserted + 1);
	                	  skip_record = true;
	                  }
	                  
	                  recordMap.put(++mapCount, element.getElementsByTagName("gambling").item(0).getTextContent());
	                  
	                  if( element.getElementsByTagName("treatment_plan_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("treatment_plan_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("treatment_plan_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  	                  
	                  if( element.getElementsByTagName("reason_termination_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("reason_termination_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("reason_termination_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  if( element.getElementsByTagName("referral_out_no").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("referral_out_no").item(0).getTextContent());
	                  }else{
		                	  log.debug("referral_out_no element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                 
	                  
	                  if( element.getElementsByTagName("refer_to_psc_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("refer_to_psc_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("refer_to_psc_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("refer_to_st_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("refer_to_st_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("refer_to_st_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("Refer_On_Day").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("Refer_On_Day").item(0).getTextContent());
	                  }else{
		                	  log.debug("Refer_On_Day element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("Refer_On_Month").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("Refer_On_Month").item(0).getTextContent());
	                  }else{
		                	  log.debug("Refer_On_Month element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("Refer_On_Year").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("Refer_On_Year").item(0).getTextContent());
	                  }else{
		                	  log.debug("Refer_On_Year element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
                	  
                	  if( element.getElementsByTagName("dis_d").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("dis_d").item(0).getTextContent());
	                  }else{
		                	  log.debug("dis_d element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }                	  
                	  
                	  if( element.getElementsByTagName("dis_hh").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("dis_hh").item(0).getTextContent());
	                  }else{
		                	  log.debug("dis_hh element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }                	  
                	  
                	  if( element.getElementsByTagName("dis_m").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("dis_m").item(0).getTextContent());
	                  }else{
		                	  log.debug("dis_m element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }                	  
                	  
                	  if( element.getElementsByTagName("dis_mm").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("dis_mm").item(0).getTextContent());
	                  }else{
		                	  log.debug("dis_mm element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }                	  
                	  
                	  if( element.getElementsByTagName("dis_y").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("dis_y").item(0).getTextContent());
	                  }else{
		                	  log.debug("dis_y element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }                	  
	                  
	                  if( element.getElementsByTagName("q5a_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_2 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_3").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_3").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_3 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_4").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_4").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_4 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_5").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_5").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_5 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_6").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_6").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_6 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_7").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_7").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_7 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_8").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_8").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_8 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_9").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_9").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_9 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_10").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_10").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_10 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_11").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_11").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_11 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_12").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_12").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_12 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_13").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_13").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_13 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_14").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_14").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_14 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_15").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_15").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_15 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_16").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_16").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_16 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_17").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_17").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_17 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_18").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_18").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_18 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_19_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_19_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5a_19_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5a_19_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5a_19_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("reason_termination_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_2 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_3").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_3").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_3 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_4").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_4").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_4 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_5").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_5").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_5 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_6").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_6").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_6 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_7").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_7").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_7 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_8").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_8").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_8 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_9").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_9").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_9 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_10").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_10").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_10 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_11").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_11").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_11 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  
	                  
	                  if( element.getElementsByTagName("q6a_12").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_12").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_12 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_13").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_13").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_13 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_14").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_14").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_14 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_15").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_15").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_15 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_16").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_16").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_16 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6a_17").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6a_17").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6a_17 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q3_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q3_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q3_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q3_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q3_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("q3_2 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q2_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q2_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q2_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q2_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q2_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("q2_2 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q7a").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q7a").item(0).getTextContent());
	                  }else{
		                	  log.debug("q7a element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	                  	                  
	                  if( element.getElementsByTagName("q7b").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q7b").item(0).getTextContent());
	                  }else{
		                	  log.debug("q7b element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q7c").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q7c").item(0).getTextContent());
	                  }else{
		                	  log.debug("q7c element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("reason_gambling_id").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("reason_gambling_id").item(0).getTextContent());
	                  }else{
		                	  log.debug("reason_gambling_id element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q4").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q4").item(0).getTextContent());
	                  }else{
		                	  log.debug("q4 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5b_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5b_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5b_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5b_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5b_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5b_2 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q5b_3").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q5b_3").item(0).getTextContent());
	                  }else{
		                	  log.debug("q5b_3 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6b_1").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6b_1").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6b_1 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                  
	                  if( element.getElementsByTagName("q6b_2").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6b_2").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6b_2 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }	                  
	                    
	                  if( element.getElementsByTagName("q6b_3").item(0) !=  null) {
	                	  recordMap.put(++mapCount, element.getElementsByTagName("q6b_3").item(0).getTextContent());
	                  }else{
		                	  log.debug("q6b_3 element not found in record:  " + recsInserted + 1);
		                	   skip_record = true;
		               }
	              }//end for loop, get elements inside supplementaryData
	              
	              if(log.getLevel() == Level.DEBUG) {
		              for (Map.Entry<Integer, String> entry : recordMap.entrySet()) {                        
		            	  log.debug("SuppValidator.parseXML()  Key: " + new Object[]{entry.getKey() +  "Value: {1} " +  entry.getValue()});		                                
		              }
	              }	              	         
	              if(!skip_record) {	            	  
	            	  insertCount =  insertRecord(recordMap);
	            	  if(insertCount == 1) 
	            		  log.debug("SuppValidator.parseXML()  record inserted " + "\r \n");
	            		  
	            	  recsInserted = recsInserted + insertCount;	            	  
		              recordMap.clear();
		              mapCount = 0;
	              }else {	            	  
	            	  log.debug("SuppValidator skip_ record flag true. Record not inserted." );	  
		              recordMap.clear();
		              mapCount = 0;
		              skip_record = false;
	              }
	              
	          } //end for loop, get list length
	          
	          return recsInserted;			

	      } catch (Exception  e) {	    	  
	    	  log.error("SuppValidator.parseXML()  record: " + e.getMessage());	         
	          return recsInserted;
	      }
	}
	
	public Integer parseXMLDebug( Document document) {
		return null;
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
	
	public Integer insertRecord(Map<Integer, String> recordMap)     {		
		Connection conn = null;
		
		try {
			
			DataSource ds = DSCreator.getDataSource();
			conn = ds.getConnection();			
			PreparedStatement pstmt = null;
					
			String sql = "{call INSERT_RAW_SUPP_OH_RECORD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n"   
								                                                                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n" 
								                                                                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n" //150
								                                                                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?\r\n"          //171 : 20231016  //170: 20231107: removed mother_tongue
								                                                                        + ")}";					                                                                        
			pstmt = conn.prepareCall(sql);
			pstmt.clearParameters();
			
			if(log.getLevel() == Level.DEBUG) {
				for (Map.Entry<Integer, String> debug_entry : recordMap.entrySet())  {	        	 
	        		 log.debug("SuppValidator.insertRecord(): Key: " + new Object[]{debug_entry.getKey() +  "Value: {1} " + debug_entry.getValue()});
	        	 }				
	        }	      
			          
	        for (Map.Entry<Integer, String> entry : recordMap.entrySet())  { 	        
				pstmt.setString(entry.getKey(), entry.getValue());			
	        }	      
	        pstmt.executeQuery();
	        return 1;
		} catch (SQLException e) {			
			log.error("SuppValidator.insertRecord() threw : " + e.getMessage());
			return 0;		
	   } catch (Exception e) {			
		  log.error("SuppValidator.insertRecord() threw : " + e.getMessage());
		  return 0;
	   }finally {
		   if(conn != null){
		        try {
		          conn.close();
		        } catch (SQLException e) {
		           log.error("SuppValidator.insertRecord() threw : " + e.getMessage());
		        }
		      }
       }
	}	

}
