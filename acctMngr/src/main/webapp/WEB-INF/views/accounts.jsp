<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Account Manager</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style.css" />" >
	</head>
	<body>
		<h1>List of Accounts</h1>
		
		<div class="navigation">
			<a href="<c:url value="/home" />">Home</a> <> 
			<a href="<c:url value="/account/create" />">Create New Account</a>
		</div>
		<br/>
		<div class="listAccountsSortedBy">
			<a href="<c:url value="/accounts/list?sort_by=fName" />">sort by first name</a> | 
			<a href="<c:url value="/accounts/list?sort_by=lName" />">sort by last name</a> | 
			<a href="<c:url value="/accounts/list?sort_by=email" />">sort by email</a> | 
			<a href="<c:url value="/accounts/list?sort_by=birth" />">sort by birth date</a> | 
		</div>
		<c:forEach items="${accountsList}" var="account" >
			<li id="account_<c:out value="account.id"/>">
				<div>
					<span class="accountHolder">
						<c:out value="${account.fName}"/>
						<c:out value="${account.lName}"/>,
						<fmt:formatDate value='${account.birth}' type='date' pattern='dd/MM/yyyy'/>
					</span>
				</div>
				<div>
					<span class="accountDetails">
						<c:out value="${account.email}"/>
					</span>
				</div>
				<div>
					<span class="accountActions">
						<a href="<c:url value="/account/${account.id}" />"> account details </a> | 
						<a href="<c:url value="/account/update/${account.id}" />"> update account </a> | 
						<a href="<c:url value="/account/delete/${account.id}" />"> delete account </a>
					</span>
				</div>
			</li>
		</c:forEach>
	</body>
</html>