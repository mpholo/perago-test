package com.perago.techtest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The object representing a diff.
 * Implement this class as you see fit. 
 *
 */
public class Diff<T extends Serializable> {

	private String operation;
	private String objectName;
	private List<Field> fields = new ArrayList<>();
    private List<Diff<T>> diff = new ArrayList<>();; 	
	
	public List<Field> getFields() {
		return fields;
	}
	public void addFields(Field field) {
		this.fields.add(field);
	}


	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public List<Diff<T>> getDiff() {
		return diff;
	}
	public void addDiff(Diff<T> diff) {
		this.diff.add(diff);
	}
	
	
	
	
	
	
	
}
