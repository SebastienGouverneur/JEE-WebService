package web;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import bean.Person;
import dao.IPersonDao;
import dao.impl.NotFoundPersonException;

@Controller
@RequestMapping("/annuaire")
public class AnnuaireController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
	private IPersonDao dao;
    
    @RequestMapping(value = "/liste", method = RequestMethod.GET)
    public ModelAndView handleListRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	if (!Utils.isConnected(request.getSession())){
    		logger.info("Returning connexion view");
    		return new ModelAndView("redirect:connexion");	
    	}
		return new ModelAndView("annuaire");

    }
    
    @RequestMapping(value = "/connexion", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleConnectionRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    		if (Utils.isConnected(request.getSession())){
    			return new ModelAndView("redirect:liste");
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
	    			return new ModelAndView("redirect:liste");
					
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
    			Person p = new Person();
				p.setNom(request.getParameter("firstname"));
				p.setPrenom(request.getParameter("lastname"));
				p.setEmail(request.getParameter("email"));
				p.setSiteweb(request.getParameter("website"));
				p.setDateNaissance(request.getParameter("birthdate"));
				
				if (!Utils.isDateValid(p.getDateNaissance()) ){
					return new ModelAndView("redirect:signup/erreur/1", "signupinfo", p);

				}
				
				String createPassword = Utils.get_SHA_512_SecurePassword(request.getParameter("createpassword"), salt);
				if (!request.getParameter("createpassword").equals(request.getParameter("confirmpassword"))){
					return new ModelAndView("redirect:signup/erreur/2", "signupinfo", p);
				}
				
				if (!Utils.isValidEmailAddress(p.getEmail())){
					return new ModelAndView("redirect:signup/erreur/3","signupinfo", p);
				}
				
				try {
					dao.findPerson(p.getEmail());
					/* the e-mail address is already used */
					return new ModelAndView("redirect:signup/erreur/4", "signupinfo", p);
					
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
    
    @RequestMapping(value = "/signup/erreur/{numError}", method = RequestMethod.GET)
    public ModelAndView handleSignupRequestWithError(@PathVariable("numError") Integer numError) throws ServletException, IOException {
		return new ModelAndView("signup", "erreur", numError);

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
