package bean.impl;

import bean.IPersonFactory;
import bean.Person;

/**
 * A person factory that delegates the Person object instantiations to the Spring container.
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 *	@see spring.xml
 */

public abstract class PersonFactory implements IPersonFactory {
	
	public abstract Person getPerson();

}
