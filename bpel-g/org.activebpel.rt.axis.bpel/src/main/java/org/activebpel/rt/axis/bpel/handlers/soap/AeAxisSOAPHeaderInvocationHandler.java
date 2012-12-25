// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/soap/AeAxisSOAPHeaderInvocationHandler.java,v 1.3 2006/10/24 17:46:30 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers.soap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activebpel.rt.util.AeUtil;
import org.apache.axis.message.SOAPHeader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An invocation handler that is used to proxy an Axis SOAPHeader.  This handler only kicks
 * in when the getChildElements() method is called.
 */
public class AeAxisSOAPHeaderInvocationHandler implements InvocationHandler
{
   /** The message being proxied. */
   private SOAPHeader mProxiedSOAPHeader;
   /** A cached version of the header converted to a DOM. */
   private Element mCachedHeaderElement;
   /** The <code>getChildElements()</code> method. */
   private Method mGetChildElementsMethod;
   /** The child elements. */
   private List<Element> mChildElements;
   
   /**
    * Constructs the invocation handler with the given soap header.
    * 
    * @param aProxiedSOAPHeader
    */
   public AeAxisSOAPHeaderInvocationHandler(SOAPHeader aProxiedSOAPHeader)
   {
      setProxiedSOAPHeader(aProxiedSOAPHeader);
   }

   /**
    * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
    */
   public Object invoke(Object aProxy, Method aMethod, Object[] args) throws Throwable
   {
      if (aMethod.equals(getGetChildElementsMethod()))
      {
         if (getChildElements() == null)
         {
            try
            {
               // Workaround for defect 2141, "I keep getting the error
               // [Fatal Error] :-1:-1: Premature end of file. in my WebLogic
               // log file running different tests":
               // The SOAP header is often absent, but the WebLogic 9.2 XML
               // parser emits this message whenever it parses an empty string,
               // so don't give it an empty string.
               String elementString = getProxiedSOAPHeader().getAsString();
               if (AeUtil.isNullOrEmpty(elementString))
               {
                  List<Element> emptyList = Collections.emptyList();
                  setChildElements(emptyList);
               }
               else
               {
                  if (getCachedHeaderElement() == null)
                  {
                     setCachedHeaderElement(getProxiedSOAPHeader().getAsDOM());
                  }
                  NodeList nl = getCachedHeaderElement().getChildNodes();
                  List<Element> list = new ArrayList<Element>(nl.getLength());
                  for (int i = 0; i < nl.getLength(); i++)
                  {
                     Node node = nl.item(i);
                     if (node instanceof Element)
                        list.add((Element) node);
                  }
                  setChildElements(list);
               }
            }
            catch (Exception ex)
            {
               List<Element> emptyList = Collections.emptyList();
               setChildElements(emptyList);
            }
         }
         return getChildElements().iterator();
      }
      return aMethod.invoke(getProxiedSOAPHeader(), args);
   }

   /**
    * Returns the getChildElements() method reference.
    *
    * @throws NoSuchMethodException
    */
   private Method getGetChildElementsMethod() throws NoSuchMethodException
   {
      if (mGetChildElementsMethod == null)
      {
         mGetChildElementsMethod = javax.xml.soap.SOAPHeader.class.getMethod("getChildElements", (Class[])null); //$NON-NLS-1$
      }
      return mGetChildElementsMethod;
   }

   /**
    * @return Returns the proxiedSOAPHeader.
    */
   protected SOAPHeader getProxiedSOAPHeader()
   {
      return mProxiedSOAPHeader;
   }

   /**
    * @param aProxiedSOAPHeader The proxiedSOAPHeader to set.
    */
   protected void setProxiedSOAPHeader(SOAPHeader aProxiedSOAPHeader)
   {
      mProxiedSOAPHeader = aProxiedSOAPHeader;
   }

   /**
    * @return Returns the cachedHeaderElement.
    */
   protected Element getCachedHeaderElement()
   {
      return mCachedHeaderElement;
   }

   /**
    * @param aCachedHeaderElement The cachedHeaderElement to set.
    */
   protected void setCachedHeaderElement(Element aCachedHeaderElement)
   {
      mCachedHeaderElement = aCachedHeaderElement;
   }

   /**
    * @return Returns the childElements.
    */
   private List<Element> getChildElements()
   {
      return mChildElements;
   }

   /**
    * @param aChildElements The childElements to set.
    */
   private void setChildElements(List<Element> aChildElements)
   {
      mChildElements = aChildElements;
   }
}
