<%@ page import="cz.zcu.kiv.typescmp.Difference" %>
<%@ page import="cz.zcu.kiv.crce.compatibility.Compatibility" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp" flush="true">
    <jsp:param name="title" value="Compatibility info" />
    <jsp:param name="compatibility" value="true" />
</jsp:include>

    <div id="telo">
        <form method="get" action="resource" accept-charset="utf-8">
            <div class="upload">
                <table class="upload">
                    <tr>
                        <th>Name</th><th>Version</th>
                    </tr>
                    <tr>
                        <td><input class="text" type="text" name="name"/></td>
                        <td><input class="text" type="text" name="version"/></td>
                        <td><div class="tlac"><input class="tlacitko" type="submit" value="Search"/></div></td>
                    </tr>
                </table>
            </div>

            <input type="hidden" name="link" value="compatibility">
        </form>


        <c:choose>
            <c:when test="${nodata}">No compatibility data found.</c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Name</th><th>Version</th><th>Difference</th>
                    </tr>
                    <c:forEach items="${lower}" var="comp">
                        <tr>
                            <td>${comp.baseResourceName}</td><td>${comp.baseResourceVersion}</td><td>${comp.diffValue}</td>
                        </tr>
                    </c:forEach>
                    <tr class="pivot">
                        <td>${pivotName}</td><td>${pivotVersion}</td>
                    </tr>
                    <c:forEach items="${upper}" var="comp">
                        <tr>
                            <td>${comp.resourceName}</td><td>${comp.resourceVersion}</td>
                            <%
                                Compatibility cmp = (Compatibility)pageContext.getAttribute("comp");
                                Difference diff = cmp.getDiffValue(); //flip or not? :]
                                pageContext.setAttribute("diff", diff);
                            %>
                            <td>${diff}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

<jsp:include page="include/footer.jsp" flush="true" />