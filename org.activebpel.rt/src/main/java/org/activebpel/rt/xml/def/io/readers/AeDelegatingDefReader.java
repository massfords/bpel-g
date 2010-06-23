// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/AeDelegatingDefReader.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.readers;

import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Creates an instance of the def and delegates the reading of its attributes
 * to a def reader returned from the given factory.
 */
public class AeDelegatingDefReader extends AeBaseDefReader
{
   /** The AeBaseDef class type to create */
   private Class mChildClass;
   /** The reader factory - creates a reader to dispatch to. */
   private IAeReaderFactory mReaderVisitorFactory;

   /**
    * Constructor.
    * 
    * @param aChildClass the AeBaseDef type to create
    * @param aReaderVisitorFactory
    */
   public AeDelegatingDefReader(Class aChildClass, IAeReaderFactory aReaderVisitorFactory)
   {
      setChildClass(aChildClass);
      setReaderVisitorFactory(aReaderVisitorFactory);
   }

   /**
    * Dispatch newly created AeBaseDef to an instance of the AeReaderVisitor to set its properties based on
    * the current element.
    * 
    * @see org.activebpel.rt.xml.def.io.readers.AeBaseDefReader#configureChild(org.activebpel.rt.xml.def.AeBaseXmlDef, org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
    */
   protected boolean configureChild(AeBaseXmlDef aParentDef, AeBaseXmlDef aNewDef, Element aElement)
      throws AeException
   {
      IAeReportingDefReader delegate = getReaderFactory().createReportingDefReader(aParentDef, aNewDef, aElement);
      delegate.read(aNewDef, aElement);
      readExtensionAttributes(aNewDef, aElement, delegate.getConsumedAttributes());
      if (delegate.hasErrors())
      {
         // report any errors to the console
         for (Iterator iter = delegate.getErrors().iterator(); iter.hasNext();)
         {
            String error = (String) iter.next();
            AeException.logWarning(error);
         }
      }
      return !delegate.hasErrors();
   }

   /**
    * Returns a mChildClass.newInstance()
    * @see org.activebpel.rt.xml.def.io.readers.AeBaseDefReader#createChild(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
    */
   protected AeBaseXmlDef createChild(AeBaseXmlDef aParent, Element aElement)
      throws AeException
   {
      try
      {
         return (AeBaseXmlDef)getChildClass().newInstance();
      }
      catch (InstantiationException e)
      {
         throw new AeException(
                  AeMessages.format("AeDelegatingDefReader.ERROR_0", getChildClass().getName()), e); //$NON-NLS-1$
      }
      catch (IllegalAccessException e)
      {
         throw new AeException(
                  AeMessages.format("AeDelegatingDefReader.ERROR_0", getChildClass().getName()), e); //$NON-NLS-1$
      }
   }

   /**
    * Reads any extension attributes into the def.
    * 
    * @param aDef
    * @param aElement
    * @param aConsumedAttributes
    */
   protected void readExtensionAttributes(AeBaseXmlDef aDef, Element aElement, Set aConsumedAttributes)
   {
      if (aElement.hasAttributes())
      {
         // Loop through and add all attributes which are qualified but not part of the 
         // xmlns namespace
         NamedNodeMap attrNodes = aElement.getAttributes();
         for (int i = 0, length = attrNodes.getLength(); i < length; i++)
         {
            Attr attr = (Attr) attrNodes.item(i);
            if (!aConsumedAttributes.contains(attr))
            {
               aDef.addExtensionAttributeDef( new AeExtensionAttributeDef(attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue()));
            }
         }
      }
   }

   /**
    * @return Returns the readerVisitorFactory.
    */
   protected IAeReaderFactory getReaderFactory()
   {
      return mReaderVisitorFactory;
   }

   /**
    * @param aReaderVisitorFactory The readerVisitorFactory to set.
    */
   protected void setReaderVisitorFactory(IAeReaderFactory aReaderVisitorFactory)
   {
      mReaderVisitorFactory = aReaderVisitorFactory;
   }

   /**
    * @return Returns the childClass.
    */
   protected Class getChildClass()
   {
      return mChildClass;
   }

   /**
    * @param aChildClass The childClass to set.
    */
   protected void setChildClass(Class aChildClass)
   {
      mChildClass = aChildClass;
   }
}
