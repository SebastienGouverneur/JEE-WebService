<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="entete_xhtml.jsp">
        <jsp:param name="title" value="Afficher une personne"/>
</jsp:include>
Annuaire - Détail d'identité<br />
<jsp:include page="menu.jsp" />
<c:choose>
<c:when test="${person.id < 0}">
Erreur: personne introuvable.
</c:when>
<c:otherwise>
<table class="tableListing">
<tr><td>Nom</td><td>${person.nom}</td></tr>
<tr><td>Prenom</td><td>${person.prenom}</td></tr>
<tr><td>Email</td><td>${person.email}</td></tr>
<tr><td>Site web</td><td>${person.siteweb}</td></tr>
<tr><td>Groupe</td><td>${person.groupe}</td></tr>
</table>

</c:otherwise>
</c:choose>

<jsp:include page="pied_xhtml.jsp" />
