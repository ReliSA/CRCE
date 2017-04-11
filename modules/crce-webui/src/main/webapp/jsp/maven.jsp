<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
    <jsp:param name="title" value="Maven search"/>
    <jsp:param name="maven" value="true" />
</jsp:include>

<div id="telo">
    <h2>Search for maven artifacts</h2>
    <!-- search by maven coordinates -->
    <form method="post" action="maven-search?by=gav">
        <h3>Search by maven coordinates</h3>
        <label for="aid">Artifact ID:</label>
        <input type="text" id="aid" name="aid"><br>

        <label for="gid">Group ID:</label>
        <input type="text" id="gid" name="gid"><br>

        <label for="ver">Version:</label>
        <input type="text" id="ver" name="ver"><br>

        <input type="submit" value="Search">
    </form>

    <!-- search by package name -->
    <form method="post" action="maven-search?by=pname">
        <h3>Search by package name</h3>
        <label for="pname">Package name:</label>
        <input type="text" id="pname" name="pname"><br>

        Filter:<br>
        <label for="lv">Lowest version</label>
        <input type="radio" id="lv" name="verFilter"><br>
        <label for="nv">Newest version</label>
        <input type="radio" id="nv" name="verFilter"><br>

        <input type="submit" value="Search">
    </form>
</div>

<jsp:include page="include/footer.jsp" flush="true"/>