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
            <div class="col-sm-12">
                <div class="mainText">
                    <c:url value="/supplyDeliveryRequest" var="to_supplyDeliveryRequest"/>
                    <form:form class="form-horizontal" action="${to_supplyDeliveryRequest}" modelAttribute="supplyDeliveryRequestForm" method="post">
                    <div class="form-group">
                        <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                            Wybierz sklep:
                            <form:select path="shopName" cssClass="form-control" name="Wybierz sklep">
                                ${shopIdOptions}
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                            Dzien:
                            <form:select path="shopDay" cssClass="form-control" name="Wybierz dzien">
                                ${shopDay}
                            </form:select>
                            Miesiac:
                            <form:select path="shopMonth" cssClass="form-control" name="Wybierz miesiac">
                                ${shopMonth}
                            </form:select>
                            Rok:
                            <form:select path="shopYear" cssClass="form-control" name="Wybierz rok">
                                ${shopYear}
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                            <input type="submit" class="btn btn-default" value="Wyslij zgloszenie">
                            <button type="reset" class="btn btn-default">Wyczysc</button>
                        </div>
                    </div>
                    </form:form>
                </div>
            </div>
        </div>
    </section>
</main>
</body>
<%@include file="fragments/footer.jsp"%>
</html>