package dao;

import java.util.Collection;

import bean.Person;
import bean.Group;

public interface IPersonDao {
	
	Collection<Group> findAllGroups();
	Collection<Person> findAllPersons();
	Person findPerson(int id);
	void savePerson(Person person);
	void saveGroup(Group group);

}
