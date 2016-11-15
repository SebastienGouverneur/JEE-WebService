package dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
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
				p.setGroupe(g);
				g.getListPerson().add(p);
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
			personList.add(p);
			
			if (rs.getObject("groupe.id-groupe") != null){
				if (!mapGroup.containsKey(rs.getInt("groupe.id-groupe"))){
					Group g = groupFactory.getGroup();
					g.setId(rs.getInt("groupe.id-groupe"));
					g.setNomGroupe(rs.getString("groupe.nom-groupe"));
					mapGroup.put(g.getId(), g);	
				}
				
				p.setGroupe(mapGroup.get(rs.getInt("groupe.id-groupe")));
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
		if (rs.getObject("groupe.id-groupe") != null){
			Group g = groupFactory.getGroup();
			g.setId(rs.getInt("groupe.id-groupe"));
			g.setNomGroupe(rs.getString("groupe.nom-groupe"));
			p.setGroupe(g);
		}
			return p;
		
		
	}

	public void savePerson(Person person) {
		// TODO Auto-generated method stub
		
	}

	public void saveGroup(Group group) {
		// TODO Auto-generated method stub
		
	}
	
	public Connection getConnection(){
		return connection;
	}

	
}
