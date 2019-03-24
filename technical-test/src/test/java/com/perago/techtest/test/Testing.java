package com.perago.techtest.test;

import java.util.HashSet;
import java.util.Set;

import com.perago.techtest.Diff;
import com.perago.techtest.DiffEngineImpl;
import com.perago.techtest.DiffException;
import com.perago.techtest.DiffRenderer;

public class Testing implements DiffRenderer{


	private String outputDiff(Diff<?> diff) {
		StringBuilder output = new StringBuilder();
		StringBuilder numbering = new StringBuilder("1");
		int i=0;
		output.append("1. "+diff.getOperation()+": "+diff.getObjectName()+"\n");
		
		for(i=0;i<diff.getFields().size();i++) {
			if(diff.getOperation()=="Create")
			  output.append("1."+(i+1)+ " "+diff.getOperation()+": "+diff.getFields().get(i).getFieldName()+" as "+diff.getFields().get(i).getFieldValue()+"\n");
			else
			{
				String originalValue=diff.getFields().get(i).getFieldValue();
				originalValue=originalValue.substring(0,originalValue.indexOf(","));
				String modifiedValue=diff.getFields().get(i).getFieldValue();
				modifiedValue=modifiedValue.substring(modifiedValue.indexOf(",")+1,modifiedValue.length());
				
			 output.append("1."+(i+1)+ " "+diff.getOperation()+": "+diff.getFields().get(i).getFieldName()+" from "+originalValue+" to "+modifiedValue+"\n");
			}
				
					
		}	
		
		for(Diff<?> relatedDiff:diff.getDiff()) {
			numbering.append(".1");
			output.append(numbering.toString()+" "+relatedDiff.getOperation()+": "+relatedDiff.getObjectName()+"\n");
			for(int j=0;j<relatedDiff.getFields().size();j++) {
				String fieldName = relatedDiff.getFields().get(j).getFieldName();
				String fieldValue = relatedDiff.getFields().get(j).getFieldValue();
				output.append(numbering.toString()+"."+(j+1)+ " "+diff.getOperation()+": "+fieldName+" as "+fieldValue+"\n");
			}
			
		}
		
		return output.toString();
	}
	
	public String render(Diff<?> diff) throws DiffException {
		
		String output=null;
		StringBuilder numbering = new StringBuilder("1");
		int i=0;
		
		if( diff.getOperation()=="Create" || diff.getOperation()=="Update")
		{
			output=outputDiff(diff);
			
		}
		else {
			output="1 "+diff.getOperation()+": "+diff.getObjectName();
		}
	
		
		return output;
	}

	public static void main(String[] args) {
		
		Person person = new Person();
		person.setFirstName("Isaac");
		person.setSurname("Leboea");
		
		Pet pet = new Pet();
	    pet.setName("Leo");
	    pet.setType("Dog");
		person.setPet(pet); 
		Person friend = new Person();
		friend.setFirstName("Tumelo");
		friend.setSurname("Modise");
		person.setFriend(friend);
		
		
		Set<String> nickNames = new HashSet<String>();
		nickNames.add("Daddy");
		nickNames.add("JayJay");
		nickNames.add("MyGuy");
		
		person.setNickNames(nickNames);
	    
	    
		DiffEngineImpl diffEngineImpl = new DiffEngineImpl();
		Testing test = new Testing();
		
		try {
			//Create
			System.out.println("***************Create Operation*****************");
			Diff<Person> newPerson = diffEngineImpl.calculate(null, person);
			System.out.println(test.render(newPerson));
			//delete
			System.out.println("***************Delete Operation*****************");
			Diff<Person> deletePerson = diffEngineImpl.calculate(person, null);
			System.out.println(test.render(deletePerson));
			System.out.println("***************Update Operation*****************");
			Person person2 = new Person();
			person2.setFirstName("Mpholo");
			person2.setSurname("Segoe");
			friend.setFirstName("Mcdonald");
			person2.setFriend(friend);
			person2.setPet(pet);
			Diff<Person> updatePerson = diffEngineImpl.calculate(person, person2);
			System.out.println(test.render(updatePerson));			

			
			
		} catch (DiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
