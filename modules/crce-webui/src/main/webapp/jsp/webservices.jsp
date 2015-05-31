<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
	<jsp:param name="title" value="Webservices IDL list" />
	<jsp:param name="webservices" value="true" />
</jsp:include>
  	<div id="telo">
  	  
		<form method="post" action="parse_idl" accept-charset="utf-8">
		<div class="upload">
			<table class="upload">
				<tr>
					<td><input class="text" type="text" name="uri"/></td>
					<td><div class="tlac"><input class="tlacitko" type="submit" value="Parse IDL"/></div></td>
				</tr>
			</table>
		</div>
		</form>
  	   	
  		<div class="vycisteni"></div>
  	</div>
  
<jsp:include page="include/footer.jsp" flush="true" />