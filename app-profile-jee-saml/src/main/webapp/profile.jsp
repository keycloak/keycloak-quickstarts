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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>RH-SSO SAML Example App</title>
        <link rel="stylesheet" type="text/css" href="styles.css"/>
    </head>
    <body>
        <jsp:useBean id="controller" class="org.keycloak.quickstart.profilejee.Controller" scope="request"/>
        <c:set var="accountUri" value="<%=controller.getAccountUri(request)%>"/>
        <c:set var="req" value="<%=request%>"/>

        <div class="wrapper" id="profile">
            <div class="menu">
                <button onclick="location.href = '?GLO=true'" type="button">Logout</button>
                <button onclick="location.href = '${accountUri}'" type="button">Account</button>
            </div>

            <div class="content">
                <div id="profile-content" class="message">
                    <table cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="label">First name</td>
                            <td><span id="firstName">${controller.getFirstName(req)}</span></td>
                        </tr>
                        <tr class="even">
                            <td class="label">Last name</td>
                            <td><span id="lastName">${controller.getLastName(req)}</span></td>
                        </tr>
                        <tr>
                            <td class="label">Username</td>
                            <td><span id="username">${controller.getUsername(req)}</span></td>
                        </tr>
                        <tr class="even">
                            <td class="label">Email</td>
                            <td><span id="email">${controller.getEmail(req)}</span></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
