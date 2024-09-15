package com.oh.datis.implement;

import java.util.Map;

import com.oh.datis.interfaces.IChannel;
import com.oh.datis.interfaces.IValidator; 

public class ReaderChannel implements IChannel {
	
    public ReaderChannel(){}   

	@Override
	public IValidator determineValidator(String  inputFileName) {
		StringBuilder stb = new StringBuilder(inputFileName);    	
		if(stb.toString().toLowerCase().contains("pds")) 
    		return new PDSValidator( );				    	    
		else if(stb.toString().toLowerCase().contains("supp"))
			return  new SuppValidator();		
		else return null;
	}
}
