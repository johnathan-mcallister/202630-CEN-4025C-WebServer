<%@ page language="java" contentType="text/html; ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>ToDo App</title>
</head>
<body>
<h1>ToDo App</h1>
<form name="myForm" action="results.jsp" method="post">
    <table>
        <tr>
            <td>First Name:</td>
            <td><input type="text" name="fName" size="50"/></td>
        </tr>
        <tr>
            <td>Last Name:</td>
            <td><input type="text" name="lName" size="50"/></td>
        </tr>
        <tr>
            <td>Email:</td>
            <td><input type="text" name="email" size="50"/></td>
        </tr>
        <tr>
            <fieldset>
                <legend>Gender</legend>

                <input type="radio" id="male" name="gender" value="Male">
                <label for="male">Male</label><br>

                <input type="radio" id="female" name="gender" value="Female">
                <label for="female">Female</label><br>
            </fieldset>
        </tr>
    </table>
    <button type="submit" onclick="">Submit</button>
</form>
</body>
</html>