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
                    <c:url value="/mapPointerRegister" var="to_mapPointerRegister"/>
                    <form:form class="form-horizontal" action="${to_mapPointerRegister}" modelAttribute="mapPointerRegisterForm" method="post">
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="pointName" cssClass="form-control" placeholder="Siec sklepow"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="nip" cssClass="form-control" placeholder="NIP"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="pointCity" cssClass="form-control" placeholder="Miasto"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="pointAddress" cssClass="form-control" placeholder="Ulica"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="pointAddressBlockNumber" cssClass="form-control" placeholder="Numer bloku"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="pointLongitude" cssClass="form-control" placeholder="Dlugosc geograficzna"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:input path="pointLatitude" cssClass="form-control" placeholder="Szerokosc geograficzna"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <form:radiobutton path="pointType" cssClass="form-control" value="Shop"/>Sklep
                                <j:if test="${sessionScope.userPrivileges==2}">
                                    <form:radiobutton path="pointType" cssClass="form-control" value="Store"/>Magazyn
                                </j:if>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-10 col-sm-8 col-md-6 col-lg-4 col-centered">
                                <input type="submit" class="btn btn-default" value="Zarejestruj punkt">
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