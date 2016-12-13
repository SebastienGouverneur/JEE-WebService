package web;

import bean.Person;

public class PersonInfoException extends Exception {

	private String messageError;
	private Person person;
	
	public PersonInfoException(String message, Person p){
		this.messageError = message;
		this.person = p;
	}
	
	public String getMessageError(){
		return this.messageError;
	}
	
	public Person getPerson(){
		return this.person;
	}
	
}
