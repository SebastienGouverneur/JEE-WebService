package bean.impl;

import bean.IPersonFactory;
import bean.Person;

public abstract class PersonFactory implements IPersonFactory {
	
	public abstract Person getPerson();

}
