<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<%@ include file="incl_processview_outline_declaration.jsp" %>
   <head>
      <%@ include file="incl_pagetitle.jsp" %>
      <link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
      <link rel="STYLESHEET" type="text/css" href="../css/std_treelook.css">
      <link rel="STYLESHEET" type="text/css" href="../css/ae_processView.css">
      <link rel="STYLESHEET" type="text/css" href="../css/ae.css">
      <script type="text/javascript" language="JavaScript" src="../script/ae_nanotree.js"></script>

      <script type="text/javascript" language="JavaScript">

         showRootNode = false;
         sortNodes = false;
         dragable = false;
         useCookieState = false;
         ignoreTreeEvent = false;
         var firstTime = true;

         /*
          * Initialize and show the tree.
          */
         function init()
         {
            container = document.getElementById('outlinediv');
            <ae:IfTrue name="treeViewBean" property="valid" >
               showTree('');
            </ae:IfTrue>
            <ae:IfFalse name="treeViewBean" property="valid" >
               container.innerHTML = '<jsp:getProperty name="treeViewBean" property="message" />';
            </ae:IfFalse>
         }

         //called by once the tree controll has been built.
         function finishedLoading()
         {
            // initally select the 'Process' level node.
            handleNode(2);
            expandNode();
            var processNode = getTreeNode(2);
            if (processNode && processNode.getID())
            {
               showProperty( processNode.getParam() );
            }
         }

         /*
          * Event handler when a node or leaf is clicked.
          */
         function standardClick(treeNode)
         {
            if (!ignoreTreeEvent)
            {
               showProperty( treeNode.getParam() );
            }
         }

         /*
          * Show BPEL Activity's properities given its location path.
          */
         function showProperty(xPath)
         {
            var graphWindow = parent.pvgraph;
            if (xPath && graphWindow && graphWindow.onOutlineSelect)
            {
               graphWindow.onOutlineSelect(xPath);
               focus();
            }

            var propWindow = parent.pvproperties;
            if (xPath && propWindow)
            {
               url = "processview_properties.jsp?<jsp:getProperty name="treeViewBean" property="pidParamName" />=<jsp:getProperty name="treeViewBean" property="pidParamValue" />&path=" + encodeURI(xPath);
               propWindow.location = url;
            }
         }

         /**
          * call back from the property view when the page has been loaded
          */
         function onPropertyViewChange(xPath)
         {
            if (!xPath || xPath == "null")
            {
               return;
            }
            if (firstTime)
            {
               firstTime = false;
               return;
            }
            currNode = findNodeWithParam(rootNode, xPath);
            ignoreTreeEvent = true;
            expandAndSelectNode(currNode);
            ignoreTreeEvent = false;
         }

         function nodeEdited(treeNode)
         {
         }

         function procOp(operation)
         {
            if (operation && operation !="")
            {
               form = document.forms["ec_form"];
               if (form)
               {
                  form.ProcessAction.value = operation;
                  form.submit();
               }
            }
         }

         <ae:IfTrue name="treeViewBean" property="valid" >
            <jsp:getProperty name="treeViewBean" property="javaScript" />
         </ae:IfTrue>
      </script>

   </head>
   <body OnLoad="init();">
      <div id="outlinediv">
        Loading BPEL process id <jsp:getProperty name="treeViewBean" property="pidParamValue" /> ...
      </div>
   </body>
</html>
