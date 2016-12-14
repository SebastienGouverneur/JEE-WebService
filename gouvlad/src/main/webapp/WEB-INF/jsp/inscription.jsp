<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Sign Up" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
	<p>Vous devez être connecté pour accéder à l'annuaire.</p>
	<br />
	<a href="/gouvlad/annuaire/connexion">Retour à la connexion</a>
	<br />
	<c:if test="${not empty signupinfo}">
	${signupinfo.messageError}	
	</c:if>
	<form method="post" action="/gouvlad/annuaire/inscription">
	
	<table class="tableSignup">
		<tr>
			<td>Nom</td>
			
			<td><input type="text" name="firstname" value="${signupinfo.person.nom}" /></td>
		</tr>
		<tr>
			<td>Prénom</td>
			<td><input type="text" name="lastname" value="${signupinfo.person.prenom}"/></td>
		</tr>
		<tr>
			<td>Adresse e-mail</td>
			<td><input type="text" name="email" value="${signupinfo.person.email}" /></td>
		</tr>
		<tr>
			<td>Site web</td>
			<td><input type="url" name="website" value="${signupinfo.person.siteweb}"/></td>
		</tr>
		<tr>
			<td>Date de naissance</td>
			<td>
				<input type="text" pattern="\d{1,2}/\d{1,2}/\d{4}" class="datepicker" name="birthdate" value="${signupinfo.person.dateNaissance}" placeholder="exemple : 28/09/1991" />
			</td>
		</tr>
		<tr>
			<td>Créez un mot de passe</td>
			<td><input type="password" name="createpassword" /></td>
		</tr>
		<tr>
			<td>Confirmez votre mot de passe</td>
			<td><input type="password" name="confirmpassword" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Créer un compte" /></td>
		</tr>
	</table>
</form>
<jsp:include page="pied_xhtml.jsp" />