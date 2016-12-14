<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Sign Up" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
	<meta http-equiv="Refresh" content="5;url=connexion">
	<p class="validateInscription">Inscription réussie !</p>
	<p>Vous allez être redirigé vers la page de connexion dans quelques secondes</p> <br />
	<p>Si vous ne souhaitez pas attendre, cliquez sur le bouton ci-dessous</p>
	<a href="/gouvlad/annuaire/connexion">Retour à la connexion</a>
	<br />
	
<jsp:include page="pied_xhtml.jsp" />