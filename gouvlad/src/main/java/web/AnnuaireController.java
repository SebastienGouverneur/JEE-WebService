package web;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import bean.Group;
import bean.IPersonFactory;
import bean.Person;
import checker.IPersonDataChecker;
import dao.IPersonDao;
import dao.impl.NotFoundPersonException;
import web.PersonInfoException;
import web.Utils;

/**
 * This class is the uniq controller of the application.
 * It contains the methods associated with the URL wanted by the user. (Also depending of the HTTP method used)
 * Each method has to perform operations (like DAO integration or POST data treatment) and return the correct view.
 *  @author Sébastien Gouverneur
 *  @author Gabriel Ladet
 *
 */
@Controller
@RequestMapping("/annuaire")
public class AnnuaireController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
	private IPersonDao dao;
    
    @Autowired
	private IPersonFactory personFactory;
    
    @Autowired
    private IPersonDataChecker personDataChecker;
    
    @RequestMapping(value = "/listePersonnes", method = RequestMethod.GET)
    public ModelAndView handleListPersonsRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
    	List<Person> personList;
    	try {
    		if (request.getSession().getAttribute("testContext") != null)
    			personList = dao.findAllPersons(true);
    		else
    			personList = dao.findAllPersons();
		} catch (SQLException e) {
			return new ModelAndView("redirect:erreurInterne");
		}
		return new ModelAndView("annuairePersonnes", "personList", personList);

    }
    
    @RequestMapping(value = "/listeGroupes", method = RequestMethod.GET)
    public ModelAndView handleListGroupsRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
    	List<Group> groupList;
    	try {
    		if (request.getSession().getAttribute("testContext") != null)
    			groupList = dao.findAllGroups(true);
    		else
    			groupList = dao.findAllGroups();
		} catch (SQLException e) {
			return new ModelAndView("redirect:erreurInterne");
		}
		return new ModelAndView("annuaireGroupes", "groupList", groupList);

    }
    
    @RequestMapping(value = "/connexion", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleConnectionRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    		if (Utils.isConnected(request.getSession())){
    			return new ModelAndView("redirect:connexionReussie");
    		}
    		
    		if (request.getMethod().equals("POST") && 
    			request.getParameter("email") != null &&
    			request.getParameter("password") != null){
    			
    			if (request.getParameter("email").equals("") ||
        				request.getParameter("password").equals("")){
        			return new ModelAndView ("connexion", "error", 1);
        		}
    			
    			try {
    				Person p;
    				if (request.getSession().getAttribute("testContext") != null)
    					p = dao.findPerson(request.getParameter("email"), true);
    				else
    					p = dao.findPerson(request.getParameter("email"));

					String hashedPassword = Utils.get_SHA_512_SecurePassword(request.getParameter("password"), p.getSalt());
					if (!hashedPassword.equals(p.getMotDePasseHash())){
		    			return new ModelAndView("redirect:connexion/erreur");
					}
					request.getSession().setAttribute("Person", p);
	    			return new ModelAndView("redirect:connexionReussie");
					
				} catch (SQLException e) {
	    			return new ModelAndView("redirect:erreurInterne");
				} catch (NotFoundPersonException e) {
	    			return new ModelAndView("redirect:connexion/erreur");

				}
    			

    		} 
    		
    		return new ModelAndView("connexion");
    		
    }
    
    @RequestMapping(value = "/connexionReussie", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleConnectionValidateRequest(HttpServletRequest request,
            HttpServletResponse response) {
    		
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
    	
			return new ModelAndView("connexionReussie");
    }
    
    @RequestMapping(value = "/connexion/erreur", method = RequestMethod.GET)
    public ModelAndView handleConnectionRequestWithError(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		return new ModelAndView("connexion", "erreur", 1);

    }
    
    @RequestMapping(value = "/inscription", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleSignupRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, SQLException, NotFoundPersonException, ParseException {
    		
    		String salt = Utils.randomSalt(8);
    	
    		if (request.getMethod().equals("POST") && 
    			request.getParameter("firstname") != null &&
    			request.getParameter("lastname") != null &&
    			request.getParameter("email") != null &&
    			request.getParameter("website") != null &&
    			request.getParameter("birthdate") != null &&
    			request.getParameter("createpassword") != null &&
    			request.getParameter("confirmpassword") != null){
    			
    			Person p = personFactory.getPerson();
				p.setNom(request.getParameter("firstname"));
				p.setPrenom(request.getParameter("lastname"));
				p.setEmail(request.getParameter("email"));
				p.setSiteweb(request.getParameter("website"));
				p.setDateNaissance(request.getParameter("birthdate"));
					if (p.getNom().equals("") || p.getPrenom().equals("") || p.getEmail().equals("")
							|| request.getParameter("createpassword").equals("")  
							|| request.getParameter("confirmpassword").equals("") 
							|| p.getSiteweb().equals("")
							|| p.getDateNaissance().equals("")){
						PersonInfoException error = new PersonInfoException("Erreur: Veuillez remplir tous les champs.", p);
						return new ModelAndView("inscription", "signupinfo", error);

					}
					
				try {
					personDataChecker.editProfilePersonChecker(p, request.getParameter("createpassword"),request.getParameter("confirmpassword"));
				} catch (PersonInfoException error) {
					return new ModelAndView("inscription", "signupinfo", error);
				};
				
				try {
					if (request.getSession().getAttribute("testContext") != null)
						dao.findPerson(p.getEmail(), true);
					else
						dao.findPerson(p.getEmail());
					
					/* the e-mail address is already used */
					PersonInfoException error = new PersonInfoException("Erreur: Cette adresse e-mail est déjà utilisée.", p);
					return new ModelAndView("inscription", "signupinfo", error);
					
				} catch (NotFoundPersonException n){
					/* Nothing to do */
				}
				
				if (!personDataChecker.isValidURL(request.getParameter("website")) && !request.getParameter("website").equals("")){
					PersonInfoException error = new PersonInfoException("Erreur: L'adresse URL spécifiée n'est pas valide.", null);
					return new ModelAndView("inscription", "signupinfo", error);
				}
				
				String createPassword = Utils.get_SHA_512_SecurePassword(request.getParameter("createpassword"), salt);
				p.setMotDePasseHash(createPassword);
				p.setSalt(salt);
				
				if (request.getSession().getAttribute("testContext") != null)
					dao.savePerson(p, true);
				else
					dao.savePerson(p);
				
				return new ModelAndView("redirect:inscriptionReussie");
    			

    		} 
    		
    		return new ModelAndView("inscription");
    		
    }
    
    @RequestMapping(value = "/inscriptionReussie", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleSignupValidateRequest(HttpServletRequest request,
            HttpServletResponse response)  {
    		
			return new ModelAndView("inscriptionReussie");
    }
    
    
    @RequestMapping(value = "/afficherPersonne/{id}", method = RequestMethod.GET)
    public ModelAndView handleDisplayPersonInfosRequest(@PathVariable("id") Integer id, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
    	Person p;
    	try {
    		if (request.getSession().getAttribute("testContext") != null)
    			p = dao.findPerson(id, true);
    		else
    			p = dao.findPerson(id);
		} catch (SQLException e) {
			return new ModelAndView("redirect:erreurInterne");
		} catch (NotFoundPersonException e) {
			p = personFactory.getPerson();
			p.setId(-1);
		}
    	return new ModelAndView("afficherPersonne", "person", p);

    }
    
    @RequestMapping(value = "/editerProfil", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleEditingProfileRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
    	
    	if (request.getMethod().equals("POST") && 
    			request.getParameter("firstname") != null &&
    			request.getParameter("lastname") != null &&
    			request.getParameter("email") != null &&
    			request.getParameter("website") != null &&
    			request.getParameter("birthdate") != null &&
    			request.getParameter("newpassword") != null &&
    			request.getParameter("confirmpassword") != null) {

    		if (request.getParameter("firstname").equals("") ||
    				request.getParameter("lastname").equals("") ||
    				request.getParameter("email").equals("")){
    			PersonInfoException error = new PersonInfoException("Erreur: Les champs nom, prénom et e-mail ne peuvent pas être vides.", null);
    			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
    			m.addObject("erreur", error);
    			return m;
    			
    		}
    		
    		Person tmpPerson = personFactory.getPerson();
    		tmpPerson.setId(((Person)(request.getSession().getAttribute("Person"))).getId());
    		tmpPerson.setNom(request.getParameter("lastname"));
    		tmpPerson.setPrenom(request.getParameter("firstname"));

    		
    		if (!request.getParameter("email").equals(((Person) (request.getSession().getAttribute("Person"))).getEmail())){
    			if (!personDataChecker.isValidEmailAddress(request.getParameter("email"))){
    				PersonInfoException error = new PersonInfoException("Erreur: L'adresse e-mail n'a pas le bon format.", null);
        			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
    				m.addObject("erreur", error);
        			return m;
    			}
    			
    			
        		
    			try {
    				if (request.getSession().getAttribute("testContext") != null)
    					dao.findPerson(request.getParameter("email"), true);
    				else
    					dao.findPerson(request.getParameter("email"));
					PersonInfoException error = new PersonInfoException("Erreur: L'adresse e-mail spécifiée est déjà utilisée par une autre personne.", null);
        			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
    				m.addObject("erreur", error);

        			return m;
				} catch (SQLException e) {
					return new ModelAndView("redirect:erreurInterne");
				} catch (NotFoundPersonException e) {
					/* Nothing to do */
				}
    		}
    			tmpPerson.setEmail(request.getParameter("email"));
    		
    			if (!personDataChecker.isBirthDateValid(request.getParameter("birthdate"))){
    				PersonInfoException error = new PersonInfoException("Erreur: La date de naissance spécifiée n'est pas valide.", null);
        			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
    				m.addObject("erreur", error);
        			return m;
    			}
    			
    			tmpPerson.setDateNaissance(request.getParameter("birthdate"));
    			
    			if (!personDataChecker.isValidURL(request.getParameter("website")) && !request.getParameter("website").equals("")){
    				PersonInfoException error = new PersonInfoException("Erreur: L'adresse URL spécifiée n'est pas valide.", null);
        			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
    				m.addObject("erreur", error);
        			return m;
    			}
    			

    			tmpPerson.setSiteweb(request.getParameter("website"));
    			
    			if (!request.getParameter("newpassword").equals("") || !request.getParameter("confirmpassword").equals("")){
    				if (!request.getParameter("newpassword").equals(request.getParameter("confirmpassword"))){
    					PersonInfoException error = new PersonInfoException("Erreur: Les mots de passe donnés sont différents.", null);
            			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
        				m.addObject("erreur", error);
            			return m;
    				}
    				
    				String newSalt = Utils.randomSalt(8);
    				tmpPerson.setSalt(newSalt);
    				tmpPerson.setMotDePasseHash(Utils.get_SHA_512_SecurePassword(request.getParameter("newpassword"), newSalt));
    				
    			}else {
    				tmpPerson.setMotDePasseHash(((Person) (request.getSession().getAttribute("Person"))).getMotDePasseHash());
    				tmpPerson.setSalt((((Person) (request.getSession().getAttribute("Person"))).getSalt()));

    			}
    			
    			try {
    				if (request.getSession().getAttribute("testContext") != null)
    					dao.savePerson(tmpPerson, true);
    				else
    					dao.savePerson(tmpPerson);
					request.getSession().setAttribute("Person", tmpPerson);
        			ModelAndView m = new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));
    				m.addObject("success", 1);
        			return m;
				} catch (SQLException e) {
					return new ModelAndView("redirect:erreurInterne");
				} catch (ParseException e) {
					return new ModelAndView("redirect:erreurInterne");
				}
    	}
    	
		return new ModelAndView("editerProfil", "profilinfo", request.getSession().getAttribute("Person"));

    }
    
    @RequestMapping(value = "/rechercherPersonne/{searchText}", method = RequestMethod.GET)
    public ModelAndView handleSearchRequestParameter(@PathVariable("searchText") String searchText, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:/annuaire/connexion");	
    	}
    	
    	List<Person> personsList;
    	
    	try {
    		if (request.getSession().getAttribute("testContext") != null)
    			personsList = dao.searchPersons(searchText, true);
    		else
    			personsList = dao.searchPersons(searchText);

		} catch (SQLException e) {
			return new ModelAndView("redirect:/annuaire/erreurInterne");
		}
    	
    	ModelAndView m = new ModelAndView("resultatRecherche");
    	m.addObject("personList", personsList);
    	m.addObject("personListSize", personsList.size());
    	m.addObject("searchText", URLDecoder.decode(searchText, "UTF-8"));
    	return m;
    }

   
    
    @RequestMapping(value = "/rechercherPersonne", method = RequestMethod.POST)
    public ModelAndView handleSearchRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	 if (request.getParameter("searchText") == null || request.getParameter("searchText").equals("")){
    			return new ModelAndView("redirect:/annuaire/pagePrincipale");
    	    }
    	 
    			return new ModelAndView("redirect:"+URLEncoder.encode(request.getParameter("searchText"), "UTF-8"));

  }
    
    
    @RequestMapping(value = "/deconnexion", method = RequestMethod.GET)
    public ModelAndView handleDisconnectionRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
  
    	request.getSession().removeAttribute("Person");
		return new ModelAndView("redirect:connexion");

    }
    
    
    @RequestMapping(value = "/style.css", method = RequestMethod.GET)
    public ModelAndView handleRequestStyle(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/css");
    	response.flushBuffer();
		return new ModelAndView("style");

    }
    
    @RequestMapping(value = "/pageIntrouvable", method = RequestMethod.GET)
    public ModelAndView handlePageNotFound(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	return new ModelAndView("pageIntrouvable");
    
    }
    
    @RequestMapping(value = "/pagePrincipale", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handlePrincipalPageRequest(HttpServletRequest request,
            HttpServletResponse response){
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}	
    	
			return new ModelAndView("pagePrincipale");
    }
    
    @RequestMapping(value = "/erreurInterne", method = RequestMethod.GET)
    public ModelAndView handleInternalError(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	return new ModelAndView("erreurInterne");
    
    }
    
}
