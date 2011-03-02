// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeBprDeploymentSource.java,v 1.36 2007/11/21 03:26:02 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.io.AeBpelIO;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.catalog.resource.AeResourceKey;
import org.activebpel.rt.bpel.server.catalog.resource.IAeResourceKey;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.AeExceptionManagementType;
import org.activebpel.rt.bpel.server.deploy.AeExceptionManagementUtil;
import org.activebpel.rt.bpel.server.deploy.AeInvokeRecoveryType;
import org.activebpel.rt.bpel.server.deploy.AeInvokeRecoveryUtil;
import org.activebpel.rt.bpel.server.deploy.AeProcessPersistenceType;
import org.activebpel.rt.bpel.server.deploy.AeProcessTransactionType;
import org.activebpel.rt.bpel.server.deploy.AeServiceDeploymentUtil;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.IAePddXmlConstants;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptor;
import org.activebpel.rt.bpel.server.deploy.pdd.AePartnerLinkDescriptorFactory;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Wraps the deployment of a single pdd from the BPR archive.
 */
public class AeBprDeploymentSource implements IAeDeploymentSource, IAePddXmlConstants
{
   /** pdd dom */
   private Document mPddDom;
   /** deserialized process def */
   private AeProcessDef mProcessDef;
   /** deployment context */
   private IAeDeploymentContext mContext;
   /** pdd resource name */
   private String mPddResource;
   /** partner link data */
   private Collection<AePartnerLinkDescriptor> mPartnerLinkData;
   /** resource key data */
   private Set mContextKeys = new HashSet();
   /** extension elements */
   private Element mExtensions;
   
   private static final String CONSOLE_ERROR = AeMessages.getString("AeBprDeploymentSource.ERROR_0"); //$NON-NLS-1$
   
   /**
    * Create a deployment source and initialize a deployment source from the pass pdd and context,
    * @param aPddResource
    * @param aPddDom
    * @param aContext
    */
   public AeBprDeploymentSource( String aPddResource, Document aPddDom, IAeDeploymentContext aContext ) 
   throws AeDeploymentException
   {
      mPddResource = aPddResource;
      mPddDom = aPddDom;
      mContext = aContext;
      init();
   }
   
   /**
    * Initializes the internal state of the deployment
    * source.
    * @throws AeDeploymentException
    */
   protected void init() throws AeDeploymentException
   {
      initProcessDef();
      initContextKeys();
      initPartnerLinkData();
      initExtensions();
   }
   
   /**
    * Initialize the context keys from wsdl references in the pdd file as well as imports from the process
    */
   protected void initContextKeys() throws AeDeploymentException
   {
      Element references = AeXmlUtil.findSubElement(getPddDom().getDocumentElement(), new QName(getPddDom().getDocumentElement().getNamespaceURI(), TAG_REFERENCES));
      if(references == null)
         references = AeXmlUtil.findSubElement(getPddDom().getDocumentElement(), new QName(getPddDom().getDocumentElement().getNamespaceURI(), TAG_WSDL_REFERENCES));
      if(references != null)
      {
         NodeList refs = references.getElementsByTagNameNS(getPddDom().getDocumentElement().getNamespaceURI(), TAG_WSDL);
         for( int i = 0; refs != null && i < refs.getLength(); i++ )
         {
            Element wsdlElement = (Element)refs.item(i);
            String location = wsdlElement.getAttribute(IAePddXmlConstants.ATT_LOCATION);
            IAeResourceKey key = new AeResourceKey(location, IAeBPELExtendedWSDLConst.WSDL_NAMESPACE);
            addContextKey( key );
         }
         
         refs = references.getElementsByTagNameNS(getPddDom().getDocumentElement().getNamespaceURI(), TAG_SCHEMA);
         for( int i = 0; refs != null && i < refs.getLength(); i++ )
         {
            Element schemaElement = (Element)refs.item(i);
            String location = schemaElement.getAttribute(IAePddXmlConstants.ATT_LOCATION);
            IAeResourceKey key = new AeResourceKey(location, XMLConstants.W3C_XML_SCHEMA_NS_URI);
            addContextKey( key );
         }
         
         refs = references.getElementsByTagNameNS(getPddDom().getDocumentElement().getNamespaceURI(), TAG_OTHER);
         for( int i = 0; refs != null && i < refs.getLength(); i++ )
         {
            Element schemaElement = (Element)refs.item(i);
            String location = schemaElement.getAttribute(IAePddXmlConstants.ATT_LOCATION);
            String typeURI = schemaElement.getAttribute(IAePddXmlConstants.ATT_TYPE_URI);
            IAeResourceKey key = new AeResourceKey(location, typeURI);
            addContextKey( key );
         }
      }
      
      // todo - (cck) should we process the import list from the process def at all here
   }

   /**
    * Create the partner link data objects.  Only the partnerRole 
    * data objects are created (because they are the only objects 
    * needed by the process deployment).
    * @throws AeDeploymentException
    */
   protected void initPartnerLinkData() throws AeDeploymentException
   {
      mPartnerLinkData = new ArrayList();

      // using the ns from the doc element here because it's possible that someone has
      // a pdd w/ one of our older namespaces
      NodeList partnerLinkNodeList = getPddDom().getElementsByTagNameNS( getPddDom().getDocumentElement().getNamespaceURI(), TAG_PARTNER_LINK );
      for( int i = 0; i < partnerLinkNodeList.getLength(); i++ )
      {
         Element partnerLinkElement = (Element)partnerLinkNodeList.item(i);
         AePartnerLinkDescriptor partnerLinkData = AePartnerLinkDescriptorFactory.getInstance().createPartnerLinkDesc( partnerLinkElement, getProcessDef() );
         mPartnerLinkData.add( partnerLinkData );
      }
   }
   
   /**
    * Loads the extensions from the pdd 
    */
   protected void initExtensions()
   {
      try
      {
         mExtensions = (Element) AeXPathUtil.selectSingleNode(getPddDom(), "/pdd:process/pdd:extensions", Collections.singletonMap("pdd", getPddDom().getDocumentElement().getNamespaceURI())); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch (AeException e)
      {
         // eat the exception, unit tests will catch errors in the expression 
      }
   }
   
   /**
    * Load the bpel and deserialize.
    * @throws AeDeploymentException
    */
   protected void initProcessDef() throws AeDeploymentException
   {
      String location = getBpelSourceLocation();
      InputStream in = null;
      try
      {
         in = getContext().getResourceAsStream( location );
         mProcessDef = AeBpelIO.deserialize(new InputSource(in));
      }
      catch (AeBusinessProcessException e)
      {
         String rootMsg = ""; //$NON-NLS-1$
         if (e.getRootCause() != null)
            rootMsg = e.getRootCause().getLocalizedMessage();
         Object[] args = {location, getPddLocation(), rootMsg};
         throw new AeDeploymentException( MessageFormat.format(CONSOLE_ERROR, args), e );
      }
      finally
      {
         AeCloser.close(in);
      }
   }
   
   /**
    * Accessor for the deployment context.
    */
   protected IAeDeploymentContext getContext()
   {
      return mContext;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getBpelSourceLocation()
    */
   public String getBpelSourceLocation()
   {
      return getProcessSourceElement().getAttribute( ATT_PROCESS_LOCATION );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getProcessName()
    */
   public QName getProcessName()
   {
      return getProcessName( getProcessSourceElement() );
   }
   
   /**
    * Accessor for process qname.
    * @param aProcessElement
    */
   public static QName getProcessName( Element aProcessElement )
   {
      String processQName = aProcessElement.getAttribute(ATT_NAME);
      // TODO - can this ever be null or empty?
      // probably should have failed some sort of
      // validation before it ever got here
      if( AeUtil.isNullOrEmpty( processQName) )
      {
         return null;
      }
      return AeXmlUtil.createQName( aProcessElement, processQName );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getProcessDef()
    */
   public AeProcessDef getProcessDef()
   {
      return mProcessDef;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getProcessSourceElement()
    */
   public Element getProcessSourceElement()
   {
      return getPddDom().getDocumentElement();
   }

   /**
    * Accessor the for pdd dom
    * @return pdd dom
    */
   protected Document getPddDom()
   {
      return mPddDom;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getPddLocation()
    */
   public String getPddLocation()
   {
      return mPddResource;
   }
   
   /**
    * Adds a context key to our context key set.
    */
   protected void addContextKey(IAeResourceKey aKey)
   {
      mContextKeys.add(aKey);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getResourceKeys()
    */
   public Set getResourceKeys()
   {
      return mContextKeys;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getPlanId()
    */
   public int getPlanId()
   {
      // plan id's don't apply to non-versioned sources
      return 0;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getPartnerLinkDescriptors()
    */
   public Collection<AePartnerLinkDescriptor> getPartnerLinkDescriptors()
   {
      return mPartnerLinkData;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getPersistenceType()
    */
   public AeProcessPersistenceType getPersistenceType()
   {
      String persistenceType = getProcessSourceElement().getAttribute(ATT_PERSISTENCE_TYPE);
      return AeProcessPersistenceType.getPersistenceType(persistenceType);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getTransactionType()
    */
   public AeProcessTransactionType getTransactionType()
   {
      String transactionType = getProcessSourceElement().getAttribute(ATT_TRANSACTION_TYPE);
      return AeProcessTransactionType.getTransactionType(transactionType);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getExceptionManagementType()
    */
   public AeExceptionManagementType getExceptionManagementType()
   {
      return AeExceptionManagementUtil.getExceptionManagementType( getPddDom() );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getServices()
    */
   public IAeServiceDeploymentInfo[] getServices() throws AeDeploymentException
   {
      return AeServiceDeploymentUtil.getServices(getProcessDef(), getProcessSourceElement());
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getInvokeRecoveryType()
    */
   public AeInvokeRecoveryType getInvokeRecoveryType()
   {
      return AeInvokeRecoveryUtil.getInvokeRecoveryType(getPddDom());
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource#getExtensions()
    */
   public Element getExtensions()
   {
      return mExtensions;
   }
}
