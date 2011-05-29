<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../include/header.jsp" flush="true">
	<jsp:param name="title" value="Plugin form" />
</jsp:include>
    
  	<div id="telo">
  	
		<h2>Plugin form</h2>
		
		<h3>Plugin: ${plugin.pluginId}</h3>
		
		<form action="#" method="post">
			<input type="hidden" name="id" value="${plugin.pluginId}" />
			<input type="hidden" name="form" value="plugin" />
		<table class="formular">	  		
			<tr>
				<th>Priority:</th>
				<td><input class="text" type="text" name="priority" value="${plugin.pluginPriority}" /></td>
				<td class="chyba">${priorityError}</td>
			</tr>
			<tr>
				<th>Keywords:</th>
				<td><input class="text" type="text" name="keywords" value="${plugin.pluginVersion}" /></td>
				<td class="chyba">${keywordsError}</td>
			</tr>
			<tr>
				<td colspan="2"><input class="tlacitko" type="submit" value="Save plugin" /></td>
			</tr>
		</table>
		</form>
  		
  	</div>
  	
<jsp:include page="../include/footer.jsp" flush="true" />