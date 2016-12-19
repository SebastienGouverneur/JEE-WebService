package checker;

import bean.Person;
import web.PersonInfoException;

/**
 * This interface contains the different methods used by the application to check the user's inputs validity.
 *  @author Sébastien Gouverneur 
 *  @author Gabriel Ladet
 *
 */
public interface IPersonDataChecker {
	public boolean isBirthDateValid(String date);
	public boolean isValidEmailAddress(String email);
	public boolean isValidURL(String url);
	public void editProfilePersonChecker(Person person, String clearPassword, String clearPasswordConfirmed) throws PersonInfoException;

}
