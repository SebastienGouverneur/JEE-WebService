package bean;

/**
 * A person factory interface that delegates the person object instantiations to the Spring container.
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 *	@see spring.xml
 */

public interface IPersonFactory {
	
	public abstract Person getPerson();
}
