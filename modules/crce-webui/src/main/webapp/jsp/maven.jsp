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
        <table>
            <tr>
                <td>
                    <label for="aid">Artifact ID:</label>
                </td>
                <td>
                    <input type="text" id="aid" name="aid">
                </td>
            </tr>

            <tr>
                <td>
                    <label for="gid">Group ID:</label>
                </td>
                <td>
                    <input type="text" id="gid" name="gid">
                </td>
            </tr>

            <tr>
                <td>
                    <label for="ver">Version:</label>
                </td>
                <td>
                    <input type="text" id="ver" name="ver">
                </td>
            </tr>

            <tr>
                <td><input type="submit" value="Search"></td>
            </tr>
        </table>
    </form>

    <!-- search by package name -->
    <form method="post" action="maven-search?by=pname">
        <h3>Search by package name</h3>
        <table>
            <tr>
                <td><label for="pname">Package name:</label></td>
                <td><input type="text" id="pname" name="pname"></td>
            </tr>

            <tr>
                <td>Filter:</td>
            </tr>

            <tr>
                <td><label for="lv">Lowest version</label></td>
                <td><input type="radio" id="lv" name="verFilter" value="lv"></td>
            </tr>

            <tr>
                <td><label for="hv">Highest version</label></td>
                <td><input type="radio" id="hv" name="verFilter" value="hv"></td>
            </tr>

            <tr>
                <td><input type="submit" value="Search"></td>
            </tr>
        </table>
    </form>
</div>

<jsp:include page="include/footer.jsp" flush="true"/>