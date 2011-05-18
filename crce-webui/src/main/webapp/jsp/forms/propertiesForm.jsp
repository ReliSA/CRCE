<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Resource form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Properties form</h2>
		
		<h3>Resource: ${resource.presentationName} - ${resource.version}</h3>
		
		<form action="#" method="post">
			<input type="hidden" name="uri" value="${resource.uri}" />
			<input type="hidden" name="form" value="editProperties" />
		
		<table class="formular">
	        <!--
	        <tr>
	        	<th>Presentation name:</th>
	        	<td><input class="text" type="text" name="presentationName" value="${resource.presentationName}" /></td>
	        	<td class="chyba">${presentationNameError}</td>
	        </tr>
	        -->
	        <tr>
	        	<th>Symbolic name:</th>
	        	<td><input class="text" type="text" name="symbolicName" value="${resource.symbolicName}" /></td>
	        	<td class="chyba">${symbolicNameError}</td>
	        </tr>
	        
	        <tr>
	        	<th>Version:</th>
	        	<td><input class="text" type="text" name="version" value="${resource.version}" /></td>
	        	<td class="chyba">${versionError}</td>
	        </tr>
	        <!-- 
	        <tr>
	        	<th>Id:</th>
	        	<td><input class="text" type="text" name="id" value="${resource.id}" /></td>
	        	<td class="chyba">${idError}</td>
	        </tr>
	        -->
	        <!-- 
	        <tr>
	        	<th>Size:</th>
	        	<td><input class="text" type="text" name="size" value="${resource.size}" /></td>
	        	<td class="chyba">${sizeError}</td>
	        </tr>
	        -->
	        <tr>
	        	<td colspan="2"><input class="tlacitko" type="submit" value="Save properties" /></td>
	        </tr>
		</table>
		</form>

  	</div>
  
<jsp:include page="../include/footer.jsp" flush="true" />