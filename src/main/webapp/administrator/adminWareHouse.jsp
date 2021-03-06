<%-- 
    Document   : adminwareHouse
    Created on : May 15, 2011, 10:48:50 PM
    Author     : Amin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="domain.* , tags.*"%>
<%@ page import="java.util.List" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@taglib prefix="pcshop" uri="/WEB-INF/pcshop.tld"%>

<!DOCTYPE html>
<!-- This will check that if the user is log in and shows the logout option! --> 
<c:choose>
    <c:when test="${sessionScope.logedinUser != null}">
        <jsp:include page="../includes/headerlogoutadmin.html" />
    </c:when>
    
    <c:otherwise>
        <jsp:include page="../includes/header.html" />
    </c:otherwise>
</c:choose>

<jsp:include page="../includes/column_left_home.jsp" />

<td width ="840" valign="top">
    <%--
    <jsp:useBean id="compFullList" class="domain.ComponentListt"  scope="application">

        Error, the bean should have been created in the servlet!
    </jsp:useBean>
    --%>
    <%--
        Each time we instantiate a new object to reflect the amount of the 
        Components in the stock
    --%>
    
    <% 
        List<Component> compFullList =
                new ComponentList().componentFullList("jdbc:mysql://localhost/pcshop?user=root&password=sesame");
        request.setAttribute("compFullList", compFullList);
    %>
    <h2>The list of available components in the warehouse!</h2>
    <form method="post" action="admin.jsp">
        <input type="submit" name="back" value="Back" />
    </form>
    
    <p></p>
    <p></p>
   
        
        
    <c:set var="compfulllist_xslt">
        <c:import url="compfulllist_xslt.xsl"/>
    </c:set>

    <x:transform xslt="${compfulllist_xslt}">
        
       <%= ComponentList.getXml(compFullList) %>

    </x:transform>
    
    
</td>


<jsp:include page="../includes/footer.jsp" />