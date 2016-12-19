package bean;

/**
 * A person class that represents a person.
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 */


public class Person {
	
	private int id;
	private String nom;
	private String prenom;
	private String email;
	private String siteweb;
	private String dateNaissance;
	private String motDePasseHash;
	private String salt;
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
	
	public String getMotDePasseHash() {
		return motDePasseHash;
	}
	
	public void setMotDePasseHash(String motDePasseHash) {
		this.motDePasseHash = motDePasseHash;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public Group getGroupe() {
		return groupe;
	}
	
	public void addToGroup(Group groupe) {
		if (this.groupe == groupe)
			return;
		
		if (this.groupe != null){
			this.groupe.removeFromPersonList(this);
		}
		
		if (groupe != null)
			groupe.addToPersonList(this);
		
		this.groupe = groupe;
	}
	
	public void removeFromGroup(){
		addToGroup(null);
	}
	
	

}
