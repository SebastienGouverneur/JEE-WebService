<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Afficher une personne" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
<h1>Annuaire</h1>
<jsp:include page="menu.jsp" />
<h2>Détail d'identité</h2>
<c:choose>
<c:when test="${person.id < 0}">
Erreur: personne introuvable.
</c:when>
<c:otherwise>
<table>
<tr><td>Nom</td><td><c:out value="${person.nom}" /></td></tr>
<tr><td>Prenom</td><td><c:out value="${person.prenom}" /></td></tr>
<tr><td>Email</td><td><c:out value="${person.email}" /></td></tr>
<tr><td>Site web</td><td><a target="_blank" href="<c:out value='${person.siteweb}' />"><c:out value="${person.siteweb}" /></a></td></tr>
<tr><td>Groupe</td><td><c:out value="${person.groupe}" /></td></tr>
</table>

</c:otherwise>
</c:choose>

<jsp:include page="pied_xhtml.jsp" />
