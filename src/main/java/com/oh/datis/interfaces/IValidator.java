package com.oh.datis.interfaces;

import java.util.ArrayList;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public interface IValidator {	
	ArrayList<String> validateXMLFile( Document document, String inputFile);
	Integer parseXML( Document document) ;
	Integer parseXMLDebug( Document document) ;
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
	Integer insertRecord(Map<Integer, String> recordMap);
}
