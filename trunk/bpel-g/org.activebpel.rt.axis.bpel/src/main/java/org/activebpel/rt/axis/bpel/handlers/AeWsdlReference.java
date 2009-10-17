//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeWsdlReference.java,v 1.12 2008/02/21 18:18:39 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeWSDLException;
import org.activebpel.rt.axis.AeWsdlImportFixup;
import org.activebpel.rt.axis.IAeWsdlReference;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.catalog.AeCatalogEvent;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogListener;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAePartnerLinkType;
import org.activebpel.rt.wsdl.def.IAeRole;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;

/**
 * Holds information obtained from wsdl def for the service desc object.  Allows
 * the wsdl to be replaced "under the hood".  If this happens, any cached properties
 * obtained from the wsdl will be flushed and the service desc's operations will
 * be updated.
 */
public class AeWsdlReference implements IAeWsdlReference, IAeCatalogListener
{

   // error constants
   private static final String NO_WSDL_FOR_PARTNER_LINK_TYPE = "AeWsdlReference.ERROR_1"; //$NON-NLS-1$
   private static final String NO_PARTNER_LINK_FOUND         = "AeWsdlReference.ERROR_2"; //$NON-NLS-1$
   private static final String NO_ROLE_FOUND                 = "AeWsdlReference.ERROR_3"; //$NON-NLS-1$
   private static final String NO_PARTNER_LINK_DEF           = "AeWsdlReference.ERROR_4"; //$NON-NLS-1$
   private static final String NO_PROCESS_DEPLOYMENT_FOUND   = "AeWsdlReference.ERROR_5"; //$NON-NLS-1$
   
   /** Current cached wsdl def.  This will be updated on replacement deployment. */
   private AeBPELExtendedWSDLDef mWsdlDef;
   /** Transport url string from the message context.  Used to format wsdl imports. */
   private String mTransportUrl;
   /** The ServiceDesc object we are attached to. */
   private ServiceDesc mServiceDesc;
   /** The parnter link string. */
   private String mPartnerLink;
   /** The process QName. */
   private QName mProcessQName;
   /** The partner link type QName. */
   private QName mPartnerLinkType;
   /** The port type QName. */
   private QName mPortTypeQName;
   /** The partner link def object. */
   private AePartnerLinkDef mPartnerLinkDef;
   /** Allowed methods. */
   private List mAllowedMethods;
   /** List of operations. */
   private ArrayList mOperations;
   /** Keeps track of overloaded methods. */
   private Map mNameToOperationsMap;
   
   /**
    * Constructor.
    * 
    * @param aServiceDesc
    * @param aParterLink
    * @param aProcessQName
    * @param aPartnerLinkType
    * @param aTransportUrl
    */
   public AeWsdlReference( ServiceDesc aServiceDesc, String aParterLink, 
         QName aProcessQName, QName aPartnerLinkType, String aTransportUrl )
   {
      mServiceDesc = aServiceDesc;
      mPartnerLink = aParterLink;
      mProcessQName = aProcessQName;
      mPartnerLinkType = aPartnerLinkType;
      mTransportUrl = aTransportUrl;
      mOperations = new ArrayList();
      mNameToOperationsMap = new HashMap();
   }

   /**
    * Copy constructor use for setting new transport URL.
    * 
    * @param aWsdlRef
    * @param aTransportUrl
    */
   public AeWsdlReference(AeWsdlReference aWsdlRef, String aTransportUrl)
   {
      this(aWsdlRef.getServiceDesc(), 
           aWsdlRef.getPartnerLink(),
           aWsdlRef.getProcessQName(),
           aWsdlRef.getPartnerLinkType(),
           aTransportUrl);
   }
  
   //----------[ Initialization Methods ]---------------------------------------
   
   /**
    * @see org.activebpel.rt.axis.IAeWsdlReference#init()
    */
   public synchronized void init() throws AeException
   {
      try
      {
         initWsdlDef();
         initPartnerLinkDef();
         initPortTypeQName();
         initOperations();
         trimWsdlDef();
      }
      catch( Throwable e )
      {
         AeException.logError( e, e.getLocalizedMessage() );
         throw new AeException(e);
      }
   }

   /**
    * Find the appropriate wsdl def object and make a copy of it (which will be
    * trimmed for this specific deployment).
    * @throws AeWSDLException
    * @throws AxisFault Thrown if the wsdl def could not be located.
    * @throws AeBusinessProcessException 
    */
   protected void initWsdlDef() throws AeWSDLException, AxisFault, AeBusinessProcessException
   {
      AeBPELExtendedWSDLDef cachedDef = null;
      IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(getProcessQName());
      if (wsdlProvider != null)
         cachedDef = AeWSDLDefHelper.getWSDLDefinitionForPLT( wsdlProvider, getPartnerLinkType());
      
      if( cachedDef == null )
         throw new AxisFault(AeMessages.format(NO_WSDL_FOR_PARTNER_LINK_TYPE, new Object[] {getPartnerLinkType()} ));
      
      // create a copy of the cached definition since we may modify it
      setWsdlDef(new AeBPELExtendedWSDLDef(cachedDef));
   }
   
   /**
    * Set the <code>AePartnerLinkDef</code> object as member data.
    * @throws AxisFault
    * @throws AeBusinessProcessException
    */
   protected void initPartnerLinkDef() throws AxisFault, AeBusinessProcessException
   {
      IAeDeploymentProvider provider = AeEngineFactory.getDeploymentProvider(); 
      IAeProcessDeployment processDeployment = provider.findCurrentDeployment( getProcessQName() );
      if (processDeployment == null)
      {
         throw new AxisFault(NO_PROCESS_DEPLOYMENT_FOUND+getProcessQName());
      }

      AeProcessDef processDef = processDeployment.getProcessDef();
      AePartnerLinkDef partnerLinkDef = processDef.findPartnerLink( getPartnerLink() );
      if( partnerLinkDef == null )
      {
         throw new AxisFault( AeMessages.format(NO_PARTNER_LINK_DEF, new Object[] {getPartnerLink(), getProcessQName()} ) );
      }
      
      setPartnerLinkDef( partnerLinkDef );
   }
   
   /**
    * Set the port type <code>QName</code> as member data.
    * @throws AxisFault
    */
   protected void initPortTypeQName() throws AxisFault
   {
      // Find the partner link type from the QName within the WSDL def 
      String roleName = getPartnerLinkDef().getMyRole();
      IAePartnerLinkType plType = getWsdlDef().getPartnerLinkType( getPartnerLinkDef().getPartnerLinkTypeName().getLocalPart() );

      if( plType == null )
      {
         throw new AxisFault( AeMessages.format(NO_PARTNER_LINK_FOUND, getPartnerLinkDef().getPartnerLinkTypeName().getLocalPart()) );
      }
      
      // Find the role and get it's operations
      IAeRole role = getRole( plType, roleName );
      setPortTypeQName( role.getPortType().getQName() );
   }
   
   /**
    * Set the operations and allowed methods as member data.
    * @throws Exception
    */
   protected void initOperations() throws Exception
   {
      getOperationsInternal().clear();
      ArrayList allowedMethods = new ArrayList();
      for( Iterator iter = getWsdlDef().getOperations( getPortTypeQName() ); iter.hasNext(); )
      {
         Operation operation = (Operation)iter.next();
         addOperation(operation);
         allowedMethods.add(operation.getName());
      }
      setAllowedMethods( allowedMethods );
   }
   
   /**
    * Removes all wsdl elements which do not pertain to this service deployment.
    * @throws AxisFault
    */
   protected void trimWsdlDef() throws AxisFault
   {
      String transportURL = getTransportUrl();
      if( AeUtil.isNullOrEmpty(transportURL) )
      {
         transportURL = "http://..."; //$NON-NLS-1$
      }
      AeWsdlImportFixup.fixupImportReferences(transportURL, getWsdlDef());
   }

   //----------[ Internal Utility Methods ]-------------------------------------
   
   /**
    * Extract the matching <code>IAeRole</code> from the given <code>IAePartnerLinkType</code>.
    * @param aPartnerLink
    * @param aRoleName
    * @throws AxisFault
    */
   protected IAeRole getRole( IAePartnerLinkType aPartnerLink, String aRoleName ) throws AxisFault 
   {
      IAeRole role = aPartnerLink.findRole( aRoleName );
      if (role == null || role.getPortType() == null)
      {
         throw new AxisFault( AeMessages.format(NO_ROLE_FOUND, aRoleName) ); 
      }
      return role;
   }
   
   /**
    * Adds an operation to the running list. 
    * @param aOperation the operation info to be added
    */
   protected void addOperation(Operation aOperation) throws Exception
   {
      // Create operation so that we are displayed in list of allowed methods. Need
      // to set a method in the operation or Axis will complain.
      OperationDesc operation = new OperationDesc();
      operation.setParent( getServiceDesc() );
      operation.setUse(getUse());
      operation.setName(aOperation.getName());
      operation.setMethod(AeBpelHandler.class.getMethod("invoke", new Class[] {MessageContext.class})); //$NON-NLS-1$

      if (aOperation.getInput() != null)
      {
         addParamsToOperation(operation, aOperation.getInput().getMessage(), true);
      }
            
      if (aOperation.getOutput() != null)
      {
         addParamsToOperation(operation, aOperation.getOutput().getMessage(), false);
      }

      addOperationDesc( operation );
   }
   
   /**
    * Keep track of operations (and overloaded operations).
    * @param aOperation
    */
   protected void addOperationDesc(OperationDesc aOperation)
   {
      getOperations().add(aOperation);
      aOperation.setParent( getServiceDesc() );
      
      // Add name to nameToOperations Map
      String name = aOperation.getName();
      ArrayList overloads = (ArrayList) getNameToOperationsMap().get(name);
      if (overloads == null)
      {
         overloads = new ArrayList();
         getNameToOperationsMap().put(name, overloads);
      }
      overloads.add(aOperation);
   }
   
   
   /**
    * Adds all parameters for the given message to the given operation.
    * @param aOperation The operation we will be adding parameters for
    * @param aMsg the message we will get parameter information from
    * @param aIsInput flag indicating if this is an input message (true)
    */
   protected void addParamsToOperation(OperationDesc aOperation, Message aMsg, boolean aIsInput)
   {
      for (Iterator iter=aMsg.getOrderedParts(null).iterator(); iter.hasNext();)
      {
         // Get the next part and obtain the return type for it (may be type or element)
         Part part = (Part)iter.next();
         QName typeName = part.getElementName();

         if (typeName == null)
         {
            typeName = part.getTypeName();
         }

         ParameterDesc param = new ParameterDesc();
         param.setMode(aIsInput ? ParameterDesc.IN : ParameterDesc.OUT);
         param.setName(part.getName());
         param.setTypeQName(typeName);
         aOperation.addParameter(param);
      }
   }
   
   //----------[ Service Operation Methods ]------------------------------------
   
   /**
    * @return Synchronized access to list of operations.
    */
   public synchronized ArrayList getOperations()
   {
      return getOperationsInternal();
   }
   
   /**
    * @see org.activebpel.rt.axis.IAeWsdlReference#removeOperationDesc(org.apache.axis.description.OperationDesc)
    */
   public synchronized void removeOperationDesc(OperationDesc aOperation) 
   {
      if( getOperationsInternal().contains(aOperation) ) 
      {
         getOperationsInternal().remove(aOperation);
         aOperation.setParent(null);
         getNameToOperationsMap().remove(aOperation.getName());
      }
   }
   
   /**
    * @see org.activebpel.rt.axis.IAeWsdlReference#getOperationsByName(java.lang.String)
    */
   public synchronized OperationDesc[] getOperationsByName(String aMethodName)
   {
      OperationDesc[] array = null;
      ArrayList overloads = (ArrayList)getNameToOperationsMap().get( aMethodName );
      if (overloads != null)
      {
         array = (OperationDesc[])overloads.toArray( new OperationDesc[overloads.size()]);
      }
      return array; 
   }
   
   /**
    * Return an operation matching the given method name.  Note that if we
    * have multiple overloads for this method, we will return the first one.
    * @return null for no match
    */
   public synchronized OperationDesc getOperationByName(String methodName)
   {
      OperationDesc operation = null;
      ArrayList overloads = (ArrayList)getNameToOperationsMap().get(methodName);
      if (overloads != null)
      {
         operation = (OperationDesc)overloads.get(0);
      }
      return operation;
   }
   

   //----------[ IAeCatalogListener Methods ]--------------------------------------
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogListener#onDeployment(org.activebpel.rt.bpel.server.catalog.AeCatalogEvent)
    */
   public synchronized void onDeployment(AeCatalogEvent aEvent)
   {
      if( aEvent.isReplacement() && AeUtil.compareObjects( aEvent.getLocationHint(), getWsdlDef().getLocationHint() ) )
      {
         try
         {
            init();
         }
         catch( Exception e )
         {
            AeException.logError( e, e.getLocalizedMessage() );
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogListener#onDuplicateDeployment(org.activebpel.rt.bpel.server.catalog.AeCatalogEvent)
    */
   public void onDuplicateDeployment(AeCatalogEvent aEvent)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogListener#onRemoval(org.activebpel.rt.bpel.server.catalog.AeCatalogEvent)
    */
   public void onRemoval(AeCatalogEvent aEvent)
   {
   }
   
   //----------[ Getters / Setters ]--------------------------------------------

   /**
    * @return Returns the processQName.
    */
   protected QName getProcessQName()
   {
      return mProcessQName;
   }
   
   /**
    * @return Returns the partnerLinkType.
    */
   protected QName getPartnerLinkType()
   {
      return mPartnerLinkType;
   }

   /**
    * @param aWsdlDef The wsdlDef to set.
    */
   protected void setWsdlDef(AeBPELExtendedWSDLDef aWsdlDef)
   {
      mWsdlDef = aWsdlDef;
   }

   /**
    * @return Returns the wsdlDef.
    */
   public synchronized AeBPELExtendedWSDLDef getWsdlDef()
   {
      return mWsdlDef;
   }
   
   /**
    * @return Returns the partnerLinkDef.
    */
   public synchronized AePartnerLinkDef getPartnerLinkDef()
   {
      return mPartnerLinkDef;
   }

   /**
    * @param aPartnerLinkDef The partnerLinkDef to set.
    */
   protected void setPartnerLinkDef(AePartnerLinkDef aPartnerLinkDef)
   {
      mPartnerLinkDef = aPartnerLinkDef;
   }
   
   /**
    * @return Returns the partnerLink.
    */
   protected String getPartnerLink()
   {
      return mPartnerLink;
   }
   
   /**
    * @return Returns the portTypeQName.
    */
   public synchronized QName getPortTypeQName()
   {
      return mPortTypeQName;
   }
   
   /**
    * @param aPortTypeQName The portTypeQName to set.
    */
   protected void setPortTypeQName(QName aPortTypeQName)
   {
      mPortTypeQName = aPortTypeQName;
   }
   
   /**
    * @see org.activebpel.rt.axis.IAeWsdlReference#getAllowedMethods()
    */
   public synchronized List getAllowedMethods()
   {
      return mAllowedMethods;
   }

   /**
    * @param aAllowedMethods The allowedMethods to set.
    */
   protected void setAllowedMethods(List aAllowedMethods)
   {
      mAllowedMethods = aAllowedMethods;
   }
   
   /**
    * @return Returns the use.
    */
   protected Use getUse()
   {
      return getServiceDesc().getUse();
   }
   
   /**
    * @return Return the transport url.
    */
   protected String getTransportUrl()
   {
      return mTransportUrl;
   }
   
   /**
    * @return Returns the serviceDesc.
    */
   protected ServiceDesc getServiceDesc()
   {
      return mServiceDesc;
   }
   
   /**
    * Internal, unsynchronized access to list of operations.
    */
   protected ArrayList getOperationsInternal()
   {
      return mOperations;
   }
   
   /**
    * @return Returns the nameToOperationsMap.
    */
   protected Map getNameToOperationsMap()
   {
      return mNameToOperationsMap;
   }
}