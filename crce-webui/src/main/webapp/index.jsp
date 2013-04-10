<%--
    Document   : index
    Created on : 9.12.2010, 15:39:19
    Author     : kalwi
--%>

<%@page import="cz.zcu.kiv.crce.metadata.Attribute"%>
<%@page import="cz.zcu.kiv.crce.metadata.legacy.LegacyMetadataHelper"%>
<%@page import="java.util.List"%>
<%
 response.sendRedirect("resource");
 %>

<%@page import="cz.zcu.kiv.crce.plugin.Plugin"%>
<%@page import="cz.zcu.kiv.crce.metadata.Requirement"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>
<%@page import="cz.zcu.kiv.crce.metadata.Capability"%>
<%@page import="cz.zcu.kiv.crce.repository.Buffer"%>
<%@page import="cz.zcu.kiv.crce.webui.internal.Activator"%>
<%@page import="cz.zcu.kiv.crce.metadata.Resource"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <title>KIV CRCE</title>
    </head>

    <body>
        <div id="header" class="header"><img src="crce.png" alt="KIV CRCE"></div>

        <div id="body" class="body">

            <p>Store bundle:</p>

            <form method="POST" enctype="multipart/form-data" action="/crce/upload" accept-charset="utf-8">
                <input type="file" name="bundle"/><br/>
                <input type="submit" value="upload"/>
            </form>
            <%
                String success = request.getParameter("success");
                if ("true".equals(success)) {
                    out.println("<font color='green'><h3>Upload successful</h3></font>");
                }
                if ("false".equals(success)) {
                    out.println("<font color='red'><h3>Upload failed</h3></font>");
                }
            %>

            <h1>Plugins in plugin manager:</h1>
            <%
                List<Plugin> plugins = Activator.instance().getPluginManager().getPlugins();
                out.println("<table>");
                out.println("<tr><th>ID</th><th>priority</th><th>description</th></tr>");
                for (Plugin plugin : plugins) {
                    out.println("<tr>");
                    out.println("<td>" + plugin.getPluginId() + "</td><td>" + plugin.getPluginPriority() + "</td><td>" + plugin.getPluginDescription() + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            %>

            <h1>Resources in buffer</h1>

            <%


                Buffer buffer = Activator.instance().getBuffer(request);
                for (Resource res : buffer.getResources()) {
                    out.println("<h2>" + res.getId() + "</h2>");

                    out.println("<table>");
                    out.println("<tr><td>Symbolic name:</td><td>" + LegacyMetadataHelper.getSymbolicName(res) + "</td></tr>");
                    out.println("<tr><td>Version:</td><td>" + LegacyMetadataHelper.getVersion(res) + "</td></tr>");
                    out.println("<tr><td>Presentation name:</td><td>" + LegacyMetadataHelper.getPresentationName(res) + "</td></tr>");
                    out.println("<tr><td>Size:</td><td>" + LegacyMetadataHelper.getSize(res) + "</td></tr>");
                    out.println("<tr><td>URI:</td><td>" + LegacyMetadataHelper.getUri(res).normalize() + "</td></tr>");
                    out.print("<tr><td>Categories:</td><td>");
                    boolean first = true;
                    for (String category : LegacyMetadataHelper.getCategories(res)) {
                        if (first) {
                            first = false;
                        } else {
                            out.print(", ");
                        }
                        out.print(category);
                    }
                    out.println("</td></tr>");
                    out.println("</table>");

                    /*
                    out.println("<h3>Properties</h3>");
                    out.println("<table border='1' cellspacing='0'><tr><td>");
                    out.println("<table>");
                    out.println("<tr><th>type</th><th>name</th><th>value</th></tr>");
                    for (Property prop : res.getProperties()) {
                        out.println("<tr><td>" + prop.getType() + "</td><td>" + prop.getName() + "</td><td>" + prop.getValue() + "</td></tr>");
                    }
                    out.println("</table>");
                    out.println("</tr></td></table>");
                    */

                    out.println("<h3>Capabilities</h3>");

                    out.println("<table border='1' cellspacing='0' width='500px'>");

                    out.println("<tr><th>Name</th><th>Properties</th></tr>");

                    for (Capability cap : res.getCapabilities()) {
                        out.println("<tr>");
                        out.println("<td>" + cap.getNamespace()+ "</td>");

                        out.println("<td>");
                        out.println("<table width='100%'>");
                        out.println("<tr><th>type</th><th>name</th><th>value</th></tr>");
                        for (Attribute<?> attr : cap.getAttributes()) {
                            out.println("<tr><td>" + attr.getAttributeType().getType()
                                    + "</td><td>" + attr.getAttributeType().getName() + "</td><td>" + attr.getValue() + "</td></tr>");
                        }
                        out.println("</table>");
                        out.println("</td>");
                        out.println("</tr>");
                    }

                    out.println("</table>");


                    out.println("<h3>Requirements</h3>");
                    out.println("<table>");
                    out.println("<tr><th>Name</th><th>Filter</th><th>Optional</th><th>Multiple</th><th>Extended</th></tr>");
                    for (Requirement req : res.getRequirements()) {
                        out.println("<tr><td>" + req.getNamespace()+ "</td>"
                                + "<td>" + req.getDirective("filter") + "</td>"
                                + "<td>" + (Boolean.getBoolean(req.getDirective("optional")) ? "Y" : "N") + "</td>"
                                + "<td>" + (Boolean.getBoolean(req.getDirective("multiple")) ? "Y" : "N") + "</td>"
                                + "<td>" + (Boolean.getBoolean(req.getDirective("extend")) ? "Y" : "N")  + "</td></tr>");
                    }
                    out.println("</table>");

                    out.println("<hr/>");
                }


            %>

        </div>
    </body>
</html>
