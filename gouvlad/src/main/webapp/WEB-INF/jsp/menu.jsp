<%String searchText = request.getParameter("searchText"); %>

<form method="get" action="/gouvlad/annuaire/afficherPersonne/<%=searchText%>">
<div id="menu">
	<a href="/gouvlad/annuaire/listePersonnes">Liste des personnes</a> - <a
		href="/gouvlad/annuaire/listeGroupes">Liste des groupes</a> - <a
		href="/gouvlad/annuaire/editerProfil">Editer mon profil</a> - <a
		href="/gouvlad/annuaire/deconnexion">Déconnexion</a> - <a>
		<input class="searchText" name="searchText" type="text" placeholder="Rechercher..."><input class="searchButton" type="submit">
	</a>
</div>
</form>