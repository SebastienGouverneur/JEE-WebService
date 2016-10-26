package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import bean.Person;
import bean.Group;

public interface IPersonDao {
	
	Collection<Group> findAllGroups();
	List<Person> findAllPersons() throws SQLException;
	Person findPerson(int id);
	void savePerson(Person person);
	void saveGroup(Group group);
	Connection getConnection();
}
