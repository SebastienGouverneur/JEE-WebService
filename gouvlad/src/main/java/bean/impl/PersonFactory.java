package bean.impl;

import bean.IPersonFactory;
import bean.Person;

/**
 * A person factory that delegates the Person object instantiations to the Spring container.
 *  @author Sébastien Gouverneur
 *  @author Gabriel Ladet
 */

public abstract class PersonFactory implements IPersonFactory {
	
	public abstract Person getPerson();

}
