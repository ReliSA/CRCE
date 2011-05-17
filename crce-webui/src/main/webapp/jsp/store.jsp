<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Store list" />
	<jsp:param name="store" value="true" />
</jsp:include>
    
  	<div id="telo">
  	
		<form class="execute_commit" method="post" action="test">
  	       
		<c:forEach items="${store}" var="resource">
	  		<div class="komponenta 
	  			<c:catch var="exception">${resource.satisfied}</c:catch>
				<c:if test="${empty exception}">
					<c:choose>
						<c:when test="${resource.satisfied == true}">
							uspech1		
						</c:when>
						<c:when test="${resource.satisfied == false}">
							neuspech1
						</c:when>
					</c:choose>
				</c:if>
			">
	  			<div class="nadpis">
	  				<div class="popis">
	  				<a class="" href="#">
	  					<span class="pName">${resource.presentationName}</span>
	  					<span class="sName">(${resource.symbolicName})</span> 
	  					<span class="version">ver: </span><span class="version_obsah">${resource.version}</span> 
	  					<span class="category">cats: </span><span class="category_obsah">
	  						<c:forEach items="${resource.categories}" var="category">
	  							${category}
	  						</c:forEach>
	  					</span>
	  				</a>
	  				</div>
	  				<div class="nabidka">
			            <a href="download?uri=${resource.uri}"><img src="graphic/save.png" alt="download" title="Download component ${resource.presentationName} ${resource.version}" /></a>
			            <a href="edit?type=deleteCompoment&uri=${resource.uri}&link=store"><img src="graphic/del.png" alt="delete" title="Delete component ${resource.presentationName} ${resource.version}"/></a>
			            <a href="#"><img src="graphic/check.png" alt="check" title="Check component ${resource.presentationName} ${resource.version} compatibility"/></a>
			          	<input type="checkbox" name="check" value="${resource.uri}" />
	          		</div>
	  				<div class="konec"></div>
	  			</div>
	  			<div class="informace">
	  				<div class="polozka"><strong>Properties: </strong> <a href="#" title="Edit properties" class="edit"><!-- <img src="graphic/edit.png" alt="edit properties" title="edit properties"/> -->[edit]</a> 
	  					<ul>
	  						<li><strong>Id:</strong> ${resource.id}</li>
	  						<li><strong>Symbolic name:</strong> ${resource.symbolicName}</li>
	  						<li><strong>Size:</strong> ${resource.size}</li>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Categories: </strong> <a class="edit" href="edit?type=category&uri=${resource.uri}" title="edit categories"><!-- <img src="graphic/edit.png" alt="edit categories" title="edit categories" /> -->[edit]</a> 
	  					<ul class="inline">
	  						<c:forEach items="${resource.categories}" var="category">
	  							<li>${category}</li>
	  						</c:forEach>
	  					</ul>
	  				<div class="vycisteni"></div>
	  				</div>
	  				<div class="polozka"><strong>Capabilities: </strong> <a class="edit" href="edit?type=addCapability&uri=${resource.uri}"><!-- <img src="graphic/add.png" alt="add capability" title="add capability" />--> [add new]</a> 
	  					<ul>
	  						<c:forEach items="${resource.capabilities}" var="capability" varStatus="capabilityId">
	  							<li>
	  								${capability.name} <a href="edit?type=capability&uri=${resource.uri}&capabilityId=${capabilityId.count}" title="edit capability" class="edit"><!-- <img src="graphic/edit.png" alt="edit capability" title="edit capability" />--> [edit]</a>
	  								<table class="poskytuje">
		  								<c:forEach items="${capability.properties}" var="property">
		  									<tr><td class="jmeno">${property.name}</td><td class="typ">${property.type}</td><td class="hodnota">${property.value}</td></tr>
		  								</c:forEach>
	  								</table>
	  							</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Requirements: </strong> <a href="edit?type=requirement&uri=${resource.uri}" class="edit"><!-- <img src="graphic/edit.png" alt="edit requrements" title="edit requirements" /> --> [edit]</a> 
						<table class="vyzaduje">
	  						<tr><th>Name</th><th>Filter</th><th>Multiple</th><th>Optional</th><th>Extend</th></tr>
	  						<tr><th colspan="5"><i>Comment</i></th></tr>
	  						<c:forEach items="${resource.requirements}" var="requirement">
	  							<tr 
	  								<c:catch var="exception">${requirement.satisfied}</c:catch>
		  							<c:if test="${empty exception}">
										<c:choose>
											<c:when test="${requirement.satisfied == true}">
												class="uspech"		
											</c:when>
											<c:when test="${requirement.satisfied == false}">
												class="neuspech"
											</c:when>
										</c:choose>
									</c:if>
	  							>
	  								<td>${requirement.name}</td>
	  								<td class="filter">${requirement.filter}</td>
	  								<td>${requirement.multiple}</td>
	  								<td>${requirement.optional}</td>
	  								<td>${requirement.extend}</td>
	  							</tr>
	  							<tr>
	  								<td colspan="5" class="komentar">${requirement.comment}</td>
	  							</tr>
	  						</c:forEach>
	  					</table>
	  				</div>
	  			</div>
	  		</div>
		</c:forEach>
		
		<c:if test="${empty store}">
			<div class="komponenta">
				<div class="nadpis"><strong>No resources in store.</strong></div>
			</div>
		</c:if>
  		
  		<c:if test="${not empty store}">
		  	<div id="animacni_odkazy">
				<a class="rozbalit" href="#">Show all</a> - <a class="sbalit" href="#">Hide all</a>
			</div>
		</c:if>
  		
  			<c:if test="${not empty store}">
  				<div class="tlac">
  				<input class="tlacitko" type="submit" value="execute tests" />
  				</div>
  			</c:if>
  		</form>
  		<c:if test="${not empty store}">
  		<form name="compataibility" action="check" method="post">
  		<div class="tlac">
  		<input class="tlacitko" type="submit" value="compatibility check"/>
  		</div>
  		</form>
  		</c:if>
  		<div class="vycisteni"></div>
  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />