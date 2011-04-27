<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Capabilities form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Capabilities form - ${resource.presentationName} ${resource.version}</h2>
		
		<a href="#"><img src="graphic/add.png" alt="add new property" title="add new property" />Add new property</a><br />
      
		<form action="#" method="post">
			<input type="hidden" name="form" value="capabilities" />
			<input type="hidden" name="presentationName" value="${resource.uri}" />
			<input type="hidden" name="capabilityId" value="${capabilityId}" />
			
			<table class="poskytuje">
				<tr><td>Name</td><td>Type</td><td>Value</td></tr>
				<c:forEach items="${capability.properties}" var="property" varStatus="counter">
		  			<tr>
		  				<td class="jmeno"><input type="text" value="${property.name}_${counter.count}" /></td>
		  				<td class="typ"><input type="text" value="${property.type}_${counter.count}" /></td>
		  				<td class="hodnota"><input type="text" value="${property.value}_${counter.count}" /></td>
		  			</tr>
		  		</c:forEach>
		  		<tr><td colspan="3"><input class="tlacitko" type="submit" value="Save capabilities" /></td></tr>
	  		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />