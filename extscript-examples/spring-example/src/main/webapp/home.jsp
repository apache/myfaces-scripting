<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html>
 <head>
  <title>Scripting Test</title>
 </head>
 <body>
   <f:view>
     <h1>
      <h:outputText value="From JSF: The greeter says '#{greeter.greeting}' to '#{person.name}'."/> <br/>
      <h:outputText value="From JSF: The greeter says '#{greeter.anotherGreeting}'."/> <br/>
     </h1>

     <h:form id="helloForm">
        <h:commandButton action="reload" value="Reload" />
     </h:form>
       
   </f:view>
 </body>
</html>
