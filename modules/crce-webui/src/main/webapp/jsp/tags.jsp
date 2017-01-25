<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Tags list" />
	<jsp:param name="tags" value="true" />
</jsp:include>
    
  	<div id="telo">
   	
  	<form action="resource?link=tags" method="post" target="_self">
	<c:choose>
		<c:when test="${sessionScope.showStoreTag == 'yes'}"> 
	  		Store: <input type="checkbox" checked name="showStoreTag" value="yes">
	  	</c:when>
		<c:otherwise> 
	  		Store: <input type="checkbox" name="showStoreTag" value="yes">
	  	</c:otherwise>
  	</c:choose>
  	
  	<c:choose>
		<c:when test="${sessionScope.showBufferTag == 'yes'}"> 
  			Buffer: <input type="checkbox" checked name="showBufferTag" value="yes" >
  		</c:when>
		<c:otherwise> 
  			Buffer: <input type="checkbox" name="showBufferTag" value="yes" >
  		</c:otherwise>
  	</c:choose>		
	
	<input type="submit" name="showTagSubmit" value="Show Tags">
	</form>
  	
	<div class="vsechnykategorie">
		<c:forEach items="${categoryList}" var="category">
	  		<span class="kategorie">
	  			<a class="" href="resource?link=tags&tag=${category.name}">
					<span class="jmeno">${category.name}</span>
					<span class="pocet">${category.count}</span>
	  			</a>
	  		</span>			
		</c:forEach>
	</div>
	
	
	<div class="komponenty">
	<c:forEach items="${resources}" var="resource">
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
	  				<div class="konec"></div>
	  			</div>
	  			<div class="informace">
	  				<div class="polozka"><strong>Properties: </strong> 
	  					<ul>
	  						<li><strong>Id:</strong> ${resource.id}</li>
	  						<li><strong>Symbolic name:</strong> ${resource.symbolicName}</li>
	  						<li><strong>Size:</strong> ${resource.size}</li>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Categories: </strong> 
	  					<ul>
	  						<c:forEach items="${resource.categories}" var="category">
	  							<li>${category}</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Capabilities: </strong> 
	  					<ul>
	  						<c:forEach items="${resource.capabilities}" var="capability" varStatus="capabilityId">
	  							<li>
	  								${capability.name} 
	  								<table class="poskytuje">
		  								<c:forEach items="${capability.properties}" var="property">
		  									<tr>
		  										<td class="jmeno">${property.name}</td>
		  										<td class="typ">${property.type}</td>
		  										<td class="hodnota">${property.value}</td>
		  									</tr>
		  								</c:forEach>
	  								</table>
	  							</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  				<div class="polozka"><strong>Requirements: </strong> 
						<table class="vyzaduje">
	  						<tr><th>Name</th><th>Filter</th><th>Multiple</th><th>Optional</th><th>Extend</th></tr>
	  						<tr><th colspan="5">Comment</th></tr>
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
  		</div>


  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />