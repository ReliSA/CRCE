<%@ page import="cz.zcu.kiv.crce.webui.internal.VersionInfo" %>
</div>
<% VersionInfo versionInfo = VersionInfo.getVersionInfo(getServletContext()); %>

<div id="paticka">CRCE version <%= versionInfo.getProductVersion() %> build rev. <%= versionInfo.getBuildRevision() %>
    <br/> &copy; 2011-2015 University of West Bohemia, Department of Computer Science --
<a href="http://relisa.kiv.zcu.cz/">ReliSA research group</a></div>


	</body>
</html>
