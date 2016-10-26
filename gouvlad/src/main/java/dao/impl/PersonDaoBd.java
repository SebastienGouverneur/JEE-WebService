package dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.stereotype.Service;


import bean.Group;
import bean.Person;
import dao.IPersonDao;


@Service
public class PersonDaoBd implements IPersonDao {
	
	private String driverName = "com.mysql.jdbc.Driver";
	private String url      = "jdbc:mysql://db4free.net/jeegouvlad";
	private String user     = "webservice";
	private String password = "jeewebservice";
	private Connection connection;
	
	
	public PersonDaoBd() throws SQLException{
		connection = DriverManager.getConnection(url, user, password);
	}
	

	public Collection<Group> findAllGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Person> findAllPersons() {
		// TODO Auto-generated method stub
		return null;
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
