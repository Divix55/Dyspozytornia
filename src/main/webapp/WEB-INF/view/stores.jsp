<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<%@include file="fragments/head.jsp"%>
<%@include file="fragments/header.jsp"%>
<body>
<main>
    <section class="mainBody">
        <div class="row">
            <div class="title"><h1>Magazyny</h1></div>

            <div class="col-sm-12 mainText">
                <table>
                    <tr>
                        <th>Nazwa punktu</th>
                        <th>Szerokosc geogr.</th>
                        <th>Dlugosc geogr.</th>
                    </tr>
                    ${mapPointerFill}
                </table>
                <j:if test="${sessionScope.userPrivileges==2}">
                <a href="<j:url value="/mapPointerRegister"/>"><p>Zarejestruj punkt</p></a>
                </j:if>
            </div>
        </div>
    </section>
</main>
</body>
<%@include file="fragments/footer.jsp"%>
</html>