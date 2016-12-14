<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Sign Up" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
<form method="post" action="/gouvlad/annuaire/editerProfil">
	<p>Edition du profil</p>
	<br />
	<jsp:include page="menu.jsp" />
	
	<br />
	<c:if test="${not empty erreur}">
	${erreur.messageError}
	</c:if>
	<c:if test="${not empty success}">
	Les modifications ont bien été enregistrées.
	</c:if>

	<table class="tableprofil">
		<tr>
			<td>Nom</td>
			
			<td><input type="text" name="lastname" value="${profilinfo.nom}" /></td>
		</tr>
		<tr>
			<td>Prénom</td>
			<td><input type="text" name="firstname" value="${profilinfo.prenom}"/></td>
		</tr>
		<tr>
			<td>Adresse e-mail</td>
			<td><input type="text" name="email" value="${profilinfo.email}" /></td>
		</tr>
		<tr>
			<td>Site web</td>
			<td><input type="url" name="website" value="${profilinfo.siteweb}"/></td>
		</tr>
		<tr>
			<td>Date de naissance</td>
			<td>
				<input type="text" pattern="\d{1,2}/\d{1,2}/\d{4}" class="datepicker" name="birthdate" value="${profilinfo.dateNaissance}" placeholder="exemple : 28/09/1991" />
			</td>
		</tr>
		<tr>
			<td>Changer de mot de passe*</td>
			<td><input type="password" name="newpassword" /></td>
		</tr>
		<tr>
			<td>Confirmez votre mot de passe*</td>
			<td><input type="password" name="confirmpassword" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Modifier" /></td>
		</tr>
		
	</table>
</form>
* Laisser ces champs vides pour ne pas les modifier.
<jsp:include page="pied_xhtml.jsp" />