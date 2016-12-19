package dao.impl;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bean.Group;
import bean.IGroupFactory;
import bean.IPersonFactory;
import bean.Person;
import dao.IPersonDao;
import dao.impl.Resources;




/**
 * This class is an implementation of the IPersonDao interface.
 * It is used to question the database in order to retrieve or store business informations.
 * 
 * @author Gabriel Ladet
 * @author Sébastien Gouverneur
 *
 */
@Service
public class PersonDaoBd implements IPersonDao {
	
	private String driverName = Resources.getString("KeyDriverName");
	private String url		  = Resources.getString("KeyUrl");
	private String user       = Resources.getString("KeyUser"); 
	private String password   = Resources.getString("KeyPassword");
	private Connection connection;
	
	@Autowired
	private IPersonFactory personFactory;
	
	@Autowired
	private IGroupFactory groupFactory;
	
	/**
	 * Constructor of the PersonDaoBd class
	 * 
	 * @throws SQLException if the connection failed
	 * @throws ClassNotFoundException 
	 */
	public PersonDaoBd() throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and find all the groups in there
	 * 
	 * @return the main method findAllGroups(Groupe, AppartenancePersonneGroupe, Personne)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Group> findAllGroups() throws SQLException{
		return findAllGroups(Resources.getString("KeyGroup"),
				Resources.getString("KeyBelong"), 
				Resources.getString("KeyPerson"));
	}
	
	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and find all the groups in there
	 * 
	 * @param test
	 * @return the main method findAllGroups(GroupeTest, AppartenancePersonneGroupeTest, PersonneTest)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Group> findAllGroups(boolean test) throws SQLException {
		return findAllGroups(Resources.getString("KeyGroupTest"), 
				Resources.getString("KeyBelongTest"), 
				Resources.getString("KeyPersonTest"));
	}

	/**
	 * Returns a list containing all information about all groups stored in database sorted in ascending order of identifiers. 
	 * The instantiated objects must therefore contain the id, the name of the group but also the list of the persons that belong to the group. 
	 * The list of people is sorted in ascending order of identifiers. 
	 * If no group is registered, the method returns an empty list. 
	 * If a group contains no people, the attribute corresponding to the list of people is an empty list
	 * 
	 * @param tableNameGroup
	 * @param tableNameBelongGroupPerson
	 * @param tableNamePerson
	 * @return a List<Group> groupList of all the groups found by the query
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Group> findAllGroups(String tableNameGroup, 
									 String tableNameBelongGroupPerson, 
									 String tableNamePerson) throws SQLException {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<Group> groupList = new LinkedList<Group>();
		HashMap<Integer, Group> mapGroup = new HashMap<Integer, Group>();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM "+tableNameGroup+" AS groupe LEFT OUTER JOIN "+tableNameBelongGroupPerson+" AS belong"
						+ " ON groupe.`id-groupe`=belong.`id-groupe` LEFT OUTER JOIN "+tableNamePerson+" AS person"
						+ " ON belong.`id-personne`=person.`id-personne` ORDER BY groupe.`id-groupe`, person.`id-personne`");
		while (rs.next()){
			Group g;
			if (mapGroup.containsKey(rs.getInt("groupe.id-groupe"))){
				g = mapGroup.get(rs.getInt("groupe.id-groupe"));
			}
			else {
				g = groupFactory.getGroup();
				g.setId(rs.getInt("groupe.id-groupe"));
				g.setNomGroupe(rs.getString("groupe.nom-groupe"));
				mapGroup.put(g.getId(), g);
				groupList.add(g);
			}
			if (rs.getObject("person.id-personne") != null){
				Person p = personFactory.getPerson();
				p.setId(rs.getInt("person.id-personne"));
				p.setNom(rs.getString("person.nom"));
				p.setPrenom(rs.getString("person.prenom"));
				p.setEmail(rs.getString("person.email"));
				p.setSiteweb(rs.getString("person.url-web"));
				p.setDateNaissance(df.format(rs.getDate("person.date-naissance")));
				p.setMotDePasseHash(rs.getString("person.mot-de-passe-hash"));
				p.setSalt(rs.getString("person.salt"));
				p.addToGroup(g);
			}
		}
		return groupList;
	}
	
	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and find all the persons in there
	 * 
	 * @return the main method findAllPersons(Personne, AppartenancePersonneGroupe, Groupe)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Person> findAllPersons() throws SQLException{
		return findAllPersons(Resources.getString("KeyPerson"), 
							  Resources.getString("KeyBelong"),
							  Resources.getString("KeyGroup"));
	}
	
	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and find all the persons in there
	 * 
	 * @param test
	 * @return the main method findAllPersons(PersonneTest, AppartenancePersonneGroupeTest, GroupeTest)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Person> findAllPersons(boolean test) throws SQLException {
		return findAllPersons(Resources.getString("KeyPersonTest"),
							  Resources.getString("KeyBelongTest"),
							  Resources.getString("KeyGroupTest"));
	}
	
	/**
	 * Returns a list of the persons found in the chosen tables
	 * 
	 * @param tableNamePerson
	 * @param tableNameBelongGroupPerson
	 * @param tableNameGroup
	 * @return a List<Person> of all the persons found by the query
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Person> findAllPersons(String tableNamePerson, 
									   String tableNameBelongGroupPerson,
									   String tableNameGroup) throws SQLException {
		HashMap<Integer, Group> mapGroup = new HashMap<Integer, Group>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<Person> personList = new LinkedList<Person>();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM "+tableNamePerson+" AS person LEFT OUTER JOIN " +
				tableNameBelongGroupPerson+" AS belong ON person.`id-personne` = belong.`id-personne`" +
						" LEFT OUTER JOIN "+tableNameGroup+" AS groupe ON belong.`id-groupe` = groupe.`id-groupe` " +
								"ORDER BY person.`id-personne`");
		while (rs.next()){
			Person p = personFactory.getPerson();
			p.setId(rs.getInt("person.id-personne"));
			p.setNom(rs.getString("person.nom"));
			p.setPrenom(rs.getString("person.prenom"));
			p.setEmail(rs.getString("person.email"));
			p.setSiteweb(rs.getString("person.url-web"));
			p.setDateNaissance(df.format(rs.getDate("person.date-naissance")));
			p.setMotDePasseHash(rs.getString("person.mot-de-passe-hash"));
			p.setSalt(rs.getString("person.salt"));
			personList.add(p);
			
			if (rs.getObject("groupe.id-groupe") != null){
				if (!mapGroup.containsKey(rs.getInt("groupe.id-groupe"))){
					Group g = groupFactory.getGroup();
					g.setId(rs.getInt("groupe.id-groupe"));
					g.setNomGroupe(rs.getString("groupe.nom-groupe"));
					mapGroup.put(g.getId(), g);	
				}
				p.addToGroup(mapGroup.get(rs.getInt("groupe.id-groupe")));
			}
		}
		return personList;
	}
	
	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and find a specific person in there
	 * 
	 * @param id
	 * @return the main method findPerson(Personne, AppartenancePersonneGroupe, Groupe)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public Person findPerson(int id) throws SQLException, NotFoundPersonException {
		return findPerson(id, Resources.getString("KeyPerson"),
				  Resources.getString("KeyBelong"),
				  Resources.getString("KeyGroup"));
	}
	
	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and find a specific person in there
	 * 
	 * @param id
	 * @param test
	 * @return the main method findPerson(PersonneTest, AppartenancePersonneGroupeTest, GroupeTest)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public Person findPerson(int id, boolean test) throws SQLException, NotFoundPersonException{
		return findPerson(id, Resources.getString("KeyPersonTest"),
							  Resources.getString("KeyBelongTest"),
							  Resources.getString("KeyGroupTest"));
	}
	
	/**
	 * Returns a "Person" object containing all the basic information about the person
	 * whose identifier is specified as a parameter.
	 * If the person is associated with a group, a "Group" object corresponding to the group must be instantiated by being
	 * consistent with the information present in the database.
	 * If the person does not belong to any group, the attribute of the corresponding bean is null.
	 * 
	 * @param id
	 * @param tableNamePerson
	 * @param tableNameBelongGroupPerson
	 * @param tableNameGroup
	 * @return an object Person p
	 * @throws SQLException if an error occurs while polling the database
	 * @throws NotFoundPersonException if person does not exist while polling the database
	 */
	public Person findPerson(int id, 
							 String tableNamePerson, 
							 String tableNameBelongGroupPerson,
							 String tableNameGroup) throws SQLException, NotFoundPersonException{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		PreparedStatement st = connection.prepareStatement("SELECT * FROM "+tableNamePerson+" " +
				"AS person LEFT OUTER JOIN "+ tableNameBelongGroupPerson+" AS belong ON " +
						"person.`id-personne` = belong.`id-personne` " +
						"LEFT OUTER JOIN "+tableNameGroup+" AS groupe ON belong.`id-groupe`=groupe.`id-groupe` " +
								"WHERE person.`id-personne` = ?");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		if (!rs.next())
			throw new NotFoundPersonException();
		Person p = personFactory.getPerson();
		p.setId(rs.getInt("person.id-personne"));
		p.setNom(rs.getString("person.nom"));
		p.setPrenom(rs.getString("person.prenom"));
		p.setEmail(rs.getString("person.email"));
		p.setSiteweb(rs.getString("person.url-web"));
		p.setDateNaissance(df.format(rs.getDate("person.date-naissance")));
		p.setMotDePasseHash(rs.getString("person.mot-de-passe-hash"));
		p.setSalt(rs.getString("person.salt"));
		if (rs.getObject("groupe.id-groupe") != null){
			Group g = groupFactory.getGroup();
			g.setId(rs.getInt("groupe.id-groupe"));
			g.setNomGroupe(rs.getString("groupe.nom-groupe"));
			p.addToGroup(g);
		}
			return p;
	}
	
	
	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and find a specific person in there
	 * 
	 * @param emailAddress
	 * @return the main method findPerson(Personne, AppartenancePersonneGroupe, Groupe)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public Person findPerson(String emailAddress) throws SQLException, NotFoundPersonException {
		return findPerson(emailAddress, Resources.getString("KeyPerson"),
				  Resources.getString("KeyBelong"),
				  Resources.getString("KeyGroup"));
	}
	
	
	
	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and find a specific person in there
	 * 
	 * @param emailAddress
	 * @param test
	 * @return the main method findPerson(PersonneTest, AppartenancePersonneGroupeTest, GroupeTest)
	 * @throws SQLException if an error occurs while polling the database
	 */
	public Person findPerson(String emailAddress, boolean test) throws SQLException, NotFoundPersonException{
		return findPerson(emailAddress, Resources.getString("KeyPersonTest"),
						  Resources.getString("KeyBelongTest"),
						  Resources.getString("KeyGroupTest"));
	}
	
	
	/**
	 * Returns a "Person" object containing all the basic information about the person
	 * whose identifier is specified as a parameter.
	 * If the person is associated with a group, a "Group" object corresponding to the group must be instantiated by being
	 * consistent with the information present in the database.
	 * If the person does not belong to any group, the attribute of the corresponding bean is null.
	 * 
	 * @param emailAddress
	 * @param tableNamePerson
	 * @param tableNameBelongGroupPerson
	 * @param tableNameGroup
	 * @return an object Person p
	 * @throws SQLException if an error occurs while polling the database
	 * @throws NotFoundPersonException if person does not exist while polling the database
	 */
	public Person findPerson(String emailAddress,
							 String tableNamePerson, 
							 String tableNameBelongGroupPerson,
							 String tableNameGroup) throws SQLException, NotFoundPersonException{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		PreparedStatement st = connection.prepareStatement("SELECT * FROM "+tableNamePerson+" " +
				"AS person LEFT OUTER JOIN "+ tableNameBelongGroupPerson+" AS belong ON " +
				"person.`id-personne` = belong.`id-personne` " +
				"LEFT OUTER JOIN "+tableNameGroup+" AS groupe ON belong.`id-groupe`=groupe.`id-groupe` " +
				"WHERE person.`email` = ?");
		st.setString(1, emailAddress);
		ResultSet rs = st.executeQuery();
		if (!rs.next())
			throw new NotFoundPersonException();
		Person p = personFactory.getPerson();
		p.setId(rs.getInt("person.id-personne"));
		p.setNom(rs.getString("person.nom"));
		p.setPrenom(rs.getString("person.prenom"));
		p.setEmail(rs.getString("person.email"));
		p.setSiteweb(rs.getString("person.url-web"));
		p.setDateNaissance(df.format(rs.getDate("person.date-naissance")));
		p.setMotDePasseHash(rs.getString("person.mot-de-passe-hash"));
		p.setSalt(rs.getString("person.salt"));
		if (rs.getObject("groupe.id-groupe") != null){
			Group g = groupFactory.getGroup();
			g.setId(rs.getInt("groupe.id-groupe"));
			g.setNomGroupe(rs.getString("groupe.nom-groupe"));
			p.addToGroup(g);
		}
		return p;
	}
	
	
	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and save a specific person in there
	 * 
	 * @param person
	 * @throws SQLException if an error occurs while polling the database
	 */
	public void savePerson(Person person) throws SQLException, ParseException {
		savePerson(person, Resources.getString("KeyPerson"),
						   Resources.getString("KeyBelong"),
						   Resources.getString("KeyGroup"),
						   false);
		
	}
	
	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and save a specific person in there
	 * 
	 * @param person
	 * @throws SQLException if an error occurs while polling the database
	 */
	public void savePerson(Person person, boolean test) throws SQLException, ParseException{
		savePerson(person, Resources.getString("KeyPersonTest"),
						   Resources.getString("KeyBelongTest"),
						   Resources.getString("KeyGroupTest"),
						   true
						   );
	}
	
	/**
	 * Returns a String representing a date changing its format by parsing it.
	 * For example, the date "28/09/1991" become "1991-09-28"
	 * 
	 * @param date
	 * @return a String[] of the date passed in parameter
	 */
	private String formatDateForDao(String date){
		String[] sdate = date.split("/");
		return sdate[2]+"-"+sdate[1]+"-"+sdate[0];
	}
	
	/**
	 * Saves all information about the "Person" object specified in the parameter. 
	 * If the identifier of the person is less than or equal to zero, the method inserts a new entry in the "Person" table 
	 * by assigning an identifier according to the rule of the auto-increment and then assigns this value to the object in memory.
	 * If the identifier of the person is greater than zero and no person associated with this identifier exists in the database, 
	 * a new entry in the table is created. 
	 * If the identifier of the person is greater than zero and there is a person with this identifier in the database, 
	 * the information relating to that entry is updated. In addition, if the person is associated with a group, 
	 * the group information is updated in the database.
	 * 
	 * @param person
	 * @param tableNamePerson
	 * @param tableNameBelong
	 * @param tableNameGroup
	 * @param test
	 * @throws SQLException if an error occurs while polling the database
	 * @throws ParseException if an error occurs while using the formatDateForDao function to parse the format date
	 */
	public void savePerson(Person person,
							String tableNamePerson,
							String tableNameBelong,
							String tableNameGroup,
							boolean test) throws SQLException, ParseException {
		PreparedStatement st;
		if (person.getId() <= 0) {
			/* Absolutely sure we're managing a new person. Insert it in db. */
			st = connection.prepareStatement("INSERT INTO "+tableNamePerson+" VALUES(NULL, ?, ?, ?, ?, ?, ?, ?)");
			st.setString(1, person.getNom());
			st.setString(2, person.getPrenom());
			st.setString(3, person.getEmail());
			st.setString(4, person.getSiteweb());
			st.setString(5, formatDateForDao(person.getDateNaissance()));
			st.setString(6, person.getMotDePasseHash());
			st.setString(7, person.getSalt());
			st.execute();
			Statement st2 = connection.createStatement();
			ResultSet rs = st2.executeQuery("SELECT `id-personne` FROM "+tableNamePerson+" ORDER BY `id-personne` DESC LIMIT 0,1");
			rs.next();
			person.setId(rs.getInt(1));
		} else {
			st = connection.prepareStatement("REPLACE INTO "+tableNamePerson+" VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
			st.setInt(1, person.getId());
			st.setString(2, person.getNom());
			st.setString(3, person.getPrenom());
			st.setString(4, person.getEmail());
			st.setString(5, person.getSiteweb());
			st.setString(6, formatDateForDao(person.getDateNaissance()));
			st.setString(7, person.getMotDePasseHash());
			st.setString(8, person.getSalt());
			st.execute();
		}
		if (person.getGroupe() != null){
			st = connection.prepareStatement("REPLACE INTO "+tableNameBelong+" VALUES(?, ?)");
			st.setInt(1, person.getId());
			st.setInt(2, person.getGroupe().getId());
			st.executeUpdate();
			if (test)
				saveGroup(person.getGroupe(), true);
			else
				saveGroup(person.getGroupe());
		} else {
			st = connection.prepareStatement("DELETE FROM "+tableNameBelong+" WHERE `id-personne` = ?");
			st.setInt(1, person.getId());
			st.execute();
			
		}
	}
	
	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and find all the persons in there
	 * 
	 * @param group
	 * @throws SQLException if an error occurs while polling the database
	 */
	public void saveGroup(Group group) throws SQLException {
		saveGroup(group, Resources.getString("KeyGroup"), Resources.getString("KeyBelong"));
		
	}
	
	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and find all the persons in there
	 * 
	 * @param group
	 * @param test
	 * @throws SQLException if an error occurs while polling the database
	 */
	public void saveGroup(Group group, boolean test) throws SQLException{
		saveGroup(group, Resources.getString("KeyGroupTest"), Resources.getString("KeyBelongTest"));
	}
	
	/**
	 * Saves all information about the "Group" object specified in the parameter.
	 * If the identifier of is less than or equal to zero, the method inserts a new entry in the "Group" table
	 * by assigning an identifier according to the rule of the auto-increment and then assigns this value to the object in memory. 
	 * If the group identifier is greater than zero and no group associated with that identifier exists in the database,
	 * a new entry in the table is created. 
	 * If the group identifier is greater than zero and there is a group with this identifier in the database,
	 * the information relating to that entry is updated. In addition, information about person / group memberships is also updated
	 * in the table in the "AppartenancePersonneGroupe" table.
	 * 
	 * @param group
	 * @param tableNameGroup
	 * @param tableNameBelong
	 * @throws SQLException if an error occurs while polling the database
	 */
	public void saveGroup(Group group, 
						  String tableNameGroup,
						  String tableNameBelong) throws SQLException{
		PreparedStatement st;
		Statement st2;
		if (group.getId() <= 0){
			/* Absolutely sure we're managing a new group. Insert it in db. */
			st = connection.prepareStatement("INSERT INTO "+tableNameGroup+" VALUES(NULL, ?)");
			st.setString(1, group.getNomGroupe());
			st.execute();
			st2 = connection.createStatement();
			ResultSet rs = st2.executeQuery("SELECT `id-groupe` FROM "+tableNameGroup+" ORDER BY `id-groupe` DESC LIMIT 0,1");
			rs.next();
			group.setId(rs.getInt(1));
			
		} else {
			st = connection.prepareStatement("REPLACE INTO "+tableNameGroup+" VALUES(?, ?)");
			st.setInt(1, group.getId());
			st.setString(2, group.getNomGroupe());
			st.execute();
		}
		/* Updates persons in the group */
		for (Person p: group.getListPerson()){
			st = connection.prepareStatement("REPLACE INTO "+tableNameBelong+" VALUES(?, ?)");
			st.setInt(1, p.getId());
			st.setInt(2, group.getId());
			st.executeUpdate();
		}
		/* Removes persons who don't belong anymore to the group */
		String builtPersonListForSQLRequest = buildListForSQL(group.getListPerson());
		st = connection.prepareStatement("DELETE FROM "+tableNameBelong+" WHERE `id-groupe`=? "+builtPersonListForSQLRequest);
		st.setInt(1, group.getId());
		st.execute();
	}
	
	/**
	 * Return a String that complete the saveGroup function if a person don't belong anymore to the group
	 * 
	 * @param list
	 * @return String res
	 */
	private String buildListForSQL(List<Person> list){
		if (list.size() == 0)
			return "";
		String res = "AND `id-personne` NOT IN (";
		for (Person p: list){
			res += p.getId()+",";
		}
		res = res.substring(0, res.length()-1); /* Removes last comma */
		return res += ")";
	}
	
	/**
	 * Return the current connection of the dao
	 * 
	 * @return connection
	 */
	public Connection getConnection(){
		return connection;
	}

	/**
	 * Overloaded method used to catch the data in the production tables 
	 * and search persons in there.
	 * 
	 * @param searchText
	 * @return the main method result searchPersons
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Person> searchPersons(String searchText) throws SQLException {
		return searchPersons(searchText, Resources.getString("KeyPerson"), Resources.getString("KeyBelong"), Resources.getString("KeyGroup"));
	}

	/**
	 * Overloaded method used to catch the data in the test tables 
	 * and search persons in there.
	 * 
	 * @param searchText
	 * @param test
	 * @return the main method result searchPersons
	 * @throws SQLException if an error occurs while polling the database
	 */
	public List<Person> searchPersons(String searchText, boolean test) throws SQLException {
		return searchPersons(searchText, Resources.getString("KeyPersonTest"), Resources.getString("KeyBelongTest"), Resources.getString("KeyGroupTest"));
	}
	
	/**
	 * Searches all persons who correspond to the specified searched text. The search is carried out
	 * on the following fields: lastname, firstname, email address, website url. It is not case sensitive.
	 * If a search returns no result, then an empty list is returned by this method.
	 * 
	 * @param searchText
	 * @param tableNamePerson
	 * @param tableNameGroup
	 * @param tableNameBelong
	 * @throws SQLException if an error occurs while polling the database
	 * @return a list of persons that correspond to the specified search terms.
	 */
	public List<Person> searchPersons(String searchText,
							String tableNamePerson,
							String tableNameBelong,
							String tableNameGroup) throws SQLException{
		List<Person> personList = new LinkedList<Person>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		PreparedStatement st = connection.prepareStatement("SELECT * FROM "+tableNamePerson+" " +
				"AS person LEFT OUTER JOIN "+ tableNameBelong+" AS belong ON " +
						"person.`id-personne` = belong.`id-personne` " +
						"LEFT OUTER JOIN "+tableNameGroup+" AS groupe ON belong.`id-groupe`=groupe.`id-groupe` " +
								"WHERE person.`nom` LIKE ? OR person.`prenom` LIKE ? OR person.`email` LIKE ? OR person.`url-web` LIKE ? "
								+ "OR groupe.`nom-groupe` LIKE ? ORDER BY person.`id-personne`");
		st.setString(1, '%'+searchText+'%');
		st.setString(2, '%'+searchText+'%');
		st.setString(3, '%'+searchText+'%');
		st.setString(4, '%'+searchText+'%');
		st.setString(5, '%'+searchText+'%');

		ResultSet rs = st.executeQuery();
		while (rs.next()){
			Person p = personFactory.getPerson();
			p.setId(rs.getInt("person.id-personne"));
			p.setNom(rs.getString("person.nom"));
			p.setPrenom(rs.getString("person.prenom"));
			p.setEmail(rs.getString("person.email"));
			p.setSiteweb(rs.getString("person.url-web"));
			p.setDateNaissance(df.format(rs.getDate("person.date-naissance")));
			p.setMotDePasseHash(rs.getString("person.mot-de-passe-hash"));
			p.setSalt(rs.getString("person.salt"));
			if (rs.getObject("groupe.id-groupe") != null){
				Group g = groupFactory.getGroup();
				g.setId(rs.getInt("groupe.id-groupe"));
				g.setNomGroupe(rs.getString("groupe.nom-groupe"));
				p.addToGroup(g);
			}
			personList.add(p);

		
		
		}
		return personList;

	}

	
}