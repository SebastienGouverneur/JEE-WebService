package web;

import bean.Person;

public class SignUpError {

	private String messageError;
	private Person person;
	
	public SignUpError(String message, Person p){
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
