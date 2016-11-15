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
		clearTable(Resources.getString("KeyPersonTest"));
		clearTable(Resources.getString("KeyBelongTest"));
		clearTable(Resources.getString("KeyGroupTest"));
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
		st.setString(7, "0000000000000000000000000000000000000000000000000000000000000000"); 
		st.setString(8, "12345678"); 
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
	}
	
	@Test
	public void testConnection() {
		Assert.assertNotNull(dao);
		Assert.assertNotNull(dao.getConnection());
	}
	
	@Test
	public void testFindAllPersons() throws SQLException, ParseException{
		Person p1 = personFactory.getPerson();
		Person p2 = personFactory.getPerson();
		
		p1.setId(1);
		p1.setNom("Gouverneur"); 
		p1.setPrenom("Sébastien"); 
		p1.setEmail("seb@gouv.com"); 
		p1.setDateNaissance("28/09/1991"); 
		p1.setSiteweb("http://sebgouv.com"); 
		
		p2.setId(2);
		p2.setNom("Ladet"); 
		p2.setPrenom("Gabriel"); 
		p2.setEmail("gabriel@ladet.net");
		p2.setDateNaissance("22/02/1993"); 
		p2.setSiteweb("http://ladet.net"); 
		
		insertPerson(p1);
		insertPerson(p2);
		
		List<Person> listPerson = dao.findAllPersons(true);
		
		assertEquals(listPerson.size(), 2);
		
		assertEquals(listPerson.get(0).getId(), 1);
		assertEquals(listPerson.get(0).getNom(), "Gouverneur"); 
		assertEquals(listPerson.get(0).getPrenom(), "Sébastien"); 
		
		assertEquals(listPerson.get(1).getId(), 2);
		assertEquals(listPerson.get(1).getNom(), "Ladet"); 
		assertEquals(listPerson.get(1).getPrenom(), "Gabriel"); 
	}
	
	@Test(timeout = 10000)
	public void testFindAllGroups() throws SQLException, ParseException {
		
		Group g1 = groupFactory.getGroup();
		Group g2 = groupFactory.getGroup();
		Group g3 = groupFactory.getGroup();
		
		Person p1 = personFactory.getPerson();
		Person p2 = personFactory.getPerson();
		Person p3 = personFactory.getPerson();
		
		
	
		
		g1.setId(1);
		g1.getListPerson().add(p1);
		g1.setNomGroupe("ISL");
		
		g2.setId(2);
		g2.getListPerson().add(p2);
		g2.getListPerson().add(p3);
		g2.setNomGroupe("FSI");
		
		g3.setId(3);
		g3.setNomGroupe("ID");
		
		p1.setId(1);
		p1.setNom("Gouverneur"); 
		p1.setPrenom("Sébastien"); 
		p1.setEmail("seb@gouv.com"); 
		p1.setDateNaissance("28/09/1991"); 
		p1.setSiteweb("http://sebgouv.com"); 
		
		p2.setId(2);
		p2.setNom("Ladet"); 
		p2.setPrenom("Gabriel"); 
		p2.setEmail("gabriel@ladet.net");
		p2.setDateNaissance("22/02/1993"); 
		p2.setSiteweb("http://ladet.net"); 
		
		p3.setId(3);
		p3.setNom("Bond"); 
		p3.setPrenom("James"); 
		p3.setEmail("jamesbond@mi6.com");
		p3.setDateNaissance("11/09/1920"); 
		p3.setSiteweb("http://bond.com"); 
		
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
	


}
