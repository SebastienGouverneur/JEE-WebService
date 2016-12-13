<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="Sign Up" scope="request" />
<jsp:include page="entete_xhtml.jsp" />
<form method="post" action="/gouvlad/annuaire/signup">
	<p>Vous devez être connecté pour accéder à l'annuaire.</p>
	<br />
	<br />
	<c:if test="${not empty erreur}">
Veuillez remplir tous les champs avant de valider.<br />
	</c:if>
	<table class="tableSignup">
		<tr>
			<td>Nom</td>
			<td><input type="text" name="firstname" /></td>
		</tr>
		<br />
		<tr>
			<td>Prénom</td>
			<td><input type="text" name="lastname" /></td>
		</tr>
		<br />
		<tr>
			<td>Adresse e-mail</td>
			<td><input type="text" name="email" /></td>
		</tr>
		<br />
		<tr>
			<td>Site web</td>
			<td><input type="url" name="website" /></td>
		</tr>
		<br />
		<tr>
			<td>Date de naissance</td>
			<td>
				<input type="text" pattern="\d{1,2}/\d{1,2}/\d{4}" class="datepicker" name="birthdate" value="" placeholder="exemple : 28/09/1991"/>
			</td>
		</tr>
		<br />
		<tr>
			<td>Créez un mot de passe</td>
			<td><input type="password" name="createpassword" /></td>
		</tr>
		<br />
		<tr>
			<td>Confirmez votre mot de passe</td>
			<td><input type="password" name="confirmpassword" /></td>
		</tr>
		<br />
		<tr>
			<td></td>
			<td><input type="submit" value="Create account" /></td>
		</tr>
	</table>
</form>
<jsp:include page="pied_xhtml.jsp" />