package com.oh.datis.exceptionhandlers;

import java.util.ArrayList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XSDValidationErrorHandler implements ErrorHandler {
  private ArrayList<String> errors = new ArrayList<>();
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    this.errors.add(paramSAXParseException.getMessage());
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    this.errors.add("ERROR: Validation of Message has encountered a fatal error: " + paramSAXParseException.getMessage());
    throw paramSAXParseException;
  }
  
  public ArrayList<String> getEncounteredErrors() {
    return this.errors;
  }
}
