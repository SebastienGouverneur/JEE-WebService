package bean;


/**
 * A group factory interface that delegates the Group object instantiation to the Spring container.
 *  @author Sébastien Gouverneur
 *  @author Gabriel Ladet
 */

public interface IGroupFactory {
	
	public abstract Group getGroup();
}
