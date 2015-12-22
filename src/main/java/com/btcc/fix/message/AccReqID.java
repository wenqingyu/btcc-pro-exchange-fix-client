package com.btcc.fix.message;

import quickfix.StringField;

public class AccReqID extends StringField {

	private static final long serialVersionUID = 2330170161864948797L;
	public static final int FIELD = 8000;
	
	public AccReqID() {
		super(8000);
	}

	 public AccReqID(String data) {
	        super(FIELD, data);
	    }
}
