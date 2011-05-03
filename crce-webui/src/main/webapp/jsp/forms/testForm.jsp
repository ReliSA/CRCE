<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Test form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Test form</h2>
		
		<c:choose>
			<c:when test="${not empty resources}">
				<h3>Resources</h3>
				<ul>
				<c:forEach items="${resources}" var="resource">
					<li>${resource.symbolicName}</li>
			  	</c:forEach>
			  	</ul>
	  		</c:when>
	  		<c:when test="${empty resources}">
	  			<h3>No resources selected!</h3>
	  		</c:when>
	  	</c:choose>
		
		<c:choose>
			<c:when test="${not empty tests}">
				<h3>Test plugins</h3>
				<form action="#" method="post">
				<table class="formular">	  		
					<c:forEach items="${tests}" var="test">
						<tr>
							<td><input type="radio" name="test" value="${test.pluginId}" /></td>
							<td>${test.pluginId}</td>
						</tr>
			  		</c:forEach>
				</table>
				
				<h3>Test parameters: </h3>
				<table id="options" class="formular">
					<tr>
						<th>Name: </th>
						<td><input type="text" name="name_1" /></td>
						<th>Value: </th>
						<td><input class="text" type="text" name="value_1" /></td>
					</tr>				
				</table>

				<p>
					<a href="#" onclick="plus(); return false;" title="Add parameter"><img src="graphic/add.png" alt="add" /> Add parameter</a>
					<a href="#" onclick="minus(); return false;" title="Remove parameter"><img src="graphic/del.png" alt="delete" /> Remove parameter</a>
				</p> 
				
				<p>
					<input class="tlacitko" type="submit" value="RUN TEST" />
				</p>
				
				</form>
	  		</c:when>
	  		<c:when test="${empty tests}">
	  			<h3>No test plugins enabled!</h3>
	  		</c:when>
  		</c:choose>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />