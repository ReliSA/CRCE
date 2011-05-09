<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Property form" />
</jsp:include>
    
	<div id="telo">
  	
		<h2>Add new property</h2>
		
		<h3>Resource: ${resource.presentationName} - ${resource.version}</h3>
      
		<form action="#" method="post">
			<input type="hidden" name="uri" value="${resource.uri}" />
			<input type="hidden" name="capabilityId" value="${capabilityId}" />
			<input type="hidden" name="form" value="property" />
		
		<table class="formular">
	        <tr>
	        	<th>Name:</th>
	        	<td><input class="text" type="text" name="name" value="" /></td>
	        	<td class="chyba">${nameError}</td>
	        </tr>
	        <tr>
	        	<th>Type:</th>
	        	<td class="typ">
		  			<!--<input class="text" type="text" name="type_${counter.count}" value="${property.type}" />  -->
		  			<select class="text" name="type_${counter.count}">
						<c:forEach items="${types}" var="type">
							<option value="${type}">${type}</option>
							</c:forEach>
					</select>
		  		</td>
	        	<td class="chyba">${typeError}</td>
	        </tr>
	        <tr>
	        	<th>Value:</th>
	        	<td><input class="text" type="text" name="value" value="" /></td>
	        	<td class="chyba">${valueError}</td>
	        </tr>
	        <tr>
	        	<td colspan="2"><input class="tlacitko" type="submit" value="Save property" /></td>
	        </tr>
		</table>
		</form>
  		
  	</div>
  
<jsp:include page="../include/footer.jsp" flush="true" />