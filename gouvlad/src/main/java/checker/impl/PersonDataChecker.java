package checker.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import bean.Person;
import checker.IPersonDataChecker;
import web.PersonInfoException;

/**
 * This class is an implementation of the IPersonDataChecker service
 *  @authors Sébastien Gouverneur & Gabriel Ladet
 *
 */
@Service
public class PersonDataChecker implements IPersonDataChecker {

	public boolean isBirthDateValid(String date) {
		 try {
	            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	            df.setLenient(false);
	            df.parse(date);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	}

	public boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
	}

	public boolean isValidURL(String url) {
		Pattern p = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");  
	    Matcher m = p.matcher(url);
	    return m.matches();
	}
	
	public void editProfilePersonChecker(Person person, String clearPassword, String clearPasswordConfirmed) throws PersonInfoException{
		if (!isBirthDateValid(person.getDateNaissance()) ){
			throw new PersonInfoException("Erreur: la date de naissance doit être de la forme jj/mm/aaaa et doit correspondre à une date valide.", person);
		}
		
		if (!clearPassword.equals(clearPasswordConfirmed)){
			throw new PersonInfoException("Erreur: les deux mots de passe ne correspondent pas.", person);
		}
		
		if (!isValidEmailAddress(person.getEmail())){
			throw new PersonInfoException("Erreur: le format de l'adresse e-mail est invalide.", person);
			
		}
		
	}

}
