// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeVariable.java,v 1.64 2008/02/21 17:03:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.activebpel.rt.attachment.AeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.activity.IAeCopyFromParent;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;
import org.activebpel.rt.bpel.impl.activity.assign.IAeFrom;
import org.activebpel.rt.message.AeEmptyMessage;
import org.activebpel.rt.message.AeMessageDataValidator;
import org.activebpel.rt.message.AeMessagePartTypeInfo;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.schema.AeXmlValidator;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** The class implementing all variable interactions. */
public class AeVariable implements IAeVariable, IAeCopyFromParent, Cloneable
{
   /** The scope which owns this variable. */
   protected IAeVariableContainer mParent;
   /** impl object for the from */
   private IAeFrom mFrom;
   /** The associated variable definition. */
   protected AeVariableDef mVariableDef;
   /** The associated message data if any. */
   protected IAeMessageData mMessageData;
   /** The associated type data if any. ComplexTypes are stored as a Document for reasons described in getElementData() */
   protected Object mTypeData;
   /** The associated element if any. This is stored as a Document for reasons described in getElementData()*/
   protected Document mElementData;
   /** Location path override if different from the definition object */
   protected String mLocationPath;
   /** The optional associated attachments for all variable types. Note that this
    * container calways has the correct attachment information for the variable even when
    * the variable is of type message.  For message type variables the getMessageData
    * method will make right the attachment information returned with the message.
    */
   protected IAeAttachmentContainer mAttachmentContainer;
   
   // Note on versionNumber: The versionNumber field is not really a version number
   // per se but rather a unique id assigned to each version of a variable within
   // a process. The process maintains an int to use and increment with each new
   // variable created. 

   /** The version number of the variable. Increments with each change */
   protected int mVersionNumber;

   /**
    * Constructs a new variable from a definition object
    * @param aParent
    * @param aVariableDef
    */
   public AeVariable(IAeVariableContainer aParent, AeVariableDef aVariableDef)
   {
      setDefinition(aVariableDef);
      setParent(aParent);
      // Incrementing the version number here to avoid a problem introduced with
      // parallel forEach's. In that case, unassigned variables will have the same
      // locationId and versionNumber which will be a problem with the persistence layer
      incrementVersionNumber();
   }
   
   /**
    * No arg ctor for testing 
    */
   protected AeVariable()
   {      
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeVariable#isElement()
    */
   public boolean isElement()
   {
      return getDefinition().isElement();
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeVariable#isMessageType()
    */
   public boolean isMessageType()
   {
      return getDefinition().isMessageType();
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeVariable#isType()
    */
   public boolean isType()
   {
      return getDefinition().isType();
   }

   /**
    * Get the definition associated with this process
    * @see org.activebpel.rt.bpel.IAeVariable#getDefinition()
    */
   public AeVariableDef getDefinition()
   {
      return mVariableDef;
   }

   /**
    * Set the definition associated with this process
    * @param aVariableDef definition object to be set
    */
   protected void setDefinition(AeVariableDef aVariableDef)
   {
      mVariableDef = aVariableDef;
   }
   
   /**
    * @return Returns the from.
    */
   public IAeFrom getFrom()
   {
      return mFrom;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeCopyFromParent#setFrom(org.activebpel.rt.bpel.impl.activity.assign.IAeFrom)
    */
   public void setFrom(IAeFrom aFrom)
   {
      mFrom = aFrom;
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeLocatableObject#hasLocationId()
    */
   public boolean hasLocationId()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#getLocationPath()
    */
   public String getLocationPath()
   {
      if (hasCustomLocationPath())
      {
         return mLocationPath;
      }
      return getDefinition().getLocationPath();
   }
   
   /**
    * Setter for the location path.
    * 
    * @param aLocationPath
    */
   public void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeLocatableObject#getLocationId()
    */
   public int getLocationId()
   {
      return getDefinition().getLocationId();
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeLocatableObject#hasCustomLocationPath()
    */
   public boolean hasCustomLocationPath()
   {
      return mLocationPath != null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeLocatableObject#setLocationId(int)
    */
   public void setLocationId(int aId)
   {
      // no op here
   }
   
   /**
    * Returns the name of the variable
    * @see org.activebpel.rt.bpel.IAeVariable#getName()
    */
   public String getName()
   {
      return mVariableDef.getName();
   }

   /**
    * Get the message data associated with the variable. Returns null if not a message.
    * @see org.activebpel.rt.bpel.IAeVariable#getMessageData()
    */
   public IAeMessageData getMessageData() throws AeUninitializedVariableException
   {
      if (mMessageData == null && !mVariableDef.hasParts() && mVariableDef.isMessageType())
      {
         mMessageData = new AeEmptyMessage(mVariableDef.getMessageType());
      }

      if (mMessageData == null)
      {
         throw new AeUninitializedVariableException(getProcess().getBPELNamespace());
      }
      
      // Give the message data a clone of our attachment container that doesn't
      // automatically update our version number and doesn't have access to this variable,
      // which would impede proper garbage collection.
      mMessageData.setAttachmentContainer(hasAttachments() ? new AeAttachmentContainer(mAttachmentContainer) : null);
      
      return mMessageData;
   }

   /**
    * Sets the message data associated with the variable.
    * @see org.activebpel.rt.bpel.IAeVariable#setMessageData(org.activebpel.rt.message.IAeMessageData)
    */
   public void setMessageData(IAeMessageData aData)
   {
      mMessageData = aData;
      if (aData != null && aData.hasAttachments())
         setAttachmentData(aData.getAttachmentContainer());
      
      incrementVersionNumber();
   }

   /**
    * Returns the associated type data if any.
    * @see org.activebpel.rt.bpel.IAeVariable#getTypeData()
    */
   public Object getTypeData() throws AeUninitializedVariableException
   {
      if (mTypeData == null)
      {
         throw new AeUninitializedVariableException(getProcess().getBPELNamespace());
      }
      else if (mTypeData instanceof Document)
      {
         return ((Document)mTypeData).getDocumentElement();
      }
      else
      {
         return mTypeData;
      }
   }

   /**
    * Set the type data associated with this variable.
    * @see org.activebpel.rt.bpel.IAeVariable#setTypeData(java.lang.Object)
    */
   public void setTypeData(Object aData)
   {
      incrementVersionNumber();
      if (aData instanceof Element)
      {
         Element e = (Element) aData;
         if (e != e.getOwnerDocument().getDocumentElement())
         {
            e = AeXmlUtil.cloneElement(e);
         }
         mTypeData = e.getOwnerDocument();
      }
      else
      {
         mTypeData = aData;
      }
   }

   /**
    * Get the data if the variable is specified as element. Returns null if not element.
    * @see org.activebpel.rt.bpel.IAeVariable#getElementData()
    */
   public Element getElementData() throws AeUninitializedVariableException
   {
      /*
       * The element and type data are stored in a document as a result of the keepSrcElementName 
       * attribute on copy operations. This attribute causes the copy operation to keep the
       * name of the source element during the copy. Since the name of an element is
       * immutable in DOM, we replace the existing element with a clone of the source. The variable
       * therefore needs to hold onto the Document as opposed to the Element in order to ensure that
       * this change is propagated to the variable, otherwise the variable would be holding onto an 
       * orphaned element as opposed to the current data. 
       */
      
      if (mElementData == null)
         throw new AeUninitializedVariableException(getProcess().getBPELNamespace());
      else
         return mElementData.getDocumentElement();
   }

   /**
    * Set the element data associated with this variable.
    * @see org.activebpel.rt.bpel.IAeVariable#setElementData(org.w3c.dom.Element)
    */
   public void setElementData(Element aData)
   {
      incrementVersionNumber();
      if (aData != aData.getOwnerDocument().getDocumentElement())
      {
         mElementData = AeXmlUtil.cloneElement(aData).getOwnerDocument();
      }
      else 
      {
         mElementData = aData.getOwnerDocument();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#getMessageType()
    */
   public QName getMessageType()
   {
      return getDefinition().getMessageType();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#getElement()
    */
   public QName getElement()
   {
      return getDefinition().getElement();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#getType()
    */
   public QName getType()
   {
      return getDefinition().getType();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#validate()
    */
   public void validate() throws AeInvalidVariableException, AeBpelException
   {
      validate(false);
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeVariable#validate(boolean)
    */
   public void validate(boolean aAllowEmptyPartData) throws AeBpelException, AeInvalidVariableException
   {
      Exception cause = null;
      String errorMessage = null;

      try
      {
         if (isType())
         {
            // For simple data, declare doc around it. Complex data must already be in form of a doc
            XMLType datatype = getDefinition().getXMLType();
            AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForType(getProcess().getProcessPlan(), getDefinition().getType());
            AeXmlValidator validator = new AeXmlValidator(def, getProcess().getEngine().getTypeMapping());
            if (! AeXmlUtil.isComplexOrAny(datatype))
            {
               errorMessage = validator.validateSimpleType(getName(), getTypeData(), getType());
            }
            else if (getTypeData() instanceof Document)
            {
               errorMessage = validator.validateComplexType((Document) getTypeData(), getType());
            }
            else if (getTypeData() instanceof Element)
            {
               errorMessage = validator.validateComplexType((Element) getTypeData(), getType());
            }
            else
            {
               Object[] args = {getName(), getType(), getTypeData()};
               errorMessage = AeMessages.format("AeVariable.ExpectedTypeDocument", args); //$NON-NLS-1$
            }
         }
         else if (isElement())
         {
            QName runtimeElementQName = mElementData == null ? null : AeXmlUtil.getElementType(mElementData.getDocumentElement());            
            AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForElement(getProcess().getProcessPlan(), runtimeElementQName);
            AeXmlValidator validator = new AeXmlValidator(def, getProcess().getEngine().getTypeMapping());
            
            errorMessage = validator.validateElement(mElementData, getElement());
         }
         else if (isMessageType())
         {
            AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForMsg(getProcess().getProcessPlan(), getDefinition().getMessageType());
            AeMessageDataValidator messageValidator = new AeMessageDataValidator(aAllowEmptyPartData, getDefinition().getMessageParts());
            errorMessage = messageValidator.validate(getMessageData(), def, getProcess().getEngine().getTypeMapping());
         }
      }
      catch(Exception e)
      {
         cause = e;
         errorMessage = e.getLocalizedMessage();
      }

      if (errorMessage != null)
      {
         String bpelNamespace = getProcess().getBPELNamespace();
         AeInvalidVariableException exception = AeInvalidVariableException.createException(bpelNamespace, getName(), errorMessage, cause);
         throw exception;
      }
   }
   
   /**
    * Getter for the version number
    */
   public int getVersionNumber()
   {
      return mVersionNumber;
   }

   /**
    * Returns a copy of this variable so it can be persisted in our snapshot for
    * any future compensation logic.
    */
   public Object clone()
   {
      try
      {
         AeVariable copy = (AeVariable) super.clone();
         
         if (hasData())
         {
            // need to do deep clones of our Node data
            if (isElement())
            {
               copy.mElementData = AeXmlUtil.cloneElement(mElementData.getDocumentElement()).getOwnerDocument();
            }
            else if (isType() && mTypeData instanceof Document)
            {
               copy.mTypeData = AeXmlUtil.cloneElement(((Document)mTypeData).getDocumentElement());
            }
            else if (isMessageType())
            {
               copy.mMessageData = (IAeMessageData) mMessageData.clone();
            }
         }
         
         if (hasAttachments())
         {
            // copy the current attachment container to the new object (this will copy it to associate with the new variable)
            // also note that even if this is a message variable we don't need to deal with its attachment container
            // at this point as it is rectified by the getMessageData call.
            copy.setAttachmentData(mAttachmentContainer);
         }
         
         // make sure the version number doesn't change (note copying attachments above could change it)
         copy.setVersionNumber(getVersionNumber());
         
         return copy;
      }
      catch (CloneNotSupportedException e)
      {
         throw new InternalError("Unexpected error during clone: " + e.getLocalizedMessage()); //$NON-NLS-1$
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeVariable#restore(org.activebpel.rt.bpel.IAeVariable)
    */
   public void restore(IAeVariable aMyClone)
   {
      AeVariable myClone = (AeVariable) aMyClone;
      // sanity check here
      if (mVariableDef == myClone.mVariableDef)
      {
         mMessageData = myClone.mMessageData;
         mTypeData = myClone.mTypeData;
         mElementData = myClone.mElementData;
         setAttachmentData(myClone.mAttachmentContainer);
         mVersionNumber = myClone.mVersionNumber;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#clear()
    */
   public void clear()
   {
      if (hasData())
      {
         incrementVersionNumber();
      }

      mElementData = null;
      mMessageData = null;
      mTypeData = null;
      mAttachmentContainer = null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#hasMessageData()
    */
   public boolean hasMessageData()
   {
      return mMessageData != null;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#incrementVersionNumber()
    */
   public void incrementVersionNumber()
   {
      // not thread safe but this isn't an issue with the current impl since only
      // one activity will be executing at a time.
      IAeBusinessProcessInternal process = getProcess();
      setVersionNumber(process.getNextVersionNumber());
      process.setNextVersionNumber(getVersionNumber() + 1);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#hasData()
    */
   public boolean hasData()
   {
      return (mElementData != null) || (mMessageData != null) || (mTypeData != null);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#setVersionNumber(int)
    */
   public void setVersionNumber(int aVersionNumber)
   {
      mVersionNumber = aVersionNumber;
   }
   
   /**
    * @return The process which owns this variable.
    */
   public IAeBusinessProcessInternal getProcess()
   {
      return getParent().getParent().getProcess();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#getParent()
    */
   public IAeVariableContainer getParent()
   {
      return mParent;
   }
   
   /**
    * Setter for the parent
    * @param aParent
    */
   protected void setParent(IAeVariableContainer aParent)
   {
      mParent = aParent;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeVariable#initializeForAssign(java.lang.String)
    */
   public void initializeForAssign(String aPartName)
   {
      if (isType())
      {
         if (!hasData())
         {
            if (getDefinition().getXMLType().isSimpleType())
            {
               mTypeData = ""; //$NON-NLS-1$
            }
            else
            {
               Document doc = AeXmlUtil.newDocument();
               doc.appendChild(doc.createElementNS("", "type")); //$NON-NLS-1$ //$NON-NLS-2$
               mTypeData = doc;
            }
         }
      }
      else if (isElement())
      {
         if (!hasData())
         {
            Document doc = AeXmlUtil.newDocument();
            QName elementName = getDefinition().getElement();
            // Note: the old code creating an element two diff ways. 
            //       The first was during message part copy where it used the node name of the src node and relied on copyNodeContents to copy the ns decl's over
            //       The other was copying element to element or string to element where it created the container element using aetgt as the prefix and setting the attribute in place
            //       I'm not convinced that we need to have support for both flavors
            Element element = doc.createElementNS(elementName.getNamespaceURI(), "aetgt:"+elementName.getLocalPart()); //$NON-NLS-1$
            element.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns:aetgt", elementName.getNamespaceURI()); //$NON-NLS-1$
            doc.appendChild(element);
            mElementData = doc;
         }
      }
      else if (isMessageType())
      {
         if (!hasData())
         {
            setMessageData(AeMessageDataFactory.instance().createMessageData(getMessageType()));
         }
         
         if (aPartName != null)
         {
            try
            {
               if (getMessageData().getData(aPartName) == null)
               {
                  AeMessagePartTypeInfo part = getDefinition().getPartInfo(aPartName);
                  
                  Object data = null;
                  // initialize default value
                  if (part.isElement())
                  {
                     data = createEmptyPart(part.getElementName().getNamespaceURI(), part.getElementName().getLocalPart());
                  }
                  else if (part.getXMLType().isSimpleType())
                  {
                     data = ""; //$NON-NLS-1$
                  }
                  else
                  {
                     data = createEmptyPart(null, aPartName);
                  }
                  
                  getMessageData().setData(aPartName, data);
               }
            }
            catch (AeBpelException e)
            {
               // there are two places where an exception can be raised.
               // 1. getMessageData() if the variable is uninitialized, but we 
               //    just set it above so that's impossible
               // 2. getDefinition().getPartInfo(aPartName) where the type info 
               //    for the part won't be found. Our static analysis checks 
               //    should catch any problems where a message part's type info 
               //    cannot be resolved OR the user is accessing a part that 
               //    doesn't exist. This type of error is raised during the 
               //    preprocessing of the def so it shouldn't come up at 
               //    runtime.
               e.logError();
            }
         }
      }
   }

   /**
    * Inits the empty part to a document with a single element with the 
    * given name.
    * 
    * @param aNamespaceURI
    * @param aLocalName
    */
   protected Document createEmptyPart(String aNamespaceURI, String aLocalName)
   {
      Document doc = AeXmlUtil.newDocument();
      
      Element root = null;
      if(aNamespaceURI == null)
      {
         root = doc.createElementNS("", aLocalName); //$NON-NLS-1$
      }
      else
      {
         root = doc.createElementNS(aNamespaceURI, "aetgt:"+aLocalName); //$NON-NLS-1$
         root.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns:aetgt", aNamespaceURI); //$NON-NLS-1$
      }
      doc.appendChild(root);
      return doc;
   }

   /**
    * Returns the attachment container for this variable.
    *
    * @see org.activebpel.rt.bpel.IAeVariable#getAttachmentData()
    */
   public IAeAttachmentContainer getAttachmentData()
   {
      if (mAttachmentContainer == null )
      {
         mAttachmentContainer = new AeVariableAttachmentContainer(); 
      }
      return mAttachmentContainer;
   }

   /**
    * Sets the attachment container for this variable. Note this does a shallow copy of 
    * the attachment information to a container owned by this variable.
    * @see org.activebpel.rt.bpel.IAeVariable#setAttachmentData(org.activebpel.rt.attachment.IAeAttachmentContainer)
    */
   public void setAttachmentData(IAeAttachmentContainer aAttachmentContainer)
   {   
      boolean otherHasAttachments = (aAttachmentContainer != null) && (aAttachmentContainer.size() != 0); 

      if (otherHasAttachments)
      {
         mAttachmentContainer = new AeVariableAttachmentContainer(aAttachmentContainer);

         // Always increment the version number despite the chance that it's the
         // exact same set of attachments.
         incrementVersionNumber();
      }
      else if (hasAttachments())
      {
         mAttachmentContainer = null;
         incrementVersionNumber();
      }
   }

   /**
    * Returns <code>true</code> if and only if this variable has attachments.
    * 
    * @see org.activebpel.rt.bpel.IAeVariable#hasAttachments()
    */
   public boolean hasAttachments()
   {
      return (mAttachmentContainer != null) && (mAttachmentContainer.size() != 0);   
   }
   
   /**
    * Extends {@link AeAttachmentContainer} to increment the variable's version
    * number if the attachment container is modified. 
    */
   protected class AeVariableAttachmentContainer extends AeAttachmentContainer
   {
      /**
       * Constructs an empty container.
       */
      public AeVariableAttachmentContainer()
      {
      }

      /**
       * Constructs a container for the given collection of attachments.
       *
       * @param aAttachments
       */
      public AeVariableAttachmentContainer(Collection aAttachments)
      {
         super(aAttachments);
      }

      /**
       * @see org.activebpel.rt.attachment.AeAttachmentContainer#add(java.lang.Object)
       */
      public boolean add(Object aObject)
      {
         return incrementVersionIfChanged(super.add(aObject));
      }

      /**
       * @see org.activebpel.rt.attachment.AeAttachmentContainer#add(int, java.lang.Object)
       */
      public void add(int aIndex, Object aObject)
      {
         int oldSize = size();
         super.add(aIndex, aObject);
         int newSize = size();
         incrementVersionIfChanged(newSize != oldSize);
      }

      /**
       * @see org.activebpel.rt.attachment.AeAttachmentContainer#addAll(java.util.Collection)
       */
      public boolean addAll(Collection aCollection)
      {
         return incrementVersionIfChanged(super.addAll(aCollection));
      }

      /**
       * @see java.util.ArrayList#clear()
       */
      public void clear()
      {
         incrementVersionIfChanged(!isEmpty());
         super.clear();
      }

      /**
       * @see java.util.ArrayList#remove(int)
       */
      public Object remove(int aIndex)
      {
         Object result = super.remove(aIndex);
         incrementVersionNumber(); // always increment the version number
         return result;
      }

      /**
       * @see org.activebpel.rt.attachment.AeAttachmentContainer#set(int, java.lang.Object)
       */
      public Object set(int aIndex, Object aObject)
      {
         Object result = super.set(aIndex, aObject);
         incrementVersionIfChanged(result != null);
         return result;
      }
      
      /**
       * Increments the variable version number if <code>aChanged</code> is
       * <code>true</code>.
       *
       * @param aChanged
       * @return aChanged
       */
      protected boolean incrementVersionIfChanged(boolean aChanged)
      {
         // Must check AeVariable.this != null to avoid null pointer exception
         // if this is called while the object is still being constructed.
         if (aChanged && (AeVariable.this != null))
         {
            incrementVersionNumber();
         }

         return aChanged;
      }
   }
}