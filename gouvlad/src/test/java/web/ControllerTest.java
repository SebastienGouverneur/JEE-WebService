package web;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import bean.Group;
import bean.IGroupFactory;
import bean.IPersonFactory;
import bean.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
public class ControllerTest extends Mockito {
	
	@Autowired
	IPersonFactory personFactory;
	
	@Autowired
	IGroupFactory groupFactory;
	
	@Autowired
	AnnuaireController controller;
	
	private HttpServletRequest getConnectedUserHttpRequestMock(){
		Group g = groupFactory.getGroup();
		g.setId(1);
		g.setNomGroupe("FSI");
		
		Person p = personFactory.getPerson();
		p.setId(1);
		p.setNom("Ladet"); 
		p.setPrenom("Gabriel"); 
		p.setEmail("gabriel@ladet.net");
		p.setDateNaissance("22/02/1993"); 
		p.setSiteweb("http://ladet.net"); 
		p.setMotDePasseHash("0000000000000000000000000000000000000000000000000000000000000000");
		p.setSalt("12345678");
		p.addToGroup(g);
		
		
		HttpServletRequest requestConnectedUser = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		
		
		when(requestConnectedUser.getSession()).thenReturn(session);
		when(requestConnectedUser.getSession().getAttribute("Person")).thenReturn(p);
		
		return requestConnectedUser;
	}
	
	private HttpServletRequest getNotConnectedUserHttpRequestMock(){
		
		HttpServletRequest requestNotConnectedUser = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(requestNotConnectedUser.getSession()).thenReturn(session);
		when(requestNotConnectedUser.getSession().getAttribute("Person")).thenReturn(null);
		return requestNotConnectedUser;

	}

	@Test
	public void testListePersonnes() throws ServletException, IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView mav = controller.handleListPersonsRequest(getConnectedUserHttpRequestMock(), response);
		assertEquals("annuairePersonnes", mav.getViewName());

		mav = controller.handleListPersonsRequest(getNotConnectedUserHttpRequestMock(), response);
		assertEquals("redirect:connexion", mav.getViewName());
	}

}
