<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="entete_xhtml.jsp">
        <jsp:param name="title" value="Annuaire"/>
</jsp:include>
<h1>Annuaire</h1><br />
<jsp:include page="menu.jsp" />
<h2>Liste des personnes inscrites</h2>
<table class="tableListing">
<tr><td><span class="bold">Nom</span></td><td><span class="bold">Prénom</span></td><td><span class="bold">Adresse e-mail</span></td><td><span class="bold">Site web</span></td><td><span class="bold">Groupe</span></td></tr>
<c:forEach var="person" items="${personList}">
<tr><td><a href="/gouvlad/annuaire/afficherPersonne/${person.id}">${person.nom}</a></td><td>${person.prenom}</td><td>${person.email}</td><td>${person.siteweb}</td><td>${person.groupe.nomGroupe}</td></tr>
</c:forEach>
</table>
<jsp:include page="pied_xhtml.jsp" />
