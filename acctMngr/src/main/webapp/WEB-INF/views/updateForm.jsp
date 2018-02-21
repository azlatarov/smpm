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
		<h1>Update Account</h1>
		
		<div class="navigation">
			<a href="<c:url value="/home" />">Home</a> <>
			<a href="<c:url value="/accounts/list" />">All Accounts</a>
		</div>
		
		<form method="POST">
			First name: <input type="text" name="fName" value="${account.fName}">
			Last name: <input type="text" name="lName" value="${account.lName}">
			Email: <input type="text" name="email"  value="${account.email}">
			Birth Date: <input type="text" name="birth"  value="<fmt:formatDate value='${account.birth}' type='date' pattern='dd/MM/yyyy'/>">
			
			<input type="submit" value="Update"/>
		</form>
	</body>
</html>