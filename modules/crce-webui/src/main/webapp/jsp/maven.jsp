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

            <!-- feedback for searching by coordinates -->
            <c:if test="${requestScope.feedback1 != null}">
                <tr>
                    <td colspan="2"><c:out value="${requestScope.feedback1}"/></td>
                </tr>
            </c:if>

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
                <td>Version filter:</td>
            </tr>

            <tr>
                <td><input type="radio" id="lv" name="verFilter" value="lv">
                <label for="lv">Lowest version</label></td>
            </tr>

            <tr>
                <td><input type="radio" id="hv" name="verFilter" value="hv" checked="true">
                <label for="hv">Highest version</label></td>
            </tr>

            <tr>
                <td>GroupId filter:</td>
            </tr>

            <tr>
                <td><input type="radio" id="nogId" name="gidFilter" value="nogId">
                    <label for="nogId">Highest groupId match</label></td>
            </tr>

            <tr>
                <td><input type="radio" id="hmatch" name="gidFilter" value="hmatch" checked="true">
                <label for="hmatch">Highest groupId match</label></td>
            </tr>

            <tr>
                <td><input type="radio" id="manualg" name="gidFilter" value="manualg">
                <label for="manualg">Manual groupId filter</label></td>
                <td><input type="text" id="manualgf" name="manualgVal"></td>
            </tr>

            <!-- feedback for searching by package name -->
                <c:if test="${requestScope.feedback2 != null}">
                    <tr>
                        <td colspan="2"><c:out value="${requestScope.feedback2}"/></td>
                    </tr>
                </c:if>

            <tr>
                <td><input type="submit" value="Search"></td>
            </tr>

            <!-- main feedback -->
            <c:if test="${requestScope.feedback3 != null}">
                <tr>
                    <td colspan="2"><c:out value="${requestScope.feedback3}"/></td>
                </tr>
            </c:if>
        </table>
    </form>
</div>

<jsp:include page="include/footer.jsp" flush="true"/>