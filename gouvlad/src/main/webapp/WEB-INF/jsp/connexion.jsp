<%@taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Connexion" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
<form method="post" action="/gouvlad/annuaire/connexion">
<h1>Annuaire</h1>
<p>Vous devez �tre connect� pour acc�der � l'annuaire.</p><br /><br />
<h2>Connexion</h2>
<c:if test="${not empty erreur}" >
Le couple adresse e-mail / mot de passe est incorrect.<br />
</c:if>
<table class="noBorder">
<tr><td>Adresse e-mail</td><td><input type="text" name="email"  /></td></tr><br />
<tr><td>Mot de passe</td><td><input type="password" name="password"  /></td></tr><br />
<tr><td></td><td><input type="submit" value="Connexion" /></td></tr>
</table>
</form>
	<a href="/gouvlad/annuaire/inscription">Inscription</a>

<jsp:include page="pied_xhtml.jsp" />