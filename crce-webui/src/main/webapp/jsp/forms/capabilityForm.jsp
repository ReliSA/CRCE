<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Capability form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Capability form - ${resource.presentationName} ${resource.version}</h2>
		
		<form action="#" method="post">
			<input type="hidden" name="uri" value="${resource.uri}" />
		
		<table class="formular">	  		
			<tr>
				<th>Capability name:</th>
				<td><input type="text" name="capability" value="${capability}" /></td>
				<td class="chyba">${capabilityError}</td>
			</tr>
			<tr>
				<td colspan="2"><input class="tlacitko" type="submit" value="Save capability" /></td>
			</tr>
		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />