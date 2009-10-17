<%@page contentType="text/html; charset=UTF-8"  isErrorPage="true" import="java.io.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%

String code = "";
String message = "";
String type = "";
String uri = null;

Object codeObj;
Object messageObj;
Object typeObj;
Throwable throwable;

// Retrieve the three possible error attributes, some may be null
codeObj = request.getAttribute("javax.servlet.error.status_code");
messageObj = request.getAttribute("javax.servlet.error.message");
typeObj = request.getAttribute("javax.servlet.error.exception_type");
throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
uri = (String) request.getAttribute("javax.servlet.error.request_uri");

if (uri == null) {
   uri = request.getRequestURI(); // in case there's no URI given
}

// Convert the attributes to string values
if (codeObj != null) code = codeObj.toString();
if (messageObj != null) message = messageObj.toString();
if (typeObj != null) type = typeObj.toString();

// The error reason is either the status code or exception type
String reason = (!"".equals(code) ? code : type);

String stackTrace = null;
if (throwable != null)
{
    StringWriter sw = new StringWriter();
    PrintWriter  pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    pw.flush();
    stackTrace = sw.toString();
    throwable = throwable.getCause();
}

%>

<html>
   <%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />
   <head>
      <title><ae:GetResource name="page_title_active_bpel" /></title>
      <link rel="shortcut icon" href="/BpelAdmin/images/favicon.ico" type="image/x-icon" />
      <link rel="STYLESHEET" type="text/css" href="/BpelAdmin/css/ae_processView.css">
      <link rel="STYLESHEET" type="text/css" href="/BpelAdmin/css/ae.css">

      <style type="text/css">
         a
         {
            padding-bottom:1px;
            border-bottom: 1px solid #006;
         }
         a:hover
         {
            text-decoration: none;
         }

         #header
         {
            border-bottom:1px solid #333;
            margin-bottom:20px;
         }

         #content
         {
            margin:5px 10px 5px 15px;
            padding:5px;
            clear:both;
         }


         #footer
         {
            margin:50px 0 0 0;
            padding:5px;
            clear:both;
            border-top:1px solid #333;
         }

         .errorpart
         {
            color:#333;
         }

         .errorpart h3
         {
            color: #900;
            margin-bottom:10px;
            padding-bottom:5px;
            border-bottom:1px solid #333;
         }

         .errorpart span
         {
            color:#006;
         }

         .errorpart p
         {
            margin-left:10px;
         }

      </style>

   </head>
   <body>
      <div id="header">
         <img  id="logo" src="/BpelAdmin/images/logo.gif">
         <div id="headertitle">
            <h1>
            Server Error
            </h1>
            <p>
               <a  href="/BpelAdmin" target="_top">Admin Console</a>
               &nbsp;|&nbsp;
               <a  href="../BpelAdminHelp" target="aeAdminHelp"><ae:GetResource name="help" /></a>
            </p>
         </div>
      </div>
      <div id="content">
             <%
               if ( "404".equals(reason) )
               {
             %>
             <p>
             HTTP 404 - The requested resource was not found.
               Click <a  href="/BpelAdmin" target="_top">here</a> to go the Admin Console.
             </p>
             <%
               }
             %>

         <div class="errorpart">
          <h3>HTTP Error Message</h3>
             <p>
                 <span>Reason :</span>  <%= reason %> <br/>
                 <span>Message :</span> <%=  message %> <br/>
                 <span>Request URI :</span> <%= uri %> <br/>
                 <span>Date :</span> <%= (new java.util.Date()).toString() %> <br/>
            </p>
         </div>

         <div class="errorpart">
            <h3>HTTP Request Information </h3>
            <p>
               <span>JSP Request Method:</span> <%= request.getMethod() %> <br/>
               <span>Request URI:</span> <%= request.getRequestURI() %> <br/>
               <span>Request Protocol:</span> <%= request.getProtocol() %> <br/>
               <span>Servlet path:</span> <%= request.getServletPath() %>   <br/>
               <span>Path info:</span> <%= request.getPathInfo()  %> <br/>
               <span>Query string:</span> <%= request.getQueryString() %> <br/>
               <span>Content length:</span> <%= request.getContentLength() %> <br/>
               <span>Content type:</span> <%= request.getContentType() %> <br/>
               <span>Server name:</span> <%= request.getServerName() %> <br/>
               <span>Server port:</span> <%= request.getServerPort() %> <br/>
               <span>Remote user:</span> <%= request.getRemoteUser() %> <br/>
               <span>Remote address:</span> <%= request.getRemoteAddr() %> <br/>
               <span>Remote host:</span> <%= request.getRemoteHost() %> <br/>
               <span>Authorization scheme:</span> <%= request.getAuthType() %> <br/>
               <span>Locale:</span> <%= request.getLocale() %>  <br/>
               <span>User-Agent:</span>  <%= request.getHeader("User-Agent") %> <br/>
              </p>
         </div>
         <div class="stacktrace">
            <%
               if (stackTrace != null)
               {
            %>
               <div class="errorpart">
                <h3>Exception:</h3>
                  <p>
                   <pre>
<%= stackTrace %>
                   </pre>
                   </p>
               </div>
            <%
               }
            %>

            <%
              int causeCount = 0;
              while (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter  pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                pw.flush();
                stackTrace = sw.toString();
                throwable = throwable.getCause();
                causeCount++;
            %>
            <div class="errorpart">
             <h3>Root Cause # <%= causeCount %>:</h3>
               <p>
                <pre>
<%= stackTrace %>
                </pre>
                </p>
            </div>
            <% }  %>
         </div>
      </div>

      <div id="footer">
         <jsp:include page="../footer.jsp" />
      </div>

   </body>
</html>
