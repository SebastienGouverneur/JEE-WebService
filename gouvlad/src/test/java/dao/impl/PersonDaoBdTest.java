package dao.impl;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import bean.Group;
import bean.IGroupFactory;
import bean.IPersonFactory;
import bean.Person;
import dao.IPersonDao;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
public class PersonDaoBdTest {
	
	@Autowired
	IPersonDao dao;
	
	
	@Autowired
	IPersonFactory personFactory;
	
	@Autowired
	IGroupFactory groupFactory;
	
	@Before
	public void clearTestTables() throws SQLException{
		clearTable(Resources.getString("KeyBelongTest"));
		clearTable(Resources.getString("KeyPersonTest"));
		clearTable(Resources.getString("KeyGroupTest"));
	}
	
	private Person getMockPersonGabriel(int id, Group group){
		Person p = personFactory.getPerson();
		p.setId(id);
		p.setNom("Ladet"); 
		p.setPrenom("Gabriel"); 
		p.setEmail("gabriel@ladet.net");
		p.setDateNaissance("22/02/1993"); 
		p.setSiteweb("http://ladet.net"); 
		p.setMotDePasseHash("0000000000000000000000000000000000000000000000000000000000000000");
		p.setSalt("12345678");
		p.addToGroup(group);
		return p;

	}
	
	private Person getMockPersonSebastien(int id, Group group){
		Person p = personFactory.getPerson();
		p.setId(id);
		p.setNom("Gouverneur"); 
		p.setPrenom("Sébastien"); 
		p.setEmail("seb@gouv.com"); 
		p.setDateNaissance("28/09/1991"); 
		p.setSiteweb("http://sebgouv.com"); 
		p.setMotDePasseHash("0000000000000000000000000000000000000000000000000000000000000000");
		p.setSalt("12345678");
		p.addToGroup(group);
		return p;
	}
	
	private Person getMockPersonJames(int id, Group group){
		Person p = personFactory.getPerson();
		p.setId(id);
		p.setNom("Bond"); 
		p.setPrenom("James"); 
		p.setEmail("jamesbond@mi6.com");
		p.setDateNaissance("11/09/1920"); 
		p.setSiteweb("http://bond.com");
		p.setMotDePasseHash("0000000000000000000000000000000000000000000000000000000000000000");
		p.setSalt("12345678");
		p.addToGroup(group);
		return p;
	}
	
	private void insertPerson(Person person) throws SQLException, ParseException{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		PreparedStatement st = dao.getConnection().prepareStatement("INSERT INTO "+Resources.getString("KeyPersonTest")+" VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
		st.setInt(1, person.getId());
		st.setString(2, person.getNom());
		st.setString(3, person.getPrenom());
		st.setString(4, person.getEmail());
		st.setString(5,  person.getSiteweb());
		st.setDate(6, new java.sql.Date(df.parse(person.getDateNaissance()).getTime()));
		st.setString(7, person.getMotDePasseHash()); 
		st.setString(8, person.getSalt()); 
		st.execute();
		
	}
	
	private void insertGroup(Group group) throws SQLException, ParseException{
		PreparedStatement st = dao.getConnection().prepareStatement("INSERT INTO "+Resources.getString("KeyGroupTest")+" VALUES(?, ?)");
		st.setInt(1, group.getId());
		st.setString(2, group.getNomGroupe());
		st.execute();
		if (group.getListPerson() == null) {
			return;
		}
		for (Person p : group.getListPerson()) {
			PreparedStatement st1 = dao.getConnection().prepareStatement("INSERT INTO "+Resources.getString("KeyBelongTest")+" VALUES(?, ?)");
			st1.setInt(1, p.getId());
			st1.setInt(2, group.getId());
			st1.execute();
		}
	}
	
	private void clearTable(String tableName) throws SQLException{
		Statement st = dao.getConnection().createStatement();
		st.execute("DELETE FROM "+tableName); 
		st.execute("ALTER TABLE "+tableName+" AUTO_INCREMENT = 1");
	}
	
	@Test(timeout = 10000)
	public void testConnection() {
		Assert.assertNotNull(dao);
		Assert.assertNotNull(dao.getConnection());
	}
	
	@Test(timeout = 10000)
	public void testFindAllPersons2PersonsWithOneGroup() throws SQLException, ParseException{
		Group g1 = groupFactory.getGroup();
		g1.setId(2);
		g1.setNomGroupe("FSI");
		
		Person p1 = getMockPersonSebastien(1, null);
		Person p2 = getMockPersonGabriel(2, g1);

		insertPerson(p1);
		insertPerson(p2);
		insertGroup(g1);
		
		List<Person> listPerson = dao.findAllPersons(true);
		
		assertEquals(listPerson.size(), 2);
		
		assertEquals(listPerson.get(0).getId(), 1);
		assertEquals(listPerson.get(0).getNom(), "Gouverneur"); 
		assertEquals(listPerson.get(0).getPrenom(), "Sébastien");
		assertNull(listPerson.get(0).getGroupe());
		
		assertEquals(listPerson.get(1).getId(), 2);
		assertEquals(listPerson.get(1).getNom(), "Ladet"); 
		assertEquals(listPerson.get(1).getPrenom(), "Gabriel");
		assertEquals(listPerson.get(1).getGroupe().getId(), 2);
		assertEquals(listPerson.get(1).getGroupe().getNomGroupe(), "FSI");
	}
	
	@Test(timeout = 10000)
	public void testFindAllPersonsWithNoPersonInDb() throws SQLException{
		List<Person> listPerson = dao.findAllPersons(true);
		assertEquals(listPerson.size(), 0);
	}
	
	@Test(timeout = 10000)
	public void testFindAllGroups3groupsWith3Persons() throws SQLException, ParseException {
		
		Group g1 = groupFactory.getGroup();
		Group g2 = groupFactory.getGroup();
		Group g3 = groupFactory.getGroup();
		
		Person p1 = getMockPersonSebastien(1, g1); 
		Person p2 = getMockPersonGabriel(2, g2); 
		Person p3 = getMockPersonJames(3, g2); 
		
		g1.setId(1);
		g1.setNomGroupe("ISL");
		
		g2.setId(2);
		g2.setNomGroupe("FSI");
		
		g3.setId(3);
		g3.setNomGroupe("ID");
		
		
		insertPerson(p1);
		insertPerson(p2);
		insertPerson(p3);

		
		insertGroup(g1);
		insertGroup(g2);
		insertGroup(g3);
		
		List<Group> listGroup = dao.findAllGroups(true);
		
		assertEquals(3, listGroup.size());
		
		/* Test first group */
		
		g1 = ((LinkedList<Group>) listGroup).pop();
		assertEquals(1, g1.getId());
		assertEquals("ISL", g1.getNomGroupe());
		assertEquals(1, g1.getListPerson().size());
		assertEquals(1, g1.getListPerson().get(0).getId());
		assertEquals("Gouverneur", g1.getListPerson().get(0).getNom());
		assertEquals("Sébastien", g1.getListPerson().get(0).getPrenom());
		assertEquals("seb@gouv.com", g1.getListPerson().get(0).getEmail());
		assertEquals("http://sebgouv.com", g1.getListPerson().get(0).getSiteweb());
		assertEquals("28/09/1991",g1.getListPerson().get(0).getDateNaissance());
		assertEquals(g1, g1.getListPerson().get(0).getGroupe());


		/* Test second group */
		
		g1 = ((LinkedList<Group>) listGroup).pop();
		assertEquals(2, g1.getId());
		assertEquals("FSI", g1.getNomGroupe());
		assertEquals(2, g1.getListPerson().size());
		assertEquals(2, g1.getListPerson().get(0).getId());
		assertEquals("Ladet", g1.getListPerson().get(0).getNom());
		assertEquals("Gabriel", g1.getListPerson().get(0).getPrenom());
		assertEquals("gabriel@ladet.net", g1.getListPerson().get(0).getEmail());
		assertEquals("http://ladet.net", g1.getListPerson().get(0).getSiteweb());
		assertEquals("22/02/1993",g1.getListPerson().get(0).getDateNaissance());
		assertEquals(g1, g1.getListPerson().get(0).getGroupe());
		
		
		assertEquals(3, g1.getListPerson().get(1).getId());
		assertEquals("Bond", g1.getListPerson().get(1).getNom());
		assertEquals("James", g1.getListPerson().get(1).getPrenom());
		assertEquals("jamesbond@mi6.com", g1.getListPerson().get(1).getEmail());
		assertEquals("http://bond.com", g1.getListPerson().get(1).getSiteweb());
		assertEquals("11/09/1920",g1.getListPerson().get(1).getDateNaissance());
		assertEquals(g1, g1.getListPerson().get(1).getGroupe());
		
		/* Test third group */
		
		g1 = ((LinkedList<Group>) listGroup).pop();
		assertEquals(3, g1.getId());
		assertEquals("ID", g1.getNomGroupe());
		assertEquals(0, g1.getListPerson().size());
		
		
	}
	
	@Test(timeout = 10000)
	public void testFindAllGroupsWithNoGroupInDb() throws SQLException {
		
		List<Group> listGroup = dao.findAllGroups(true);
		assertEquals(listGroup.size(), 0);
	}
	
	@Test(timeout = 10000)
	public void testFindPersonById() throws SQLException, ParseException, NotFoundPersonException{
		Group g1 = groupFactory.getGroup();
		g1.setId(2);
		g1.setNomGroupe("FSI");
		
		Person p1 = getMockPersonSebastien(1, null);
		Person p2 = getMockPersonGabriel(2, g1);

		insertPerson(p1);
		insertPerson(p2);
		insertGroup(g1);
		
		Person p3 = dao.findPerson(1, true);
		Person p4 = dao.findPerson(2, true);
		
		assertEquals(p3.getId(), 1);
		assertEquals(p3.getNom(), "Gouverneur"); 
		assertEquals(p3.getPrenom(), "Sébastien");
		assertNull(p3.getGroupe());
		
		assertEquals(p4.getId(), 2);
		assertEquals(p4.getNom(), "Ladet"); 
		assertEquals(p4.getPrenom(), "Gabriel");
		assertEquals(p4.getGroupe().getId(), 2);
		assertEquals(p4.getGroupe().getNomGroupe(), "FSI");
		
	}
	
	@Test(timeout = 10000, expected=NotFoundPersonException.class)
	public void testNotFoundPersonById() throws SQLException, NotFoundPersonException{
		dao.findPerson(1, true);
	}
	
	@Test(timeout = 10000)
	public void testFindPersonByEmailAddress() throws SQLException, ParseException, NotFoundPersonException{
		Group g1 = groupFactory.getGroup();
		g1.setId(2);
		g1.setNomGroupe("FSI");
		
		Person p1 = getMockPersonSebastien(1, null);
		Person p2 = getMockPersonGabriel(2, g1);

		insertPerson(p1);
		insertPerson(p2);
		insertGroup(g1);
		
		Person p3 = dao.findPerson("seb@gouv.com", true);
		Person p4 = dao.findPerson("gabriel@ladet.net", true);
		
		assertEquals(p3.getId(), 1);
		assertEquals(p3.getNom(), "Gouverneur"); 
		assertEquals(p3.getPrenom(), "Sébastien");
		assertNull(p3.getGroupe());
		
		assertEquals(p4.getId(), 2);
		assertEquals(p4.getNom(), "Ladet"); 
		assertEquals(p4.getPrenom(), "Gabriel");
		assertEquals(p4.getGroupe().getId(), 2);
		assertEquals(p4.getGroupe().getNomGroupe(), "FSI");
		
	}
	
	@Test(timeout = 10000, expected=NotFoundPersonException.class)
	public void testNotFoundPersonByEmailAddress() throws SQLException, NotFoundPersonException{
		dao.findPerson("gab@l.net", true);
	}
	
	@Test(timeout = 10000)
	public void testSavePersonNewPersonNoId() throws SQLException, ParseException {
		Group g1 = groupFactory.getGroup();
		
		Person p1 = getMockPersonSebastien(0, g1);
		g1.setId(1);
		g1.setNomGroupe("ISL");
		dao.savePerson(p1, true);
		
		List<Person> p1_1 = dao.findAllPersons(true);
		List<Group> g1_1 = dao.findAllGroups(true);
		assertEquals(1, p1_1.size());
		assertEquals(1, g1_1.size());
		assertEquals(1, p1.getId());
		
	}
	
	@Test(timeout = 10000)
	public void testSavePersonNewPersonUpdateGroup() throws SQLException, ParseException {
		Group g1 = groupFactory.getGroup();
		
		Person p1 = getMockPersonSebastien(1, g1);
		Person p2 = getMockPersonJames(2, null);
		Person p3 = getMockPersonGabriel(3, g1);
		g1.setId(1);
		g1.setNomGroupe("ISL");
		
		insertPerson(p1);
		insertPerson(p2);
		insertGroup(g1);
		
		g1.setNomGroupe("Nouveau nom");
		dao.savePerson(p3, true);
		
		List<Person> p1_1 = dao.findAllPersons(true);
		List<Group> g1_1 = dao.findAllGroups(true);
		assertEquals(3, p1_1.size());
		assertEquals(1, g1_1.size());
		assertEquals("Nouveau nom", g1_1.get(0).getNomGroupe());
		
		assertEquals(1, p1_1.get(0).getId());
		assertEquals("Gouverneur", p1_1.get(0).getNom());
		assertEquals("Sébastien", p1_1.get(0).getPrenom());
		assertEquals("seb@gouv.com", p1_1.get(0).getEmail());
		assertEquals("http://sebgouv.com", p1_1.get(0).getSiteweb());
		assertEquals("28/09/1991", p1_1.get(0).getDateNaissance());
		assertEquals(1, p1_1.get(0).getGroupe().getId());
		
		assertEquals(2, p1_1.get(1).getId());
		assertEquals("Bond", p1_1.get(1).getNom());
		assertEquals("James", p1_1.get(1).getPrenom());
		assertEquals("jamesbond@mi6.com", p1_1.get(1).getEmail());
		assertEquals("http://bond.com", p1_1.get(1).getSiteweb());
		assertEquals("11/09/1920", p1_1.get(1).getDateNaissance());
		assertNull(p1_1.get(1).getGroupe());
		
		assertEquals(3, p1_1.get(2).getId());
		assertEquals("Ladet", p1_1.get(2).getNom());
		assertEquals("Gabriel", p1_1.get(2).getPrenom());
		assertEquals("gabriel@ladet.net", p1_1.get(2).getEmail());
		assertEquals("http://ladet.net", p1_1.get(2).getSiteweb());
		assertEquals("22/02/1993", p1_1.get(2).getDateNaissance());
		assertEquals(1, p1_1.get(2).getGroupe().getId());

		
	}
	
	@Test(timeout = 10000)
	public void testSavePersonUpdatePersonGroup() throws SQLException, ParseException {
		Group g1 = groupFactory.getGroup();
		g1.setId(1);
		g1.setNomGroupe("ISL");
		Person p1 = getMockPersonGabriel(1, g1);
		
		Group g2 = groupFactory.getGroup();
		g2.setId(2);
		g2.setNomGroupe("FSI");
		
		insertPerson(p1);
		insertGroup(g1);
		insertGroup(g2);
		
		p1.setNom("Nouveau Ladet");
		p1.setPrenom("Nouveau Gabriel");
		p1.setEmail("gab@lad.com");
		p1.setDateNaissance("20/02/1993");
		p1.setMotDePasseHash("1111111111111111111111111111111111111111111111111111111111111111");
		p1.setSalt("99999999");
		p1.setSiteweb("http://google.com");
		p1.addToGroup(g2);
		dao.savePerson(p1, true);
		
		List<Person> p1_1 = dao.findAllPersons(true);
		List<Group> g1_1 = dao.findAllGroups(true);
		assertEquals(1, p1_1.size());
		assertEquals(2, g1_1.size());
		Person p1_1_1 = p1_1.get(0);
		assertEquals(p1_1_1.getNom(), "Nouveau Ladet");
		assertEquals(p1_1_1.getPrenom(), "Nouveau Gabriel");
		assertEquals(p1_1_1.getEmail(), "gab@lad.com");
		assertEquals(p1_1_1.getDateNaissance(), "20/02/1993");
		assertEquals(p1_1_1.getMotDePasseHash(), "1111111111111111111111111111111111111111111111111111111111111111");
		assertEquals(p1_1_1.getSalt(), "99999999");
		assertEquals(p1_1_1.getSiteweb(), "http://google.com");
		assertEquals(p1_1_1.getGroupe().getId(), 2);

	}
	
	@Test(timeout = 10000)
	public void testSavePersonUpdatePersonRemoveGroup() throws SQLException, ParseException {
		Group g1 = groupFactory.getGroup();
		g1.setId(1);
		g1.setNomGroupe("ISL");
		Person p1 = getMockPersonGabriel(1, g1);
		insertPerson(p1);
		insertGroup(g1);
		
		p1.addToGroup(null);
		dao.savePerson(p1, true);
		List<Person> p1_1 = dao.findAllPersons(true);
		List<Group> g1_1 = dao.findAllGroups(true);
		assertEquals(1, p1_1.size());
		assertEquals(1, g1_1.size());
		assertNull(p1_1.get(0).getGroupe());
		
	}
	
	
	@Test(timeout = 10000)
	public void testSaveGroup() throws SQLException, ParseException{
		Group g1 = groupFactory.getGroup();
		Person p1 = getMockPersonSebastien(1, g1);
		
		g1.setId(0);
		g1.setNomGroupe("ISL");
		
		insertPerson(p1);
		dao.saveGroup(g1, true);
		List<Group> listGroup = dao.findAllGroups(true);
		assertEquals(listGroup.get(0).getId(), 1);
		assertEquals(listGroup.get(0).getNomGroupe(), "ISL");
		assertEquals(listGroup.get(0).getListPerson().size(), 1);
		Person p = listGroup.get(0).getListPerson().get(0);
		assertEquals(p.getId(), 1);
		assertEquals(p.getNom(), "Gouverneur"); 
		assertEquals(p.getPrenom(), "Sébastien");
		assertEquals(p.getGroupe(), listGroup.get(0));
		
		g1.setId(1);
		g1.setNomGroupe("FSI");
		dao.saveGroup(g1, true);
		
		listGroup = dao.findAllGroups(true);
		assertEquals(listGroup.get(0).getId(), 1);
		assertEquals(listGroup.get(0).getNomGroupe(), "FSI");
		p = listGroup.get(0).getListPerson().get(0);
		assertEquals(p.getId(), 1);
		assertEquals(p.getNom(), "Gouverneur"); 
		assertEquals(p.getPrenom(), "Sébastien");
		assertEquals(p.getGroupe(), listGroup.get(0));
	}
	
	@Test(timeout = 10000)
	public void testSaveGroupNotMaxId() throws SQLException, ParseException{
		Group g1 = groupFactory.getGroup();
		Group g2 = groupFactory.getGroup();
		Person p1 = getMockPersonSebastien(1, g2);
		Person p2 = getMockPersonGabriel(2, g2);
		
		g1.setId(2);
		g1.setNomGroupe("ISL");
		
		g2.setId(1);
		g2.setNomGroupe("FSI");
		
		insertPerson(p1);
		insertPerson(p2);
		dao.saveGroup(g1, true);
		dao.saveGroup(g2, true);
		
		List<Group> listGroup = dao.findAllGroups(true);
		assertEquals(listGroup.get(1).getId(), 2);
		assertEquals(listGroup.get(1).getNomGroupe(), "ISL");
		assertEquals(listGroup.get(1).getListPerson().size(), 0);
		
		assertEquals(listGroup.get(0).getId(), 1);
		assertEquals(listGroup.get(0).getNomGroupe(), "FSI");
		assertEquals(listGroup.get(0).getListPerson().size(), 2);


		
		Person p = listGroup.get(0).getListPerson().get(0);
		assertEquals(p.getId(), 1);
		assertEquals(p.getNom(), "Gouverneur"); 
		assertEquals(p.getPrenom(), "Sébastien");
		assertEquals(p.getGroupe(), listGroup.get(0));
		
		p = listGroup.get(0).getListPerson().get(1);
		assertEquals(p.getId(), 2);
		assertEquals(p.getNom(), "Ladet"); 
		assertEquals(p.getPrenom(), "Gabriel");
		assertEquals(p.getGroupe(), listGroup.get(0));
	}
	
	@Test(timeout = 10000)
	public void testSaveGroupUnregisterUser() throws SQLException, ParseException{
		Group g1 = groupFactory.getGroup();
		Person p1 = getMockPersonGabriel(1, g1);
		
		g1.setId(1);
		g1.setNomGroupe("FSI");
		insertPerson(p1);
		insertGroup(g1);
		
		p1.removeFromGroup();
		dao.saveGroup(g1, true);
		
		List<Group> listGroup = dao.findAllGroups(true);
		assertEquals(listGroup.size(), 1);
		assertEquals(listGroup.get(0).getId(), 1);
		assertEquals(listGroup.get(0).getNomGroupe(), "FSI");
		assertEquals(listGroup.get(0).getListPerson().size(), 0);
		
		List<Person> p1_1 = dao.findAllPersons(true);
		assertEquals(p1_1.size(), 1);
		assertEquals(p1_1.get(0).getId(), 1);
		assertEquals(p1_1.get(0).getNom(), "Ladet"); 
		assertEquals(p1_1.get(0).getPrenom(), "Gabriel");
		assertNull(p1_1.get(0).getGroupe());
		
	}
	


}