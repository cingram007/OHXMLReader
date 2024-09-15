package com.oh.datis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Lucian_Acatrinei
 */
public class XMLUtil {
 public static final String STRING_REPORT_DATE_FORMAT = "yyyy-MM-dd";
 public static final String STRING_REPORT_PRINTED_ON_DATE_FORMAT = "MMM d', 'yyyy HH:mm a";
 public static final String STRING_DATE_FORMAT_Z = "yyyy-MM-dd'Z'";
 
 
 public static final SimpleDateFormat SimpleReportDateFormat = new SimpleDateFormat(STRING_REPORT_DATE_FORMAT);

   public static String getXMLStringFromDoc(Document doc) {
       String result=null;
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            writer.flush();
            StringBuffer sb = writer.getBuffer();
            result = writer.getBuffer().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return result;
   }   
       // creates a XMLGregorianCalendar
    
    // parameters fromDateTime in format specified by dateFormat
    //            dateFormat: a format understood by SimpleDateFormat i.e "yyyy-MM-dd'T'HH:mm:ss.SSS".. or just date "yyyy-MM-dd'Z'" etc.
    // returns XMLGregorianCalendar
    public static XMLGregorianCalendar asXMLGregorianCalendarWithFormat(String fromDateTime, String dateFormat) throws IllegalArgumentException{
            DatatypeFactory df = null;
            GregorianCalendar gc =null;            
            if (dateFormat == null || dateFormat.isEmpty()){
                 throw new IllegalArgumentException("Invalid dateFormat should be a format understood by SimpleDateFormat i.e \"yyyy-MM-dd'T'HH:mm:ss.SSS\".. or just date \"yyyy-MM-dd'Z'\" etc.");
            }
            try {
                df = DatatypeFactory.newInstance();
            }catch(DatatypeConfigurationException dce) {
                throw new IllegalStateException(
		"Exception while obtaining DatatypeFactory instance", dce);
	    }

            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            java.util.Date date = null;
	    try { 
                date = sdf.parse(fromDateTime);
            }catch (Exception e) 
                { e.printStackTrace(); 
            }

            if (date == null) {
                return null;
            } else {
                gc = new GregorianCalendar();
                gc.setTimeInMillis(date.getTime());	
                XMLGregorianCalendar xmlGregorianCalendar=df.newXMLGregorianCalendar(gc);	            
                return xmlGregorianCalendar;
            }
    }
    
    public static Node getNode(NodeList nodeList, String nodeName){
        Node result = null;
        if (nodeList!=null && nodeList.getLength()>0){
            for (int i=0;i<nodeList.getLength();i++){
                Node node =nodeList.item(i);
                if (node.getNodeName()!= null && node.getNodeName().equals(nodeName)){
                    result = node;
                    break;
                }
            }
        }
        return result;    
    }
    public static Node addNodeWithValue(Document doc, Node node, String nodeName, String value){
       Node result =  doc.createElement(nodeName);
       result.setTextContent(value);
       node.appendChild(result);
       return result;
    }
    
    public static void removeAllSubnodes(Node node){

        if (node!=null && node.getChildNodes().getLength() >0){
            while (node.getChildNodes().getLength()>0){
                Node child =node.getChildNodes().item(1);
                if (child== null){
                    break;
                }
                node.removeChild(child);
            }
        }
    }

    public static void copyAllSubnodes(Node srcNode, Node destNode ){
        if (srcNode!=null && destNode!=null && srcNode.getChildNodes().getLength() >0){
            for (int i =0; i<srcNode.getChildNodes().getLength();i++){
                Node node = srcNode.getChildNodes().item(i);
                if (node!=null){
                   destNode.appendChild(node.cloneNode(true));
                }
            }
        }
    }
    
    //populates a table node whith row node(s), each "row" containing one or more items
    //doc: a document - needed to create nodes
    //xmlTable: table of XMLItems containing actual values
    //tableNode: a xml node which will be populated
    //genericRowNode: a row node - will be cloned as needed
    //genericItemNode: formated name for item node
    //itemDescriptionNode: a description subnode of genericItemNode -
    //itemOtherNode: node to containing otherText
    //otherPropertyName: the key property of XMLItems pointing to "otherText" value     
    //     i.e
    //    <dtn:REASON_CONSASSESS_NOT_COMPLTBL>    <-  tableNode
    //				<dtn:REASON_ROW>   <- genericRowNode
    //					<dtn:ITEMC1> <- from genericItemNode + counter
    //						<dtn:DESCRIPTION>Length of Assessment</dtn:DESCRIPTION>  <- itemDescriptionNode
    //						<dtn:CHECK>Y</dtn:CHECK> <- check node is standard
    //					</dtn:ITEMC1>
    //					<dtn:ITEMC2>
    //					    <dtn:CHECK>Y</dtn:CHECK>
    //						<dtn:DESCRIPTION>Other</dtn:DESCRIPTION>
    //						<dtn:OTHERTXT>Client does not like to complete self assessment too often</dtn:OTHERTXT>   <- itemOtherNode
    //					</dtn:ITEMC2>
    //				</dtn:REASON_ROW>
    //                         ...
//    static public void populateXMLTable(Document doc, XMLTable xmlTable, Node tableNode, Node genericRowNode, 
//                                        String genericItemNode, Node itemDescriptionNode,Node itemCheckNode,
//                                        Node itemOtherNode, String otherPropertyName){
//        XMLUtil.removeAllSubnodes(tableNode);
//        XMLUtil.removeAllSubnodes(genericRowNode);
//        for (int i=0; i<xmlTable.rows;i++){
//            Node msRow = genericRowNode.cloneNode(true);
//            List fields= xmlTable.getColumnList(i);
//            for (int j=0; j<xmlTable.columns;j++){
//                Node msItem=doc.createElement(genericItemNode.replace(ReportConstants.FORMAT_MARKER, String.valueOf(j+1)));
//                XMLItem xmlItem =(XMLItem)fields.get(j);
//                if (xmlItem != null){
//                    OcanReference reference = (OcanReference) xmlItem.getObj();
//                    if (reference!=null){
//                        Node descNode=itemDescriptionNode.cloneNode(true);
//                        descNode.setTextContent(reference.getLabel()); 
//                        String check =xmlItem.getProperty(ReportConstants.CHECK_KEY);
//                        if (check!=null && !check.isEmpty()){
//                            Node checkNode=itemCheckNode.cloneNode(true);
//                            checkNode.setTextContent(check);
//                            msItem.appendChild(checkNode);
//                        }
//                        if (itemOtherNode!= null && otherPropertyName!=null && !otherPropertyName.isEmpty()){
//                            String other =xmlItem.getProperty(otherPropertyName);
//                            if (other!=null && !other.isEmpty()){
//                                Node otherNode=itemOtherNode.cloneNode(true);
//                                otherNode.setTextContent(other);
//                                msItem.appendChild(otherNode);
//                            }
//                        }
//                        msItem.appendChild(descNode);
//                    }
//                }
//                msRow.appendChild(msItem);
//            }
//            tableNode.appendChild(msRow);
//        }
//   }
//    // similar with populateXMLTable, 
//    static public void populateXMLTableWithHeder(Document doc, XMLTable xmlTable, Node tableNode, Node genericRowNode, 
//                                        String genericItemNode, Node itemDescriptionNode, Node itemCheckNode,
//                                        Node headerNode, String headerPropertyName){
//        XMLUtil.removeAllSubnodes(tableNode);
//        XMLUtil.removeAllSubnodes(genericRowNode);
//        for (int i=0; i<xmlTable.rows;i++){
//            Node msRow = genericRowNode.cloneNode(true);
//            List fields= xmlTable.getColumnList(i);
//            for (int j=0; j<xmlTable.columns;j++){
//                Node msItem=doc.createElement(genericItemNode.replace(ReportConstants.FORMAT_MARKER, String.valueOf(j+1)));
//                XMLItem xmlItem =(XMLItem)fields.get(j);
//                if (xmlItem != null){
//                    OcanReference reference = (OcanReference) xmlItem.getObj();
//                    if (reference!=null){
//                        Node descNode=itemDescriptionNode.cloneNode(true);
//                        descNode.setTextContent(reference.getLabel()); 
//                        String check =xmlItem.getProperty(ReportConstants.CHECK_KEY);
//                        if (check!=null && !check.isEmpty()){
//                            Node checkNode=itemCheckNode.cloneNode(true);
//                            checkNode.setTextContent(check);
//                            msItem.appendChild(checkNode);
//                        }
//                        if (headerNode!= null && headerPropertyName!=null && !headerPropertyName.isEmpty()){
//                            String header =xmlItem.getProperty(headerPropertyName);
//                            if (header!=null && !header.isEmpty()){
//                                Node node=headerNode.cloneNode(true);
//                                node.setTextContent(header);
//                                msItem.appendChild(node);
//                            }
//                        }
//                        msItem.appendChild(descNode);
//                    }
//                }
//                msRow.appendChild(msItem);
//            }
//            tableNode.appendChild(msRow);
//        }
//   }

    // creates a date string in format STRING_REPORT_DATE_FORMAT
    // from date string in format "yyyy-MM-dd'Z'"
    public static String getCalendarReportStringFormat(String dateZ){
        String result = null;
        if (dateZ!=null){
            XMLGregorianCalendar xmlCalDate= XMLUtil.asXMLGregorianCalendarWithFormat(dateZ, "yyyy-MM-dd'Z'");
            if (xmlCalDate!=null){
                result= XMLUtil.getCalendarReportStringFormat(xmlCalDate.toGregorianCalendar()); 
            }
        }
        return result;

    }
    
    public static String getCalendarReportStringFormat(Calendar calendar){
            return getCalendarStringWithFormat(calendar, XMLUtil.STRING_REPORT_DATE_FORMAT);
    }

    public static String getCalendarReportStringFormat(Date date){
            return getCalendarStringWithFormat(date, XMLUtil.STRING_REPORT_DATE_FORMAT);
    }
    public static String getCalendarPrintedOnReportStringFormat(Calendar calendar){
            return getCalendarStringWithFormat(calendar, XMLUtil.STRING_REPORT_PRINTED_ON_DATE_FORMAT);
    }

    public static String getCalendarPrintedOnReportStringFormat(Date date){
            return getCalendarStringWithFormat(date, XMLUtil.STRING_REPORT_PRINTED_ON_DATE_FORMAT);
    }

    public static String getCalendarStringWithFormat(Calendar calendar, String dateFormat){
		   String result = null;
		   if (calendar !=null){
			   SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			   TimeZone tzc=calendar.getTimeZone();		   
			   sdf.setTimeZone(tzc);
			   result = sdf.format(calendar.getTime());
		   }
		   return result;
    }
    
    //creates a string from Calendar in format specified by dateFormat 
    // no timezone
    //dateFormat: a format understood by SimpleDateFormat applicable for Date i.e "yyyy-MM-dd'Z'" etc.
    public static String getCalendarStringWithFormat(Date date, String dateFormat){
		   String result = null;
		   if (date !=null){
			   SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			   result = sdf.format(date.getTime());
		   }
		   return result;
    }

    //creates a string from XMLGregorianCalendar in format specified by dateFormat
    //  i.e : '2014-02-25Z' , no time zone functionality (using defaults)
    //   dateFormat: a format understood by SimpleDateFormat i.e "yyyy-MM-dd'T'HH:mm:ss.SSS".. or just date "yyyy-MM-dd'Z'" etc.
    // returns String
    public static String getCalendarStringWithFormat(XMLGregorianCalendar xmlGregorianCalendar, String dateFormat){
		   String result = null;
		   if (xmlGregorianCalendar !=null){
			   SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                           //TimeZone tzc=xmlGregorianCalendar.getTimeZone(DatatypeConstants.FIELD_UNDEFINED);		   
			   //sdf.setTimeZone(tzc);
			   result = sdf.format(xmlGregorianCalendar.toGregorianCalendar().getTime());
		   }
		   return result;
    }

      
    public static Properties loadProperties(String resourceName){
    Properties configProp = new Properties();
        InputStream in = Thread.currentThread().getClass().getResourceAsStream(resourceName);
        if (in==null){
            if (resourceName.charAt(0)!='/' && resourceName.charAt(0)!='\\'){
              in  = Thread.currentThread().getClass().getResourceAsStream("/"+resourceName);
            }
        }
        try {
            configProp.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    return configProp;
    }

    
    public static URL openURL(String href){
            URL url = null;            
            if (href != null){
                InputStream in= null;
                try{
                    url=Thread.currentThread().getClass().getResource(href);
                    if (url == null){
                        if (href.charAt(0)!='/' && href.charAt(0)!='\\'){
                            url=Thread.currentThread().getClass().getResource("/"+href);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return url;
    }

    public static String convertNullStringToEmpty(String aString){
       String result="";
       if (aString != null){
           result=aString;
       }
       return result;
   }
    
   public static String[] createValuesArray(String... value){
       String[] result=null;
       if (value != null){
          result=value;          
       }
       return result;
   }
   public static String[] createValuesArray(String value){
       String[] result=null;
       if (value != null){
          result=new String[1] ;
          result[0]=value;
       }
       return result;
   }
   

   
   public static ArrayList createValuesList(String[] values){
      ArrayList result= null;
      if (values!=null){
          result=new ArrayList();
          for (int i=0; i<values.length;i++){
              result.add(values[i]);
          }
      }
      return result;
   }

   //wrapper
   public static ArrayList createValuesList(String value){
     return createValuesList(createValuesArray (value));
  }
   
  public static byte[] readBytesfromFile(String fileName){
        byte[] result = null;
        if (fileName == null || fileName.isEmpty()){
            return result;
        }
        File file = new File (fileName); 
        try {
               InputStream input =  new FileInputStream(file);
               int size = (int)file.length();
               result = new byte[size];               
               input.read(result, 0, size);
               input.close();
        } catch (Exception ex) {
              ex.printStackTrace();
        }
        return result;
    }
   
   public static boolean writeBytesToFile(byte[] bytes, String fileName){
        return writeBytesToFile(bytes, fileName, true);
    }
    
    public static boolean writeBytesToFile(byte[] bytes, String fileName, boolean overwrite){
        boolean result = false;
        if (fileName == null || fileName.isEmpty()||
            bytes == null){
            return result;
        }
        File file = new File (fileName); 
        if (file.exists() && overwrite){
            file.delete();
            file = new File (fileName);
        }
        try {
               OutputStream output =  new FileOutputStream(file);
               output.write(bytes);
               output.flush();
               output.close();
               result = true;
        } catch (Exception ex) {
              ex.printStackTrace();
        }
        return result;
    }

    public static String readFileToString(String fileName){
        String result = null;
        if (fileName == null || fileName.isEmpty()){
            return result;
        }
        File file = new File (fileName);
        try {
               StringBuffer strbuff = new StringBuffer();
               FileReader reader = new FileReader(file);
               char chr = 0;
               int cri = -1;
                while ((cri = reader.read())>-1){
                  chr = (char)cri;
                  strbuff.append(chr);
                }
                result = strbuff.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
   
    
    public static boolean writeStringToFile(String stringBuff, String fileName, boolean overwrite){
        boolean result = false;
        if (fileName == null || fileName.isEmpty()||
            stringBuff == null){
            return result;
        }
        File file = new File (fileName);
        if (file.exists() && overwrite){
            file.delete();
            file = new File (fileName);
        }
        try {
               OutputStream output =  new FileOutputStream(file);
               FileWriter writer = new FileWriter(file);
               writer.write(stringBuff);
               writer.flush();
               writer.close();
               output.flush();
               output.close();
               result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
   
    // converts a BigInteger to String
    // length is minimum length e.g. bi=123 then result is 0123 
    // defResult is returned if bi is null
    public static String bigIntegerToString(BigInteger bi, int length, String defResult){
        String result=defResult;
        if (bi!=null && length>0){
            result=String.valueOf(bi.intValue());
            if (result.length()<length){
                char [] chr = new char[length-result.length()];
                Arrays.fill(chr,'0');
                result=String.copyValueOf(chr)+result;
            }
        }
        return result;
    }
    public static Node getNodebyAttributeValue(NodeList nodeList, String nodeName , String atributteName, String atributteValue){
        Node result = null;
        if (nodeList!=null && nodeList.getLength()>0){
            for (int i=0;i<nodeList.getLength();i++){
                Node node =nodeList.item(i);
                if (node.getNodeName()!= null && node.getNodeName().equals(nodeName)){
                    NamedNodeMap attributes = node.getAttributes();
                    if (attributes!=null && attributes.getLength()>0){
                        Node attrName=attributes.getNamedItem(atributteName);
                        String strAttrVal= attrName.getTextContent();
                        if (strAttrVal!=null && strAttrVal.equals(atributteValue)) {
                            result= node;
                            break;
                        }
                    }                    
                }
            }
        }
        return result;    
    }
    
}
