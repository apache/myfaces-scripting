<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
<head>
    <title>Hello World</title>
</head>
<body>
<f:view>
    <h:form id="mainForm">
        <h:panelGrid columns="2">
            <h:outputLabel for="name" value="xxxPlease enter your name"/>
            <h:inputText id="name" value="#{helloWorld.name}" required="true"/>
			<h:inputText id="name2" value="#{testbean.mystr}" required="true"/>
            <h:commandButton value="Press me" action="#{helloWorld.send}"/>
            <h:messages showDetail="true" showSummary="false"/>
            <h:outputFormat value="#{testbean.xxx2}"/>
            <h:commandButton value="Press me dynamic" action="#{testbean.doit}"/>
        </h:panelGrid>
    </h:form>
</f:view>
</body>
</html>
