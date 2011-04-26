<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Requirement form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Requirement form - ${presentationName} ${version}</h2>
		
		<form action="#" method="post">
			<input type="hidden" name="presentationName" value="${presentationName}" />
			<input type="hidden" name="version" value="${version}" />
			<input type="hidden" name="requirementId" value="${reguirementId}" />
		
		<table class="formular">
			<tr>
		  		<th>Name:</th>
		  		<td><input class="text" type="text" name="name" value="${requirement.name}" /></td>
		  		<td class="chyba">${nameError}</td>
		  	</tr>
		  	<tr>
		  		<th>Filter:</th>
		  		<td><textarea class="text" name="filter">${requirement.filter}</textarea></td>
				<td class="chyba">${filterError}</td>		
			</tr>
			<tr>
		  		<th>Comment:</th>
		  		<td><textarea class="text" name="comment">${requirement.comment}</textarea></td>
				<td class="chyba">${commentError}</td>		
			</tr>
			<tr>  				
		  		<th>Multiple:</th>
		  		<td><input type="checkbox" name="multiple" <c:if test="${requirement.multiple}">checked="checked"</c:if> /></td>
		  		<td class="chyba">${multipleError}</td>
		  	</tr>
		  	<tr>
		  		<th>Optional:</th>	
		  		<td><input type="checkbox" name="optional" <c:if test="${requirement.optional}">checked="checked"</c:if> /></td>
		  		<td class="chyba">${optionalError}</td>
		  	</tr>
		  	<tr>
		  		<th>Extend:</th>
		  		<td><input type="checkbox" name="extend" <c:if test="${requirement.extend}">checked="checked"</c:if> /></td>
	  			<td class="chyba">${extendError}</td>
	  		</tr>
	  		<tr>
	  			<td colspan="5"><input class="tlacitko" type="submit" value="Save requirement" /></td>
	  		</tr>
	  		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />