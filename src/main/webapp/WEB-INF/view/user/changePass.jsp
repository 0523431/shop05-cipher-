<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>비밀번호 변경</title>
<script>
	function changesubmit(cpf) {
		if(cpf.password.value == "") {
			alert("현재 비밀번호를 입력하세요");
			cpf.password.focus();
			return;
		}
		if(cpf.newpass.value == "") {
			alert("변경 비밀번호를 입력하세요");
			cpf.newpass.focus();
			return;
		}
		if(cpf.newpass.value != cpf.new_confirm.value) {
			alert("변경 비밀번호와 재입력한 비밀번호가 다름니다");
			cpf.new_confirm.value ="";
			cpf.new_confirm.focus();
			return;
		}
		cpf.submit();
		// this.form.submit(); 은 안되는 내용임
		// 왜?
		// onclick에서 넘어온 내용이 이미 this.form == cpf가 됨
	}
</script>
<link rel="stylesheet" href="${path}/css/main.css">

</head>
<body>

<h2>비밀번호 변경하기</h2>
<form action="changePass.shop" method="post" name="cpf">

<table>
	<tr><td>기존 비밀번호</td>
		<td><input type="password" name="password"></td>
	</tr>
	<tr><td>새 비밀번호</td>
		<td><input type="password" name="newpass"></td>
	</tr>
	<tr><td>새 비밀번호 재확인</td>
		<td><input type="password" name="new_confirm"></td>
	</tr>
	<tr><td colspan="2">
			<input type="button" onclick="changesubmit(this.form)" value="비밀번호 변경">
		</td>
</table>

</form>
</body>
</html>