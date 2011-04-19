<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Requirements form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Requirements form - ${presentationName} ${version}</h2>
		
		<a href="#"><img src="graphic/add.png" alt="add new requirement" title="add new requirement" />Add new requirement</a><br />
      
		<form action="#" method="post">
			<input type="hidden" name="presentationName" value="${presentationName}" />
			<input type="hidden" name="version" value="${version}" />
			<table class="vyzaduje">
	  			<tr><th>Name</th><th>Filter</th><th>Multiple</th><th>Optional</th><th>Extend</th></tr>
	  			<c:forEach items="${requirements}" var="requirement" varStatus="counter">
		  			<tr>
		  				<td><input class="text" type="text" name="name_${counter.count}" value="${requirement.name}" /></td>
		  				<td class="filter"><input class="text" type="text" name="filter_${counter.count}" value="${requirement.filter}" /></td>
		  				<td><input type="checkbox" name="multiple_${counter.count}" <c:if test="${requirement.multiple}">checked="checked"</c:if> /></td>
		  				<td><input type="checkbox" name="optional_${counter.count}" <c:if test="${requirement.optional}">checked="checked"</c:if> /></td>
		  				<td><input type="checkbox" name="extend_${counter.count}" <c:if test="${requirement.extend}">checked="checked"</c:if> /></td>
		  			</tr>
	  			</c:forEach>
	  			<tr><td colspan="5"><input class="tlacitko" type="submit" value="save values" /></td></tr>
	  		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="include/footer.jsp" flush="true" />