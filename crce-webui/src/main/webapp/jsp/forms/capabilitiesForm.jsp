<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Capabilities form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Capabilities form</h2>
		
		<h3>${resource.presentationName} - ${resource.version}</h3>
		
		<p>
			<a href="edit?type=addCapabilityProperty&uri=${resource.uri}&capabilityId"><img src="graphic/add.png" alt="add new property" title="add new property" />Add new property</a><br />
		</p>
      
		<form action="#" method="post">
			<input type="hidden" name="form" value="capabilities" />
			<input type="hidden" name="presentationName" value="${resource.uri}" />
			<input type="hidden" name="capabilityId" value="${capabilityId}" />
			
			<table class="poskytuje">
				<tr><th>Name</th><th>Type</th><th>Value</th></tr>
				<c:forEach items="${capability.properties}" var="property" varStatus="counter">
		  			<tr>
		  				<td class="jmeno"><input class="text" type="text" name="name_${counter.count}" value="${property.name}" /></td>
		  				<td class="typ"><input class="text" type="text" name="type_${counter.count}" value="${property.type}" /></td>
		  				<td class="hodnota"><input class="text" type="text" name="value_${counter.count}" value="${property.value}" /></td>
		  			</tr>
		  		</c:forEach>
		  		<tr><td colspan="3"><input class="tlacitko" type="submit" value="Save capabilities" /></td></tr>
	  		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />