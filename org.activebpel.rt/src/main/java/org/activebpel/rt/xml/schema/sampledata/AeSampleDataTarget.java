//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/AeSampleDataTarget.java,v 1.9 2008/03/17 19:36:28 vvelusamy Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.xml.schema.AeAcceptAllCompatibleComplexTypes;
import org.activebpel.rt.xml.schema.AeAcceptAllCompatibleElements;
import org.activebpel.rt.xml.schema.AeAcceptAllComplexTypes;
import org.activebpel.rt.xml.schema.AeAcceptAllGlobalElements;
import org.activebpel.rt.xml.schema.IAeComplexTypeFilter;
import org.activebpel.rt.xml.schema.IAeElementFilter;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Schema;

/**
 *  Sample data target information for sample data generation.
 */
public class AeSampleDataTarget
{
   /** The target Schema Element for sample data generation. */
   private ElementDecl mElement;
   /** The target complex type for sample data generation. */
   private ComplexType mComplexType;
   /** List of available Schemas. */
   private List mSchemas = new ArrayList();
   /** Flag indicating if the target type is a simple type. */
   private boolean mSimpleType;
   /** list of elements to display */
   private List mElementList;
   /** list of complex types to display */
   private List mComplexTypeList;
   /** filter used to select elements */
   private IAeElementFilter mElementFilter;
   /** filter used to select types */
   private IAeComplexTypeFilter mComplexTypeFilter;
   /** The context used when generating sample data. */
   private Object mContext;
   /** Map of prefix->namespace that should be used for sample data generation. */
   private Map mNamespaces = new HashMap();

   /**
    * C'tor.
    */
   public AeSampleDataTarget()
   {
   }
   
   /**
    * Gets a list of available top-level element declarations.
    *
    * @return List list of compatible Element objects.
    */
   public List getElementList()
   {
      if (mElementList == null)
      {
         List compatibleElements = buildCompatibleElementsList();
         setElementList(compatibleElements);
      }
      return mElementList;
   }

   /**
    * Builds a list of available top-level element declarations.
    *
    * If the user provided a filter then the filter will determine what it means
    * for an element to be compatible. If there was no filter defined then we'll
    * create a default one to only include compatible elements. An element
    * is deemed to be compatible if any of the following are true:
    * - element is the same as the target's ElementDecl member data
    * - element is an SG element for the target's ElementDecl
    * - element's type is the same type as the target's ComplexType
    * - element's type is derived from the target's ComplexType
    *
    * @return List list of compatible Element objects.
    */
   protected List buildCompatibleElementsList()
   {
      IAeElementFilter filter = getElementFilter();

      List list = new ArrayList();

      for ( Iterator it=getSchemas().iterator(); it.hasNext(); )
      {
         Schema schema = (Schema)it.next();
         for ( Enumeration enu=schema.getElementDecls(); enu.hasMoreElements(); )
         {
            ElementDecl e = (ElementDecl) enu.nextElement();

            if (filter.accept(e))
            {
               list.add(e);
            }
         }
      }

      return list;
   }

   /**
    * Gets a list of available top-level complex type declarations.
    *
    * @return List of compatible ComplexType objects.
    */
   public List getComplexTypeList()
   {
      if (mComplexTypeList == null)
         setComplexTypeList(buildCompatibleComplexTypeList());
      return mComplexTypeList;
   }

   /**
    * Gets a list of available top-level complex type declarations. This list is
    * filtered to only include compatible types. A type is deemed to be
    * compatible if any of the following are true:
    * - type is the same as the target's ComplexType member data
    * - type is derived from the target's ComplexType
    *
    * @return List of compatible ComplexType objects.
    */
   protected List buildCompatibleComplexTypeList()
   {
      if (isSimpleType())
      {
         return Collections.EMPTY_LIST;
      }

      IAeComplexTypeFilter filter = getComplexTypeFilter();

      List list = new ArrayList();

      for ( Iterator it=getSchemas().iterator(); it.hasNext(); )
      {
         Schema schema = (Schema)it.next();
         for ( Enumeration enu=schema.getComplexTypes(); enu.hasMoreElements(); )
         {
            ComplexType type = (ComplexType) enu.nextElement();

            if (filter.accept(type))
            {
               list.add(type);
            }
         }
      }

      return list;
   }

   /**
    * Sets the complexType target.
    * @param aType
    */
   public void setComplexType(ComplexType aType)
   {
      mComplexType = aType;
   }

   /**
    * Gets the complexType object.
    *
    * @return ComplexType
    */
   public ComplexType getComplexType()
   {
      return mComplexType;
   }

   /**
    * Gets the Element.
    *
    * @return ElementDecl
    */
   public ElementDecl getElement()
   {
      return mElement;
   }

   /**
    * Sets the element.
    *
    * @param aElement
    */
   public void setElement(ElementDecl aElement)
   {
      mElement = aElement;
   }

   /**
    * @return boolean true if the target is a complexType.
    */
   public boolean isComplexType()
   {
      return getComplexType() != null;
   }

   /**
    * Indicates if the target type is a simple type.
    * @return boolean
    */
   public boolean isSimpleType()
   {
      return mSimpleType;
   }

   /**
    * Sets the flag indicates if the target type is a simple type.
    * @param aSimpleType
    */
   public void setSimpleType(boolean aSimpleType)
   {
      mSimpleType = aSimpleType;
   }

   /**
    * @return boolean true if target is an Element.
    */
   public boolean isElement()
   {
      return mElement != null;
   }

   /**
    * Gets the list of available Schemas.
    *
    * @return List
    */
   public List getSchemas()
   {
      return mSchemas;
   }

   /**
    * Sets the list of Schemas.
    *
    * @param aSchemas
    */
   public void setSchemas(List aSchemas)
   {
      mSchemas = aSchemas;
   }

   /**
    * @param aComplexTypeList the complexTypeList to set
    */
   protected void setComplexTypeList(List aComplexTypeList)
   {
      mComplexTypeList = aComplexTypeList;
   }

   /**
    * @param aElementList the elementList to set
    */
   protected void setElementList(List aElementList)
   {
      mElementList = aElementList;
   }

   /**
    * @return the complexTypeFilter
    */
   public IAeComplexTypeFilter getComplexTypeFilter()
   {
      if (mComplexTypeFilter == null)
      {
         if (getElement() == null && getComplexType() == null)
         {
            // user didn't provide a context so return all complex types
            mComplexTypeFilter = new AeAcceptAllComplexTypes();
         }
         else
         {
            // user provided a context so use that to determine the compatible types
            mComplexTypeFilter = new AeAcceptAllCompatibleComplexTypes(getComplexType());
         }
      }
      return mComplexTypeFilter;
   }

   /**
    * @param aComplexTypeFilter the complexTypeFilter to set
    */
   public void setComplexTypeFilter(IAeComplexTypeFilter aComplexTypeFilter)
   {
      mComplexTypeFilter = aComplexTypeFilter;
   }

   /**
    * @return the elementFilter
    */
   public IAeElementFilter getElementFilter()
   {
      if (mElementFilter == null)
      {
         if (getElement() == null && getComplexType() == null)
         {
            // user didn't provide any context so they'll get all of the global
            // elements
            mElementFilter = new AeAcceptAllGlobalElements();
         }
         else
         {
            // user provided a context so use that to determine what is compatible
            mElementFilter = new AeAcceptAllCompatibleElements(getElement(), getComplexType());
         }
      }
      return mElementFilter;
   }

   /**
    * @param aElementFilter the elementFilter to set
    */
   public void setElementFilter(IAeElementFilter aElementFilter)
   {
      mElementFilter = aElementFilter;
   }

   /**
    * @return Returns the namespaces.
    */
   public Map getNamespaces()
   {
      return mNamespaces;
   }

   /**
    * @param aNamespaces the namespaces to set
    */
   public void setNamespaces(Map aNamespaces)
   {
      mNamespaces = aNamespaces;
   }
   
   /**
    * Adds a namespace to the target.
    * 
    * @param aPrefix
    * @param aNamespace
    */
   public void addNamespace(String aPrefix, String aNamespace)
   {
      getNamespaces().put(aPrefix, aNamespace);
   }

   /**
    * @return Returns the context.
    */
   public Object getContext()
   {
      return mContext;
   }

   /**
    * @param aContext the context to set
    */
   public void setContext(Object aContext)
   {
      mContext = aContext;
   }
}
