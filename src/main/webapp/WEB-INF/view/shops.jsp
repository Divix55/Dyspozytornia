<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<%@include file="fragments/head.jsp"%>
<%@include file="fragments/header.jsp"%>
<body onLoad="load()">
<main>
    <section class="mainBody">
        <div class="row">
            <div class="title"><h1>Punkty</h1></div>

            <div class="col-sm-12 mainText">
                <table>
                    <tr>
                        <th style="width: 10%"> Nazwa punktu </th>
                        <th style="display:none">Szerokosc geogr.</th>
                        <th style="display:none">Dlugosc geogr.</th>
                        <th> NIP </th>
                        <th> Miasto </th>
                        <th> Adres </th>
                        <th> Lokal </th>
                    </tr>
                    ${mapPointerFill}
                </table>
                <a href="<j:url value="/mapPointerRegister"/>"><p>Zarejestruj punkt</p></a>
                <div id="map" style="width: 500px; height: 500px;">
                </div>
            </div>
        </div>
    </section>
</main>
</body>
<%@include file="fragments/footer.jsp"%>
</html>