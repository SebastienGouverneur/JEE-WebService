package src.main.webapp;

import java.util.List;


public class GroupBean {
	
	private int id;
	private String monGroupe;
	private List<PersonBean> listPerson;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMonGroupe() {
		return monGroupe;
	}
	public void setMonGroupe(String monGroupe) {
		this.monGroupe = monGroupe;
	}
	public List<PersonBean> getListPerson() {
		return listPerson;
	}
	public void setListPerson(List<PersonBean> listPerson) {
		this.listPerson = listPerson;
	}

}
