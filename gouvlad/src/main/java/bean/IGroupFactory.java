package bean;


/**
 * A group factory interface that delegates the Group object instantiation to the Spring container.
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 *	@see spring.xml
 */

public interface IGroupFactory {
	
	public abstract Group getGroup();
}
