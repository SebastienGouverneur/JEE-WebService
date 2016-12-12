package web;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
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
    @RequestMapping(value = "/style.css", method = RequestMethod.GET)
    public ModelAndView handleRequestStyle(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
		return new ModelAndView("style");

    }
    

}
