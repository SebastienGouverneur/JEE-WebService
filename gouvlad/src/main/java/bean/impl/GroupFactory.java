package bean.impl;

import bean.Group;
import bean.IGroupFactory;

public abstract class GroupFactory implements IGroupFactory {
	
	public abstract Group getGroup();

}
