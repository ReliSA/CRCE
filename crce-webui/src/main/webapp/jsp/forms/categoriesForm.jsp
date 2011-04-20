<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Categories form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Categories form - ${presentationName} ${version}</h2>
		
		<a href="#"><img src="graphic/add.png" alt="add new category" title="add new category" />Add new category</a><br />
      
	  	<ul>
	  		<c:forEach items="${categories}" var="category">
				<li>${category} <a href="edit?action=deleteCategory&presentationName=${presentationName}&version=${version}&category=${category}"><img src="graphic/del.png" alt="delete category" title="delete category" /></a></li>
	  		</c:forEach>
	  	</ul>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />