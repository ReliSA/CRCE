<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Capability form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Add new capability</h2>
		
		<h3>${resource.presentationName} - ${resource.version}</h3>
		
		<form action="#" method="post">
			<input type="hidden" name="uri" value="${resource.uri}" />
			<input type="hidden" name="form" value="capability" />
		<table class="formular">	  		
			<tr>
				<th>Capability name:</th>
				<td><input class="text" type="text" name="capability" value="${capability}" /></td>
				<td class="chyba">${capabilityError}</td>
			</tr>
			<tr>
				<td colspan="2"><input class="tlacitko" type="submit" value="Save capability" /></td>
			</tr>
		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />