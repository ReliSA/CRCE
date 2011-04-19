<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Requirements form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Properties form - ${presentationName} ${version}</h2>
		
		<a href="#"><img src="graphic/add.png" alt="add new property" title="add new property" />Add new property</a><br />
      
		<form action="#" method="post">
			<input type="hidden" name="presentationName" value="${presentationName}" />
			<input type="hidden" name="version" value="${version}" />
			<input type="hidden" name="capability" value="${capability}" />
			<table class="poskytuje">
				<tr><td>Name</td><td>Type</td><td>Value</td></tr>
				<c:forEach items="${properties}" var="property">
		  			<tr>
		  				<td class="jmeno"><input type="text" value="${property.name}" /></td>
		  				<td class="typ"><input type="text" value="${property.type}" /></td>
		  				<td class="hodnota"><input type="text" value="${property.value}" /></td>
		  			</tr>
		  		</c:forEach>
	  		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="include/footer.jsp" flush="true" />