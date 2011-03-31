<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

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
   <%
   		String hello = (String) session.getAttribute("hello");
   		Resource[] resources = (Resource[]) session.getAttribute("resources");
   		Plugin[] plugins = (Plugin[]) session.getAttribute("plugins");
   		Resource[] store = (Resource[]) session.getAttribute("store");
   		if(hello!=null && resources!=null && plugins!=null && store!=null)
   		{
   		 	out.print(hello);
   		 	out.print("Resources size : "+resources.length+" Plugin size "+plugins.length+"Store length : "+store.length);
   		}
   		else out.print("DIED");
   %>
    </body>
</html>
