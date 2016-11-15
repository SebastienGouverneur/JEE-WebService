package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.impl.NotFoundPersonException;

import bean.Person;
import bean.Group;

public interface IPersonDao {
	
	List<Group> findAllGroups(boolean test) throws SQLException;
	List<Group> findAllGroups() throws SQLException;
	List<Person> findAllPersons() throws SQLException;
	List<Person> findAllPersons(boolean test) throws SQLException;
	Person findPerson(int id) throws SQLException, NotFoundPersonException;
	Person findPerson(int id, boolean test) throws SQLException, NotFoundPersonException;
	void savePerson(Person person);
	void saveGroup(Group group);
	Connection getConnection();
}
