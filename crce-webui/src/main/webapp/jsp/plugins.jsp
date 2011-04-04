<%@page import="cz.zcu.kiv.crce.plugin.Plugin"%>
<%@page import="cz.zcu.kiv.crce.metadata.Requirement"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>
<%@page import="cz.zcu.kiv.crce.metadata.Capability"%>
<%@page import="cz.zcu.kiv.crce.repository.Buffer"%>
<%@page import="cz.zcu.kiv.crce.webui.internal.Activator"%>
<%@page import="cz.zcu.kiv.crce.metadata.Resource"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>
<%@page import="cz.zcu.kiv.crce.plugin.Plugin"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

   <%
   /*
   		String hello = (String) session.getAttribute("hello");
   		Resource[] resources = (Resource[]) session.getAttribute("resources");
   		
   		Plugin[] plugins = (Plugin[]) session.getAttribute("plugins");
   		plugins[0]*/
   		/*
   		Resource[] store = (Resource[]) session.getAttribute("store");
   		if(hello!=null && resources!=null && plugins!=null && store!=null)
   		{
   		 	out.print(hello);
   		 	out.print("Resources size : "+resources.length+" Plugin size: "+plugins.length+" Store length: "+store.length);
   		}
   		else out.print("DIED");
   	*/
   %>
   
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
  <head>
    <meta name="generator" content="PSPad editor, www.pspad.com" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    
    <link charset="utf-8" href="css/styl.css" rel="stylesheet" type="text/css" />
    
    <script type="text/javascript" src="js/jquery-1.5.1.js"></script>
    
    <script type="text/javascript">
      $(document).ready(function(){
        $(".informace").hide();
      
        $(".popis").click(function(){
          $(this).parent().next(".informace").slideToggle(500)
          return false;
        });
        
        $(".rozbalit").click(function(){
          $(".informace").slideDown(500)
          return false;
        });
        
        $(".sbalit").click(function(){
          $(".informace").slideUp(500)
          return false;
        });
      
      });
    </script>

    <title>Software components storage</title>
  </head>
 
  <body>

  <div id="stranka">
  	
  	<div id="hlavicka">
  		<div class="logo_img"><a href="#"><img src="graphic/crce.png" alt="logo" /></a></div>
  		<div class="nazev">Software components storage</div>
      
  		<div class="vyhledavani">
        <form method="get" action="#">
          <input class="text" type="text" name="search" />
          <input class="tlacitko" type="submit" value="search" />
        </form>
      </div>
  	</div>
  	<div class="konec"></div>
    
    <ul id="menu" class="vycisteni">
    	<li><a href="#">Buffer</a></li>
    	<li><a href="#">Store</a></li>
        <li><a href="#">Search</a></li>
        <li><a class="aktivni" href="#">Plugins</a></li>
    </ul>
    
  	<div id="telo">
  	       
      <c:forEach items="${plugins}" var="plugin">
  		<div class="komponenta">
  			<div class="nadpis">
  				<a class="popis" href="#">${plugin.pluginId}</a>
  				<div class="nabidka">
            <a href="#"><img src="graphic/save.png" alt="download" title="Download component ${resource.presentationName} ${resource.version}" /></a>
            <a href="form.html"><img src="graphic/edit.png" alt="edit" title="Edit component ${resource.presentationName} ${resource.version}" /></a>
            <a href="#"><img src="graphic/del.png" alt="delete" title="Delete component Lorem ${resource.presentationName} ${resource.version}"/></a>
          </div>
  				<div class="konec"></div>
  			</div>
  			<div class="informace">
  				<div class="polozka"><strong>Description:</strong> ${plugin.pluginDescription}</div>	
  				<div class="polozka"><strong>Priority:</strong> ${plugin.pluginPriority}</div>
  				<div class="polozka"><strong>Version:</strong> ${plugin.pluginVersion}</div>
  				<div class="polozka"><strong>Keywords:</strong> 
  					<ul>
  						<c:forEach items="${plugin.keywords}" var="keyword">
  							<li>${keyword}</li>
  						</c:forEach>
  					</ul>
  				</div>
  			</div>
  		</div>
  	</c:forEach>
  		
  		<div id="animacni_odkazy">
  		  <a class="rozbalit" href="#">Show all</a> - <a class="sbalit" href="#">Hide all</a>
  		</div>
  	</div>
  
  	<div id="paticka">&copy; ASWI project 2011</div>

  </div>
  </body>
</html>



