<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="entete_xhtml.jsp">
        <jsp:param name="title" value="Annuaire"/>
</jsp:include>
Annuaire - Liste des groupes<br />
<jsp:include page="menu.jsp" />
<table class="tableListing">
<tr><td>Nom du groupe</td></tr>
<c:forEach var="group" items="${groupList}">
<tr><td>${group.nomGroupe}</td></tr>
</c:forEach>
</table>
<jsp:include page="pied_xhtml.jsp" />
