<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Category form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Add new category</h2>
		
		<h3>${resource.presentationName} - ${resource.version}</h3>
		
		<form name="addCategory" action="#" method="post">
			<input type="hidden" name="form" value="addCategory" />
			<input type="hidden" name="uri" value="${resource.uri}" />
		
		<table class="formular">	  		
			<tr>
				<th>Category name:</th>
				<td><input type="text" name="category" value="${category}" /></td>
				<td class="chyba">${categoryError}</td>
			</tr>
			<tr>
				<td><input class="tlacitko" type="submit" value="Save category" /></td>
			</tr>
		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />