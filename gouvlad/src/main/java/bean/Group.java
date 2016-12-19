package bean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A group class that represents a group.
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 */

public class Group {
	
	private int id;
	private String nomGroupe;
	private List<Person> listPerson = new LinkedList<Person>();
	private static final HashMap<Integer, Group> groupSet = new HashMap<Integer, Group>();
	
	public Group(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNomGroupe() {
		return nomGroupe;
	}
	public void setNomGroupe(String monGroupe) {
		this.nomGroupe = monGroupe;
	}
	public List<Person> getListPerson() {
		return listPerson;
	}
	public void setListPerson(List<Person> listPerson) {
		this.listPerson = listPerson;
	}
	
	public HashMap<Integer, Group> getGroups(){
		return groupSet;
	}

	protected void removeFromPersonList(Person person) {
		this.listPerson.remove(person);
	}

	protected void addToPersonList(Person person) {
		this.listPerson.add(person);
		
	}
	
	public void addPerson(Person person){
		person.addToGroup(this);
	}
}
