
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>

<spring:message code="orders" var="labelOrders"/>
<spring:message code="order" var="labelOrder"/>
<spring:message code="order.id" var="labelId"/>
<spring:message code="order.account" var="labelAccount"/>
<spring:message code="order.amount" var="labelAmount"/>
<spring:message code="order.action" var="labelAction"/>
<spring:message code="order.action.start" var="labelActionStart"/>
<spring:message code="order.action.view" var="labelActionView"/>

<spring:url value="/orders/start" var="urlStart" />
<spring:url value="/orders/view" var="urlView" />

<spring:message code="budgets" var="labelBudgets"/>

<div id="ordersList">

	<p>${message}</p>
	
	<h1 class="title">${labelOrders}</h1>
	<table border="1" class="viewtable">
		<thead>
			<tr>
				<td>${labelId}</td>
				<td>${labelAccount}</td>
				<td>${labelAmount}</td>
				<security:authorize access="isAuthenticated()">
					<td>${labelAction}</td>
				</security:authorize>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="order" items="${orders}">
				<tr>
					<td>${order.id}</td>
					<td>${order.account}</td>
					<td>${order.amount}</td>
					<security:authorize access="isAuthenticated()">
						<c:choose>
							<c:when test="${order.executionId==null}">
								<security:authorize access="hasRole('referent')">
									<td><a href="${urlStart}/${order.id}">${labelActionStart}</a></td>	
								</security:authorize>
								<security:authorize access="!hasRole('referent')">
									<td>-</a></td>	
								</security:authorize>
							</c:when>
							<c:otherwise>
								<td><a href="${urlView}/${order.id}">${labelActionView}</a></td>
							</c:otherwise>
						</c:choose>
					</security:authorize>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br />
	<h1 class="title">${labelBudgets}</h1>
	<table border="1" class="viewtable">
		<thead>
			<tr>
				<td>${labelId}</td>
				<td>${labelAccount}</td>
				<td>${labelAmount}</td>
			</tr>
		</thead>	
		<tbody>
			<c:forEach var="budget" items="${budgets}">
				<tr>
					<td>${budget.id}</td>
					<td>${budget.account}</td>
					<td>${budget.amount}</td>	
				</tr>
			</c:forEach>
		</tbody>
	</table>

</div>
