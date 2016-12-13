<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="entete_xhtml.jsp">
        <jsp:param name="title" value="Annuaire"/>
</jsp:include>
Annuaire - Liste des personnes inscrites<br />
<jsp:include page="menu.jsp" />
<table class="tableListing">
<tr><td>Nom</td><td>Prénom</td><td>Adresse e-mail</td><td>Site web</td><td>Groupe</td></tr>
<c:forEach var="person" items="${personList}">
<tr><td><a href="/gouvlad/annuaire/afficherPersonne/${person.id}">${person.nom}</a></td><td>${person.prenom}</td><td>${person.email}</td><td>${person.siteweb}</td><td>${person.groupe.nomGroupe}</td></tr>
</c:forEach>
</table>
<jsp:include page="pied_xhtml.jsp" />
