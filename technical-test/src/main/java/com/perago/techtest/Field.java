package com.perago.techtest;

import java.util.ArrayList;
import java.util.List;

public class Field {

	private String fieldName;
	private String fieldValue;
	
	
	public Field(String fieldName, String fieldValue) {
		
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
}
