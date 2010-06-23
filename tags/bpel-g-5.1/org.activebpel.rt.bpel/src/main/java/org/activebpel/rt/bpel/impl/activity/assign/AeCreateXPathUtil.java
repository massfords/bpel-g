//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeCreateXPathUtil.java,v 1.7 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import java.util.StringTokenizer;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionRunnerVariableResolver;
import org.activebpel.rt.bpel.impl.expr.xpath.AeWSBPELXPathVariableContext;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathFunctionContext;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathNamespaceContext;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.jaxen.VariableContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Util for creating xml constructs within a DOM to satisfy an XPath query
 */
public class AeCreateXPathUtil
{
   /**
    * Finds or creates the node indicated by the passed path and returns it.
    * Acceptable paths are of a form /a/b[#index]/@abc.
    * @param aPath The path to find or create.
    * @param aTargetDoc The context root to create the path within.
    * @param aContext Context for the copy operation
    * @param aNamespaceContext Resolves any namespace prefixes (will be the same
    *        as the aContext param except for propertyAliases.)
    * @return The node found or created matching the path.
    * @throws AeBusinessProcessException Thrown if problem with path.
    */
   public static Node findOrCreateXPath(String aPath, Document aTargetDoc, IAeCopyOperationContext aContext, IAeNamespaceContext aNamespaceContext) throws AeBusinessProcessException
   {
      StringTokenizer strtok = new StringTokenizer(aPath, "/[]@", true); //$NON-NLS-1$
      String nextToken;
      Node currentNode = aTargetDoc;
      if (!aPath.startsWith("/")) //$NON-NLS-1$
         currentNode = aTargetDoc.getDocumentElement();
      while (strtok.hasMoreTokens())
      {
         nextToken = strtok.nextToken();
         if (nextToken.equals("/")) //$NON-NLS-1$
         {
            ; // ignore
         }
         else if (nextToken.equals("]")) //$NON-NLS-1$
         {
            ; // ignore
         }
         else if (nextToken.equals("[")) //$NON-NLS-1$
         {
            if (strtok.hasMoreTokens())
            {
               String indexExpr = strtok.nextToken().trim();
               currentNode = createIndexedNode(indexExpr, currentNode, aContext, aNamespaceContext);
            }
            else
            {
               throw new AeBusinessProcessException(AeMessages.getString("AeActivityAssignImpl.ERROR_9")); //$NON-NLS-1$
            }
         }
         else if (nextToken.equals("@")) //$NON-NLS-1$
         {
            // if we have more tokens then get the attribute name
            if (strtok.hasMoreTokens())
            {
               // grab the attribute name qualifier
               nextToken = strtok.nextToken();
               currentNode = createAttributeNode(nextToken, currentNode, aNamespaceContext);
            }
         }
         else if (nextToken.indexOf('(') != -1)
         {
            break;
         }
         else
         {
            currentNode = createElementNode(nextToken, currentNode, aNamespaceContext);            
         }
      }

      return currentNode;
   }

   /**
    * Parses the next token for creation of an element node in the
    * passed currentNode.
    * @param aToken
    * @param aCurrentNode
    * @param aContext
    * @return Element the new element node
    * @throws AeBusinessProcessException if the node can not be created
    */
   protected static Element createElementNode(String aToken, Node aCurrentNode, IAeNamespaceContext aContext)
      throws AeBusinessProcessException
   {
      Element newElement = null;

      // get the namespace info based on path (may be empty - null)
      String localName, namespaceURI, prefix;
      int colonOffset;
      if ((colonOffset = aToken.indexOf(":")) >= 0) //$NON-NLS-1$
      {
         if (colonOffset > 0 && colonOffset < (aToken.length() - 1))
         {
            prefix = aToken.substring(0, colonOffset);
            localName = aToken.substring(colonOffset + 1);
            namespaceURI = aContext.resolvePrefixToNamespace(prefix);
         }
         else
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeActivityAssignImpl.ERROR_12") + aToken); //$NON-NLS-1$
         }
      }
      else
      {
         localName = aToken;
         namespaceURI = null;
         prefix = null;
      }

      // check if we are at root of document or in an element
      if (aCurrentNode.getNodeType() == Node.DOCUMENT_NODE)
      {
         Document doc = (Document)aCurrentNode;
         if (doc.getDocumentElement() == null)
         {
            newElement = doc.createElementNS(namespaceURI, aToken);
            doc.appendChild(newElement);
            if (namespaceURI != null)
               newElement.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, namespaceURI); //$NON-NLS-1$
         }
         else
         {
            // TODO make sure they didn't mess up and have a different name or namespace
            newElement = doc.getDocumentElement();
         }
      }
      else if(aCurrentNode.getNodeType() == Node.ELEMENT_NODE)
      {
         Element element = (Element) aCurrentNode;
         boolean found = false;
         for (Node child = element.getFirstChild();(!found) && (child != null); child = child.getNextSibling())
         {
            if (localName.equals(child.getLocalName()))
            {
               // if both null or matching
               if (AeUtil.compareObjects(namespaceURI, child.getNamespaceURI()))
               {
                  newElement = (Element)child;
                  found = true;
                  break;
               }
            }
         }
         // if we didn't find the node then create it now
         if (!found)
         {
            // check if namespace has a prefix and if so use it
            if(namespaceURI != null)
            {
               String newPrefix = AeXmlUtil.getPrefixForNamespace(element, namespaceURI);
               if(newPrefix == null)
               {
                  newElement = aCurrentNode.getOwnerDocument().createElementNS(namespaceURI, aToken);
                  newElement.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, namespaceURI); //$NON-NLS-1$
               }
               else if(newPrefix.equals("")) //$NON-NLS-1$
               {
                  newElement = aCurrentNode.getOwnerDocument().createElementNS(namespaceURI, localName);
               }
               else
               {
                  newElement = aCurrentNode.getOwnerDocument().createElementNS(namespaceURI, newPrefix + ":" + localName); //$NON-NLS-1$
               }
            }
            else
            {
               newElement = aCurrentNode.getOwnerDocument().createElementNS(namespaceURI, localName);
            }
            element.appendChild(newElement);
         }
      }
      else
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeActivityAssignImpl.ERROR_17") + aToken); //$NON-NLS-1$
      }
      return newElement;
   }

   /**
    * Parses the next token for creation of an attribute node in the
    * passed currentNode.
    * @param aToken
    * @param aCurrentNode
    * @param aNamespaceResolver
    * @return Attr the new attribute node
    * @throws AeBusinessProcessException if the node can not be created
    */
   protected static Attr createAttributeNode(String aToken, Node aCurrentNode, IAeNamespaceContext aNamespaceResolver)
      throws AeBusinessProcessException
   {
      Attr newAttribute = null;

      // get the namespace info based on path (may be empty - null)
      String localName, namespaceURI, prefix;
      int colonOffset;
      if ((colonOffset = aToken.indexOf(":")) >= 0) //$NON-NLS-1$
      {
         if (colonOffset > 0 && colonOffset < (aToken.length() - 1))
         {
            prefix = aToken.substring(0, colonOffset);
            localName = aToken.substring(colonOffset + 1);
            namespaceURI = aNamespaceResolver.resolvePrefixToNamespace(prefix);
         }
         else
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeActivityAssignImpl.ERROR_19") + aToken); //$NON-NLS-1$
         }
      }
      else
      {
         localName = aToken;
         namespaceURI = null;
         prefix = null;
      }

      // check that we are at an element, since this is an attribute
      if (aCurrentNode.getNodeType() == Node.ELEMENT_NODE)
      {
         Element element = (Element)aCurrentNode;
         // check if namespace has a prefix and if so use it
         if(namespaceURI != null)
         {
            String newPrefix = AeXmlUtil.getPrefixForNamespace(element, namespaceURI);
            if(AeUtil.isNullOrEmpty(newPrefix))
            {
               newAttribute = element.getOwnerDocument().createAttributeNS(namespaceURI, aToken);
               element.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, namespaceURI); //$NON-NLS-1$
            }
            else
            {
               newAttribute = aCurrentNode.getOwnerDocument().createAttributeNS(namespaceURI, newPrefix + ":" + localName); //$NON-NLS-1$
            }
         }
         else
         {
            newAttribute = aCurrentNode.getOwnerDocument().createAttributeNS(namespaceURI, localName);
         }
         element.setAttributeNodeNS(newAttribute);
      }
      else
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeActivityAssignImpl.ERROR_22") + aToken); //$NON-NLS-1$
      }
      return newAttribute;
   }

   /**
    * Accepts an expression representing an index number for the replication
    * or finding within the parent of the passed node.
    * @param aIndexExpr The index expression to evaluate
    * @param aCurrentNode The node which will be replicated until index is reached
    * @param aContext Copy operation context
    * @param aNamespaceContext Resolves namespace prefixes
    * @return the indexed node
    * @throws AeBusinessProcessException If error creating or finding the nodes
    */
   protected static Node createIndexedNode(String aIndexExpr, Node aCurrentNode, IAeCopyOperationContext aContext, IAeNamespaceContext aNamespaceContext) throws AeBusinessProcessException
   {
      // get the index of the element we are to find or create
      int index = convertIndex(aIndexExpr, aCurrentNode, aContext, aNamespaceContext);

      // check the children to see if we already have the indexed element
      Node parent = aCurrentNode.getParentNode();
      boolean found = false;
      int count = 0;
      for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
      {
         if (aCurrentNode.getLocalName().equals(child.getLocalName()))
         {
            if (AeUtil.compareObjects(aCurrentNode.getNamespaceURI(), child.getNamespaceURI()))
            {
               if (++count == index)
               {
                  aCurrentNode = child;
                  found = true;
                  break;
               }
            }
         }
      }

      // if indexed element not found then create all neccesary elements inbetween
      if (! found)
      {
         String newPrefix = null;
         String namespaceURI = aCurrentNode.getNamespaceURI();
         String newName = null;
         // check if namespace has a prefix and if so use it
         if(namespaceURI != null)
         {
            newPrefix = AeXmlUtil.getPrefixForNamespace((Element)parent, namespaceURI);
            if(newPrefix == null)
            {
               newPrefix = "ans"; //$NON-NLS-1$
               newName = newPrefix + ":" + aCurrentNode.getLocalName(); //$NON-NLS-1$
            }
            else if(newPrefix.equals("")) //$NON-NLS-1$
            {
               newName = aCurrentNode.getLocalName();
               newPrefix = null;
            }
            else
            {
               newName = newPrefix + ":" + aCurrentNode.getLocalName(); //$NON-NLS-1$
            }
         }
         else
         {
            newName = aCurrentNode.getLocalName();
         }
         // create new entries up to and including the index requested
         Document doc = aCurrentNode.getOwnerDocument();
         while (count < index)
         {
            Element element = doc.createElementNS(namespaceURI, newName);
            if(newPrefix != null)
               element.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + newPrefix, namespaceURI); //$NON-NLS-1$
            aCurrentNode = parent.appendChild(element);
            count++;
         }
      }

      return aCurrentNode;
   }

   /**
    * Converts the passed expression string to the integer number it represents.
    * @param aExpr the expression to convert
    * @param aCopyContext
    * @param aNamespaceContext 
    * @return int the index value
    */
   public static int convertIndex(String aExpr, Node aContext, IAeCopyOperationContext aCopyContext, IAeNamespaceContext aNamespaceContext) throws AeBusinessProcessException
   {
      int index = 0;
      boolean numberExpr = true;

      // check each character for digits if not break and return
      for (int i = 0; i < aExpr.length(); ++i)
      {
         int digit = Character.digit(aExpr.charAt(i), 10);
         if (digit >= 0)
         {
            index = (index * 10) + digit;
         }
         else
         {
            numberExpr = false;
            break;
         }
      }

      // if this was not just a numeric expression then evaluate through rhino scope
      if(! numberExpr)
      {
         try
         {
            AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aCopyContext.getBPELNamespace());
            IAeFunctionExecutionContext functionExecContext = aCopyContext.createFunctionExecutionContext(aContext, xpathHelper);
            AeXPathFunctionContext functionContext = new AeXPathFunctionContext(functionExecContext);
            AeXPathNamespaceContext nsContext = new AeXPathNamespaceContext(aNamespaceContext);
            VariableContext varContext = null;
            // Resolve $varName bpel variables if the BPEL version is not 1.1
            if (!IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aCopyContext.getBPELNamespace()))
            {
               varContext = new AeWSBPELXPathVariableContext(functionExecContext, new AeExpressionRunnerVariableResolver(functionExecContext.getAbstractBpelObject()));
            }
            Object data = xpathHelper.executeXPathExpression(aExpr, aContext, functionContext, varContext, nsContext);
            data = xpathHelper.unwrapXPathValue(data, aCopyContext.isEmptyQuerySelectionAllowed());
            if(data instanceof Number)
               index = ((Number)data).intValue();
            else
               // Was index = Integer.parseInt(data.toString());
               // except that one of the test cases processes constructs the variable offset dynamically
               // and XPath returns a double.
               index = Double.valueOf(data.toString()).intValue();
         }
         catch(Exception ex)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeActivityAssignImpl.ERROR_28") + aExpr + " : " + ex.getMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
         }
      }

      return index;
   }

}
 