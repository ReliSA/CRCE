<%@page import="cz.zcu.kiv.crce.metadata.Resource"%>
<%@page import="cz.zcu.kiv.crce.webui.internal.Activator"%>

<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
  <head>
    <meta name="generator" content="PSPad editor, www.pspad.com" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    
    <link charset="utf-8" href="css/styl.css" rel="stylesheet" type="text/css" />

    <title>Software components storage</title>
  </head>
 
  <body>

  <div id="stranka">
  	
  	<div id="hlavicka">
  		<div class="logo_img"><a href="index.html"><img src="graphic/logo.png" alt="logo" /></a></div>
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
        <li><a href="#">Plugins</a></li>
    </ul>
    
  	<div id="telo">
  	
      <h2>Component form</h2>
      <table>
      <form action="#" method="post">
        <tr>
        	<th>Presentation name:</th>
        	<td><input class="text" type="text" name="presentationName" value="${component.presentationName}" /></td>
        	<td class="chyba">${presentationNameError}</td>
        </tr>
        <tr>
        	<th>Version:</th>
        	<td><input class="text" type="text" name="version" value="${component.version}" /></td>
        	<td class="chyba">${versionError}</td>
        </tr>
        <tr>
        	<th>Id:</th>
        	<td><input class="text" type="text" name="id" value="${component.id}" /></td>
        	<td class="chyba">${idError}</td>
        </tr>
        <tr>
        	<th>Symbolic name:</th>
        	<td><input class="text" type="text" name="symbolicName" value="${component.symbolicName}" /></td>
        	<td class="chyba">${symbolicNameError}</td>
        </tr>
        <tr>
        	<th>URI:</th>
        	<td><input class="text" type="text" name="uri" value="${component.uri}" /></td>
        	<td class="chyba">${uriError}</td>
        </tr>
        <tr>
        	<th>Relative URI:</th>
        	<td><input class="text" type="text" name="relativeUri" value="${component.relativeUri}" /></td>
        	<td class="chyba">${relativeUriError}</td>
        </tr>
        <tr>
        	<td colspan="2"><input class="tlacitko" type="submit" value="save values" /></td>
        </tr>
      </form>
      </table>
  		
  	</div>
  
  	<div id="paticka">&copy; ASWI project 2011</div>

  </div>
  </body>
</html>


