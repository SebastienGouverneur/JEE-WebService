package bean;

import java.util.List;


public class Group {
	
	private int id;
	private String nomGroupe;
	private List<Person> listPerson;
	
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

}
