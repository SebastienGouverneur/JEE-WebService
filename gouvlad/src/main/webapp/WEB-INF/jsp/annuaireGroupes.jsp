<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Liste des groupes" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
<h1>Annuaire</h1>  
<jsp:include page="menu.jsp" />
<h2>Liste des groupes</h2>
<table>
<c:forEach var="group" items="${groupList}">
<tr><td>${group.nomGroupe}</td></tr>
</c:forEach>
</table>
<jsp:include page="pied_xhtml.jsp" />
