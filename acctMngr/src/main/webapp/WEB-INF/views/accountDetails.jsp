<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Account Manager - Create Form</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style.css" />" >
	</head>
	<body>
		<h1>Account Details</h1>
		
		<div class="navigation">
			<a href="<c:url value="/home" />">Home</a> <>
			<a href="<c:url value="/accounts/list" />">All Accounts</a> <>
			<a href="<c:url value="/account/create" />">Create New Account</a>
		</div>
		
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
	</body>
</html>