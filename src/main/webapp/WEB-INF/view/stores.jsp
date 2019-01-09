<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>

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
                    <tr>
                        <td>${name1}</td>
                        <td>${latitude1}</td>
                        <td>${longitude1}</td>
                    </tr>
                    <tr>
                        <td>${name2}</td>
                        <td>${latitude2}</td>
                        <td>${longitude2}</td>
                    </tr>
                    <tr>
                        <td>${name3}</td>
                        <td>${latitude3}</td>
                        <td>${longitude3}</td>
                    </tr>
                    <tr>
                        <td>${name4}</td>
                        <td>${latitude4}</td>
                        <td>${longitude4}</td>
                    </tr>
                </table>
            </div>
        </div>
    </section>
</main>
</body>
<%@include file="fragments/footer.jsp"%>
</html>