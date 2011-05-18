<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Requirements form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Requirements form</h2>
		
		<h3><Strong>Resource:</Strong> ${resource.presentationName} - ${resource.version}</h3>
		
		<p>
			<a class="edit-addnew" href="edit?type=addRequirement&uri=${resource.uri}"><!-- <img src="graphic/add.png" alt="add new requirement" title="add new requirement" /> -->[add new requirement]</a><br />
      	</p>
		
		<form action="#" method="post">
			<input type="hidden" name="uri" value="${uri}" />
			<input type="hidden" name="form" value="requirements" />
			
		<table class="vyzaduje formy">
	  		<tr><th>Name</th><th>Filter</th><th>Multiple</th><th>Optional</th><th>Extend</th></tr>
	  		<tr><th colspan="5"><i>Comment</i></th></tr><br />
	  		<c:forEach items="${resource.requirements}" var="requirement" varStatus="counter">
		  		<tr>
		  			<td class="jmeno"><input class="text" type="text" name="name_${counter.count}" value="${requirement.name}" /></td>
		  			<td class="filter"><input class="text" type="text" name="filter_${counter.count}" value="${requirement.filter}" /></td>
		  			<td><input type="checkbox" name="multiple_${counter.count}" <c:if test="${requirement.multiple}">checked="checked"</c:if> /></td>
		  			<td><input type="checkbox" name="optional_${counter.count}" <c:if test="${requirement.optional}">checked="checked"</c:if> /></td>
		  			<td><input type="checkbox" name="extend_${counter.count}" <c:if test="${requirement.extend}">checked="checked"</c:if> /></td>
		  		</tr>
		  		<tr><td colspan="5" class="komentar"><input type="text" name="comment_${counter.count}" value='${requirement.comment}' /></td></tr>
	  		</c:forEach>
	  		<tr><td colspan="5"><input class="tlacitko" type="submit" value="Save requirements" /></td></tr>
	  	</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />