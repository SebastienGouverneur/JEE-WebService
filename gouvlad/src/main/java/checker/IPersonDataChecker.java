package checker;

import bean.Person;
import web.PersonInfoException;

public interface IPersonDataChecker {
	public boolean isBirthDateValid(String date);
	public boolean isValidEmailAddress(String email);
	public boolean isValidURL(String url);
	public void editProfilePersonChecker(Person person, String clearPassword, String clearPasswordConfirmed) throws PersonInfoException;

}
