<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Plugins list" />
	<jsp:param name="plugins" value="true" />
</jsp:include>
    
  	<div id="telo">
  	       
		<c:forEach items="${plugins}" var="plugin">
			<div class="komponenta">
	  			<div class="nadpis">
	  				<div class="popis">
	  				<a class="" href="#"><span class="pName">${plugin.pluginId}</span></a>
	  				</div>
	  				<div class="nabidka">
			            <a href="#"><img src="graphic/edit.png" alt="edit" title="Edit plugin ${plugin.pluginId}" /></a>
	          		</div>
	  				<div class="konec"></div>
	  			</div>
	  			<div class="informace">
	  				<div class="polozka"><strong>Description:</strong> ${plugin.pluginDescription}</div>	
	  				<div class="polozka"><strong>Priority:</strong> ${plugin.pluginPriority} <a class="edit" href="edit?type=ZADAT&uri=${resource.uri}" title="edit plugin priority">[edit]</a></div>
	  				<div class="polozka"><strong>Version:</strong> ${plugin.pluginVersion} <a class="edit" href="edit?type=ZADAT&uri=${resource.uri}" title="edit plugin version">[edit]</a></div>
	  				<div class="polozka"><strong>Keywords:</strong> 
	  					<ul>
	  						<c:forEach items="${plugin.pluginKeywords}" var="keyword">
	  							<li>${keyword}</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  			</div>
	  		</div>
  		</c:forEach>
  		
  		<c:if test="${empty plugins}">
			<div class="komponenta">
				<div class="nadpis"><strong>No plugins.</strong></div>
			</div>
		</c:if>
  		
  		<c:if test="${not empty plugins}">
  		<div id="animacni_odkazy">
			<a class="rozbalit" href="#">Show all</a> - <a class="sbalit" href="#">Hide all</a>
  		</div>
  		</c:if>
  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />