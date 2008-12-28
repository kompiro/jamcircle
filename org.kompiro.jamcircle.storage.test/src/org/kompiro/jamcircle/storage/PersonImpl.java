package org.kompiro.jamcircle.storage;
public class PersonImpl{
	
	private Person person;

	public PersonImpl(Person person){
		this.person = person;
	}
	
	public String getName(){
		String name = person.getName();
		System.out.println("getName:" + name);
		return name;
	}
	
	public void setName(String name){
		System.out.println("setName:" + name);
		person.setName(name);
	}
	
}

