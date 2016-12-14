<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="entete_xhtml.jsp">
        <jsp:param name="title" value="Annuaire"/>
</jsp:include>
<h1>Annuaire</h1>  
<jsp:include page="menu.jsp" />
<h2>Liste des groupes</h2>
<table class="tableListing">
<c:forEach var="group" items="${groupList}">
<tr><td>${group.nomGroupe}</td></tr>
</c:forEach>
</table>
<jsp:include page="pied_xhtml.jsp" />
