package bean;

public class Person {
	
	private int id;
	private String nom;
	private String prenom;
	private String email;
	private String siteweb;
	private String dateNaissance;
	private Group groupe;
	
	
	public Person(){
		
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getPrenom() {
		return prenom;
	}
	
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getSiteweb() {
		return siteweb;
	}
	
	public void setSiteweb(String siteweb) {
		this.siteweb = siteweb;
	}
	
	public String getDateNaissance() {
		return dateNaissance;
	}
	
	public void setDateNaissance(String dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
	
	public Group getGroupe() {
		return groupe;
	}
	public void setGroupe(Group groupe) {
		this.groupe = groupe;
	}
	
	
	

}
