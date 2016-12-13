package web;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import bean.Group;
import bean.IPersonFactory;
import bean.Person;
import dao.IPersonDao;
import dao.impl.NotFoundPersonException;

@Controller
@RequestMapping("/annuaire")
public class AnnuaireController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
	private IPersonDao dao;
    
    @Autowired
	private IPersonFactory personFactory;
    
    @RequestMapping(value = "/listePersonnes", method = RequestMethod.GET)
    public ModelAndView handleListPersonsRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
    	List<Person> personList;
    	try {
		personList = dao.findAllPersons();
		} catch (SQLException e) {
			return new ModelAndView("redirect:erreur_interne");
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
		groupList = dao.findAllGroups();
		} catch (SQLException e) {
			return new ModelAndView("redirect:erreur_interne");
		}
		return new ModelAndView("annuaireGroupes", "groupList", groupList);

    }
    
    @RequestMapping(value = "/connexion", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleConnectionRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    		if (Utils.isConnected(request.getSession())){
    			return new ModelAndView("redirect:listePersonnes");
    		}
    		
    		if (request.getMethod().equals("POST") && 
    			request.getParameter("email") != null &&
    			request.getParameter("password") != null){
    			try {
					Person p = dao.findPerson(request.getParameter("email"));
					String hashedPassword = Utils.get_SHA_512_SecurePassword(request.getParameter("password"), p.getSalt());
					if (!hashedPassword.equals(p.getMotDePasseHash())){
		    			return new ModelAndView("redirect:connexion/erreur");
					}
					request.getSession().setAttribute("Person", p);
	    			return new ModelAndView("redirect:listePersonnes");
					
				} catch (SQLException e) {
	    			return new ModelAndView("redirect:erreur_interne");
				} catch (NotFoundPersonException e) {
	    			return new ModelAndView("redirect:connexion/erreur");

				}
    			

    		} 
    		
    		return new ModelAndView("connexion");
    		
    }
    
    @RequestMapping(value = "/connexion/erreur", method = RequestMethod.GET)
    public ModelAndView handleConnectionRequestWithError(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		return new ModelAndView("connexion", "erreur", 1);

    }
    
    @RequestMapping(value = "/signup", method = {RequestMethod.GET, RequestMethod.POST})
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
						SignUpError error = new SignUpError("Erreur: Veuillez remplir tous les champs.", p);
						return new ModelAndView("signup", "signupinfo", error);

					}
				if (!Utils.isDateValid(p.getDateNaissance()) ){
					SignUpError error = new SignUpError("Erreur: la date de naissance doit être de la forme jj/mm/aaaa et doit correspondre à une date valide.", p);
					return new ModelAndView("signup", "signupinfo", error);
				}
				
				String createPassword = Utils.get_SHA_512_SecurePassword(request.getParameter("createpassword"), salt);
				if (!request.getParameter("createpassword").equals(request.getParameter("confirmpassword"))){
					SignUpError error = new SignUpError("Erreur: les deux mots de passe ne correspondent pas.", p);
					return new ModelAndView("signup", "signupinfo", error);
				}
				
				if (!Utils.isValidEmailAddress(p.getEmail())){
					SignUpError error = new SignUpError("Erreur: le format de l'adresse e-mail est invalide.", p);
					return new ModelAndView("signup", "signupinfo", error);
				}
				
				try {
					dao.findPerson(p.getEmail());
					/* the e-mail address is already used */
					SignUpError error = new SignUpError("Erreur: Cette adresse e-mail est déjà utilisée.", p);
					return new ModelAndView("signup", "signupinfo", error);
					
				} catch (NotFoundPersonException n){
					/* Nothing to do */
				}
				
				
				p.setMotDePasseHash(createPassword);
				p.setSalt(salt);
				
				dao.savePerson(p);
				request.getSession().setAttribute("Person", p);
				
				return new ModelAndView("redirect:person");
    			

    		} 
    		
    		return new ModelAndView("signup");
    		
    }
    
    /*@RequestMapping(value = "/signup/erreur/{numError}", method = RequestMethod.GET)
    public ModelAndView handleSignupRequestWithError(@PathVariable("numError") Integer numError) throws ServletException, IOException {
		return new ModelAndView("signup", "erreur", numError);

    }*/
    
    @RequestMapping(value = "/afficherPersonne/{id}", method = RequestMethod.GET)
    public ModelAndView handleDisplayPersonInfosRequest(@PathVariable("id") Integer id) throws ServletException, IOException {
		Person p;
    	try {
			p = dao.findPerson(id);
		} catch (SQLException e) {
			return new ModelAndView("redirect:erreur_interne");
		} catch (NotFoundPersonException e) {
			p = personFactory.getPerson();
			p.setId(-1);
		}
    	return new ModelAndView("afficherPersonne", "person", p);

    }
    
    
    @RequestMapping(value = "/style.css", method = RequestMethod.GET)
    public ModelAndView handleRequestStyle(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/css");
    	response.flushBuffer();
		return new ModelAndView("style");

    }
    
    /*@RequestMapping(value = "/signup.jpg", method = RequestMethod.GET)
    public ResponseEntity getImage(String path, HttpServletResponse response) {
        try {
            if (StringUtils.isEmpty(path)) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } else {
                response.setHeader("Content-Type", "image/jpg");
                FileInputStream fin = new FileInputStream(path);
                ServletOutputStream out = response.getOutputStream();

                IOUtils.copy(fin, out);
                IOUtils.closeQuietly(fin);

                response.flushBuffer();
                return new ResponseEntity(HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }*/
    
    

}
