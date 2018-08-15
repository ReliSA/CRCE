<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Categories form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Existing categories</h2>
		
		<h3><strong>Resource:</strong> ${resource.presentationName} - ${resource.version}</h3>

		<ul class="categorie-edit-ul">
		  	<c:forEach items="${resource.categories}" var="category">
				<li>${category} <a class="edit-addnew" href="edit?type=deleteCategory&uri=${resource.uri}&category=${category}"><!-- <img src="graphic/del.png" alt="delete category" title="delete category" /> -->[remove]</a></li>
		  	</c:forEach>
		</ul>
		
		<p>		
			<a class="edit-addnew" href="edit?type=addCategory&uri=${resource.uri}"><!-- <img src="graphic/add.png" alt="add new category" title="add new category" /> -->[add new category]</a><br />
      	</p>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />