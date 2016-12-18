<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Liste des personnes" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
<h1>Annuaire</h1>
<jsp:include page="menu.jsp" />
<h2>Liste des personnes inscrites</h2>
<table class="tableListing">
<tr><td><span class="bold">Nom</span></td><td><span class="bold">Prénom</span></td><td><span class="bold">Adresse e-mail</span></td><td><span class="bold">Site web</span></td><td><span class="bold">Groupe</span></td></tr>
<c:forEach var="person" items="${personList}">
<tr class="clickable" onClick="window.location.href='/gouvlad/annuaire/afficherPersonne/${person.id}';"><td><c:out value="${person.nom}" /></td><td><c:out value="${person.prenom}" /></td><td><c:out value="${person.email}" /></td><td>
<c:if test="${person.siteweb != ''}">
<a target="_blank" href="${person.siteweb}"><c:out value="${person.siteweb}" /></a>
</c:if>
</td><td><c:out value="${person.groupe.nomGroupe}" /></td></tr>
</c:forEach>
</table>
<jsp:include page="pied_xhtml.jsp" />
