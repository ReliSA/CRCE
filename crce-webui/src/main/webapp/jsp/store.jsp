<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Store list" />
	<jsp:param name="store" value="true" />
</jsp:include>
    
  	<div id="telo">
  	
		<form method="post" action="#">
  	       
		<c:forEach items="${store}" var="resource">
	  		<div class="komponenta">
	  			<div class="nadpis">
	  				<a class="popis" href="#">
	  					<span class="sName">${resource.symbolicName}</span> 
	  					<span class="version">${resource.version}</span> 
	  					<span class="pName">${resource.presentationName}</span>
	  					<span class="category"><c:forEach items="${resource.categories}" var="category">
	  							${category}
	  						</c:forEach></span>
	  				</a>
	  				<div class="nabidka">
			            <a href="download?uri=${resource.uri}"><img src="graphic/save.png" alt="download" title="Download component ${resource.presentationName} ${resource.version}" /></a>
			            <a href="edit?uri=${resource.uri}&link=store"><img src="graphic/del.png" alt="delete" title="Delete component ${resource.presentationName} ${resource.version}"/></a>
			            <a href="#"><img src="graphic/set.png" alt="check" title="Check component ${resource.presentationName} ${resource.version} compatibility"/></a>
			          	<input type="checkbox" name="${resource.presentationName}_${resource.version}" />
	          		</div>
	  				<div class="konec"></div>
	  			</div>
	  			<div class="informace">
	  				<div class="polozka"><strong>Properties: </strong> <a href="#" title="add new property"><img src="graphic/add.png" alt="add new property" title="add new property"/> Add new</a> 
	  					<ul>
	  						<li><strong>Id:</strong> ${resource.id} <a href="#" title="edit id"><img src="graphic/edit.png" alt="edit id" title="edit id" /></a></li>
	  						<li><strong>Symbolic name:</strong> ${resource.symbolicName} <a href="#" title="edit symbolic name"><img src="graphic/edit.png" alt="edit symbolic name" title="edit symbolic name" /></a></li>
	  						<li><strong>Size:</strong> ${resource.size} <a href="#" title="edit size"><img src="graphic/edit.png" alt="edit size" title="edit size" /></a></li>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Categories: </strong> <a href="#" title="edit categories"><img src="graphic/edit.png" alt="edit categories" title="edit categories" /> Edit</a> 
	  					<ul>
	  						<c:forEach items="${resource.categories}" var="category">
	  							<li>${category}</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Capabilities: </strong> <a href="#"><img src="graphic/add.png" alt="add capability" title="add capability" /> Add new</a> 
	  					<ul>
	  						<c:forEach items="${resource.capabilities}" var="capability">
	  							<li>
	  								${capability.name} <a href="#" title="edit capability"><img src="graphic/edit.png" alt="edit capability" title="edit capability" /></a>
	  								<ul>
		  								<c:forEach items="${capability.properties}" var="property">
		  									<li>${property.name} (${property.type}) - ${property.value} <a href="#" title="edit property"><img src="graphic/edit.png" alt="edit property" title="edit property" /></a></li>
		  								</c:forEach>
	  								</ul>
	  							</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Requirements: </strong> <a href="#"><img src="graphic/add.png" alt="add new requrements" title="add new requirements" /> Add new</a> 
	  					<ul>
	  						<c:forEach items="${resource.requirements}" var="requirement">
	  							<li>${requirement.name} - ${requirement.filter} <a href="#"><img src="graphic/edit.png" alt="edit" title="edit requirement" /></a></li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  			</div>
	  		</div>
		</c:forEach>
  		
	  	<div id="animacni_odkazy">
			<a class="rozbalit" href="#">Show all</a> - <a class="sbalit" href="#">Hide all</a>
		</div>
  		
  			<input class="tlacitko" type="submit" value="EXECUTE" />
  		</form>
  	
  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />