package bean.impl;

import bean.Group;
import bean.IGroupFactory;

/**
 * A group factory that delegates the Group object instantiation to the Spring container.
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 *	@see spring.xml
 */
public abstract class GroupFactory implements IGroupFactory {
	
	public abstract Group getGroup();

}
