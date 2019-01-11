<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<%@include file="fragments/head.jsp"%>
<%@include file="fragments/header.jsp"%>
<body>
<main>
    <section class="mainBody">
        <div class="row">
            <div class="title"><h1>Punkty</h1></div>

            <div class="col-sm-12 mainText">
                <table>
                    <tr>
                        <th>Nazwa punktu</th>
                        <th>Szerokosc geogr.</th>
                        <th>Dlugosc geogr.</th>
                    </tr>
                    ${mapPointerFill}
                </table>
            </div>
        </div>
    </section>
</main>
</body>
<%@include file="fragments/footer.jsp"%>
</html>