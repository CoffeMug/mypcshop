<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>


<jsp:include page="/includes/header.html" />
<jsp:include page="/includes/column_left_home.jsp" />


<!-- start the middle column -->

<td valign="top" class ="onlineshop">
    Invalid username and/or password or you may not have access to this area, please try
    <a href='<%= response.encodeURL("loginPage.jsp")%>'>again</a>.
    <jsp:include page="/includes/footer.jsp" />