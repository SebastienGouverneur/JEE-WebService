package dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import bean.Group;
import bean.IPersonFactory;
import bean.Person;
import dao.IPersonDao;


@Service
public class PersonDaoBd implements IPersonDao {
	
	private String driverName = "com.mysql.jdbc.Driver";
	private String url      = "jdbc:mysql://db4free.net/jeegouvlad";
	private String user     = "webservice";
	private String password = "jeewebservice";
	private Connection connection;
	
	@Autowired
	private IPersonFactory personFactory;

	
	
	public PersonDaoBd() throws SQLException{
		connection = DriverManager.getConnection(url, user, password);
	}
	

	public Collection<Group> findAllGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Person> findAllPersons() throws SQLException {
		
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
		List<Person> personList = new LinkedList<Person>();
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM Personne");
		while (rs.next()){
			Person p = personFactory.getPerson();
			p.setId(rs.getInt(1));
			p.setNom(rs.getString(2));
			p.setPrenom(rs.getString(3));
			p.setEmail(rs.getString(4));
			p.setSiteweb(rs.getString(5));
			p.setDateNaissance(df.format(rs.getDate(6)));
			personList.add(p);
		}
		return personList;
	}

	public Person findPerson(int id) {
		// TODO Auto-generated method stub
		return null;
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
