package web;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
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
import dao.IPersonDao;
import dao.impl.NotFoundPersonException;
import dao.impl.PersonDaoBdTest;

/**
 * Test class for the controller.
 *  @author Sébastien Gouverneur
 *  @author Gabriel Ladet
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
public class ControllerTest extends Mockito {
	
	@Autowired
	IPersonDao dao;
	
	@Autowired
	IPersonFactory personFactory;
	
	@Autowired
	IGroupFactory groupFactory;
	
	@Autowired
	AnnuaireController controller;
	
	@Autowired
	PersonDaoBdTest daoUtils;
	
	private final static Object temoin = new Object();
	
	
	@Before
	public void clearTestTables() throws SQLException{
		daoUtils.clearTestTables();
	}
	
	
	private HttpServletRequest getConnectedUserHttpRequestMock(){
		Group g = groupFactory.getGroup();
		g.setId(1);
		g.setNomGroupe("FSI");
		
		Person p = daoUtils.getMockPersonGabriel(1, g);
		
		
		HttpServletRequest requestConnectedUser = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		
		
		when(requestConnectedUser.getSession()).thenReturn(session);
		when(requestConnectedUser.getSession().getAttribute("Person")).thenReturn(p);
		when(requestConnectedUser.getSession().getAttribute("testContext")).thenReturn(temoin);

		
		return requestConnectedUser;
	}
	
	private HttpServletRequest getNotConnectedUserHttpRequestMock(){
		
		HttpServletRequest requestNotConnectedUser = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(requestNotConnectedUser.getSession()).thenReturn(session);
		when(requestNotConnectedUser.getSession().getAttribute("Person")).thenReturn(null);
		when(requestNotConnectedUser.getSession().getAttribute("testContext")).thenReturn(temoin);
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
	
	@Test
	public void testListeGroupes() throws ServletException, IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView mav = controller.handleListGroupsRequest(getConnectedUserHttpRequestMock(), response);
		assertEquals("annuaireGroupes", mav.getViewName());

		mav = controller.handleListGroupsRequest(getNotConnectedUserHttpRequestMock(), response);
		assertEquals("redirect:connexion", mav.getViewName());
	}
	
	@Test
	public void testConnexion() throws ServletException, IOException, SQLException, ParseException {
        HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView mav = controller.handleConnectionRequest(getConnectedUserHttpRequestMock(), response);
		assertEquals("redirect:connexionReussie", mav.getViewName());
		
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");

		mav = controller.handleConnectionRequest(request, response);
		assertEquals("connexion", mav.getViewName());
		
		when(request.getMethod()).thenReturn("POST");
		mav = controller.handleConnectionRequest(request, response);
		/*
		 * POST request with email and password == null
		 */
		assertEquals("connexion", mav.getViewName());
		
		/* Insert a person in database */
		/* Integration test with a valid password*/
		Person p = daoUtils.getMockPersonGabriel(1, null);
		p.setMotDePasseHash(Utils.get_SHA_512_SecurePassword("1234", "12345678"));
		p.setSalt("12345678");
		daoUtils.insertPerson(p);
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("password")).thenReturn("1234");
		mav = controller.handleConnectionRequest(request, response);
		assertEquals("redirect:connexionReussie", mav.getViewName());
		
		/* Integration test with an invalid password*/
		when(request.getParameter("password")).thenReturn("12345");
		mav = controller.handleConnectionRequest(request, response);
		assertEquals("redirect:connexion/erreur", mav.getViewName());
		
		/* Integration test with an invalid user*/
		when(request.getParameter("email")).thenReturn("gabriel@ldt.net");
		mav = controller.handleConnectionRequest(request, response);
		assertEquals("redirect:connexion/erreur", mav.getViewName());


	}
	
	@Test
	public void testConnexionReussie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView mav = controller.handleConnectionValidateRequest(getConnectedUserHttpRequestMock(), response);
		assertEquals("connexionReussie", mav.getViewName());

		mav = controller.handleConnectionValidateRequest(getNotConnectedUserHttpRequestMock(), response);
		assertEquals("redirect:connexion", mav.getViewName());
	}
	
	@Test
	public void testConnexionErreur() throws ServletException, IOException {
       HttpServletResponse response = mock(HttpServletResponse.class);
       ModelAndView mav = controller.handleConnectionRequestWithError(getNotConnectedUserHttpRequestMock(), response);
       assertEquals("connexion", mav.getViewName());
       assertEquals(1, mav.getModel().get("erreur"));
	}
	
	@Test
	public void testInscription() throws ServletException, IOException, SQLException, NotFoundPersonException, ParseException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
	    ModelAndView mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
		
		/* POST request without fields */
		when(request.getMethod()).thenReturn("POST");
	    mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
		
		/* POST request with fields and empty  one */
		when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("1234");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));
	    
	    /* POST request with invalid format email address */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("1234");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));
	    
	    /* POST request with invalid format website url */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("1234");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));
	    
	    /* POST request with invalid date format */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/021993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("1234");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));
	    
	    /* POST request with incorrect date */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/13/1993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("1234");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));
	    
	    /* POST request with different passwords */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("12345");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));
		
		/* POST request with valid fields */
		when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("createpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("1234");	
		mav = controller.handleSignupRequest(request, response);
		assertEquals("redirect:inscriptionReussie", mav.getViewName());
		
		/* POST request with email address already in database */
		mav = controller.handleSignupRequest(request, response);
		assertEquals("inscription", mav.getViewName());
	    assertNotNull(mav.getModel().get("signupinfo"));

		
	}
	
	@Test
	public void testInscriptionReussie() {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView mav = controller.handleSignupValidateRequest(getNotConnectedUserHttpRequestMock(), response);
		assertEquals("inscriptionReussie", mav.getViewName());
		
	}
	
	@Test
	public void testAfficherPersonne() throws ServletException, IOException, SQLException, ParseException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		ModelAndView mav = controller.handleDisplayPersonInfosRequest(1, getNotConnectedUserHttpRequestMock(), response);
		assertEquals("redirect:connexion", mav.getViewName());
		
		mav = controller.handleDisplayPersonInfosRequest(1, getConnectedUserHttpRequestMock(), response);
		assertEquals("afficherPersonne", mav.getViewName());
		assertEquals(-1, ((Person)(mav.getModel().get("person"))).getId());
		
		Person p = daoUtils.getMockPersonGabriel(1, null);
		daoUtils.insertPerson(p);
		mav = controller.handleDisplayPersonInfosRequest(1, getConnectedUserHttpRequestMock(), response);
		assertEquals("afficherPersonne", mav.getViewName());
		assertEquals(1, ((Person)(mav.getModel().get("person"))).getId());

	}
	
	
	@Test
	public void testEditerProfil() throws ServletException, IOException, SQLException, ParseException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
	    ModelAndView mav = controller.handleEditingProfileRequest(request, response);
		assertEquals("redirect:connexion", mav.getViewName());
		
		request = getConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		mav = controller.handleEditingProfileRequest(request, response);
		assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));

		/* POST request without fields */
		when(request.getMethod()).thenReturn("POST");
	    mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
		
		/* POST request with fields and empty  one */
		when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("newpassword")).thenReturn("");
		when(request.getParameter("confirmpassword")).thenReturn("");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
	    
	    /* POST request with invalid format email address */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("newpassword")).thenReturn("");
		when(request.getParameter("confirmpassword")).thenReturn("");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
	    
	    /* POST request with invalid format website url */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("newpassword")).thenReturn("");
		when(request.getParameter("confirmpassword")).thenReturn("");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
	    
	    /* POST request with invalid date format */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/021993");
		when(request.getParameter("newpassword")).thenReturn("");
		when(request.getParameter("confirmpassword")).thenReturn("");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
	    
	    /* POST request with incorrect date */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/13/1993");
		when(request.getParameter("newpassword")).thenReturn("");
		when(request.getParameter("confirmpassword")).thenReturn("");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
	    
	    /* POST request with different passwords */
	    when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("newpassword")).thenReturn("1234");
		when(request.getParameter("confirmpassword")).thenReturn("12345");
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));
		
		
		
		/* POST request with valid fields */
		Person p = daoUtils.getMockPersonGabriel(1, null);
		daoUtils.insertPerson(p);
		when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn("gabriel@ladet.net");
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("newpassword")).thenReturn("12345");
		when(request.getParameter("confirmpassword")).thenReturn("12345");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNotNull(mav.getModel().get("success"));
		
		/* POST request with email address already associated to another user */
		p = daoUtils.getMockPersonSebastien(2, null);
		daoUtils.insertPerson(p);
		when(request.getParameter("firstname")).thenReturn("Gabriel");
		when(request.getParameter("lastname")).thenReturn("Ladet");
		when(request.getParameter("email")).thenReturn(p.getEmail());
		when(request.getParameter("website")).thenReturn("http://ladet.net");
		when(request.getParameter("birthdate")).thenReturn("22/02/1993");
		when(request.getParameter("newpassword")).thenReturn("12345");
		when(request.getParameter("confirmpassword")).thenReturn("12345");	
		mav = controller.handleEditingProfileRequest(request, response);
	    assertEquals("editerProfil", mav.getViewName());
		assertNotNull(mav.getModel().get("profilinfo"));
		assertNull(mav.getModel().get("success"));

		
	}
	
	@Test
	public void testRechercheGet() throws ServletException, IOException, SQLException, ParseException {
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		
	    ModelAndView mav = controller.handleSearchRequestParameter("test", request, response);
		assertEquals("redirect:/annuaire/connexion", mav.getViewName());
		
		request = getConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		/* Special character in the research term to verify encoding */
	    mav = controller.handleSearchRequestParameter("tést", request, response);
	    assertEquals("resultatRecherche", mav.getViewName());
		assertEquals(0, ((List<Person>)(mav.getModel().get("personList"))).size());
		assertEquals(0, mav.getModel().get("personListSize"));
		assertEquals("tést", mav.getModel().get("searchText"));
		
		Person p = daoUtils.getMockPersonGabriel(1, null);
		daoUtils.insertPerson(p);
		mav = controller.handleSearchRequestParameter("gabriel", request, response);
	    assertEquals("resultatRecherche", mav.getViewName());
		assertEquals(1, ((List<Person>)(mav.getModel().get("personList"))).size());
		assertEquals(1, mav.getModel().get("personListSize"));
		assertEquals("gabriel", mav.getModel().get("searchText"));
		
	}
	
	@Test
	public void testRecherchePost() throws ServletException, IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("POST");
		
		/* Test with an empty research */
		when(request.getParameter("searchText")).thenReturn("");
		ModelAndView mav = controller.handleSearchRequest(request, response);
		assertEquals("redirect:/annuaire/pagePrincipale", mav.getViewName());
		
		/* Test with an encoded  parameter */
		when(request.getParameter("searchText")).thenReturn("tést");
		mav = controller.handleSearchRequest(request, response);
		assertEquals("redirect:t%C3%A9st", mav.getViewName());

		
		
	}
	
	@Test
	public void testDeconnexion() throws ServletException, IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		ModelAndView mav = controller.handleDisconnectionRequest(request, response);
		assertEquals("redirect:connexion", mav.getViewName());
	}
	
	@Test
	public void testCSS() throws ServletException, IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		ModelAndView mav = controller.handleRequestStyle(request, response);
		assertEquals("style", mav.getViewName());
		
	}
	
	@Test
	public void testPageIntrouvable() throws ServletException, IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		ModelAndView mav = controller.handlePageNotFound(request, response);
		assertEquals("pageIntrouvable", mav.getViewName());
		
	}
	
	@Test
	public void testPagePrincipale() {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		
		ModelAndView mav = controller.handlePrincipalPageRequest(request, response);
		assertEquals("redirect:connexion", mav.getViewName());
		
		request = getConnectedUserHttpRequestMock();
		mav = controller.handlePrincipalPageRequest(request, response);
		assertEquals("pagePrincipale", mav.getViewName());


		
	}
	
	@Test
	public void testErreurInterne() throws ServletException, IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = getNotConnectedUserHttpRequestMock();
		when(request.getMethod()).thenReturn("GET");
		ModelAndView mav = controller.handleInternalError(request, response);
		assertEquals("erreurInterne", mav.getViewName());
		
	}
	
	

}
