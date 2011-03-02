// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeServiceDesc.java,v 1.9.14.1 2008/04/21 16:06:00 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;

/**
 * Custom Active Endpoints Service Description implementation.
 */
public class AeServiceDesc implements ServiceDesc
{
   /** The name of this service */
   private String mName;

   /** The documentation of this service */
   private String mDocumentation;

   /** Style/Use */
   private Style mStyle = Style.RPC;
   private Use mUse = Use.ENCODED;

   /** Indicates if the service description has been initialized */
   private boolean mInitialized;

   // Style and Use are related.  By default, if Style==RPC, Use should be
   // ENCODED.  But if Style==DOCUMENT, Use should be LITERAL.  So we want
   // to keep the defaults synced until someone explicitly sets the Use.
   private boolean mUseSet;

   /** Our operations - a list of OperationDescs */
   private ArrayList mOperations = new ArrayList();

   /** A collection of namespaces which will map to this service */
   private List mNamespaceMappings;

   /**
    * Specifies where our WSDL document lives.  If this is non-null, the "?WSDL"
    * generation will automatically return this file instead of dynamically
    * creating a WSDL.  BE CAREFUL because this means that Handlers will
    * not be able to add to the WSDL for extensions/headers....
    */
   private String mWSDLFile;

   /**
    * An endpoint URL which someone has specified for this service.  If
    * this is set, WSDL generation will pick it up instead of defaulting
    * to the transport URL.
    */
   private String mEndpointURL;

   /** Place to store user-extensible service-related properties */
   private HashMap mProperties;

   /** Lookup caches */
   private HashMap mNameToOperationsMap;

   /** List of allowed methods */
   /** null allows everything, an empty ArrayList allows nothing */
   private List mAllowedMethods;

   /** List if disallowed methods */
   private List mDisallowedMethods;

   /** Our typemapping for resolving Java&lt;->XML type issues */
   private TypeMapping mTypeMapping = DefaultTypeMappingImpl.getSingletonDelegate();

   private TypeMappingRegistry mTypeMappingRegistry;
   
   /**
    * Default constructor
    */
   public AeServiceDesc()
   {
   }

   /**
    * What kind of service is this?
    * @see org.apache.axis.description.ServiceDesc#getStyle()
    */
   public Style getStyle()
   {
      return mStyle;
   }

   /**
    * Set the style.
    */
   public void setStyle(Style aStyle)
   {
      mStyle = aStyle;

      // Use hasn't been explicitly set, so track style
      if (! mUseSet)
         mUse = aStyle == Style.RPC ? Use.ENCODED : Use.LITERAL;
   }

   /**
    * What kind of use is this?
    * @see org.apache.axis.description.ServiceDesc#getUse()
    */
   public Use getUse()
   {
      return mUse;
   }

   /**
    * Set the use.
    */
   public void setUse(Use aUse)
   {
      mUseSet = true;
      mUse = aUse;
   }

   /**
    * Determine whether or not this is a "wrapped" invocation.  Whether
    * the outermost XML element of the "main" body element represents a
    * method call, with the immediate children of that element representing
    * arguments to the method.
    *
    * @return true if this is wrapped (i.e. RPC or WRAPPED style), false otherwise
    * @see org.apache.axis.description.ServiceDesc#isWrapped()
    */
   public boolean isWrapped()
   {
      return ((mStyle == Style.RPC) || (mStyle == Style.WRAPPED));
   }

   /**
    * The wsdl file of the service.
    * @return filename or null
    * @see org.apache.axis.description.ServiceDesc#getWSDLFile()
    */
   public String getWSDLFile()
   {
      return mWSDLFile;
   }

   /**
    * Set the wsdl file of the service.  This causes the named file to be returned
    * on a ?wsdl, probe.
    * @param aWSDLFile filename
    * @see org.apache.axis.description.ServiceDesc#setWSDLFile(java.lang.String)
    */
   public void setWSDLFile(String aWSDLFile)
   {
      mWSDLFile = aWSDLFile;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getAllowedMethods()
    */
   public List getAllowedMethods()
   {
      return mAllowedMethods;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setAllowedMethods(java.util.List)
    */
   public void setAllowedMethods(List aAllowedMethods)
   {
      mAllowedMethods = aAllowedMethods;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getTypeMapping()
    */
   public TypeMapping getTypeMapping()
   {
      return mTypeMapping;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setTypeMapping(org.apache.axis.encoding.TypeMapping)
    */
   public void setTypeMapping(TypeMapping aTypeMapping)
   {
      mTypeMapping = aTypeMapping;
   }

   /**
    * Gets the name of the service
    * @see org.apache.axis.description.ServiceDesc#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Sets the name of the service
    * @param name
    * @see org.apache.axis.description.ServiceDesc#setName(java.lang.String)
    */
   public void setName(String name)
   {
      mName = name;
   }

   /**
    * Get the documentation for the service
    * @see org.apache.axis.description.ServiceDesc#getDocumentation()
    */
   public String getDocumentation()
   {
      return mDocumentation;
   }

   /**
    * Set the documentation for the service
    * @see org.apache.axis.description.ServiceDesc#setDocumentation(java.lang.String)
    */
   public void setDocumentation(String aDocumentation)
   {
      mDocumentation = aDocumentation;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getDisallowedMethods()
    */
   public List getDisallowedMethods()
   {
      return mDisallowedMethods;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setDisallowedMethods(java.util.List)
    */
   public void setDisallowedMethods(List aDisallowedMethods)
   {
      mDisallowedMethods = aDisallowedMethods;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#addOperationDesc(org.apache.axis.description.OperationDesc)
    */
   public void addOperationDesc(OperationDesc aOperation)
   {
      mOperations.add(aOperation);
      aOperation.setParent(this);
      if (mNameToOperationsMap == null)
         mNameToOperationsMap = new HashMap();

      // Add name to nameToOperations Map
      String name = aOperation.getName();
      ArrayList overloads = (ArrayList) mNameToOperationsMap.get(name);
      if (overloads == null)
      {
         overloads = new ArrayList();
         mNameToOperationsMap.put(name, overloads);
      }
      overloads.add(aOperation);
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#removeOperationDesc(org.apache.axis.description.OperationDesc)
    */
   public void removeOperationDesc(OperationDesc aOperation) 
   {
		if( mOperations.contains(aOperation) ) 
		{
			mOperations.remove(aOperation);
			aOperation.setParent(null);
			if (mNameToOperationsMap == null)
			   mNameToOperationsMap = new HashMap();
			
			// Remove name from nameToOperations Map
			mNameToOperationsMap.remove(aOperation.getName());
		}
   }
   
   /**
    * Get all the operations as a list of OperationDescs.
    * @return reference to the operations array. This is not a copy
    * @see org.apache.axis.description.ServiceDesc#getOperations()
    */
   public ArrayList getOperations()
   {
      return mOperations;
   }

   /**
    * Get all overloaded operations by name.
    * @param methodName
    * @return null for no match, or an array of OperationDesc objects
    * @see org.apache.axis.description.ServiceDesc#getOperationsByName(java.lang.String)
    */
   public OperationDesc[] getOperationsByName(String methodName)
   {
      if (mNameToOperationsMap == null)
          return null;

      ArrayList overloads = (ArrayList)mNameToOperationsMap.get(methodName);
      if (overloads == null)
          return null;

      OperationDesc [] array = new OperationDesc [overloads.size()];
      return (OperationDesc[])overloads.toArray(array);
   }

   /**
    * Return an operation matching the given method name.  Note that if we
    * have multiple overloads for this method, we will return the first one.
    * @return null for no match
    * @see org.apache.axis.description.ServiceDesc#getOperationByName(java.lang.String)
    */
   public OperationDesc getOperationByName(String methodName)
   {
      if (mNameToOperationsMap == null)
          return null;

      ArrayList overloads = (ArrayList)mNameToOperationsMap.get(methodName);
      if (overloads == null)
          return null;


      return (OperationDesc)overloads.get(0);
   }

   /**
    * Map an XML QName to an operation.  Returns the first one it finds
    * in the case of mulitple matches.
    * @return null for no match
    * @see org.apache.axis.description.ServiceDesc#getOperationByElementQName(javax.xml.namespace.QName)
    */
   public OperationDesc getOperationByElementQName(QName aQname)
   {
      OperationDesc[] overloads = getOperationsByQName(aQname);

      // Return the first one....
      if ((overloads != null) && overloads.length > 0)
         return overloads[0];

      return null;
   }

   /**
    * Return all operations which match this QName.
    * @return null for no match
    * @see org.apache.axis.description.ServiceDesc#getOperationsByQName(javax.xml.namespace.QName)
    */
   public OperationDesc[] getOperationsByQName(QName aQname)
   {
      return getOperationsByName(aQname.getLocalPart());
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setNamespaceMappings(java.util.List)
    */
   public void setNamespaceMappings(List aNamespaces)
   {
      mNamespaceMappings = aNamespaces;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getDefaultNamespace()
    */
   public String getDefaultNamespace()
   {
      if (mNamespaceMappings == null || mNamespaceMappings.isEmpty())
         return null;

      return (String) mNamespaceMappings.get(0);
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setDefaultNamespace(java.lang.String)
    */
   public void setDefaultNamespace(String aNamespace)
   {
      if (mNamespaceMappings == null)
         mNamespaceMappings = new ArrayList();

      mNamespaceMappings.add(0, aNamespace);
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setProperty(java.lang.String, java.lang.Object)
    */
   public void setProperty(String aName, Object aValue)
   {
      if (mProperties == null)
         mProperties = new HashMap();

      mProperties.put(aName, aValue);
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getProperty(java.lang.String)
    */
   public Object getProperty(String aName)
   {
      if (getProperties() == null)
      {
         return null;
      }
      return getProperties().get(aName);
   }
   
   /**
    * Getter for properties map.
    */
   protected Map getProperties()
   {
      return mProperties;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getEndpointURL()
    */
   public String getEndpointURL()
   {
      return mEndpointURL;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setEndpointURL(java.lang.String)
    */
   public void setEndpointURL(String aEndpointURL)
   {
      mEndpointURL = aEndpointURL;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getTypeMappingRegistry()
    */
   public TypeMappingRegistry getTypeMappingRegistry()
   {
      return mTypeMappingRegistry;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#setTypeMappingRegistry(org.apache.axis.encoding.TypeMappingRegistry)
    */
   public void setTypeMappingRegistry(TypeMappingRegistry aTypeMappingRegistry)
   {
      mTypeMappingRegistry = aTypeMappingRegistry;
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#isInitialized()
    */
   public boolean isInitialized()
   {
      return mInitialized;
   }

   /**
    * Sets the status of the service description to be initialed based on input flag.
    * @param aFlag true = initialized, false is not
    */
   public void setInitialized(boolean aFlag)
   {
      mInitialized = aFlag;
   }
}
