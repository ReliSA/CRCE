<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // TODO: just one form

%>

<jsp:include page="include/header.jsp" flush="true">
    <jsp:param name="title" value="Maven search"/>
    <jsp:param name="maven" value="true" />
</jsp:include>

<div id="telo">
    <h2>Maven artifacts search</h2>
    <!-- search by maven coordinates -->
    <form method="post" class="formular" action="maven-search" enctype="multipart/form-data">
    		<table>
    			<tr>
    				<td><label for="resolver-conf-file">Resolver configration file (optional)</label></td>
    				<td><input type="file" id="resolver-conf-file" name="resolver-conf-file"></td>
    			</tr>
    			<!-- feedback for configuration file -->
                <c:if test="${requestScope.confFeedback != null}">
                    <tr>
                        <td colspan="2" class="chyba"><c:out value="${requestScope.confFeedback}"/></td>
                    </tr>
                </c:if>
    		</table>
    		<h3>Search by maven coordinates</h3>
    		<table>

                <tr>
                    <td>
                        <label for="gid">Group ID</label>
                    </td>
                    <td>
                        <input type="text" id="gid" name="gid">
                    </td>
                </tr>

                <tr>
                    <td>
                        <label for="aid">Artifact ID</label>
                    </td>
                    <td>
                        <input type="text" id="aid" name="aid">
                    </td>
                </tr>

                <tr>
                    <td>
                        <label for="ver">Version</label>
                    </td>
                    <td>
                        <input type="text" id="ver" name="ver">
                    </td>
                </tr>

    			<tr>
                    <td>Version filter:</td>
                </tr>

                <tr>
                    <td colspan="2"><input type="radio" id="no-v" name="coord-ver-filter" value="no-v" checked="true">
                    <label for="no-v">No version filter (only if all three coordinates are set)</label></td>
                </tr>

                <tr>
                    <td><input type="radio" id="lv" name="coord-ver-filter" value="lv">
                    <label for="lv">Lowest version</label></td>
                </tr>

                <tr>
                    <td><input type="radio" id="hv" name="coord-ver-filter" value="hv">
                    <label for="hv">Highest version</label></td>
                </tr>

                <!-- feedback for searching by coordinates -->
                <c:if test="${requestScope.coordFeedback != null}">
                    <tr>
                        <td colspan="2" class="chyba"><c:out value="${requestScope.coordFeedback}"/></td>
                    </tr>
                </c:if>

                <tr>
                    <td><input type="submit" value="Search" name="coord-search"></td>
                </tr>
            </table>

    		<h3>Search by fully qualified name</h3>
            <table>
                <tr>
                    <td><label for="pname">Fully qualified name:</label></td>
                    <td><input type="text" id="pname" name="pname"></td>
                </tr>

                <tr>
                    <td>Version filter</td>
                </tr>

                <tr>
                    <td><input type="radio" id="lv" name="package-ver-filter" value="lv">
                    <label for="lv">Lowest version</label></td>
                </tr>

                <tr>
                    <td><input type="radio" id="hv" name="package-ver-filter" value="hv" checked="true">
                    <label for="hv">Highest version</label></td>
                </tr>

                <tr>
                    <td>GroupId filter</td>
                </tr>

                <tr>
                    <td><input type="radio" id="no-gid" name="gid-filter" value="no-gid">
                        <label for="no-gid">No groupId filter</label></td>
                </tr>

                <tr>
                    <td><input type="radio" id="h-match" name="gid-filter" value="h-match" checked="true">
                    <label for="h-match">Highest groupId match</label></td>
                </tr>

                <tr>
                    <td><input type="radio" id="manual-gid" name="gid-filter" value="manual-gid">
                    <label for="manual-gid">Manual groupId filter</label></td>
                    <td><input type="text" id="manualgVal" name="manualgVal"></td>
                </tr>

                <!-- feedback for searching by package name -->
                    <c:if test="${requestScope.packageFeedback != null}">
                        <tr>
                            <td colspan="2" class="chyba"><c:out value="${requestScope.packageFeedback}"/></td>
                        </tr>
                    </c:if>

                <tr>
                    <td><input type="submit" value="Search" name="package-search"></td>
                </tr>

                <!-- main feedback -->
                <c:if test="${requestScope.mainFeedback != null}">
                    <tr>
                        <td colspan="2"><c:out value="${requestScope.mainFeedback}"/></td>
                    </tr>
                </c:if>
            </table>

    	</form>
</div>

<jsp:include page="include/footer.jsp" flush="true"/>