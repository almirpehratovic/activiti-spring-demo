<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url value="/orders" var="urlHome" />
<div id="header">
	<spring:url value="/j_spring_security_check" var="loginUrl" />
	<spring:url value="/j_spring_security_logout" var="logoutUrl" />
	<spring:url value="/users/register" var="urlRegister" />
	<spring:message var="labelUsername" code="user.username" />
	<spring:message var="labelPassword" code="user.password" />
	<spring:message var="labelLogin" code="user.login" />
	<spring:message var="labelLogout" code="user.logout" />
	<spring:message var="messageWelcome" code="user.welcome" />
	
	
	<spring:url value="/orders/tasks" var="urlTasks" />
	<spring:message var="labelHome" code="menu.home" />
	<spring:message var="labelTasks" code="menu.tasks" />
	
	<spring:url value="/resources/images/bosnia32.png" var="imageBosnia" />
	<spring:url value="/resources/images/usa32.png" var="imageEngland" />
	<spring:url value="?lang=bs" var="urlBos" />
	<spring:url value="?lang=en" var="urlEng" />

	<security:authorize access="isAnonymous()">
		<div id="login">
			<form name="loginForm" action="${loginUrl}" method="post">
					<table>
						<tr>
							<td>${labelUsername}:</td>
							<td><input type="text" name="j_username" /></td>
							<td>${labelPassword}</td>
							<td><input type="password" name="j_password" /></td>
							<td><input name="submit" type="submit" value="${labelLogin}" /></td>
							<td class="languages">
								<a href="${urlBos}"> <img alt="bos"	src="${imageBosnia}" />	</a> 
								<a href="${urlEng}"> <img alt="eng" src="${imageEngland}" /> </a>
							</td>
						</tr>
					</table>
			</form>
		</div>
	</security:authorize>
	<security:authorize access="isAuthenticated()">
		${messageWelcome}&nbsp;<security:authentication property="principal.username"/>
		<a href="${urlHome}">${labelHome}</a>&nbsp;
		<a href="${urlTasks}">${labelTasks}</a>&nbsp;
		<a href="${logoutUrl}">${labelLogout}</a>&nbsp;
	</security:authorize>

</div>