<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Category form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Category form - ${presentationName} ${version}</h2>
		
		<form action="#" method="post">
			<input type="hidden" name="presentationName" value="${presentationName}" />
			<input type="hidden" name="version" value="${version}" />
		
		<table class="formular">	  		
			<tr>
				<th>Category name:</th>
				<td><input type="text" name="category" value="${category}" /></td>
				<td class="chyba">${categoryError}</td>
			</tr>
			<tr>
				<td colspan="2"><input class="tlacitko" type="submit" value="Save category" /></td>
			</tr>
		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />