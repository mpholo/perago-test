package com.perago.techtest;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiffEngineImpl implements DiffEngine {

	public <T extends Serializable> T apply(T original, Diff<?> diff) throws DiffException {

		try {
			
			Class<? extends Object> originalClass = original.getClass();
			Method[] originalMethodArr = originalClass.getMethods();
			for (int i = 0; i < originalMethodArr.length; i++) {
				Method method = originalMethodArr[i];
				
				
				
				if (method.getName().startsWith("get") && method.getParameterTypes().length == 0
						&& !method.getName().equals("getClass")) {
					
					Object value = method.invoke(original, null);
					
					String fieldName = Introspector.decapitalize(method.getName().substring(3));
					
					for(Field field: diff.getFields()) {
						System.out.println(fieldName +" has changed  to "+value);
						if(fieldName==field.getFieldName() && value !=field.getFieldValue()) {
							System.out.println(fieldName +" has changed  to "+value);
						}
					}
				}

			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<Field> extractProperies(Object obj) {

		Object value = null;
		List<Field> fields = new ArrayList<>();

		try {
			Class<? extends Object> objClass = obj.getClass();
			Method[] objMethodArr = objClass.getMethods();

			for (Method objMethod : objMethodArr) {
				if (objMethod.getName().startsWith("get") && objMethod.getParameterTypes().length == 0
						&& !objMethod.getName().equals("getClass")) {

					value = objMethod.invoke(obj, null);
					String fieldName = Introspector.decapitalize(objMethod.getName().substring(3));
					if (value != null && (objClass.isPrimitive()
							|| objMethod.getGenericReturnType().getTypeName() == "java.lang.String")) {
						fields.add(new Field(fieldName, value.toString()));
					} else if (value != null) {
						extractProperies(obj);

					}

				}
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return fields;

	}

	public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {

		Diff<T> diff = new Diff<T>();
		List<Object> modifiedValues = new ArrayList<>();
		List<String> objectName = new ArrayList<>();

		try {
			if (original == null && modified != null || (original != null && modified != null)) {

				if (original == null && modified != null) {
					diff.setOperation("Create");
				} else if (original != null && modified != null) {
					diff.setOperation("Update");
				}

				diff.setObjectName(modified.getClass().getSimpleName());

				Class<? extends Serializable> modifiedClass = modified.getClass();
				Method[] modifiedClassMethodsArr = modifiedClass.getMethods();

				for (Method modifiedClassMethod : modifiedClassMethodsArr) {

					if (modifiedClassMethod.getName().startsWith("get")
							&& modifiedClassMethod.getParameterTypes().length == 0
							&& !modifiedClassMethod.getName().equals("getClass")) {

						Object modifiedValue = modifiedClassMethod.invoke(modified, null);

						String fieldName = Introspector.decapitalize(modifiedClassMethod.getName().substring(3));

						if (modifiedValue != null && !(modifiedClassMethod.getGenericReturnType().getClass()
								.isPrimitive()
								|| modifiedClassMethod.getGenericReturnType().getTypeName() == "java.lang.String")) {
                                                      
							if (original == null && modified != null) {
                                if(modifiedValue instanceof Collection<?>) {
                                	Collection<String> collection=(Collection<String>)modifiedValue;
                                	String allFields="";
                                	for(Object v :collection.toArray()) {
                                		
                                		allFields+=v+",";
                                	
                                	}
                                	allFields=allFields.substring(0, allFields.length()-1);
                                	diff.addFields(new Field(fieldName,allFields));
                                } else {
            					modifiedValues.add(modifiedValue);
								objectName.add(fieldName);
                                }
								
							}

						} else {

							if (original == null && modified != null) {
								diff.addFields(new Field(fieldName,
								modifiedValue != null ? modifiedValue.toString() : null));
							} else if (original != null && modified != null) {
								
                               
								Class<? extends Serializable> originalClass = original.getClass();
								Method[] originalClassMethodsArr = originalClass.getMethods();
								for (Method originalClassMethod : originalClassMethodsArr) {

									if (originalClassMethod.getName().startsWith("get")
											&& originalClassMethod.getParameterTypes().length == 0
											&& !originalClassMethod.getName().equals("getClass")) {

										Object originalValue = originalClassMethod.invoke(original, null);

										String fieldName2 = Introspector.decapitalize(originalClassMethod.getName().substring(3));

										if (originalValue != null && !(originalClassMethod.getGenericReturnType().getClass()
												.isPrimitive()
												|| originalClassMethod.getGenericReturnType().getTypeName() == "java.lang.String")) {
										
												
												/*The method to cyclic relationship code should go here
												 * Pass original values and modified values to the method
												 * compare the actual values of the child object and return the difference
												 */
											
																					

										} else {
                                       		if (originalClassMethod.getName()==modifiedClassMethod.getName()
												 && originalValue!=modifiedValue) {
												
                                       			String v1=originalValue != null ? originalValue.toString() : null;
                                       			String v2= modifiedValue != null ? modifiedValue.toString() : null;
                                       			String valuesChanges =  v1+","+v2;
												diff.addFields(new Field(fieldName2,valuesChanges));
											} 

										}

									}
								}
								
								
							}

						}

					}
				}
			} else if (original != null && modified == null) {
				diff.setOperation("Delete");
				diff.setObjectName(original.getClass().getSimpleName());

			}

			for (int i = 0; i < modifiedValues.size(); i++) {

				Object objValue = modifiedValues.get(i);

				List<Field> fields = extractProperies(objValue);

				Diff<T> relatedDiff = new Diff<T>();
				for (Field f : fields) {

					relatedDiff.addFields(new Field(f.getFieldName(), f.getFieldValue()));
					relatedDiff.setObjectName(objectName.get(i));
					relatedDiff.setOperation(diff.getOperation());

				}
				diff.addDiff(relatedDiff);	

			}
			

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return diff;
	}

}
