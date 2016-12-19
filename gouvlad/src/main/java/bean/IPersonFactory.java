package bean;

/**
 * A person factory interface that delegates the person object instantiations to the Spring container.
 *  @author Sébastien Gouverneur
 *  @author Gabriel Ladet
 *	
 */

public interface IPersonFactory {
	
	public abstract Person getPerson();
}
