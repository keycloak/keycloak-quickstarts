<!--
    JBoss, Home of Professional Open Source
    Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
    contributors by the @authors tag. See the copyright.txt in the
    distribution for a full listing of individual contributors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

<%@page contentType="text/html" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RH-SSO Example App</title>

        <link rel="stylesheet" type="text/css" href="styles.css"/>
    </head>
    <body style="display: block">
        <jsp:useBean id="controller" class="org.keycloak.quickstart.appjee.Controller" scope="request"/>
        <% controller.handleLogout(request); %>
        
        <c:set var="isLoggedIn" value="<%=controller.isLoggedIn(request)%>"/>
        
        <c:if test="${isLoggedIn}">
            <c:set var="accountUri" value="<%=controller.getAccountUri(request)%>"/>
        </c:if>
        
        <div class="wrapper">
            <c:if test="${!isLoggedIn}">
                <div id="not-authenticated" style="display: block" class="menu">
                    <button onclick="location.href = 'protected.jsp'" type="button">Login</button>
                </div>
            </c:if>

            <c:if test="${isLoggedIn}">
                <div id="authenticated" style="display: block" class="menu">
                    <button onclick="location.href = 'index.jsp?action=logout'">Logout</button>
                    <button onclick="location.href = '${accountUri}'" type="button">Account</button>
                </div>
            </c:if>

            <div class="content">
                <button onclick="location.href = 'index.jsp?action=public'">Invoke Public</button>
                <button onclick="location.href = 'index.jsp?action=secured'">Invoke Secured</button>
                <button onclick="location.href = 'index.jsp?action=admin'">Invoke Admin</button>

                <div class="message" id="message"><% out.print(controller.getMessage(request)); %></div>
            </div>
        </div>
    </body>
</html>
