// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeBPELExtendedWSDLDef.java,v 1.110 2008/03/26 13:56:28 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import com.ibm.wsdl.TypesImpl;
import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.AeWSDLException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUnsynchronizedCharArrayWriter;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.castor.AeSchemaParserUtil;
import org.activebpel.rt.wsdl.def.castor.AeWSDLSchemaResolver;
import org.activebpel.rt.wsdl.def.policy.AePolicyImpl;
import org.activebpel.rt.wsdl.def.policy.IAePolicy;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.exolab.castor.xml.Namespaces;
import org.exolab.castor.xml.schema.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.wsdl.*;
import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Read, write, modify and create BPEL Extended WSDL documents.  This class
 * supports Partner Link Type, Message Property and Message Property Alias
 * extensions to native WSDL elements.  This class takes advantage of the
 * extension architecture provided with the JWSDL API.
 */
public class AeBPELExtendedWSDLDef implements IAeBPELExtendedWSDLConst, IAeMutableNamespaceContext
{
   /** Construct the anyType Qname for check when finding type. */
   protected static final QName ANY_TYPE = new QName(Schema.DEFAULT_SCHEMA_NS, SchemaNames.ANYTYPE);

   /** Map of schemas which have already been loaded and we are caching */
   protected static final Schema sDefaultSchema = new Schema(Schema.DEFAULT_SCHEMA_NS);

   /** Default WSDL def for unnamed locations  */
   protected static final AeBPELExtendedWSDLDef sDefaultDef = new AeBPELExtendedWSDLDef();

   /** Map of schemas which have already been loaded and we are caching */
   protected final Map<String, Schema> mSchemaDefs = new LinkedHashMap<String, Schema>(); // Use a linked hashmap because schema import order matters

   /** List of Partner Link Type extensibility element implementations. */
   private List<IAePartnerLinkType> mPartnerLinkTypeExtElements;

   /** List of message property extensibility element implementations. */
   private List<IAeProperty> mPropExtElements;

   /** List of message property alias extensibility element implementations. */
   private List<IAePropertyAlias> mPropAliasExtElements;

   /** Extension Registry for BPEL extensions. */
   private ExtensionRegistry mExtRegistry;

   /** WSDL Definition. */
   private Definition mDefinition;

   /** Location hint used to load the wsdl. */
   private String mLocation;

   /** The standard schema resolver. */
   private IAeStandardSchemaResolver mStandardResolver;

	/** WSDL locator to use to load schemas. */
   private WSDLLocator mLocator;

   /** List of references this WSDL object imports or includes */
   private List<String> mSchemaReferences = Collections.<String>emptyList();
   
   /** List of policy extensibility element implementations. */
   private List<IAePolicy> mPolicyExtElements;
   
   /**
    * Default Constructor.  Dummy blank entry creation.
    */
   public AeBPELExtendedWSDLDef()
   {
      try
      {
         WSDLFactory lFactory = WSDLFactory.newInstance();
         Definition lDef = lFactory.newDefinition();
         mDefinition = lDef;
         mPartnerLinkTypeExtElements = Collections.<IAePartnerLinkType>emptyList();
         mPropExtElements = Collections.<IAeProperty>emptyList();
         mPropAliasExtElements = Collections.<IAePropertyAlias>emptyList();
         mPolicyExtElements = Collections.<IAePolicy>emptyList();
      }
      catch (WSDLException e)
      {
         // should never happen, but just in case
         throw new RuntimeException(e.getMessage());
      }
   }

   /**
    * Constructor.
    * @param aLocator
    * @param aLocation The wsdl location hint.
    */
   public AeBPELExtendedWSDLDef(WSDLLocator aLocator, String aLocation, IAeStandardSchemaResolver aStandardResolver)
   throws AeWSDLException
   {
      mLocation = aLocation;
      mLocator = aLocator;
      mStandardResolver = aStandardResolver;
      read(aLocator);
   }

   /**
    * Constructor.  Reads in a WSDL DOM Document containing potential Partner
    * Link Type, Property and Property Alias extensions.
    * @param aLocator locator resolves WSDL imports used by the given WSDL document
    */
   public AeBPELExtendedWSDLDef(WSDLLocator aLocator, IAeStandardSchemaResolver aStandardResolver) throws AeWSDLException
   {
      this(aLocator, null, aStandardResolver);
   }

   /**
    * Copy constructor, just does a reread for now, better optimizations in the future here.
    * @param aDef
    */
   public AeBPELExtendedWSDLDef(AeBPELExtendedWSDLDef aDef) throws AeWSDLException
   {
      this(aDef.getLocator(), aDef.getLocationHint(), aDef.getStandardResolver());
   }

   /**
    * Creates an entry to hold a single schema.  This is a place holder for more direct schema
    * import handling in the future.
    */
   public AeBPELExtendedWSDLDef(Schema aSchema) throws AeWSDLException
   {
      this(new Schema[] {aSchema});
      getWSDLDef().setTargetNamespace(aSchema.getTargetNamespace());
   }
   
   /**
    * Accepts array of schemas
    * @param aSchemas
    * @throws AeWSDLException
    */
   public AeBPELExtendedWSDLDef(Schema[] aSchemas) throws AeWSDLException
   {
      this();
      if(aSchemas != null)
      {
         Set<String> namespaces = new HashSet<String>();
          for (Schema schema : aSchemas) {
              if (schema != null) {
                  catalogSchemaAndImports(schema, namespaces, true);
              }
          }
         refreshSchemaRefs();
      }
   }

   /**
    * Refreshes the schema references from the list of loaded schema defs.
    * 
    * @throws AeWSDLException
    */
   protected void refreshSchemaRefs() throws AeWSDLException
   {
      List<String> schemaRefs = new ArrayList<String>();
      for (Iterator<Schema> iter = getSchemas(); iter.hasNext(); )
      {
         Schema schema = iter.next();
         try
         {
            String schemaLoc = schema.getSchemaLocation();
            if (AeUtil.notNullOrEmpty(schemaLoc))
               schemaRefs.add(AeUTF8Util.urlDecode(schemaLoc));
         }
         catch (UnsupportedEncodingException ex)
         {
            throw new AeWSDLException(ex);
         }
      }
      mSchemaReferences = schemaRefs;
   }

   /**
    * Returns true if the part is a complex encoded type.
    * @param aPart
    */
   public boolean isComplexEncodedType(Part aPart)
   {
      try
      {
         if (aPart.getElementName() != null)
            return true;

         return AeXmlUtil.isComplexOrAny(findType(aPart.getTypeName()));
      }
      catch (AeException e)
      {
         return false;
      }
   }

   /**
    * Returns true if the type of the part is derived from a simple type.
    * @param aPart
    */
   public boolean isDerivedSimpleType(Part aPart)
   {
      if (aPart.getElementName() == null)
      {
         try
         {
            XMLType type = findType(aPart.getTypeName());
            if (type != null)
               return type.isSimpleType() && type.getDerivationMethod() != null;
         }
         catch(AeException e)
         {
         }
      }
      return false;
   }


   /**
    * Creates a minimal WSDL Definition model.
    * @param aTargetNS WSDL Target Namespace.
    * @param aDefName WSDL Definitions' name attribute. Can be null.
    * @throws AeWSDLException
    */
   public void createWSDL(String aTargetNS, String aDefName) throws AeWSDLException
   {
      ClassLoader previousClassLoader = null;
      try
      {
         // Set class loader to that which loaded us, to ensure we load the xerces parser
         // Crimson was giving poor error messages for parse failures
         previousClassLoader = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

         WSDLFactory lFactory = WSDLFactory.newInstance();
         WSDLWriter lWriter = lFactory.newWSDLWriter();
         Definition lDef = lFactory.newDefinition();

         lDef.setTargetNamespace(aTargetNS);
         lDef.addNamespace("tns", aTargetNS); //$NON-NLS-1$

         // Set optional name attribute in the Definitions element.
         if (aDefName != null)
            lDef.setQName(new QName("", aDefName)); //$NON-NLS-1$

         // Generate a DOM document from our WSDL model.
         Document mDoc = lWriter.getDocument(lDef);

         // Create Extention Registry for this definition so that we're aware
         // of extension elements.
         read(mDoc.getDocumentElement());
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }
      finally
      {
         if( previousClassLoader != null )
            Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
   }

   /**
    * Reads in a WSDL Element containing potential Partner Link Type, message
    * Property and message Property Alias extension elements.
    * @param aLocator WSDL locator which supports reading of WSDL.
    * @throws AeWSDLException
    */
   public void read(WSDLLocator aLocator) throws AeWSDLException
   {
      ClassLoader previousClassLoader = null;
      try
      {
         // Set class loader to that which loaded us, to ensure we load the xerces parser
         // Crimson was giving poor error messages for parse failures
         previousClassLoader = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

         WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
         reader.setFeature("javax.wsdl.verbose", false); //$NON-NLS-1$
         reader.setFeature("javax.wsdl.importDocuments", true); //$NON-NLS-1$

         // Register a BPEL aware Extension Registry with the WSDL reader.
         reader.setExtensionRegistry(getExtensionRegistry());

         // Read in the WSDL DOM document into a WSDL definition.
         Definition def = reader.readWSDL(aLocator);
         processExtElements(def);

         // Touch the XML DOM extension nodes reachable from the WSDL definition
         // to finish any deferred DOM work. This is a workaround for defect
         // 1718, "Getting a MalformedURLException when running a load test on
         // .NET in a clustered environment," which can occur when multiple
         // threads examine the same extension element (and is not restricted
         // to just the .NET environment).
         touchXmlNodes(def);

         // Save the WSDL definition.
         setWSDLDef(def);
      }
      catch (IllegalArgumentException e)
      {
         throw new AeWSDLException(e);
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }
      finally
      {
         if( previousClassLoader != null )
            Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
   }

   /**
    * Reads in a WSDL Element containing potential Partner Link Type, message
    * Property and message Property Alias extension elements.
    * @throws AeWSDLException
    */
   public void read(Element aWSDLElement) throws AeWSDLException
   {
      try
      {
         WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
         reader.setFeature("javax.wsdl.verbose", false); //$NON-NLS-1$
         reader.setFeature("javax.wsdl.importDocuments", true); //$NON-NLS-1$

         // Register a BPEL aware Extension Registry with the WSDL reader.
         reader.setExtensionRegistry(getExtensionRegistry());

         // Read in the WSDL DOM document into a WSDL definition.
         Definition def = reader.readWSDL((WSDLLocator)null, aWSDLElement);
         processExtElements(def);

         // Save the WSDL definition.
         setWSDLDef(def);
      }
      catch (IllegalArgumentException e)
      {
         throw new AeWSDLException(e);
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }
   }

   /**
    * Processes the extensibility elements read by the WSDLReader and sets them
    * in our extended definition.
    * @param aDef the definition which was read
    */
   private void processExtElements(Definition aDef) throws AeWSDLException
   {
      // Build lists of found extensibility elements.
      List<IAePartnerLinkType> partnerLinks = new ArrayList<IAePartnerLinkType>();
      List<IAeProperty> properties = new ArrayList<IAeProperty>();
      List<IAePropertyAlias> propertyAliases = new ArrayList<IAePropertyAlias>();
      List<IAePolicy> policies = new ArrayList<IAePolicy>();

       for (ExtensibilityElement extElem : (Iterable<ExtensibilityElement>) aDef.getExtensibilityElements()) {
           if (extElem instanceof IAePartnerLinkType)
               partnerLinks.add((IAePartnerLinkType) extElem);
           else if (extElem instanceof IAeProperty)
               properties.add((IAeProperty) extElem);
           else if (extElem instanceof IAePropertyAlias)
               propertyAliases.add((IAePropertyAlias) extElem);
           else if (extElem instanceof IAePolicy)
               policies.add((IAePolicy) extElem);
       }

      setPartnerLinkTypeExtElements(partnerLinks);
      setPropExtElements(properties);
      setPropAliasExtElements(propertyAliases);
      setPolicyExtElements(policies);
      
      // build the schema map
      buildSchemaMap(aDef);
   }

   /**
    * Create an extension registry for the BPEL extensions to WSDL. This
    * registry is used to associate a serializers, deserializers, and
    * implementation object for each extension element. Supported extensions
    * include Partner Link Types, Message Properties and Message Property Alias.
    *
    * @return ExtensionRegistry - Returns the extension registry.
    */
   private ExtensionRegistry loadExtensionRegistry()
   {
      // Create a new ExtensionRegistry for the BPEL extensions to WSDL.
      // This registry is used to associate a serializers, deserializers, and
      // implementation object for each extension element.
      ExtensionRegistry registry = new ExtensionRegistry();
      AeWSDLExtensionLoader.loadRegistry(registry);
      return registry;
   }

   /**
    * Serializes the WSDL definition out to the given writer stream.
    *
    * @param aWriter output character stream.
    */
   public void write(Writer aWriter) throws AeWSDLException
   {
      Definition aDef = getWSDLDef();

      try
      {
         WSDLFactory lFactory = WSDLFactory.newInstance();
         WSDLWriter lWriter = lFactory.newWSDLWriter();
         lWriter.writeWSDL(aDef, aWriter);
         aWriter.flush();
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }
      catch (IOException e)
      {
         throw new AeWSDLException(e);
      }
   }

   /**
    * Serializes the WSDL definition returning it as a DOM document.
    * @return Document
    * @throws AeWSDLException
    */
   public Document write() throws AeWSDLException
   {
      Document lWSDLDoc = null;

      Definition lDef = getWSDLDef();

      if (lDef != null)
      {
         try
         {
            WSDLFactory lFactory = WSDLFactory.newInstance();
            WSDLWriter lWriter = lFactory.newWSDLWriter();
            lWSDLDoc = lWriter.getDocument(lDef);
         }
         catch (WSDLException e)
         {
            throw new AeWSDLException(e);
         }
      }
      return lWSDLDoc;
   }

   /**
    * Used for debugging WSDL output, and WSDL generation from CatalogServlet.
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      try
      {
          AeUnsynchronizedCharArrayWriter sw = new AeUnsynchronizedCharArrayWriter();
          write(sw);
          return new String( sw.toCharArray() );
      }
      catch( AeWSDLException e )
      {
          e.logError();
          return null;
      }
   }

   /**
    * Creates a new Partner Link Type extension element adding it to the WSDL
    * definition.
    *
    * @param aBpelNamespace the BPEL namespace of the host process.
    * @param aName the name of this Partner Link Type.
    * @return IAePartnerLinkType the created Partner Link implementation.
    *
    * @throws AeWSDLException If PLT already exists OR wrapping caught exception creating def.
    */
   public IAePartnerLinkType createPartnerLinkType(String aBpelNamespace, String aName) throws AeWSDLException
   {
      IAeBPELWSDLExtensionIOFactory extFactory = AeBPELWSDLExtensionIOFactory.getFactory(aBpelNamespace);
      QName pltQName = extFactory.getPartnerLinkTypeQName();

      if ( getPartnerLinkType(pltQName.getNamespaceURI(), aName) != null )
         throw new AeWSDLException( AeMessages.getString("AeBPELExtendedWSDLDef.ERROR_12") ); //$NON-NLS-1$

      Definition lDef = getWSDLDef();
      AePartnerLinkTypeImpl lPartnerLinkType = null;
      ExtensionRegistry lExtReg = getExtensionRegistry();

      try
      {
         lPartnerLinkType = (AePartnerLinkTypeImpl)lExtReg.createExtension(Definition.class, pltQName);
         lPartnerLinkType.setName(aName);
         lPartnerLinkType.setElementType(pltQName);
         lPartnerLinkType.setRequired(Boolean.TRUE);
         lDef.addExtensibilityElement(lPartnerLinkType);

         // Add the new Partner Link implementation to the list of Partner Links.
         getPartnerLinkTypeExtElements().add(lPartnerLinkType);
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }

      return lPartnerLinkType;
   }

   /**
    * Creates a new Property extension element adding it to the WSDL definition.
    *
    * @param aBpelNamespace the BPEL namespace of the host process.
    * @param aName the name of this Property.
    * @param aTypeName the property type name.
    * @param aIsType true if property is of schema type, false if schema element type.
    * @return IAeProperty the created Property implementation.
    * @throws AeWSDLException
    */
   public IAeProperty createProperty(String aBpelNamespace, String aName, QName aTypeName,
                                      boolean aIsType) throws AeWSDLException
   {
      IAeBPELWSDLExtensionIOFactory extFactory = AeBPELWSDLExtensionIOFactory.getFactory(aBpelNamespace);

      // Check if this property element is already defined.
      if ( getProperty(extFactory.getPropertyQName().getNamespaceURI(), aName, aTypeName) != null )
      {
         String errMsg =
            MessageFormat.format( AeMessages.getString("AeBPELExtendedWSDLDef.ERROR_PROP_EXISTS"), //$NON-NLS-1$
                    aName, aTypeName, extFactory.getPropertyQName().getNamespaceURI());

         throw new AeWSDLException(errMsg);
      }

      ExtensionRegistry lExtReg = getExtensionRegistry();
      Definition lDef = getWSDLDef();
      AePropertyImpl lProp = null;

      try
      {
         lProp = (AePropertyImpl)lExtReg.createExtension(Definition.class, extFactory.getPropertyQName());
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }

      lProp.setQName(new QName(getTargetNamespace(), aName));
      lProp.setElementType(extFactory.getPropertyQName());
      lProp.setRequired(Boolean.TRUE);

      if ( aIsType )
         lProp.setTypeName(aTypeName);
      else
         lProp.setElementName(aTypeName);

      lDef.addExtensibilityElement(lProp);

      // Add the new Property implementation to the list of Properties.
      getPropExtElements().add(lProp);

      return lProp;
   }

   /**
    * Creates a new Property Alias extension element adding it to the WSDL
    * definition.
    *
    * @param aBpelNamespace the BPEL namespace of the host process.
    * @param aPropName the name of this Property Alias.
    * @param aTypeName the type name of this Property Alias.
    * @param aType indicator: message, element, or complex type name
    * @param aPart the part value of this Property Alias.
    * @param aQuery the query value of this Property Alias.
    * @return IAePropertyAlias the created Property Alias implementation.
    * @throws AeWSDLException
    */
   public IAePropertyAlias createPropertyAlias( String aBpelNamespace, QName aPropName, QName  aTypeName,
                                                 int aType, String aPart, String aQuery) throws AeWSDLException
   {
      IAeBPELWSDLExtensionIOFactory extFactory = AeBPELWSDLExtensionIOFactory.getFactory(aBpelNamespace);

      // Check if this property alias element is already defined.
      if ( getPropertyAlias( extFactory.getPropertyAliasQName().getNamespaceURI(),
                             aPropName, aTypeName, aPart, aQuery ) != null )
      {
         String errMsg =
            MessageFormat.format( AeMessages.getString("AeBPELExtendedWSDLDef.ERROR_PROP_ALIAS_EXISTS"), //$NON-NLS-1$
                    aPropName, aTypeName, extFactory.getPropertyAliasQName().getNamespaceURI());

         throw new AeWSDLException(errMsg);
      }

      Definition lDef = getWSDLDef();
      AePropertyAliasImpl lPropAlias = null;
      ExtensionRegistry lExtReg = getExtensionRegistry();

      try
      {
         lPropAlias = (AePropertyAliasImpl)lExtReg.createExtension( Definition.class,
                                                                    extFactory.getPropertyAliasQName() );
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }

      lPropAlias.setPropertyName(aPropName);
      lPropAlias.setElementType(extFactory.getPropertyAliasQName());

      switch(aType)
      {
         case IAePropertyAlias.MESSAGE_TYPE:
            lPropAlias.setMessageName(aTypeName);
            break;

         case IAePropertyAlias.ELEMENT_TYPE:
            lPropAlias.setElementName(aTypeName);
            break;

         case IAePropertyAlias.TYPE:
            lPropAlias.setTypeName(aTypeName);
            break;
      }

      lPropAlias.setPart(aPart);
      lPropAlias.setQuery(aQuery);
      lPropAlias.setRequired(Boolean.TRUE);

      lDef.addExtensibilityElement(lPropAlias);

      // Add the new Property Alias implementation to the list of Property Alias.
      getPropAliasExtElements().add(lPropAlias);

      return lPropAlias;
   }

   /**
    * Creates a new Policy extension element adding it to the WSDL
    * definition.
    * 
    * @param aBpelNamespace the BPEL namespace of the host process.
    * @param aNamespace namespace for the reference uri (optional)
    * @param aId the wsu:Id for the reference uri
    * @return IAePolicy the created Policy implementation.
    * 
    * @throws AeWSDLException If Policy with same already exists OR wrapping caught exception creating def.
    */
   public IAePolicy createPolicy(String aBpelNamespace, String aNamespace, String aId) throws AeWSDLException
   {
      QName policyQName = new QName(IAeConstants.WSP_NAMESPACE_URI, IAePolicy.POLICY_ELEMENT);
      
      if ( getPolicy(aNamespace, aId) != null )
         throw new AeWSDLException( AeMessages.format("AeBPELExtendedWSDLDef.ERROR_DUPLICATE_WSU_ID", aId) ); //$NON-NLS-1$
         
      Definition lDef = getWSDLDef();
      AePolicyImpl policy = null;
      ExtensionRegistry lExtReg = getExtensionRegistry();

      try
      {
         policy = (AePolicyImpl)lExtReg.createExtension(Definition.class, policyQName);
         policy.setReferenceId(aId);
         policy.setElementType(policyQName);
         policy.setRequired(Boolean.TRUE);
         lDef.addExtensibilityElement(policy);

         // Add the new policy implementation to the list
         getPolicyExtElements().add(policy);
      }
      catch (WSDLException e)
      {
         throw new AeWSDLException(e);
      }

      return policy;
   }
   
   /**
    * Retrieve a Partner Link Type extension implementation by name.
    *
    * @param aName the name of the Partner Link Type to retrieve.
    * @return AePartnerLinkTypeImpl the Partner Link Type implementation object,
    * null if not found.
    */
   public IAePartnerLinkType getPartnerLinkType(String aName)
   {
      return getPartnerLinkType(null, aName);
   }

   /**
    * Retrieve a Partner Link Type extension implementation by name and optional namespace.
    *
    * @param aPltNamespace the namespace of the Partner Link Type element. If null then only
    *    get the PLT by name.
    * @param aName the name of the Partner Link Type to retrieve.
    * @return AePartnerLinkTypeImpl the Partner Link Type implementation object,
    * null if not found.
    */
   private IAePartnerLinkType getPartnerLinkType(String aPltNamespace, String aName)
   {
      IAePartnerLinkType lPartnerLinkType = null;

      Iterator<IAePartnerLinkType> lIt = getPartnerLinkTypeExtElements().iterator();
      while (lIt.hasNext())
      {
         IAePartnerLinkType lPartnerLinkElem = lIt.next();

         ExtensibilityElement extElem = (ExtensibilityElement)lPartnerLinkElem;

         if ( lPartnerLinkElem.getName().equals(aName) &&
              ( aPltNamespace == null ||
                ( aPltNamespace != null && extElem.getElementType().getNamespaceURI().equals(aPltNamespace))) )
         {
            lPartnerLinkType = lPartnerLinkElem;
            break;
         }
      }
      return lPartnerLinkType;
   }

   /**
    * Retreive a Property extension implementation by name and optional namespace and type.
    *
    * @param aPropertyNamespace the namespace of the Property element or null if not to be considered.
    * @param aPropName the name of the Property to retrieve.
    * @param aType  the message property type or null if not to be considered.
    * @return IAeProperty a Property implementation object or null if not found.
    */
   private IAeProperty getProperty(String aPropertyNamespace, String aPropName, QName aType)
   {
      IAeProperty property = null;

       for (IAeProperty propElem : getPropExtElements()) {
           ExtensibilityElement extElem = (ExtensibilityElement) propElem;

           if (aPropName.equals(propElem.getQName().getLocalPart()) &&
                   (aPropertyNamespace == null ||
                           (aPropertyNamespace != null && extElem.getElementType().getNamespaceURI().equals(aPropertyNamespace))
                   ) &&
                   (aType == null || (aType != null && aType.equals(propElem.getTypeName())))) {
               property = propElem;
               break;
           }
       }
      return property;
   }

   /**
    * Retreive a Property Alias extension implementation by property name, property type, part name
    * and query.
    *
    * @param aPropAliasNamespace the namespace of the Property Alias element or null if not to be considered.
    * @param aPropName the Property name.
    * @param aTypeName the type name of this Property Alias.
    * @param aPart the part value of this Property Alias.
    * @param aQuery the query value of this Property Alias.
    *
    * @return IAePropertyAlias a Property implementation object or null if not found.
    */
   private IAePropertyAlias getPropertyAlias(String aPropAliasNamespace, QName aPropName, QName aTypeName,
                                              String aPart, String aQuery)
   {
      IAePropertyAlias propertyAlias = null;

       for (IAePropertyAlias propAliasElem : getPropAliasExtElements()) {
           ExtensibilityElement extElem = (ExtensibilityElement) propAliasElem;

           if (aPropName.equals(propAliasElem.getPropertyName()) &&
                   aTypeName.equals(propAliasElem.getQName()) &&
                   AeUtil.compareObjects(aPart, propAliasElem.getPart()) &&
                   AeUtil.compareObjects(aQuery, propAliasElem.getQuery()) &&
                   (aPropAliasNamespace == null ||
                           (aPropAliasNamespace != null && extElem.getElementType().getNamespaceURI().equals(aPropAliasNamespace))
                   )) {
               propertyAlias = propAliasElem;
               break;
           }
       }
      return propertyAlias;
   }

   /**
    * Removes a Partner Link Type extension implementation by object instance.
    *
    * @param aPartnerLinkType The partner link type instance to remove.
    */
   public void removePartnerLinkType(IAePartnerLinkType aPartnerLinkType)
   {
      getPartnerLinkTypeExtElements().remove( aPartnerLinkType );
      getWSDLDef().getExtensibilityElements().remove( aPartnerLinkType );
   }

   /**
    * Removes a Property extension implementation by object instance.
    *
    * @param aProperty The property instance to be removed.
    */
   public void removeProperty(IAeProperty aProperty)
   {
      getPropExtElements().remove( aProperty );
      getWSDLDef().getExtensibilityElements().remove( aProperty );
   }

   /**
    * Removes a Property Alias extension implementation by object instance.
    *
    * @param aAlias the alias instance to be removed.
    */
   public void removePropertyAlias(IAePropertyAlias aAlias)
   {
      getPropAliasExtElements().remove( aAlias );
      getWSDLDef().getExtensibilityElements().remove( aAlias );
   }

   /**
    * Removes a Policy extension implementation by object instance
    * 
    * @param aPolicy the policy to be removed
    */
   public void removePolicy(IAePolicy aPolicy)
   {
      getPolicyExtElements().remove( aPolicy );
      getWSDLDef().getExtensibilityElements().remove( aPolicy );
   }
   
   //
   // Helper methods
   //

   /**
    * Get an iterator of Operations associated with the given a PortType name.
    * @param aPortType a PortType QName.
    * @return Iterator for list of Operations.
    */
   @SuppressWarnings("unchecked")
   public Iterator<Operation> getOperations(QName aPortType)
   {
      Definition lDef = getWSDLDef();
      List<Operation> lOperations = null;

      if (lDef != null)
      {
         PortType lPortType = lDef.getPortType(aPortType);
         if (lPortType != null)
            lOperations = lPortType.getOperations();
      }

      if (lOperations == null)
         lOperations = Collections.emptyList();

      return lOperations.iterator();
   }

   /**
    * Get a single operation by name associated with the given a PortType name.
    * Returns null if no operation with the given name is found or is undefined.
    * This method does not support operation overloading.
    * 
    * @param aPortType
    * @param aOperationName
    */
   public Operation getOperation(QName aPortType, String aOperationName)
   {
      Definition lDef = getWSDLDef();

      if (lDef != null)
      {
         PortType lPortType = lDef.getPortType(aPortType);
         if (lPortType != null)
         {
            Operation operation = lPortType.getOperation(aOperationName, null, null);
            if (!operation.isUndefined())
               return operation;
         }
      }

      return null;
   }

   /**
    * Return true if the portType/operation combination already exists.
    *
    * @param aPortType The port type to check.
    * @param aOpName The operation name to check.
    *
    * @return boolean True if the operation already exists in the definition.
    */
   public boolean operationExists(QName aPortType, String aOpName)
   {
      Iterator<Operation> iter = getOperations(aPortType);
      while (iter.hasNext())
      {
         Operation oper = iter.next();
         if (oper.getName().equals(aOpName.trim()))
            return true;
      }

      return false;
   }

   /**
    * Returns the named portType, or null if it does not exist.
    *
    * @param aPortType Name of the desired portType.
    *
    * @return PortType
    */
   public PortType getPortType(QName aPortType)
   {
      Definition lDef = getWSDLDef();
      PortType pt = null;

      if (lDef != null)
         pt = lDef.getPortType(aPortType);

      return pt;
   }

   /**
    * Add the QNames specfied in the array list. New prefixes, of the form
    * ns1, ns2 ... ns<i>N</i>, etc., will be added as needed and available.
    *
    * @param aQNames The list of required QNames.
    */
   public void addQNames( Collection<QName> aQNames )
   {
      Definition lDef = getWSDLDef();

      if (aQNames.size() > 0 && lDef != null)
      {
         @SuppressWarnings("unchecked")
		Set<String> prefixes = lDef.getNamespaces().keySet();

         Iterator<QName> iter = aQNames.iterator();
         while (iter.hasNext())
         {
            QName name = iter.next();
            String uri = name.getNamespaceURI();
            String prefix = lDef.getPrefix(uri);
            if (prefix == null || prefix.length() <= 0)
            {
               // Add the namespace with appropriate prefix.
               //
               for (int i = 1; i < 1000; i++)
               {
                  prefix = "ns" + i; //$NON-NLS-1$
                  if (!prefixes.contains(prefix))
                  {
                     lDef.addNamespace(prefix, uri);
                     break;
                  }
               }
            }
         }
      }
   }

   /**
    * Add an import reference to the WSDL.
    *
    * @param aLoc The physical location.
    * @param aNS The namespace for.
    */
   public void addImport( String aLoc, String aNS )
   {
      if ( !AeUtil.isNullOrEmpty( aLoc ) && !AeUtil.isNullOrEmpty( aNS ))
      {
         Import imp = null ;
         if ( getWSDLDef() != null )
         {
            // Don't add import to self - endless loop recursion on parse.
            // TODO: figure out the right way to prevent infinite recursion here.
            // if ( getWSDLDef().getTargetNamespace().equals( aNS ))
            //   return ;

            // Create an import as specified by the caller.
            //
            @SuppressWarnings("unchecked")
			List<Import> imports = getWSDLDef().getImports( aNS );
            if ( imports != null )
            {
                for (Import anImport : imports) {
                    imp = anImport;
                    if (imp.getLocationURI().equals(aLoc) &&
                            imp.getNamespaceURI().equals(aNS)) {
                        // Already have this import - don't add another.  Definition
                        //  apparently doesn't bother to check.
                        //
                        return;
                    }
                }
            }
         }

         imp = getWSDLDef().createImport();
         imp.setLocationURI( aLoc );
         imp.setNamespaceURI( aNS );
         getWSDLDef().addImport( imp );
      }
   }

   /**
    * Add a portType/operation combination to the definition.  Creates the
    * named portType if it does not exist.
    *
    * @param aPortType  The port type's QName.
    * @param aOperation The operation to add.
    * @param aLoc The physical location, to add an Import if required, or null.
    * @param aNS The namespace for an Import if required, or null.
    */
   public void addOperation(QName aPortType, Operation aOperation, String aLoc, String aNS)
   {
      Definition lDef = getWSDLDef();

      if (lDef != null)
      {
         PortType pt = getPortType(aPortType);
         if (pt == null)
         {
            pt = lDef.createPortType();
            if (pt != null)
            {
               pt.setQName(aPortType);
               pt.setUndefined(false);
               lDef.addPortType(pt);
            }
         }

         if (pt != null)
         {
            if (!AeUtil.isNullOrEmpty(aLoc) && !AeUtil.isNullOrEmpty(aNS))
            {
               // Create an import as specified by the caller.
               //
               Import imp = getWSDLDef().createImport();
               imp.setLocationURI(aLoc);
               imp.setNamespaceURI(aNS);
               getWSDLDef().addImport(imp);
            }

            // Add the required namespace references.
            //
            List<QName> qNames = new ArrayList<QName>();
            // If Input message is present add its QName
            if (aOperation.getInput() != null)
            {
               Message inputMsg = aOperation.getInput().getMessage();
               if (inputMsg != null)
               {
                  qNames.add(inputMsg.getQName());
               }
            }
            // If Output message is present add its QName
            if (aOperation.getOutput() != null)
            {
               Message outputMsg = aOperation.getOutput().getMessage();
               if (outputMsg != null)
               {
                  qNames.add(outputMsg.getQName());
               }
            }
            // If Fault messages are present add their QNames
            @SuppressWarnings("unchecked")
			Map<String,?> faultNames = aOperation.getFaults();
            if (faultNames != null)
            {
               Iterator<String> iter = faultNames.keySet().iterator();
               while (iter.hasNext())
               {
                  Fault fault = aOperation.getFault(iter.next());
                  qNames.add(fault.getMessage().getQName());
               }
            }

            addQNames(qNames);

            // Finally, add the new operation to the WSDL.
            //
            pt.addOperation(aOperation);
         }
      }
   }

   /**
    * Add a Message to the definition.  The message is referenced by the
    * Input and Output "message" attribute.
    *
    * @param aMessage The message to add.
    */
   public void addMessage(Message aMessage)
   {
      Definition lDef = getWSDLDef();
      if (lDef != null)
      {
         if (aMessage != null)
         {
            // Add the messages QName to the array..
            List<QName> qNames = new ArrayList<QName>();
            // Add it to the current QName list.
            qNames.add(aMessage.getQName());

            // Add the namespace reference
            addQNames(qNames);

            // Now add the message to the definition
            lDef.addMessage(aMessage);
         }
      }
   }

   /**
    * Get the types area of the WSDL.
    *
    * @return Types, null if types area is not in WSDL file.
    */
   public Types getTypes()
   {
      return getWSDLDef().getTypes();
   }

   /**
    * Return an iterator of parsed schema objects defined within the Types section.
    * @return Iterator, iterator of declared Schema objects.
    */
   public Iterator<Schema> getSchemas()
   {
      return mSchemaDefs.values().iterator();
   }

   /**
    * Adds a Schema element to the types section of the def as an
    * UnknownExtensibilityElement.
    * @param aSchema The Schema element to add.
    * @param aRebuildSchemas True if schemas should be rebuilt, False otherwise
    * @throws AeWSDLException
    */
   public void addSchema(Element aSchema, boolean aRebuildSchemas) throws AeWSDLException
   {
      Definition def = getWSDLDef();

      if (def != null)
      {
         UnknownExtensibilityElement extElement = new UnknownExtensibilityElement();
         extElement.setElement(aSchema);

         if ( def.getTypes() == null )
            def.setTypes(new TypesImpl());

         def.getTypes().addExtensibilityElement(extElement);
         
      }

      if (aRebuildSchemas)
         rebuildSchemas();
   }
   
   /**
    * Adds a Schema element to the types section of the def as an
    * UnknownExtensibilityElement.
    * @param aSchema The Schema element to add.
    * @throws AeWSDLException
    */
   public void addSchema(Element aSchema) throws AeWSDLException
   {
      addSchema(aSchema, true);
   }

   /**
    * Clears the member data for the previously built/cached schemas and recreates
    * it. Should be called whenever a new Schema object is added to the def.
    * @throws AeWSDLException
    */
   public void rebuildSchemas() throws AeWSDLException
   {
      mSchemaDefs.clear();
      buildSchemaMap(getWSDLDef());
   }

   /**
    * Build the internal map of schemas and namespaces declared in types area.
    * @param aDef The definition being built, note may not be put in wsdl def member.
    */
   protected void buildSchemaMap(Definition aDef) throws AeWSDLException
   {
      buildSchemaMap(aDef, true);
   }
   
   /**
    * Adds a schema import to the types sections
    * @param aLoc the location of the schema import
    * @param aNS the namespace of the schema import
    * @throws AeException
    */
   public void addSchemaImport(String aLoc, String aNS) throws AeException
   {
      if (getWSDLDef() != null)
      {
         Types types = getWSDLDef().getTypes();
         if (types == null)
         {
            types = getWSDLDef().createTypes();
            getWSDLDef().setTypes(types);
         }
         
         UnknownExtensibilityElement extElement = new UnknownExtensibilityElement();
         types.addExtensibilityElement(extElement);
         StringBuilder buff = new StringBuilder();
         buff.append("<xs:schema xmlns:xs='").append(XMLConstants.W3C_XML_SCHEMA_NS_URI).append("'>\n"); //$NON-NLS-1$//$NON-NLS-2$
         buff.append("\t\t<xs:import namespace=\'").append(aNS).append("' schemaLocation='").append(aLoc).append("'/>\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
         buff.append("\t</xs:schema>"); //$NON-NLS-1$
         
         Document doc = AeXmlUtil.toDoc(buff.toString());
         extElement.setElement(doc.getDocumentElement());
         extElement.setElementType(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "import")); //$NON-NLS-1$
      }
   }

   /**
    * Build the internal map of schemas and namespaces declared in types area.
    * @param aDef The definition being built, note may not be put in wsdl def member.
    * @param aRecurse true if we should recurse into imported definitions.
    */
   protected void buildSchemaMap(Definition aDef, boolean aRecurse) throws AeWSDLException
   {
      // Get the Types section.
      Types types = aDef.getTypes();
      if ((types != null) && !types.getExtensibilityElements().isEmpty())
      {
         Set<String> schemaRefs = new HashSet<String>();
         List<Schema> schemas = new ArrayList<Schema>();

         // First, create a list of all the <schema> elements.
          for (UnknownExtensibilityElement extElement : (Iterable<UnknownExtensibilityElement>) types.getExtensibilityElements()) {
              if ("schema".equals(extElement.getElement().getLocalName())) //$NON-NLS-1$
              {
                  try {
                      // need to convert the parsed elements back to text as
                      // the schema reader expects an input stream
                      Element element = AeSchemaParserUtil.extractSchemaElement(extElement);
                      StringWriter sw = new StringWriter(2048);
                      StreamResult result = new StreamResult(sw);
                      TransformerFactory transFactory = TransformerFactory.newInstance();
                      Transformer transformer = transFactory.newTransformer();
                      transformer.transform(new DOMSource(element), result);

                      // now read back in via the schema reader
                      InputSource input = new InputSource(new StringReader(sw.toString()));
                      input.setSystemId(aDef.getDocumentBaseURI());

                      // Create the URI resolver to use when reading the schema.
                      AeWSDLSchemaResolver resolver = new AeWSDLSchemaResolver(getLocator(), aDef, getStandardResolver());

                      Schema schema = AeSchemaParserUtil.readSchema(input, resolver);
                      if (schema != null) {
                          schemas.add(schema);

                          // Add schema location to our master list of imported/included schemas
                          if (schema.getSchemaLocation() != null)
                              schemaRefs.add(AeUTF8Util.urlDecode(schema.getSchemaLocation()));
                      }

                      // Add any schemas which were resolved, add the URL decoded ref to our list of references
                      for (Iterator<String> refIter = resolver.getURIReferences(); refIter.hasNext(); )
                          schemaRefs.add(AeUTF8Util.urlDecode(refIter.next()));
                  } catch (Exception e) {
                      throw new AeWSDLException(e);
                  }
              }
          }

         // if we have messages then recursively process schemas in imported wsdl
         if(aRecurse && (! aDef.getMessages().isEmpty()) && (! aDef.getImports().isEmpty()))
         {
             for (List<Import> impObj : (Iterable<List<Import>>) aDef.getImports().values()) {
                 for (Import imp : impObj) {
                     if (imp.getDefinition() != null) {
                         buildSchemaMap(imp.getDefinition(), false);
                     }
                 }
             }
         }

         // Merge schemas with the same namespace into a single Schema object
         schemas = mergeSchemaList(schemas);

         // Now iterate through the remaining schemas and catalog them
          for (Schema schema : schemas) catalogSchemaAndImports(schema, new HashSet<String>(), true);

         // Add all references we've collected so far to the master list for the WSDL def
         mSchemaReferences = new ArrayList<String>(schemaRefs);
      }
   }

   /**
    * This method will iterate through the list of schemas and merge any schemas with the
    * same target namespace.  Multiple schemas with the same target namespace probably shouldn't
    * be defined in a WSDL file, but this method allows us to handle that case anyway.
    *
    * @param aSchemaList
    * @throws AeWSDLException
    */
   private List<Schema> mergeSchemaList(List<Schema> aSchemaList) throws AeWSDLException
   {
      Map<String, Schema> mergedSchemas = new HashMap<String, Schema>();

      try
      {
          for (Schema schema : aSchemaList) {
              String targetNS = schema.getTargetNamespace();
              if (mergedSchemas.containsKey(targetNS)) {
                  Schema schema2 = mergedSchemas.get(targetNS);
                  mergedSchemas.put(targetNS, AeSchemaUtil.mergeSchemas(schema2, schema));
              } else {
                  mergedSchemas.put(targetNS, schema);
              }
          }
      }
      catch (SchemaException se)
      {
         throw new AeWSDLException(AeMessages.getString("AeBPELExtendedWSDLDef.ERROR_1") //$NON-NLS-1$
               + se.getLocalizedMessage());
      }

      return new ArrayList<Schema>(mergedSchemas.values());
   }

   /**
    * Recursively catalog schema and process imports which are imported by by the passed schema.
    * @param aSchema schema to process imports from.
    * @param aCatalogedNamespaces a set of the namespaces that have already been cataloged or are currently being cataloged - this should prevent infinite recursion
    */
   protected void catalogSchemaAndImports(Schema aSchema, Set<String> aCatalogedNamespaces, boolean aRecurse)
   {
      // catalog schema if not cataloged
      String namespace = aSchema.getTargetNamespace();
      if (!aCatalogedNamespaces.contains(namespace))
      {
         fixupArray(aSchema);

         aCatalogedNamespaces.add(namespace);

         // process imports first (order matters)
         if(aRecurse)
         {
            @SuppressWarnings("unchecked")
			Enumeration<Schema> en = aSchema.getImportedSchema();
            while (en != null && en.hasMoreElements())
            {
               catalogSchemaAndImports(en.nextElement(), aCatalogedNamespaces, true);
            }
         }

         // make sure this is not a schema for import only use
         if(! AeUtil.isNullOrEmpty(namespace))
            putSchema(namespace, aSchema);
      }
   }

   /**
    * Fix any complex types derived from soapenc:Array that are a restricted
    * derivation and fail to allow for child elements.
    * @param aSchema
    */
   protected void fixupArray(Schema aSchema)
   {
      try
      {
         for(@SuppressWarnings("unchecked")
        		 Enumeration<ComplexType> e = aSchema.getComplexTypes(); e.hasMoreElements();)
         {
            ComplexType complexType = e.nextElement();
            if (complexType.isRestricted() && complexType.getParticleCount()==0)
            {
               if (AeSchemaUtil.isArray(complexType))
               {
                  ModelGroup modelGroup = new ModelGroup();
                  Namespaces ns = aSchema.getNamespaces();
                  String prefix = ns.getNamespacePrefix(IAeBPELExtendedWSDLConst.SOAP_ENCODING);
                  String prefixWithColon = AeUtil.notNullOrEmpty(prefix) ? prefix + ":" : ""; //$NON-NLS-1$ //$NON-NLS-2$
                  modelGroup.setReference(prefixWithColon + "Array"); //$NON-NLS-1$
                  modelGroup.setSchema(complexType.getSchema());
                  modelGroup.setMinOccurs(0);
                  modelGroup.setMaxOccurs(1);
                  complexType.addGroup(modelGroup);
               }
            }
         }
      }
      catch (Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }

   /**
    * Returns an iterator of schema simple type QNames that are defined for all
    * schemas.
    * @return Iterator an iterator of schema simple type QNames.
    */
   public Iterator<QName> getSchemaSimpleTypeNames()
   {
      List<QName> types = new ArrayList<QName>();
      Iterator<Schema> it = getSchemas();
      while (it.hasNext())
      {
         Schema schema = it.next();
         String tns = schema.getTargetNamespace();
         if (tns == null)
            tns = ""; //$NON-NLS-1$

         @SuppressWarnings("unchecked")
		Enumeration<SimpleType> simpleList = schema.getSimpleTypes();
         while (simpleList.hasMoreElements())
         {
            SimpleType simpleType = simpleList.nextElement();
            QName qname = new QName(tns, simpleType.getName());
            types.add(qname);
         }
      }
      return types.iterator();
   }

   /**
    * Returns an iterator of schema complex type QNames that are defined for all
    * schemas.
    * @return Iterator an iterator of schema simple type QNames.
    */
   public Iterator<QName> getComplexTypeNames()
   {
      List<QName> types = new ArrayList<QName>();
      Iterator<Schema> it = getSchemas();
      while (it.hasNext())
      {
         Schema schema = it.next();
         String tns = schema.getTargetNamespace();
         if (tns == null)
            tns = ""; //$NON-NLS-1$

         @SuppressWarnings("unchecked")
         Enumeration<ComplexType> complextList = schema.getComplexTypes();
         while (complextList.hasMoreElements())
         {
            ComplexType complexType = complextList.nextElement();
            QName qname = new QName(tns, complexType.getName());
            types.add(qname);
         }
      }
      return types.iterator();
   }

   /**
    * Returns an iterator of schema global (top-level) element definition QNames
    * that defined for all schemas.
    * @return Iterator an iterator of schema global element QNames.
    */
   public Iterator<QName> getSchemaGlobalElementNames()
   {
      List<QName> elements = new ArrayList<QName>();
      Iterator<Schema> it = getSchemas();
      while (it.hasNext())
      {
         Schema schema = it.next();
         String tns = schema.getTargetNamespace();
         if (tns == null)
            tns = ""; //$NON-NLS-1$

         @SuppressWarnings("unchecked")
		 Enumeration<ElementDecl> elementDecls = schema.getElementDecls();
         while (elementDecls.hasMoreElements())
         {
            ElementDecl element = elementDecls.nextElement();
            QName qname = new QName(tns, element.getName());
            elements.add(qname);
         }
      }
      return elements.iterator();
   }

   /**
    * Looks in WSDL Definition for a schema associated with the passed namespace.
    * If found it attempts to parse it for return as a schema definition.
    * @param aNamespaceURI The namespace URI of the schema being searched for
    * @return Schema The parsed schema object or null if not found
    */
   public synchronized Schema getSchemaForNamespace(String aNamespaceURI)
   {
      // Check if request is for the default schema
      if (Schema.DEFAULT_SCHEMA_NS.equals(aNamespaceURI))
         return sDefaultSchema;

      // Only need to read schema when it is not in our cache
      return getSchema(aNamespaceURI);
   }

   /**
    * Finds a schema declared type.
    * @param aType the type to be found
    * @return the xml type of the passed type name
    * @throws AeException thrown when errors encountered finding the type
    */
   public XMLType findType(QName aType) throws AeException
   {
      XMLType type = null;
      try
      {
	      Schema schema = getSchemaForNamespace(aType.getNamespaceURI());
	      if (schema != null)
	      {
	         // switched logic from call to getType since it messes up when a default namespace
	         // is assigned, so now we check for anyType, complex then simple directly (defect 458)
	         // note added explicit anyType check since that was done explicitly by findType

	         // check explicitly for any type
	         if(ANY_TYPE.equals(aType))
               type = new AnyType(schema);

	         // check for complex type
	         if(type == null)
	            type = schema.getComplexType(aType.getLocalPart());

	         // check for simple type
	         if(type == null)
               type = schema.getSimpleType(aType.getLocalPart(), aType.getNamespaceURI());
	      }
      }
      catch (Throwable th)
      {
         // convert the possible illegal argument exception from simple type or other errors to AeException
         throw new AeException(AeMessages.format("AeBPELExtendedWSDLDef.ERROR_FINDING_TYPE", aType), th); //$NON-NLS-1$
      }
      return type;
   }

   /**
    * Finds a schema declared element.
    * @return Message, null is not found.
    */
   public ElementDecl findElement(QName aType)
   {
      Schema schema = getSchemaForNamespace(aType.getNamespaceURI());
      if (schema != null)
         return schema.getElementDecl(aType.getLocalPart(), aType.getNamespaceURI());
      return null;
   }

   /**
    * Find the local or global ElementDecl declared/referenced by the
    * given name relative to its enclosing type.
    * @param aType
    * @param aName
    */
   public static ElementDecl findElement(ComplexType aType, String aName)
   {
      ElementDecl elementDecl = aType.getElementDecl(aName);
      if (elementDecl == null )
      {
         XMLType base = aType.getBaseType();
         while(base instanceof ComplexType && elementDecl == null)
         {
            elementDecl = ((ComplexType)base).getElementDecl(aName);
            base = base.getBaseType();
         }
      }
      return elementDecl;
   }

   /**
    * Get a WSDL Message object given it's QName.
    *
    * @param aMsg the name of the desired Message.
    * @return Message, null is not found.
    */
   public Message getMessage(QName aMsg)
   {
      Message lMessage = null;
      Definition lDef = getWSDLDef();

      if (lDef != null)
      {
         lMessage = lDef.getMessage(aMsg);
      }

      return lMessage;
   }

   /**
    * Returns true if the definition defines the passed message directly.
    *
    * @param aMsg the name of the desired Message.
    */
   public boolean definesMessage(QName aMsg)
   {
      Message message = null;
      Definition def = getWSDLDef();

      if (def != null && def.getMessages() != null)
      {
         message = (Message) def.getMessages().get(aMsg);
      }

      return message != null;
   }

   /**
    * Add a namespace association to this objects WSDL definition.
    *
    * @param aPrefix the prefix to use for this namespace. Use null or an empty
    * string to describe the default namespace (i. e. xmlns="...").
    * @param aNamespaceURI the namespace URI to associate the prefix with.
    * If null, the namespace association will be removed.
    */
   public void setNamespace(String aPrefix, String aNamespaceURI)
   {
      Definition lDef = getWSDLDef();

      if (lDef != null)
      {
         lDef.addNamespace(aPrefix, aNamespaceURI);
      }
   }

   /**
    * @see org.activebpel.rt.xml.IAeMutableNamespaceContext#getOrCreatePrefixForNamespace(java.lang.String, java.lang.String)
    */
   public String getOrCreatePrefixForNamespace(String aPreferredPrefix, String aNamespace)
   {
      return getOrCreatePrefixForNamespace(aPreferredPrefix, aNamespace, false);
   }

   /**
    * @see org.activebpel.rt.xml.IAeMutableNamespaceContext#getOrCreatePrefixForNamespace(java.lang.String, java.lang.String, boolean)
    */
   public String getOrCreatePrefixForNamespace(String aPreferredPrefix, String aNamespace, boolean aAllowDefaultNamespace)
   {
      if ( AeUtil.isNullOrEmpty(aPreferredPrefix) )
      {
         // no preferred prefix provide, check if a prefix already mapped for the given namespace,
         // if so just return that prefix.

         String knownPrefix = getPrefix(aNamespace);
         if ( knownPrefix != null )
            return knownPrefix;
      }
      else
      {
         // Check if the preferred prefix is already mapped for the given namespace, if so
         // just return the preferred prefix.

         String namespace = getNamespace(aPreferredPrefix);
         if ( namespace != null && namespace.equals(aNamespace) )
            return aPreferredPrefix;
      }

      final String preferredPrefix = AeUtil.isNullOrEmpty(aPreferredPrefix) ? "ns" : aPreferredPrefix ; //$NON-NLS-1$
      String testPrefix = preferredPrefix;

      String mappedNamespace = getNamespace(testPrefix);
      if (aNamespace.equals(mappedNamespace))
      {
         // the mapping is already in place, nothing to do
      }
      else
      {
         // it's mapped to something else or isn't mapped at all
         // keep going until mappedNamespace is null
         int index = 0;
         while ( mappedNamespace != null )
         {
            testPrefix = preferredPrefix + String.valueOf( index++ );
            mappedNamespace = getNamespace(testPrefix);
         }

         // we found a unique prefix
         setNamespace( testPrefix, aNamespace);
      }
      return testPrefix;
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set<String> resolveNamespaceToPrefixes(String aNamespace)
   {
      Set<String> prefixes = new HashSet<String>();
       for (Entry<String, String> entry : (Iterable<Entry<String, String>>) getWSDLDef().getNamespaces().entrySet()) {
           String prefix = entry.getKey();
           String ns = entry.getValue();
           if (AeUtil.compareObjects(aNamespace, ns)) {
               prefixes.add(prefix);
           }
       }
      return prefixes;
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return getNamespace(aPrefix);
   }

   /**
    * Add a namespace association to the given WSDL definition.
    *
    * @param aDef the WSDL definition.
    * @param aPrefix the prefix to use for this namespace. Use null or an empty
    * string to describe the default namespace (i. e. xmlns="...").
    * @param aNamespaceURI the namespace URI to associate the prefix with.
    * If null, the namespace association will be removed.
    */
   public void setNamespace(Definition aDef, String aPrefix, String aNamespaceURI)
   {
      if (aDef != null)
      {
         aDef.addNamespace(aPrefix, aNamespaceURI);
      }
   }

   /**
    * Gets the namespace associated with the given prefix.
    *
    * @param aPrefix The prefix to find the namespace.
    * @return String The namespace for the prefix or null if not found.
    */
   public String getNamespace(String aPrefix)
   {
      return getWSDLDef().getNamespace(aPrefix);
   }

   /**
    * Get the target namespace in which these WSDL elements are defined.
    * @return String the target namespace
    */
   public String getTargetNamespace()
   {
      return getWSDLDef().getTargetNamespace();
   }

   /**
    * Get all WSDL Message elements defined here.
    * @return Map list of defined WSDL Messages
    */
   @SuppressWarnings("unchecked")
   public Map<QName,Message> getMessages()
   {
      return getWSDLDef().getMessages();
   }

   /**
    * Get a list of Message Part objects associated with the Message name.
    * @param aMessageName the Message name.
    * @return Iterator of Part objects.
    */
   @SuppressWarnings("unchecked")
   public Iterator<Part> getMessageParts(QName aMessageName)
   {
      // Get the Message object for this Message name.
      Message message = getMessage(aMessageName);

      if (message != null)
      {
         // Retrieve Parts defined for this message.
         Map<String,Part> partsMap = message.getParts();
         if (partsMap != null)
            return message.getOrderedParts(null).iterator();
      }

      return Collections.EMPTY_LIST.iterator();
   }

   /**
    * Get all WSDL portType elements defined here.
    * @return Map list of defined WSDL PortType objects
    */
   @SuppressWarnings("unchecked")
   public Map<QName,PortType> getPortTypes()
   {
      return getWSDLDef().getPortTypes();
   }

   /**
    * Get all WSDL binding elements defined here.
    * @return Map list of defined WSDL Binding objects
    */
   @SuppressWarnings("unchecked")
   public Map<QName,Binding> getBindings()
   {
      return getWSDLDef().getBindings();
   }

   /**
    * Get all WSDL service elements defined here.
    * @return Map list of defined WSDL Service objects
    */
   @SuppressWarnings("unchecked")
   public Map<QName,Service> getServices()
   {
      return getWSDLDef().getServices();
   }

   //
   // Getter and setter methods
   //

   /**
    * Returns a list of schema references which this WSDL object imports or includes.
    */
   public List<String> getSchemaReferences()
   {
      return mSchemaReferences;
   }

   /**
    * Returns the extension registry.
    *
    * @return ExtensionRegistry
    */
   public ExtensionRegistry getExtensionRegistry()
   {

      if (mExtRegistry == null)
      {
         setExtensionRegistry(loadExtensionRegistry());
      }
      return mExtRegistry;
   }

   /**
    * Sets the extension registry.
    *
    * @param aExtRegistry The extRegistry to set
    */
   public void setExtensionRegistry(ExtensionRegistry aExtRegistry)
   {
      mExtRegistry = aExtRegistry;
   }

   /**
    * Returns the partnerLnkExtElements.
    *
    * @return List of IAePartnerLinkType objects.
    */
   public List<IAePartnerLinkType> getPartnerLinkTypeExtElements()
   {
      return mPartnerLinkTypeExtElements;
   }

   /**
    * Sets the partnerLnkExtElements.
    *
    * @param partnerLnkExtElements The partnerLnkExtElements to set
    */
   public void setPartnerLinkTypeExtElements(List<IAePartnerLinkType> partnerLnkExtElements)
   {
      mPartnerLinkTypeExtElements = partnerLnkExtElements;
   }

   /**
    * Returns the propExtElements.
    *
    * @return List of IAeProperty objects.
    */
   public List<IAeProperty> getPropExtElements()
   {
      return mPropExtElements;
   }

   /**
    * Sets the propExtElements.
    *
    * @param propExtElements The propExtElements to set
    */
   public void setPropExtElements(List<IAeProperty> propExtElements)
   {
      mPropExtElements = propExtElements;
   }

   /**
    * Returns the propAliasExtElements.
    *
    * @return List of IAePropertyAlias objects
    */
   public List<IAePropertyAlias> getPropAliasExtElements()
   {
      return mPropAliasExtElements;
   }

   /**
    * Sets the propAliasExtElements.
    *
    * @param propAliasExtElements The propAliasExtElements to set
    */
   public void setPropAliasExtElements(List<IAePropertyAlias> propAliasExtElements)
   {
      mPropAliasExtElements = propAliasExtElements;
   }

   //
   // Private methods
   //

   /**
    * Returns the WSDL definition.
    *
    * @return Definition
    */
   public Definition getWSDLDef()
   {
      return mDefinition;
   }

   /**
    * Sets the WSDL definition.
    *
    * @param definition The definition to set
    */
   private void setWSDLDef(Definition definition)
   {
      mDefinition = definition;
   }

   /**
    * Returns the Schema object for the given namespace URI, or null if not found.
    * @param aNamespaceURI the namespace URI we are looking for
    */
   private Schema getSchema(String aNamespaceURI)
   {
      return mSchemaDefs.get(aNamespaceURI);
   }

   /**
    * Adds the Schema object to our HashMap under the given namespace URI key.
    * @param aNamespaceURI the namespace URI of the Schema we are adding
    * @param aSchema the Schema object to be added
    */
   private void putSchema(String aNamespaceURI, Schema aSchema)
   {
      mSchemaDefs.put(aNamespaceURI, aSchema);
   }

   //
   // Helper Methods
   //

   /**
    * Get a prefix associated with this namespace URI. Or null if
    * there are no prefixes associated with this namespace URI.
    * @param aNamespace a namespace URI.
    * @return String Namespace prefix
    */
   public String getPrefix(String aNamespace)
   {
      return getWSDLDef().getPrefix(aNamespace);
   }
   
   /**
    * Gets all of the prefixes associated with the given namespace
    * URI.  Returns an empty Set if no prefixes are found.
    * 
    * @param aNamespace
    */
   public Set<String> getPrefixes(String aNamespace)
   {
      Set<String> prefixes = new HashSet<String>();
      
      @SuppressWarnings("unchecked")
	  Map<String,String> namespaces = getWSDLDef().getNamespaces();
       for (Entry<String, String> entry : namespaces.entrySet()) {
           String prefix = entry.getKey();
           String namespace = entry.getValue();

           if (AeUtil.compareObjects(namespace, aNamespace)) {
               prefixes.add(prefix);
           }
       }

      return prefixes;
   }

   /**
    * Returns a QName object given a Qname String of the format
    * "Namespace_Prefix: LocalPart". E.g. "tns:testRequest".
    *
    * @param aQstr a String in the form of "tns:element"
    * @return QName
    */
   public QName parseQName(String aQstr)
   {
      return parseQName(aQstr, getWSDLDef());
   }

   /**
    * Returns a QName string representation given a QName object.
    * @param aQName
    * @return String QName of form "prefix:localPart".
    */
   public String qNameToString(QName aQName)
   {
      return getWSDLDef().getPrefix(aQName.getNamespaceURI()) + ":" + aQName.getLocalPart(); //$NON-NLS-1$
   }

   /**
    * Get an iterator of Imports for the specified namespaceURI.
    * @param aNamespaceURI the namespaceURI associated with the desired imports.
    * @return Iterator an Iterator of corresponding Imports.
    */
   public Iterator<Import> getImports(String aNamespaceURI)
   {
      @SuppressWarnings("unchecked")
	  List<Import> imports = getWSDLDef().getImports(aNamespaceURI);
      if (imports != null)
         return imports.iterator();
      else
         return Collections.<Import>emptyList().iterator();
   }

   /**
    * Helper method to return a QName object given a QName String of the format
    * "Namespace_Prefix:LocalPart". E.g. "tns:testRequest".
    * @param aQstr a String in the form of "tns:element"
    * @param aDef the aQstr's WSDL definition. Need for prefix lookup.
    * @return QName
    */
   protected static QName parseQName(String aQstr, Definition aDef)
   {
      QName qname = null;
      int i = aQstr.indexOf(":"); //$NON-NLS-1$
      if (i == -1)
      {
         System.err.println(MessageFormat.format(AeMessages.getString("AeBPELExtendedWSDLDef.ERROR_0"), //$NON-NLS-1$
                                                 new Object[] {aQstr}));
      }
      else
      {
         String lNsPrefix = aQstr.substring(0, i);
         qname = new QName(aDef.getNamespace(lNsPrefix), aQstr.substring(i + 1));
      }
      return qname;
   }

   /**
    * @return the default schema for standard xsd types.
    */
   public static Schema getDefaultSchema()
   {
      return sDefaultSchema;
   }

   /**
    * TODO this may be unnecessary when getWSDLForNS is modified in design layer
    * @return Returns the default AeBPELExtendedWSDLDef (empty def object) so nulls won't be encountered.
    */
   public static AeBPELExtendedWSDLDef getDefaultDef()
   {
      return sDefaultDef;
   }

   /**
    * Accessor for wsdl location hint.
    * @return Wsdl location hint or null if none was specified at construction time.
    */
   public String getLocationHint()
   {
      return mLocation;
   }

   /**
    * Returns the WSDL locator for this definition.
    */
   public WSDLLocator getLocator()
   {
      return mLocator;
   }

   /**
    * @return Returns the standardResolver.
    */
   public IAeStandardSchemaResolver getStandardResolver()
   {
      if (mStandardResolver == null)
      {
         mStandardResolver = AeStandardSchemaResolver.newInstance();
      }
      return mStandardResolver;
   }

   /**
    * Returns true if the part is derived from the soap encoded array
    *
    * @param aPart
    */
   public boolean isArray(Part aPart)
   {
      boolean isArray = false;
      try
      {
         XMLType type = findType(aPart.getTypeName());
         if (type != null)
         {
            // find the base type
            isArray = AeSchemaUtil.isArray(type);
         }
      }
      catch (AeException e)
      {
         // eat the exception
      }

      return isArray;
   }

   /**
    * Returns the order of the parameters as set in the operation or null if none
    * specified.
    *
    * @param aOperation
    */
   public static List<String> getParameterOrder(Operation aOperation)
   {
      @SuppressWarnings("unchecked")
	  List<String> order = aOperation.getParameterOrdering();
      if (AeUtil.isNullOrEmpty(order))
         order = null;
      return order;
   }

   /**
    * Returns true if the xsi:type attribute is missing from this Document and
    * it is required since the part is a type.
    * @param part
    * @param aData
    */
   public static boolean isXsiTypeRequired(Part part, Document aData)
   {
      return part.getTypeName() != null && AeXmlUtil.getXSIType((aData).getDocumentElement()) == null;
   }

   /**
    * Touches the XML DOM extension nodes reachable from the given WSDL
    * <code>Definition</code> to finish any deferred DOM work. This is a
    * workaround for defect 1718, "Getting a MalformedURLException when running
    * a load test on .NET in a clustered environment," which can occur when
    * multiple threads examine the same extension element (and is not restricted
    * to just the .NET environment).
    *
    * @param aDefinition
    */
   protected static void touchXmlNodes(Definition aDefinition)
   {
      touchXmlNodes(aDefinition.getServices().values());
      touchXmlNodes(aDefinition.getBindings().values());
   }

   /**
    * Touches the XML DOM extension nodes reachable from the given collection,
    * which can be a collection of WSDL <code>Service</code>, <code>Port</code>,
    * <code>Binding</code>, <code>BindingOperation</code>, or
    * <code>ExtensibilityElement</code> objects.
    *
    * @param aCollection
    */
   protected static void touchXmlNodes(Collection<?> aCollection)
   {
       for (Object item : aCollection) {
           // The item might be a Service, Port, Binding, or BindingOperation,
           // all of which can have extensibility elements.
           if (item instanceof ElementExtensible) {
               AeXmlUtil.touchXmlNodes(((ElementExtensible) item).getExtensibilityElements());

               if (item instanceof Service) {
                   AeXmlUtil.touchXmlNodes(((Service) item).getPorts().values());
               } else if (item instanceof Binding) {
                   AeXmlUtil.touchXmlNodes(((Binding) item).getBindingOperations());
               }
           } else if (item instanceof UnknownExtensibilityElement) {
               AeXmlUtil.touchXmlNodes(((UnknownExtensibilityElement) item).getElement());
           }
       }
   }

   /**
    * Gets the substitution group level for specified group head and group member.
    * For the member element that does not belong the group, level = -1, If two elements
    * belong to a group but in the same level, level = 0, If the member element is
    * in the head element group, level = n, where n = 1, 2, 3...
    * @param aHeadElementName
    * @param aMemberElementName
    * @return substitution group level
    */
   public int getSubstitutionGroupLevel(QName aHeadElementName, QName aMemberElementName)
   {
      if( aHeadElementName == null | aMemberElementName == null)
         return -1;

      ElementDecl headElementDecl = findElement(aHeadElementName);
      ElementDecl memberElementDecl = findElement(aMemberElementName);
      return AeSchemaUtil.getSubstitutionGroupLevel(headElementDecl, memberElementDecl);
   }

   /**
    * return true if aMemberElementName belongs to the substitution group of aHeadElementName
    * @param aHeadElementName
    * @param aMemberElementName
    * 
    * TODO shouldn't this return true if the headElement and memberElement are the same QName
    */
   public boolean isCompatibleSGElement(QName aHeadElementName, QName aMemberElementName)
   {
      return getSubstitutionGroupLevel(aHeadElementName, aMemberElementName) > 0;
   }
   
   /**
    * Retrieve a policy extension implementation by wsu:Id.
    * 
    * returns null if not found.
    */
   public IAePolicy getPolicy(String aId)
   {
      return getPolicy(null, aId);
   }
   
   /**
    * Retrieve a policy extension implementation by relative URI and optional namespace.
    * 
    * @param aNamespace the namespace of the policy element. If null then only 
    *    get the policy by id.
    * @param aId the wsu:Id to retrieve.
    * 
    * @return the policy implementation object,
    * null if not found.
    */
   public IAePolicy getPolicy(String aNamespace, String aId)
   {
      IAePolicy policy = null;

      // Internal reference in this def
      if ( AeUtil.isNullOrEmpty(aNamespace) || // relative reference
            getTargetNamespace().equals(aNamespace)) // matches target ns for wsdl doc
      {
         Iterator<IAePolicy> lIt = getPolicyExtElements().iterator();
         while (lIt.hasNext())
         {
            IAePolicy policyElem = lIt.next();

            if ( policyElem.getReferenceId().equals(aId))
            {
                policy = policyElem;
                break;
            }
         }
      }
      else
      {
         // look in imports
         for (Iterator<Import> it = getImports(aNamespace); it.hasNext();)
         {
            Import importDef = it.next();
            Definition def = importDef.getDefinition();
            List<?> extElements = def.getExtensibilityElements();
            if (AeUtil.isNullOrEmpty(extElements))
            {
               return null;
            }

             for (Object ext : extElements) {
                 if (ext instanceof IAePolicy) {
                     IAePolicy policyElem = (IAePolicy) ext;
                     if (policyElem.getReferenceId().equals(aId)) {
                         policy = policyElem;
                         break;
                     }
                 }
             }
            
            if (policy != null)
               break;
         }
      }
      
      return policy;
   }

   /**
    * @return the policyExtElements
    */
   public List<IAePolicy> getPolicyExtElements()
   {
      return mPolicyExtElements;
   }

   /**
    * @param aPolicyExtElements the policyExtElements to set
    */
   public void setPolicyExtElements(List<IAePolicy> aPolicyExtElements)
   {
      mPolicyExtElements = aPolicyExtElements;
   }
}
