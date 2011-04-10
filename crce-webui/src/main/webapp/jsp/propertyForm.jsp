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
  	
      <h2>Property form</h2>
      <table>
      <form action="#" method="post">
        <tr>
        	<th>Name:</th>
        	<td><input class="text" type="text" name="name" value="${property.name}" /></td>
        	<td class="chyba">${nameError}</td>
        </tr>
        <tr>
        	<th>Type:</th>
        	<td><input class="text" type="text" name="type" value="${property.type}" /></td>
        	<td class="chyba">${typeError}</td>
        </tr>
        <tr>
        	<th>Value:</th>
        	<td><input class="text" type="text" name="value" value="${property.value}" /></td>
        	<td class="chyba">${valueError}</td>
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

