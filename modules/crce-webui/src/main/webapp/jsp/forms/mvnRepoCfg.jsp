<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">

<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link charset="utf-8" href="css/styl.css" rel="stylesheet"
	type="text/css" />
<title>Maven Repository Config File</title>
</head>


<body>

	<div class="repo_cfg">
		<p>
			<c:forEach items="${cfg}" var="cnf">
				<c:out value="${cnf}"></c:out> <br />
			</c:forEach>
		</p>
	</div>

</body>
</html>