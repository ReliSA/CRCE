<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Component form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Component form</h2>
		
		<form action="#" method="post">
		<table>
	        <tr>
	        	<th>Presentation name:</th>
	        	<td><input class="text" type="text" name="presentationName" value="${component.presentationName}" /></td>
	        	<td class="chyba">${presentationNameError}</td>
	        </tr>
	        <tr>
	        	<th>Version:</th>
	        	<td><input class="text" type="text" name="version" value="${component.version}" /></td>
	        	<td class="chyba">${versionError}</td>
	        </tr>
	        <tr>
	        	<th>Id:</th>
	        	<td><input class="text" type="text" name="id" value="${component.id}" /></td>
	        	<td class="chyba">${idError}</td>
	        </tr>
	        <tr>
	        	<th>Symbolic name:</th>
	        	<td><input class="text" type="text" name="symbolicName" value="${component.symbolicName}" /></td>
	        	<td class="chyba">${symbolicNameError}</td>
	        </tr>
	        <tr>
	        	<th>URI:</th>
	        	<td><input class="text" type="text" name="uri" value="${component.uri}" /></td>
	        	<td class="chyba">${uriError}</td>
	        </tr>
	        <tr>
	        	<th>Relative URI:</th>
	        	<td><input class="text" type="text" name="relativeUri" value="${component.relativeUri}" /></td>
	        	<td class="chyba">${relativeUriError}</td>
	        </tr>
	        <tr>
	        	<td colspan="2"><input class="tlacitko" type="submit" value="Save values" /></td>
	        </tr>
		</table>
		</form>

  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />