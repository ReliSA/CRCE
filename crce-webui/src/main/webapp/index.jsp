<%-- 
    Document   : index
    Created on : 9.12.2010, 15:39:19
    Author     : kalwi
--%>

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
                Plugin[] plugins = Activator.instance().getPluginManager().getPlugins();
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
                for (Resource res : buffer.getRepository().getResources()) {
                    out.println("<h2>" + res.getId() + "</h2>");
                    
                    out.println("<table>");
                    out.println("<tr><td>Symbolic name:</td><td>" + res.getSymbolicName() + "</td></tr>");
                    out.println("<tr><td>Version:</td><td>" + res.getVersion() + "</td></tr>");
                    out.println("<tr><td>Presentation name:</td><td>" + res.getPresentationName() + "</td></tr>");
                    out.println("<tr><td>Size:</td><td>" + res.getSize() + "</td></tr>");
                    out.println("<tr><td>URI:</td><td>" + res.getUri().normalize() + "</td></tr>");
                    out.print("<tr><td>Categories:</td><td>");
                    String[] cats = res.getCategories();
                    for (int i = 0; i < cats.length; i++) {
                        out.print(cats[i] + (i < cats.length - 1 ? ", " : ""));
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
                        out.println("<td>" + cap.getName() + "</td>");
                        
                        out.println("<td>");
                        out.println("<table width='100%'>");
                        out.println("<tr><th>type</th><th>name</th><th>value</th></tr>");
                        for (Property prop : cap.getProperties()) {
                            out.println("<tr><td>" + prop.getType() + "</td><td>" + prop.getName() + "</td><td>" + prop.getValue() + "</td></tr>");
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
                        out.println("<tr><td>" + req.getName() + "</td>"
                                + "<td>" + req.getFilter() + "</td>"
                                + "<td>" + (req.isOptional() ? "Y" : "N") + "</td>"
                                + "<td>" + (req.isMultiple() ? "Y" : "N") + "</td>"
                                + "<td>" + (req.isExtend() ? "Y" : "N")  + "</td></tr>");
                    }
                    out.println("</table>");
                    
                    out.println("<hr/>");
                }
                

            %>

        </div>
    </body>
</html>
