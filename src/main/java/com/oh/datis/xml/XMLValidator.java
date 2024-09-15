package com.oh.datis.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.oh.datis.dbhandler.DSCreator;
import com.oh.datis.exceptionhandlers.XSDValidationErrorHandler;
import com.oh.datis.implement.ReaderChannel;
import com.oh.datis.interfaces.IChannel;
import com.oh.datis.interfaces.IValidator;

public class XMLValidator {
	public static final String HONOUR_ALL_SCHEMA_LOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
	public static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
	public static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
	public static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
	public static final Integer MAX_SUBSTANCE_CHILDREN = new Integer(25);
	public static final Integer MAX_GAMBLING_CHILDREN = new Integer(25);
	private static final Logger log = Logger.getLogger(XMLValidator.class);

	DocumentBuilder documentBuilder = null;
	static Map<String, String> persistMap = null;
	InputSource inputSource = null;	
	WatchKey watchKey = null;
	private static final Map<WatchKey, Path> pathkeys = new HashMap<WatchKey, Path>();
	
	public static void main(String[] paramArrayOfString) {

		try {					
			String log4jConfPath = ".\\log4j.properties";
			PropertyConfigurator.configure(log4jConfPath);

			WatchService watchService = FileSystems.getDefault().newWatchService();			
			Path sourceDirPath = Paths.get(new File(".\\file_Intake").toURI());			
			WatchKey watchKey = sourceDirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			pathkeys.put(watchKey, sourceDirPath);
			
			File des = new File(".\\file_processed");			
			if (!des.exists())
				des.mkdir();			

			Path targetDirPath = Paths.get(des.toURI());			
			
			File issueFile = new File(".\\file_failed");			
			if (!issueFile.exists())
				issueFile.mkdir();
			
			Path issueDirPath = Paths.get(issueFile.toURI());
			
			while ((watchKey = watchService.take()) != null) {
				Path dir = pathkeys.get(watchKey);
				for (WatchEvent<?> event : watchKey.pollEvents()) {					
					log.log(Level.DEBUG, "Event kind:" + event.kind() + ". File affected: " + event.context() + ".");

					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path name = ev.context();
					Path child = dir.resolve(name);					

					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {

						try {
							// https://stackoverflow.com/questions/40779633/java-the-process-cannot-access-the-file-because-it-is-being-used-by-another-pr
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							log.error("XMLValidator raised exception: " + e.getMessage());
						}

						// here is a file or a folder modified in the folder
						File fileCaught = child.toFile();
						if(fileCaught == null) {
							log.debug("XMLValidato file not caught ");
						}
						
						// Start processing xml file
						IChannel readerChannel = new ReaderChannel();
						
						// determine  Validator						
						IValidator validator = readerChannel.determineValidator(fileCaught.getName());
						if(validator == null)
							continue;

						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);						
						InputSource inputSource = new InputSource(new FileReader(fileCaught));
						Document document = dbf.newDocumentBuilder().parse(inputSource);	
						
						log.debug("XMLValidator file caught: " + fileCaught.getName());	
						
						if(document == null){
							log.debug("no document: " + document);
						}

					   ArrayList<String> arrayList = validator.validateXMLFile(document,	fileCaught.getName());						

						int insertCount = 0;
						log.debug("file caught: " + fileCaught.toURI());
						Path sourceFilePath = Paths.get(fileCaught.toURI());		//				
						
						//LocalDateTime now = LocalDateTime.now();						
						//StringBuilder ldateFormat = new StringBuilder(DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss").format(now));
						
						//StringBuilder sbfileCaught = new StringBuilder(fileCaught.getName());
						//sbfileCaught.append( "_");
						//sbfileCaught.append( ldateFormat);

						// I'll need to check the errors in the Validation object						
						for (String str : arrayList) 
							log.debug("XMLValidator raised exception: " + str);						
						
						if(arrayList.size() > 0) {														
							//Path issueFilePath = issueDirPath.resolve(sbfileCaught.toString());
							//Files.move(sourceFilePath, issueFilePath, StandardCopyOption.ATOMIC_MOVE);
							
							Path issueFilePath = issueDirPath.resolve(fileCaught.getName());
							Files.move(sourceFilePath, issueFilePath, StandardCopyOption.ATOMIC_MOVE);
							break;
						}

						// return number of records inserted
						insertCount = insertCount + validator.parseXML(document);
						log.info(insertCount + " record(s) inserted");
												
						//Path targetFilePath = targetDirPath.resolve(sbfileCaught.toString());						
						//Files.move(sourceFilePath, targetFilePath, StandardCopyOption.ATOMIC_MOVE);
						
						Path targetFilePath = targetDirPath.resolve(fileCaught.getName());
						Files.move(sourceFilePath, targetFilePath, StandardCopyOption.ATOMIC_MOVE);

					} // end processing intake file
				}
				watchKey.reset();
			}
		} catch (Exception e) {
			log.error("XMLValidator.main() threw : " + e.getMessage());
		}
	}

	public ArrayList<String> validateXMLFile(String paramString1, String paramString2) {
		ArrayList<String> arrayList = new ArrayList<String>();
		Document document = null;
		try {			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			documentBuilder = dbf.newDocumentBuilder();

			log.info("validateXMLFile paramString1: " + paramString1);
			inputSource = new InputSource(new FileReader(paramString1));
			document = documentBuilder.parse(inputSource);
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			schemaFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
			schemaFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
			schemaFactory.setFeature("http://apache.org/xml/features/validate-annotations", false);
			schemaFactory.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", false);
			log.info("validateXMLFile paramString2: " + paramString2);
			StreamSource streamSource = new StreamSource(new File(paramString2));
			Schema schema = schemaFactory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			XSDValidationErrorHandler xSDValidationErrorHandler = new XSDValidationErrorHandler();
			validator.setErrorHandler((ErrorHandler) xSDValidationErrorHandler);
			validator.validate(new DOMSource(document));
			arrayList = xSDValidationErrorHandler.getEncounteredErrors();
			if (arrayList.size() == 0) {
				// System.out.println("Root Element :" +
				// document.getDocumentElement().getNodeName());
				log.info("Root Element :" + document.getDocumentElement().getNodeName());
			}
		} catch (ParserConfigurationException pce) {
			log.error("XMLValidator.validateXMLFile() threw : " + pce.getMessage());
			arrayList.add("Error encountered trying to parse through the inputted XML file");
		} catch (SAXException saxException) {
			log.error("XMLValidator.validateXMLFile() threw : " + saxException.getMessage());
			arrayList.add(saxException.getMessage());
		} catch (IOException iOException) {
			log.error("XMLValidator.validateXMLFile() threw :  " + iOException.getMessage());
			arrayList.add("XMLValidator.validateXMLFile() threw : " + iOException.getMessage());
		}
		return arrayList;
	}
}
