<%@page import="cz.zcu.kiv.crce.plugin.Plugin"%>
<%@page import="cz.zcu.kiv.crce.metadata.Requirement"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>
<%@page import="cz.zcu.kiv.crce.metadata.Capability"%>
<%@page import="cz.zcu.kiv.crce.repository.Buffer"%>
<%@page import="cz.zcu.kiv.crce.webui.internal.Activator"%>
<%@page import="cz.zcu.kiv.crce.metadata.Resource"%>
<%@page import="cz.zcu.kiv.crce.metadata.Property"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
   
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
    	<li><a class="aktivni" href="resource?link=buffer">Buffer</a></li>
    	<li><a href="resource?link=store">Store</a></li>
    	<li><a href="#">Upload</a></li>
        <li><a href="resource?link=plugins">Plugins</a></li>
    </ul>
    
  	<div id="telo">
  	
  	  <form method="post" action="#">
  	       
      <c:forEach items="${buffer}" var="resource">
  		<div class="komponenta">
  			<div class="nadpis">
  				<a class="popis" href="#">
  					<span class="sName">${resource.symbolicName}</span> 
  					<span class="version">${resource.version}</span> 
  					<span class="pName">${resource.presentationName}</span>
  					<span class="category"><c:forEach items="${resource.categories}" var="category">${$category}</c:forEach></span>
  				</a>
  				<div class="nabidka">
  					<a href="#"><img src="graphic/commit.png" alt="commit" title="Commit component ${resource.presentationName} ${resource.version}" /></a>
		            <a href="#"><img src="graphic/save.png" alt="download" title="Download component ${resource.presentationName} ${resource.version}" /></a>
		            <a href="#"><img src="graphic/del.png" alt="delete" title="Delete component ${resource.presentationName} ${resource.version}"/></a>
		            <a href="#"><img src="graphic/check.png" alt="check" title="Check component ${resource.presentationName} ${resource.version} compatibility"/></a>
		          	<input type="checkbox" name="${resource.presentationName}_${resource.version}" />
          		</div>
  				<div class="konec"></div>
  			</div>
  			<div class="informace">
  				<div class="polozka"><strong>Properties: </strong> <a href="#" title="add new property"><img src="graphic/add.png" alt="add new property" title="add new property"/>Add new</a> 
  					<ul>
  						<li><strong>Id:</strong> ${resource.id} <a href="#" title="edit id"><img src="graphic/edit.png" alt="edit id" title="edit id" /></a></li>
  						<li><strong>Symbolic name:</strong> ${resource.symbolicName} <a href="#" title="edit symbolic name"><img src="graphic/edit.png" alt="edit symbolic name" title="edit symbolic name" /></a></li>
  						<li><strong>URI:</strong> ${resource.uri} <a href="#" title="edit uri"><img src="graphic/edit.png" alt="edit uri" title="edit uri" /></a></li>
  						<li><strong>Relative URI:</strong> ${resource.relativeUri} <a href="#" title="edit relative uri"><img src="graphic/edit.png" alt="edit relative uri" title="edit relative uri" /></a></li>
  						<li><strong>Size:</strong> ${resource.size} <a href="#" title="edit size"><img src="graphic/edit.png" alt="edit size" title="edit size" /></a></li>
  					</ul>
  				</div>
  				<div class="polozka"><strong>Categories: </strong> <a href="#" title="edit categories"><img src="graphic/edit.png" alt="edit categories" title="edit categories" />Edit</a> 
  					<ul>
  						<c:forEach items="${resource.categories}" var="category">
  							<li>${category}</li>
  						</c:forEach>
  					</ul>
  				</div>
  				<div class="polozka"><strong>Capabilities: </strong> <a href="#"><img src="graphic/add.png" alt="add capability" title="add capability" />Add new</a> 
  					<ul>
  						<c:forEach items="${resource.capabilities}" var="capability">
  							<li>
  								${capability.name} <a href="#" title="edit capability"><img src="graphic/edit.png" alt="edit capability" title="edit capability" /></a>
  								<ul>
	  								<c:forEach items="${capability.properties}" var="property">
	  									<li>${property.name} (${property.type}) - ${property.value} <a href="#" title="edit property"><img src="graphic/edit.png" alt="edit property" title="edit property" /></a></li>
	  								</c:forEach>
  								</ul>
  							</li>
  						</c:forEach>
  					</ul>
  				</div>
  				<div class="polozka"><strong>Requirements: </strong> <a href="#"><img src="graphic/add.png" alt="add new requrements" title="add new requirements" />Add new</a> 
  					<ul>
  						<c:forEach items="${resource.requirements}" var="requirement">
  							<li>${requirement.name} - ${requirement.filter} <a href="#"><img src="graphic/edit.png" alt="edit" title="edit requirement" /></a></li>
  						</c:forEach>
  					</ul>
  				</div>
  			</div>
  		</div>
  	</c:forEach>
  		
  		<div id="animacni_odkazy">
  		  <a class="rozbalit" href="#">Show all</a> - <a class="sbalit" href="#">Hide all</a>
  		</div>
  		
  		<input class="tlacitko" type="submit" value="EXECUTE" />
  	</form>
  	
  	</div>
  
  	<div id="paticka">&copy; ASWI project 2011</div>

  </div>
  </body>
</html>



