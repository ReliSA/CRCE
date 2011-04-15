<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Requirement form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Requirement form</h2>
      
		<form action="#" method="post">
		<table>
	        <tr>
	        	<th>Name:</th>
	        	<td><input class="text" type="text" name="name" value="${requirement.name}" /></td>
	        	<td class="chyba">${nameError}</td>
	        </tr>
	        <tr>
	        	<th>Filter:</th>
	        	<td><input class="text" type="text" name="filter" value="${requirement.filter}" /></td>
	        	<td class="chyba">${filterError}</td>
	        </tr>
	        <tr>
	        	<th>Comment:</th>
	        	<td><textarea class="text" name="comment">${requirement.comment}</textarea></td>
	        	<td class="chyba">${commentError}</td>
	        </tr>
	        <tr>
	        	<td colspan="2"><input class="tlacitko" type="submit" value="save values" /></td>
	        </tr>      
		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="include/footer.jsp" flush="true" />