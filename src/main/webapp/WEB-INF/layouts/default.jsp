<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<spring:message code="app.title" var="appTitle" />
<spring:url value="/resources/styles/main.css" var="urlCssMain"/>
<spring:url value="/resources/scripts/jquery-1.11.3.min.js" var="urlScriptJquery"/>
<spring:url value="/resources/scripts/main.js" var="urlScriptMain"/>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${urlCssMain}" rel="stylesheet" />
<script src="${urlScriptJquery}"></script>
<script src="${urlScriptMain}"></script>
<title>${appTitle}</title>
</head>
<body>
	<div id="pageWrapper">
		<div id="headerWrapper">
			<tiles:insertAttribute name="header" ignore="true" />
		</div>
		<div id="contentWrapper">
			<tiles:insertAttribute name="menu" ignore="true" />
			<div id="main">
				<tiles:insertAttribute name="body" />
				<tiles:insertAttribute name="footer" ignore="true" />
			</div>
		</div>
	</div>
</body>
</html>