package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import bean.Person;
import dao.impl.NotFoundPersonException;
import bean.Group;

/**
 * This interface contains the different methods used by the application to access the database.
 * @author Sébastien Gouverneur
 * @author Gabriel Ladet
 *
 */

public interface IPersonDao {
	
	List<Group> findAllGroups(boolean test) throws SQLException;
	List<Group> findAllGroups() throws SQLException;
	List<Person> findAllPersons() throws SQLException;
	List<Person> findAllPersons(boolean test) throws SQLException;
	List<Person> searchPersons(String searchText) throws SQLException;
	List<Person> searchPersons(String searchText, boolean test) throws SQLException;
	Person findPerson(int id) throws SQLException, NotFoundPersonException;
	Person findPerson(int id, boolean test) throws SQLException, NotFoundPersonException;
	Person findPerson(String emailAddress) throws SQLException, NotFoundPersonException;
	Person findPerson(String emailAddress, boolean test) throws SQLException, NotFoundPersonException;
	void savePerson(Person person, boolean test) throws SQLException, ParseException;
	public void savePerson(Person person) throws SQLException, ParseException;
	void saveGroup(Group group) throws SQLException;
	void saveGroup(Group group, boolean test) throws SQLException;
	Connection getConnection();

}