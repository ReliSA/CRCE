<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Property form" />
</jsp:include>
    
	<div id="telo">
  	
		<h2>Property form</h2>
      
		<form action="#" method="post">
		<table>
	        <tr>
	        	<th>Name:</th>
	        	<td><input class="text" type="text" name="name" value="${property.name}" /></td>
	        	<td class="chyba">${nameError}</td>
	        </tr>
	        <tr>
	        	<th>Type:</th>
	        	<td><input class="text" type="text" name="type" value="${property.type}" /></td>
	        	<td class="chyba">${typeError}</td>
	        </tr>
	        <tr>
	        	<th>Value:</th>
	        	<td><input class="text" type="text" name="value" value="${property.value}" /></td>
	        	<td class="chyba">${valueError}</td>
	        </tr>
	        <tr>
	        	<td colspan="2"><input class="tlacitko" type="submit" value="Save values" /></td>
	        </tr>
		</table>
		</form>
  		
  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />