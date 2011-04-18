<%@page import="cz.zcu.kiv.crce.plugin.Plugin"%>
<%@page import="cz.zcu.kiv.crce.metadata.Requirement"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>
<%@page import="cz.zcu.kiv.crce.metadata.Capability"%>
<%@page import="cz.zcu.kiv.crce.repository.Buffer"%>
<%@page import="cz.zcu.kiv.crce.webui.internal.Activator"%>
<%@page import="cz.zcu.kiv.crce.metadata.Resource"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
   
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
	<head>
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

    	<title>Software components storage - ${param.title}</title>
	</head>
 
	<body>

	<div id="stranka">
		
		<c:choose>
		<c:when test="${param.buffer}">
			<c:set scope="session" var="source" value="buffer"/>		
		</c:when>
		<c:when test="${param.store}">
			<c:set scope="session" var="source" value="store"/>		
		</c:when>
		
		<c:when test="${param.plugins}">
			<c:set scope="session" var="source" value="plugins"/>		
		</c:when>
		</c:choose>
		  	
	  	<div id="hlavicka">
	  		<div class="logo_img"><a href="index.html"><img src="graphic/crce.png" alt="logo" /></a></div>
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
	    	<li><a <c:if test="${param.buffer}"> class="aktivni"</c:if> href="resource?link=buffer">Upload</a></li>
	    	<li><a <c:if test="${param.store}"> class="aktivni"</c:if> href="resource?link=store">Store</a></li>
	    	<li><a <c:if test="${param.plugins}"> class="aktivni"</c:if> href="resource?link=plugins">Plugins</a></li>
	    </ul>