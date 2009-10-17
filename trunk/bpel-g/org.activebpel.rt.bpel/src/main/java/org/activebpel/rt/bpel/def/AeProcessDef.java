// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeProcessDef.java,v 1.75.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeStaticAnalysisException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.def.visitors.AeCreateInstanceMessageExchangeVisitor;
import org.activebpel.rt.bpel.def.visitors.AeCreateInstanceOperationVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefCorrelatedReceiveVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefEntryPointInitialVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefEntryPointPropertiesVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefOnEventVariableTypeVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefOneWayVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefPartnerLinkTypeVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefVariableTypeVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.def.visitors.AeIsolatedScopeVisitor;
import org.activebpel.rt.bpel.def.visitors.AeScopeSnapshotOptimizationVisitor;
import org.activebpel.rt.bpel.def.visitors.IAeDefMessagePartsMapVisitor;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.bpel.def.visitors.preprocess.AeValidationPreprocessingVisitor;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.util.AeIntMap;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Definition for bpel process
 */
public class AeProcessDef extends AeScopeDef
{
   /** The namespace of the process (may be bpel 1.1 or 2.0). */
   private String mNamespace;
   /** The target namespace of the process. */
   private String mTargetNamespace;
   /** The query language override. */
   private String mQueryLanguage;
   /** The expression language override. */
   private String mExpressionLanguage;
   /** The suppress join failure override. */
   private boolean mSuppressJoinFailure;
   /** The exit on standard fault override. */
   private Boolean mExitOnStandardFault;
   /** True if this process was designed to enable instance compensation after completion. */
   private boolean mEnableInstanceCompensation;
   /** True if this is an abstract (non-executable) process. */
   private boolean mAbstractProcess;
   /** Comment associated with process. */
   private String mComment = ""; //$NON-NLS-1$
   /** Built-in comment from bpel editor. */
   private String mProcessPreambleComment = ""; //$NON-NLS-1$
   /** Partners container. */
   private AePartnersDef mPartners;
   /** List of create instance activities. */
   private Collection mCreateInstances;
   /** Maps the partnerLinkOpKey to the collection of properties. */
   private Map mCorrelatedReceivesProperties = new HashMap();
   /** Maps the partnerLinkOpKey to the message parts */
   private Map mCorrelatedReceivesMessagePartsMap = new HashMap();
   /** Process fully qualified name. */
   private QName mProcessName;
   /** indicates that this process has multiple start activities */
   private boolean mMultiStart;
   /** indicates that this process contains serializable scopes */
   private boolean mContainsSerializableScopes;
   /** cache of property names and message types to property aliases */
   // TODO revisit: do we really need this map?
   private Map mPropertyAliasMap = new HashMap();
   /** cache of property names to property types. */
   private Map mPropertyTypeMap = new HashMap();
   /** Maps location paths to location ids */
   private final Map mLocationPathsToIds = new HashMap();
   /** Maps location ids to location paths */
   private final AeIntMap mLocationIdsToPaths = new AeIntMap();
   /** Collection of port types plus operations that are one-way receives. */
   // TODO revisit: do we really need this map?
   private Set mOneWayReceives = new HashSet();
   /** maps partner link name and operation to a list of property sets that can be in the request */
   // TODO revisit: do we really need this map?
   private Map mCorrelationPropertiesMap = new HashMap();
   /** map of create instance activities and their message exchange values (if they're not-null) */
   // TODO revisit: do we really need this map?
   private Map mMessageExchangeMap = new HashMap();
   /** List of the process's imports. */
   private List mImports = new LinkedList();
   /** The 'extensions' child. */
   private AeExtensionsDef mExtensionsDef;
   /** A map of partner link locations to partner link defs. */
   private Map mPartnerLinkMap = new HashMap();
   /** A map of partner link names to fully qualified partner link locations. */
   private Map mPartnerLinkNameMap = new HashMap();
   /** set to true if no IMA apart from the lone createInstance IMA uses the createInstance IMA's plink/operation  */
   private boolean mCreateInstanceOnlyOperation;
   /** WSBPEL Abstract process profile URI. */
   private String mAbstractProcessProfile;
   /** The 'disable selection failure' flag. */
   private boolean mDisableSelectionFailure;
   /** The 'create target xpath' flag. */
   private boolean mCreateTargetXPath;

   /**
    * Default constructor
    */
   public AeProcessDef()
   {
      super();
   }

   /**
    * Adds the port type and operation to the collection of one way receives.
    *
    * @param aPartnerLinkType
    * @param aOperation
    */
   public void addOneWayReceive(QName aPartnerLinkType, String aOperation)
   {
      mOneWayReceives.add(makeOneWayReceiveKey(aPartnerLinkType, aOperation));
   }

   /**
    * Returns true if the partner link's port type and operation is a one-way receive.
    *
    * @param aPartnerLinkOpKey
    */
   public boolean isOneWayReceive(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      // TODO (MF) put the one-way info on the partner link def itself
      AePartnerLinkDef plDef = findPartnerLink(aPartnerLinkOpKey);
      QName plinkDef = plDef.getPartnerLinkTypeName();
      return mOneWayReceives.contains(makeOneWayReceiveKey(plinkDef, aPartnerLinkOpKey.getOperation()));
   }

   /**
    * Makes a key from the concatenation of the port type and the operation.
    *
    * @param aPartnerLinkType
    * @param aOperation
    */
   protected Object makeOneWayReceiveKey(QName aPartnerLinkType, String aOperation)
   {
      // TODO (EPW) Create an actual key object for this.
      return aPartnerLinkType + aOperation;
   }

   /**
    * Caches a property alias with the process def for faster lookup at runtime.
    * @param aPropAlias
    */
   public void cachePropertyAlias(IAePropertyAlias aPropAlias)
   {
      mPropertyAliasMap.put(makeKey(aPropAlias), aPropAlias);
   }

   /**
    * Caches a property type.
    *
    * @param aPropertyName The name of the property.
    * @param aPropertyType The type of the property.
    */
   public void cachePropertyType(QName aPropertyName, QName aPropertyType)
   {
      mPropertyTypeMap.put(aPropertyName, aPropertyType);
   }

   /**
    * Retrieves a previously cached property alias by its type, name and property name.
    * @param aType constant from IAePropertyAlias indicates the type that of alias we're looking for: message, element, or type
    * @param aName The qname of the message, element, or type
    * @param aPropertyName The property name for the property alias
    */
   protected IAePropertyAlias getPropertyAlias(int aType, QName aName, QName aPropertyName)
   {
      Object key = makeKey(aType, aName, aPropertyName);
      return (IAePropertyAlias) mPropertyAliasMap.get(key);
   }

   /**
    * Retrieves previously cached property alias or throws if not found
    * @param aType
    * @param aName
    * @param aPropertyName
    * @throws AeStaticAnalysisException
    */
   public IAePropertyAlias getPropertyAliasOrThrow(int aType, QName aName, QName aPropertyName) throws AeStaticAnalysisException
   {
      IAePropertyAlias alias = getPropertyAlias(aType, aName, aPropertyName);
      if (alias == null)
      {
         Object[] args = {new Integer(aType), aName, aPropertyName.getNamespaceURI(), aPropertyName.getLocalPart()};
         
         String msg = AeMessages.format("AeProcessDef.MissingPropertyAlias", args); //$NON-NLS-1$
         throw new AeStaticAnalysisException(msg);
      }
      return alias;
   }

   /**
    * Gets the property alias to use for correlation
    * @param aMessagePartsMap
    * @param aProperty
    * @throws AeStaticAnalysisException
    */
   public IAePropertyAlias getPropertyAliasForCorrelation(AeMessagePartsMap aMessagePartsMap, QName aProperty) throws AeStaticAnalysisException
   {
      IAePropertyAlias propAlias = getPropertyAlias(IAePropertyAlias.MESSAGE_TYPE, aMessagePartsMap.getMessageType(), aProperty);
      if (propAlias == null && aMessagePartsMap.isSinglePartElement() && !getNamespace().equals(IAeBPELConstants.BPWS_NAMESPACE_URI))
      {
         propAlias = getPropertyAlias(IAePropertyAlias.ELEMENT_TYPE, aMessagePartsMap.getSingleElementPart(), aProperty);
      }
      if (propAlias == null)
      {
         Object[] args = {new Integer(IAePropertyAlias.MESSAGE_TYPE), aMessagePartsMap.getMessageType(), aProperty.getNamespaceURI(), aProperty.getLocalPart()};
         String msg = AeMessages.format("AeProcessDef.MissingPropertyAlias", args); //$NON-NLS-1$
         throw new AeStaticAnalysisException(msg);
      }
      return propAlias;
   }

   /**
    * Retrieves a previously cached property type by its property name.
    * @param aPropertyName The property name.
    * @return The property type.
    */
   public QName getPropertyType(QName aPropertyName)
   {
      return (QName) mPropertyTypeMap.get(aPropertyName);
   }

   /**
    * Makes a key for the property alias so it can be stored in our map.
    * @param aPropAlias
    */
   private Object makeKey(IAePropertyAlias aPropAlias)
   {
      return makeKey(aPropAlias.getType(), aPropAlias.getQName(), aPropAlias.getPropertyName());
   }

   /**
    * Makes a key based on the property alias type, type name, and property name
    * @param aAliasType
    * @param aTypeQName
    * @param aPropName
    */
   private Object makeKey(int aAliasType, QName aTypeQName, QName aPropName)
   {
      return aAliasType + "." + aTypeQName + "." + aPropName; //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Setter for the multi-start field
    * @param aFlag
    */
   public void setMultiStart(boolean aFlag)
   {
      mMultiStart = aFlag;
   }

   /**
    * Getter for the multi start field
    */
   public boolean isMultiStart()
   {
      return mMultiStart;
   }

   /**
    * Runs visitors that are common to 'preProcessForValidation' and 'preProcessForExecution'.
    *
    * @param aContextProvider
    * @throws AeBusinessProcessException
    */
   protected void runCommonVisitors(IAeContextWSDLProvider aContextProvider,
         IAeExpressionLanguageFactory aExpressionLanguageFactory, boolean aReportErrors)
         throws AeBusinessProcessException
   {
      // Assign variable type information for implicit OnEvent variables.
      new AeDefOnEventVariableTypeVisitor(aContextProvider).visit(this);

      // assign variable type information for variables
      AeDefVariableTypeVisitor varVisitor = new AeDefVariableTypeVisitor(aContextProvider, aReportErrors);
      varVisitor.findTypeInfo(this);

      // set the strategy for &lt;from&gt; and &lt;to&gt; assigns
      // Note that this visitor should run after any implicit variables visitor
      // since it depends on the implicit variables having been created in order
      // to validate the copy operations.
      AeDefVisitorFactory.getInstance(getNamespace()).createCopyOperationStrategyVisitor(aExpressionLanguageFactory).visit(this);

      // set the strategy for the wsio activities and their message data production or consumption
      AeDefVisitorFactory.getInstance(getNamespace()).createMessageDataStrategyVisitor().visit(this);

      // populate the partner link type objects on the partner link defs
      AeDefPartnerLinkTypeVisitor plTypeViz = new AeDefPartnerLinkTypeVisitor(aContextProvider);
      plTypeViz.visit(this);

      // Assign message parts maps to web service activities.
      IAeDefMessagePartsMapVisitor msgPartsMapVisitor = AeDefVisitorFactory.getInstance(getNamespace()).createMessagePartsMapVisitor(aContextProvider);
      msgPartsMapVisitor.assignMessagePartsMaps(this, aReportErrors);

      AeCreateInstanceMessageExchangeVisitor createInstanceMxVisitor = new AeCreateInstanceMessageExchangeVisitor();
      createInstanceMxVisitor.visit(this);

      // Set isolated scopes on activities.
      AeIsolatedScopeVisitor isolatedScopeVisitor = new AeIsolatedScopeVisitor();
      isolatedScopeVisitor.visit(this);
      
      // Perform extension preprocessing
      AeValidationPreprocessingVisitor  validationPreprocessingVisitor = new AeValidationPreprocessingVisitor(aContextProvider, aExpressionLanguageFactory);
      validationPreprocessingVisitor.visit(this);
   }

   /**
    * Runs the visitors that are needed for validation to be successful.  Either this OR the
    * preProcessForExecution method should be called, but not both.  If the process will be
    * executed after validating it, then preProcessForExecution should be called.
    *
    * @param aContextProvider
    */
   public void preProcessForValidation(IAeContextWSDLProvider aContextProvider,
         IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      try
      {
         runCommonVisitors(aContextProvider, aExpressionLanguageFactory, false);

         AeDefOneWayVisitor oneWayReceiveVisitor = new AeDefOneWayVisitor(aContextProvider);
         oneWayReceiveVisitor.visit(this);
      }
      catch (AeBusinessProcessException ex)
      {
         // Don't care about the exception when validating.
         ex.printStackTrace();
      }
   }

   /**
    * Runs all of the pre-processing that we need to do on the def objects. This
    * includes the creation of the xpaths on the def, the variable usage for
    * serializable scopes and discovering type information for variables.  It also
    * pulls up Invoke "implicit" scopes.
    *
    * @param aContextProvider a WSDL provider used to located WSDL definitions
    * @param aExpressionLanguageFactory the expression language factory to use
    */
   public void preProcessForExecution(IAeContextWSDLProvider aContextProvider,
         IAeExpressionLanguageFactory aExpressionLanguageFactory) throws AeBusinessProcessException
   {
      runCommonVisitors(aContextProvider, aExpressionLanguageFactory, true);

      // visitor will mark scopes that don't have any nested compensation handlers in order to avoid recording a snapshot when the scope completes
      accept(new AeScopeSnapshotOptimizationVisitor());

      // assign variable usages for definitions
      IAeDefVisitor resourceLocker = AeDefVisitorFactory.getInstance(getNamespace()).createResourceLockingVisitor(aExpressionLanguageFactory);
      resourceLocker.visit(this);

      // NOTE: parent assignments must be run before this
      // examine all receives and picks (for onMessages)
      // whose createInstance property is set to true
      // list contents: partnerLinkKey:operationName
      AeDefEntryPointInitialVisitor entryPtVisitor = new AeDefEntryPointInitialVisitor();
      entryPtVisitor.visit( this );
      mCreateInstances = entryPtVisitor.getCreateInstanceCollection();
      setMultiStart(entryPtVisitor.getCreateInstanceActivityCount() > 1);

      AeDefOneWayVisitor oneWayReceiveVisitor = new AeDefOneWayVisitor(aContextProvider);
      oneWayReceiveVisitor.visit(this);

      AeDefCorrelatedReceiveVisitor duplicateReceiveVisitor = new AeDefCorrelatedReceiveVisitor();
      duplicateReceiveVisitor.visit(this);

      // If there's a single createInstance IMA then run the additional visitor below in
      // order to test to see if it's the only IMA with that plink and operation. If so,
      // there's an optimization in routing the inbound receive where we won't bother
      // trying to match the inbound receive to a message receiver since it'll always
      // result in a new process instance.
      // Note: This visitor depends on the multi-start flag having been set as well as the
      //       AeDefCorrelatedReceiveVisitor having run.
      if (!isMultiStart())
      {
         AeCreateInstanceOperationVisitor opVisitor = new AeCreateInstanceOperationVisitor();
         opVisitor.visit(this);
         setCreateInstanceOperationOnly(opVisitor.isCreateInstanceOnly());
      }

      // NOTE: parent assignments must be run before this
      // build map for all properties
      // values are the set of properties
      // key is partnerLinkName:portTypeQName:operationName
      AeDefEntryPointPropertiesVisitor propsVisitor = new AeDefEntryPointPropertiesVisitor();
      propsVisitor.visit( this );
      getCorrelatedReceivesProperties().putAll(propsVisitor.getPropertiesMap());
      getCorrelatedReceivesMessagePartsMap().putAll(propsVisitor.getMessagePartsMap());

      // this visitor caches the property aliases so we don't have to resolve at runtime
      IAeDefVisitor propAliasViz = AeDefVisitorFactory.getInstance(getNamespace()).createPropertyAliasInlineVisitor(aContextProvider,
            aExpressionLanguageFactory);
      accept(propAliasViz);
   }

   /**
    * Gets the qname for this process.
    */
   public QName getQName()
   {
      if( mProcessName == null )
      {
         mProcessName = new QName(getTargetNamespace(), getName());
      }
      return  mProcessName;
   }

   /**
    * Accessor method to obtain the target namespace for this process.
    *
    * @return target namespace of the process
    */
   public String getTargetNamespace()
   {
      return mTargetNamespace;
   }

   /**
    * Mutator method to set the target namespace for this process.
    *
    * @param aTargetNamespace the target namespace for this process
    */
   public void setTargetNamespace(String aTargetNamespace)
   {
      mTargetNamespace = aTargetNamespace;
   }

   /**
    * Accessor method to obtain the query language for this process.
    *
    * @return query language for this process
    */
   public String getQueryLanguage()
   {
      return mQueryLanguage;
   }

   /**
    * Mutator method to set the query language for this process.
    *
    * @param aQueryLanguage the query language for this process
    */
   public void setQueryLanguage(String aQueryLanguage)
   {
      mQueryLanguage = aQueryLanguage;
   }

   /**
    * Accessor method to obtain the expression language for this process.
    *
    * @return expression language for this process
    */
   public String getExpressionLanguage()
   {
      return mExpressionLanguage;
   }

   /**
    * Mutator method to set the expression language for this process.
    *
    * @param aExpressionLanguage the expression language for this process
    */
   public void setExpressionLanguage(String aExpressionLanguage)
   {
      mExpressionLanguage = aExpressionLanguage;
   }

   /**
    * Accessor method to determine if the suppress join failure flag is set
    * for this process.
    *
    * @return boolean flag indicating if the suppress join failure flag is set
    *          for this process
    */
   public boolean getSuppressJoinFailure()
   {
      return mSuppressJoinFailure;
   }

   /**
    * Mutator method to set the suppress join failure flag for this process.
    *
    * @param aSuppressJoinFailure flag indicating if join failure warning should be
    *         suppressed for this process
    */
   public void setSuppressJoinFailure(boolean aSuppressJoinFailure)
   {
      mSuppressJoinFailure = aSuppressJoinFailure;
   }

   /**
    * Accessor method to determine if instance compensation is enabled for this process.
    *
    * @return boolean flag indicating if instance compensation is enabled for this process
    */
   public boolean getEnableInstanceCompensation()
   {
      return mEnableInstanceCompensation;
   }

   /**
    * Mutator method to set the enable instance compensation flag for this process.
    *
    * @param aEnableInstanceCompensation the enable instance compensation flag for this process
    */
   public void setEnableInstanceCompensation(boolean aEnableInstanceCompensation)
   {
      mEnableInstanceCompensation = aEnableInstanceCompensation;
   }

   /**
    * Accessor method to determine if this is an abstract process.
    *
    * @return boolean flag indicating if this is an abstract process
    */
   public boolean isAbstractProcess()
   {
      return IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI.equals( getNamespace() )
            ||  ( mAbstractProcess && IAeBPELConstants.BPWS_NAMESPACE_URI.equals(getNamespace()) );
   }

   /**
    * Mutator method to set the abstract process flag for this process.
    *
    * @param aAbstractProcess the abstract process flag for this process
    */
   public void setAbstractProcess(boolean aAbstractProcess)
   {
      mAbstractProcess = aAbstractProcess;
   }

   /**
    * @return Returns the abstractProcessProfile.
    */
   public String getAbstractProcessProfile()
   {
      return mAbstractProcessProfile;
   }

   /**
    * @param aAbstractProcessProfile The abstractProcessProfile to set.
    */
   public void setAbstractProcessProfile(String aAbstractProcessProfile)
   {
      mAbstractProcessProfile = aAbstractProcessProfile;
   }

   /**
    * Get a list of partners which are part of this process.
    * @return an iterator of the partner collection
    */
   public Iterator getPartnerDefs()
   {
      if (getPartnersDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getPartnersDef().getValues();
   }

   /**
    * Gets the partners container.
    */
   public AePartnersDef getPartnersDef()
   {
      return mPartners;
   }

   /**
    * Sets the 'partners' def.
    *
    * @param aDef
    */
   public void setPartnersDef(AePartnersDef aDef)
   {
      mPartners = aDef;
   }

   /**
    * Gets the BPEL comment.
    */
   public String getComment()
   {
      return mComment;
   }

   /**
    * Sets BPEL comment.
    * @param aString
    */
   public void setComment(String aString)
   {
      mComment = aString;
   }

   /**
    * Gets the preamble XML comment.
    * @return String the comment.
    */
   public String getProcessPreambleComment()
   {
      return mProcessPreambleComment;
   }

   /**
    * Sets the preable XML comment.
    * @param aString the comment.
    */
   public void setProcessPreambleComment(String aString)
   {
      mProcessPreambleComment = aString;
   }

   /**
    * Determines if the given partnerLink and operation target a create instance
    * activity.
    * @param aPartnerLinkOpKey
    * @return true if this is an initial entry point
    */
   public boolean isCreateInstance(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      return mCreateInstances.contains(aPartnerLinkOpKey);
   }

   /**
    * Returns true if this process def has a single create instance IMA and no other
    * IMA uses the same plink/operation.
    */
   public boolean isCreateInstanceOperationOnly()
   {
      return mCreateInstanceOnlyOperation;
   }

   /**
    * Setter for the createInstanceOperationOnly flag. This flag is used to report if
    * there is a single create instance IMA and no other IMA uses the same plink/operation.
    * @param aFlag
    */
   protected void setCreateInstanceOperationOnly(boolean aFlag)
   {
      mCreateInstanceOnlyOperation = aFlag;
   }

   /**
    * Return the correlated property QNames for the
    * partnerLink, port type and operation
    * @param aPartnerLinkOpKey partnerLink:opName key
    * @return collection of property QNames
    */
   public Collection getCorrelatedPropertyNames(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      Collection props = (Collection) getCorrelatedReceivesProperties().get(aPartnerLinkOpKey);
      if (props == null)
         props = Collections.EMPTY_SET;

      return props;
   }

   /**
    * Returns location id corresponding to a location path or <code>-1</code> if not found.
    */
   public int getLocationId(String aLocationPath)
   {
      Integer id = (Integer) mLocationPathsToIds.get(aLocationPath);
      return (id == null) ? -1 : id.intValue();
   }

   /**
    * Returns location path corresponding to a location id or <code>null</code> if not found.
    */
   public String getLocationPath(int aLocationId)
   {
      return (String) mLocationIdsToPaths.get(aLocationId);
   }

   /**
    * @return True if the process contains serializable scopes.
    */
   public boolean containsSerializableScopes()
   {
      return mContainsSerializableScopes;
   }

   /**
    * @param aB flag indicating the process contains serializable scopes.
    */
   public void setContainsSerializableScopes(boolean aB)
   {
      mContainsSerializableScopes = aB;
   }

   /**
    * Getter for the max location id.
    */
   public int getMaxLocationId()
   {
      return mLocationIdsToPaths.size();
   }

   /**
    * Gets a coll of correlation property sets that are applicable to the given
    * partner link and operation.
    * @param aPartnerLinkOpKey
    */
   public AeCorrelationCombinations getCorrelationProperties(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      return (AeCorrelationCombinations) mCorrelationPropertiesMap.get(aPartnerLinkOpKey);
   }

   /**
    * Adds a mapping for the given partner link and operation to the set of
    * correlation property names that are part of the message.
    * @param aPartnerLinkOpKey
    * @param aListOfSets
    */
   public void setCorrelationProperties(AePartnerLinkOpKey aPartnerLinkOpKey, AeCorrelationCombinations aListOfSets)
   {
      mCorrelationPropertiesMap.put(aPartnerLinkOpKey, aListOfSets);
   }

   /**
    * Gets the messageExchange value for the createInstance receive with the given
    * partner link and operation. The messageExchange attribute is used to match
    * replies with receives. This method is only used during process creation.
    * @param aPartnerLinkOpKey
    */
   public String getCreateInstanceMessageExchange(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      return (String) mMessageExchangeMap.get(aPartnerLinkOpKey);
   }

   /**
    * Adds a mapping of partner link and operation to a messageExchange value.
    * This is used by a visitor during the preprocessing stage of the process definition.
    * The visitor records the messageExchange value for createInstances since we'll
    * need that information later when creating a process.
    * @param aPartnerLinkOpKey
    * @param aMessageExchange
    */
   public void addCreateInstanceMessageExchange(AePartnerLinkOpKey aPartnerLinkOpKey, String aMessageExchange)
   {
      mMessageExchangeMap.put(aPartnerLinkOpKey, aMessageExchange);
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace The namespace to set.
    */
   public void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * @return Returns the exitOnStandardFault.
    */
   public Boolean getExitOnStandardFault()
   {
      return mExitOnStandardFault;
   }

   /**
    * @param aExitOnStandardFault The exitOnStandardFault to set.
    */
   public void setExitOnStandardFault(Boolean aExitOnStandardFault)
   {
      mExitOnStandardFault = aExitOnStandardFault;
   }

   /**
    * Add an import def to the process.
    *
    * @param aImportDef
    */
   public void addImportDef(AeImportDef aImportDef)
   {
      mImports.add(aImportDef);
   }

   /**
    * @return Returns the imports.
    */
   public Iterator getImportDefs()
   {
      return mImports.iterator();
   }

   /**
    * @param aImports The imports to set.
    */
   public void setImports(List aImports)
   {
      mImports = aImports;
   }

   /**
    * Finds an import matching the passed namesapce.
    * @param aNamespace
    * @return AeImportDef the importdef matching the passed namespace or null if not found.
    */
   public AeImportDef findImportDef(String aNamespace)
   {
      for(Iterator iter=getImportDefs(); iter.hasNext(); )
      {
         AeImportDef imp = (AeImportDef)iter.next();
         if( AeUtil.compareObjects(aNamespace, imp.getNamespace()) )
            return imp;
      }
      return null;
   }

   /**
    * @return Returns the extensionsDef.
    */
   public AeExtensionsDef getExtensionsDef()
   {
      return mExtensionsDef;
   }

   /**
    * @param aExtensionsDef The extensionsDef to set.
    */
   public void setExtensionsDef(AeExtensionsDef aExtensionsDef)
   {
      mExtensionsDef = aExtensionsDef;
   }

   /**
    * Get a list of extension elements which are part of this process.
    * @return an iterator of the extension element collection
    */
   public Iterator getExtensionDefs()
   {
      if (getExtensionsDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getExtensionsDef().getValues();
   }

   /**
    * @return Returns the partnerLinkMap.
    */
   protected Map getPartnerLinkMap()
   {
      return mPartnerLinkMap;
   }

   /**
    * @param aPartnerLinkMap The partnerLinkMap to set.
    */
   protected void setPartnerLinkMap(Map aPartnerLinkMap)
   {
      mPartnerLinkMap = aPartnerLinkMap;
   }

   /**
    * @return Returns the partnerLinkNameMap.
    */
   protected Map getPartnerLinkNameMap()
   {
      return mPartnerLinkNameMap;
   }

   /**
    * @param aPartnerLinkNameMap The partnerLinkNameMap to set.
    */
   protected void setPartnerLinkNameMap(Map aPartnerLinkNameMap)
   {
      mPartnerLinkNameMap = aPartnerLinkNameMap;
   }

   /**
    * Adds a partner link mapping to the process def.  This mapping is used later to locate partner
    * links by name and by full path.  Adding a partner link mapping will do two things.  First, a
    * mapping from location path to partner link def will be made in the partner link map.  Second,
    * a mapping from partner link name to a Set of partner link locations will be updated.  If the
    * same partner link name is used by various partner links (for example, if a scoped partner
    * link uses the same name as one at the process level), then the map from partner link name
    * to partner link location will contain a Set of two partner link locations.
    *
    * @param aPartnerLink
    */
   public void addPartnerLinkMapping(AePartnerLinkDef aPartnerLink)
   {
      getPartnerLinkMap().put(aPartnerLink.getLocationPath(), aPartnerLink);

      // Now update the Set of partner link locations for this partner link name.
      Set locations = (Set) getPartnerLinkNameMap().get(aPartnerLink.getName());
      if (locations == null)
      {
         locations = new HashSet();
         getPartnerLinkNameMap().put(aPartnerLink.getName(), locations);
      }
      locations.add(aPartnerLink.getLocationPath());
   }

   /**
    * Returns the partner link found at the given location.  Internally, the engine should refer to
    * partner links by their path whenever possible.  This method can be used to get the partner
    * link def for a given path.
    *
    * @param aPartnerLinkLocation
    */
   public AePartnerLinkDef getPartnerLinkByLocation(String aPartnerLinkLocation)
   {
      return (AePartnerLinkDef) getPartnerLinkMap().get(aPartnerLinkLocation);
   }

   /**
    * Returns the partner link with the given name.  If the full path to the partner link is not
    * known, this method will return the partner link with the given name.  Note that if multiple
    * partner links with the same name are declared in the process, then this method will be
    * non-deterministic.  In such a case, the <code>getPartnerLinkByLocation</code> method should
    * be used if possible, or the <code>getPartnerLink</code> should be used (in cases where the
    * partner link declared at the process scope is really what is desired).
    *
    * @param aPartnerLinkName
    */
   public AePartnerLinkDef getPartnerLinkByName(String aPartnerLinkName) throws AeAmbiguousPartnerLinkException
   {
      Set locations = (Set) getPartnerLinkNameMap().get(aPartnerLinkName);
      if (locations == null)
         return null;

      if (locations.size() > 1)
         throw new AeAmbiguousPartnerLinkException(locations);

      String location = (String) locations.iterator().next();
      return getPartnerLinkByLocation(location);
   }

   /**
    * Gets the partner link def given the partner link string.  The string will either be the
    * partner link name or location.  Searches the location map first, if the def is not found
    * then we'll assume that the param is a simple name.
    *
    * @param aPartnerLink
    */
   public AePartnerLinkDef findPartnerLink(String aPartnerLink)
   {
      try
      {
         AePartnerLinkDef plDef = getPartnerLinkByLocation(aPartnerLink);
         // If we don't find it, then maybe it has instance info in the location.
         if (plDef == null && AeLocationPathUtils.hasInstanceInfo(aPartnerLink))
            plDef = getPartnerLinkByLocation(AeLocationPathUtils.removeInstanceInfo(aPartnerLink));
         // Still can't find it?  Perhaps the caller is only passing in the partner link name.
         if (plDef == null)
            plDef = getPartnerLinkByName(aPartnerLink);

         return plDef;
      }
      catch (AeAmbiguousPartnerLinkException ex)
      {
         // TODO the ambiguous partner link exception should always be caught during static analysis, but it may be nice to throw externally
         throw new RuntimeException(ex);
      }
   }

   /**
    * Gets the appropriate partner link def given a partner link key.
    *
    * @param aPartnerLinkKey
    */
   public AePartnerLinkDef findPartnerLink(AePartnerLinkDefKey aPartnerLinkKey)
   {
      if (aPartnerLinkKey == null)
         return null;

      String location;

      // Legacy case: if the partner link id is -1, then this is an upgraded
      // database, so search on the partner link name.
      if (aPartnerLinkKey.getPartnerLinkId() == -1)
      {
         location = aPartnerLinkKey.getPartnerLinkName();
      }
      else
      {
         location = getLocationPath(aPartnerLinkKey.getPartnerLinkId());
      }

      return findPartnerLink(location);
   }

   /**
    * Gets an Iterator over all partner link defs in this process.
    */
   public Iterator getAllPartnerLinkDefs()
   {
      return getPartnerLinkMap().values().iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Sets the map of location paths/ids in place and builds an inverse lookup map
    * @param aLocationPathsToIds
    */
   public void setLocationPathsToIds(Map aLocationPathsToIds)
   {
      mLocationPathsToIds.clear();
      mLocationPathsToIds.putAll(aLocationPathsToIds);

      mLocationIdsToPaths.clear();
      for (Iterator iter = aLocationPathsToIds.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         mLocationIdsToPaths.put(entry.getValue(), entry.getKey());
      }
   }

   /**
    * Adds the given namespace to the namespace map using the given prefix (if available).  If
    * the prefix is already mapped to some other namespace, this method will search until it
    * finds an available prefix and use that.  Returns the prefix that was eventually used.
    *
    * @param aPreferredPrefix
    * @param aNamespace
    */
   public String allocateNamespace(String aPreferredPrefix, String aNamespace)
   {
      String prefix = findAvailablePrefix(aPreferredPrefix, aNamespace);
      addNamespace(prefix, aNamespace);
      return prefix;
   }

   /**
    * Finds an available prefix that is "like" the preferred prefix passed in.  If the passed
    * in prefix is available it is used, otherwise a new one is found.
    *
    * @param aPreferredPrefix
    */
   protected String findAvailablePrefix(String aPreferredPrefix, String aNamespace)
   {
      String prefix = aPreferredPrefix;
      String ns = getNamespace(aPreferredPrefix);

      int counter = 1;
      while (ns != null && !ns.equals(aNamespace))
      {
         prefix = aPreferredPrefix + counter;
         ns = getNamespace(prefix);
         counter++;
      }

      return prefix;
   }

   /**
    * @return Returns the disableSelectionFailure.
    */
   public boolean isDisableSelectionFailure()
   {
      return mDisableSelectionFailure;
   }

   /**
    * @param aDisableSelectionFailure The disableSelectionFailure to set.
    */
   public void setDisableSelectionFailure(boolean aDisableSelectionFailure)
   {
      mDisableSelectionFailure = aDisableSelectionFailure;
   }

   /**
    * @return Returns the createTargetXPath.
    */
   public boolean isCreateTargetXPath()
   {
      return mCreateTargetXPath;
   }

   /**
    * @param aCreateTargetXPath The createTargetXPath to set.
    */
   public void setCreateTargetXPath(boolean aCreateTargetXPath)
   {
      mCreateTargetXPath = aCreateTargetXPath;
   }

   /**
    * Gets the message parts map for correlation
    * @param aKey
    */
   public AeMessagePartsMap getMessageForCorrelation(AePartnerLinkOpKey aKey)
   {
      return (AeMessagePartsMap) getCorrelatedReceivesMessagePartsMap().get(aKey);
   }

   /**
    * @return Returns the messagePartsMap.
    */
   protected Map getCorrelatedReceivesMessagePartsMap()
   {
      return mCorrelatedReceivesMessagePartsMap;
   }

   /**
    * @return Returns the partnerLinkProperties.
    */
   protected Map getCorrelatedReceivesProperties()
   {
      return mCorrelatedReceivesProperties;
   }
   
   /**
    * Returns the map of location paths to IDs.
    */
   protected Map getLocationPathsToIdsMap()
   {
      return mLocationPathsToIds;
   }
}
