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


@Service
public class PersonDaoBd implements IPersonDao {
	
	@SuppressWarnings("unused")
	private String driverName = Resources.getString("KeyDriverName");
	private String url		  = Resources.getString("KeyUrl");
	private String user       = Resources.getString("KeyUser"); 
	private String password   = Resources.getString("KeyPassword");
	private Connection connection;
	
	@Autowired
	private IPersonFactory personFactory;
	
	@Autowired
	private IGroupFactory groupFactory;

	
	
	public PersonDaoBd() throws SQLException{
		connection = DriverManager.getConnection(url, user, password);
	}
	
	public List<Group> findAllGroups() throws SQLException{
		return findAllGroups(Resources.getString("KeyGroup"),
				Resources.getString("KeyBelong"), 
				Resources.getString("KeyPerson"));
	}
	
	public List<Group> findAllGroups(boolean test) throws SQLException {
		return findAllGroups(Resources.getString("KeyGroupTest"), 
				Resources.getString("KeyBelongTest"), 
				Resources.getString("KeyPersonTest"));
	}

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

	public List<Person> findAllPersons() throws SQLException{
		return findAllPersons(Resources.getString("KeyPerson"), 
							  Resources.getString("KeyBelong"),
							  Resources.getString("KeyGroup"));
	}
	
	public List<Person> findAllPersons(boolean test) throws SQLException {
		return findAllPersons(Resources.getString("KeyPersonTest"),
							  Resources.getString("KeyBelongTest"),
							  Resources.getString("KeyGroupTest"));
	}
	
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

	public Person findPerson(int id) throws SQLException, NotFoundPersonException {
		return findPerson(id, Resources.getString("KeyPerson"),
				  Resources.getString("KeyBelong"),
				  Resources.getString("KeyGroup"));
	}
	
	public Person findPerson(int id, boolean test) throws SQLException, NotFoundPersonException{
		return findPerson(id, Resources.getString("KeyPersonTest"),
							  Resources.getString("KeyBelongTest"),
							  Resources.getString("KeyGroupTest"));
	}
	
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
	
	public void savePerson(Person person) throws SQLException, ParseException {
		savePerson(person, Resources.getString("KeyPerson"),
						   Resources.getString("keyBelong"),
						   Resources.getString("keyGroup"),
						   false);
		
	}
	
	public void savePerson(Person person, boolean test) throws SQLException, ParseException{
		savePerson(person, Resources.getString("KeyPersonTest"),
						   Resources.getString("KeyBelongTest"),
						   Resources.getString("KeyGroupTest"),
						   true
						   );
	}
	
	private String formatDateForDao(String date){
		String[] sdate = date.split("/");
		return sdate[2]+"-"+sdate[1]+"-"+sdate[0];
	}

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
			
		}
		else {
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
		}
		else {
			st = connection.prepareStatement("DELETE FROM "+tableNameBelong+" WHERE `id-personne` = ?");
			st.setInt(1, person.getId());
			st.execute();
		}
		
	}
	
	public void saveGroup(Group group) throws SQLException {
		saveGroup(group, Resources.getString("KeyGroup"), Resources.getString("KeyBelong"));
		
	}
	
	public void saveGroup(Group group, boolean test) throws SQLException{
		saveGroup(group, Resources.getString("KeyGroupTest"), Resources.getString("KeyBelongTest"));
	}
	
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
	
	public Connection getConnection(){
		return connection;
	}

}
