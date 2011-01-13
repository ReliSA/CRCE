<%-- 
    Document   : index
    Created on : 9.12.2010, 15:39:19
    Author     : kalwi
--%>

<%@page import="cz.zcu.kiv.crce.metadata.Requirement"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>
<%@page import="cz.zcu.kiv.crce.metadata.Capability"%>
<%@page import="cz.zcu.kiv.crce.repository.Stack"%>
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

            <h1>Resources in stack:</h1>

            <%


                Stack stack = Activator.getStack();
                for (Resource res : stack.getStoredResources()) {
                    out.println("<h2>" + res.getId() + "</h2>");
                    out.println("Symbolic name: " + res.getSymbolicName() + "<br/>");
                    out.println("Version: " + res.getVersion() + "<br/>");
                    out.println("Presentation name : " + res.getPresentationName() + "<br/>");
                    out.print("Categories: ");
                    for (String cat : res.getCategories()) {
                        out.print(cat + ", ");
                    }
                    out.println("<br/>");
                    out.println("<h3>Properties</h3>");
                    for (Property prop : res.getProperties()) {
                        out.println("Property: " + prop.getName() + ", value: " + prop.getValue() + ", type: " + prop.getType() + "<br/>");
                    }

                    out.println("<h3>Capabilities</h3>");
                    for (Capability cap : res.getCapabilities()) {
                        out.println("Name: " + cap.getName() + "<br/>");
                        out.println("<ul>");
                        for (Property prop : cap.getProperties()) {
                            out.println("<li>Property: " + prop.getName() + ", value: " + prop.getValue() + ", type: " + prop.getType() + "</li>");
                        }
                        out.println("</ul>");
                    }
                    out.println("<h3>Requirements</h3>");
                    for (Requirement req : res.getRequirements()) {
                        out.println("Name: " + req.getName()
                                + ", filter: " + req.getFilter()
                                + ", opt: " + (req.isOptional() ? "Y" : "N")
                                + ", mul: " + (req.isMultiple() ? "Y" : "N")
                                + ", ext: " + (req.isExtend() ? "Y" : "N") + "<br/>");
                    }
                    out.println("<hr/>");
                }


            %>

        </div>
    </body>
</html>
