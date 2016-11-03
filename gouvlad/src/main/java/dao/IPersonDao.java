package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import bean.Person;
import bean.Group;

public interface IPersonDao {
	
	List<Group> findAllGroups(boolean test) throws SQLException;
	List<Group> findAllGroups() throws SQLException;
	List<Person> findAllPersons() throws SQLException;
	List<Person> findAllPersons(boolean test) throws SQLException;
	Person findPerson(int id);
	void savePerson(Person person);
	void saveGroup(Group group);
	Connection getConnection();
}
