<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Plugins list" />
	<jsp:param name="plugins" value="true" />
</jsp:include>
    
  	<div id="telo">
  	       
		<c:forEach items="${plugins}" var="plugin">
			<div class="komponenta">
	  			<div class="nadpis">
	  				<a class="popis" href="#">${plugin.pluginId}</a>
	  				<div class="nabidka">
	            <a href="#"><img src="graphic/save.png" alt="download" title="Download plugin ${plugin.pluginDescription}" /></a>
	            <a href="form.html"><img src="graphic/edit.png" alt="edit" title="Edit plugin ${plugin.pluginDescription}" /></a>
	            <a href="#"><img src="graphic/del.png" alt="delete" title="Delete plugin ${plugin.pluginDescription}"/></a>
	          </div>
	  				<div class="konec"></div>
	  			</div>
	  			<div class="informace">
	  				<div class="polozka"><strong>Description:</strong> ${plugin.pluginDescription}</div>	
	  				<div class="polozka"><strong>Priority:</strong> ${plugin.pluginPriority}</div>
	  				<div class="polozka"><strong>Version:</strong> ${plugin.pluginVersion}</div>
	  				<div class="polozka"><strong>Keywords:</strong> 
	  					<ul>
	  						<c:forEach items="${plugin.keywords}" var="keyword">
	  							<li>${keyword}</li>
	  						</c:forEach>
	  					</ul>
	  				</div>
	  			</div>
	  		</div>
  		</c:forEach>
  		
  		<div id="animacni_odkazy">
			<a class="rozbalit" href="#">Show all</a> - <a class="sbalit" href="#">Hide all</a>
  		</div>
  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />