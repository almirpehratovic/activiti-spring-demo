
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<spring:url value="/orders/tasks" var="urlAction"/>
<br /> <br />
<div id="tasksList">
	<c:forEach var="task" items="${tasks}">
		<table border="1">
			<tr>
				<td class="taskdata">Id:</td>
				<td>${task.id}</td>
				<td class="taskdata">Naziv:</td>
				<td class="title">${task.name}</td>
			</tr>
			<tr>
				<td class="taskdata">Prioritet:</td>
				<td>${task.priority}</td>
				<td class="taskdata">Kreiran:</td>
				<td>${task.dateCreated}</td>
			</tr>
			<tr>
				<td class="taskdata">Varijable:</td>
				<td colspan="3">
					<c:forEach items="${task.variables}" var="variable">
						${variable.key}:${variable.value}<br />
					</c:forEach>
				</td>
			</tr>
			<tr>
				<td class="taskdata">Akcije:</td>
				<td colspan="3">
					<a href="${urlAction}/${task.id}">Odobri</a>
				</td>
			</tr>
		</table>
		<br />
	</c:forEach>
	
	
</div>
