//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/message/AeMessagePartsMap.java,v 1.4 2006/09/27 00:33:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.message; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Container for the part info for a specific WSDL message.
 */
public class AeMessagePartsMap implements Serializable
{
   private static final long serialVersionUID = 9066805253378567206L;
   /** map of part names to AeMessagePartTypeInfo objects */
   private final Map<String,AeMessagePartTypeInfo> mParts = new HashMap<>();
   /** name of the message */
   private final QName mMessageType;
   
   /**
    * Ctor accepts message name
    * @param aMessageType
    */
   public AeMessagePartsMap(QName aMessageType)
   {
      mMessageType = aMessageType;
   }
   
   /**
    * Returns true if the message has a single part that is an element
    */
   public boolean isSinglePartElement()
   {
      return getSingleElementPart() != null;
   }
   
   /**
    * Returns the QName of the single message part element or null if this message isn't a single message part element message.
    */
   public QName getSingleElementPart()
   {
      if (getPartsCount() == 1)
      {
         AeMessagePartTypeInfo info = getPartsMap().values().iterator().next();
         return info.getElementName();
      }
      return null;
   }
   
   /**
    * Getter for the parts count
    */
   public int getPartsCount()
   {
      return getPartsMap().size();
   }
   
   /**
    * Adds the part info
    * @param aMessagePartTypeInfo
    */
   public void addPartInfo(AeMessagePartTypeInfo aMessagePartTypeInfo)
   {
      getPartsMap().put(aMessagePartTypeInfo.getName(), aMessagePartTypeInfo);
   }
   
   /**
    * Adds the part info 
    * @param aPart the part to be added.
    * @param aDef
    */
   public void addPartInfo(Part aPart, AeBPELExtendedWSDLDef aDef) throws AeException
   {
      XMLType type = null;
      
      if (aPart.getTypeName() != null)
      {
         type = aDef.findType(aPart.getTypeName());
      }
      else if (aPart.getElementName() != null)
      {
         ElementDecl element = aDef.findElement(aPart.getElementName());
         if (element != null)
         {
            type = element.getType();
         }
      }
      
      if (type == null)
      {
         throw new AeException(AeMessages.format("AeMessagePartsMap.ERROR_NoPartType", new Object[] { aPart.getName(), getMessageType() })); //$NON-NLS-1$
      }
      
      addPartInfo(new AeMessagePartTypeInfo(aPart, type));
   }

   /**
    * Getter for the part info
    * @param aName - name of the part
    */
   public AeMessagePartTypeInfo getPartInfo(String aName)
   {
      return getPartsMap().get(aName);
   }
   
   /**
    * Gets the single element part info or null if its not a single element part message.
    */
   public AeMessagePartTypeInfo getSingleElementPartInfo()
   {
      if (isSinglePartElement())
         return getPartInfo((String) getPartNames().next());
      return null;
   }
   
   /**
    * Gets an iterator for the part names
    */
   public Iterator getPartNames()
   {
      return getPartsMap().keySet().iterator();
   }

   /**
    * @return Returns the messageName.
    */
   public QName getMessageType()
   {
      return mMessageType;
   }

   /**
    * @return Returns the parts map.
    */
   protected Map<String,AeMessagePartTypeInfo> getPartsMap()
   {
      return mParts;
   }
   
   /**
    * Provides string representation for debugging
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("AeMessagePartsMap messageType: ").append(getMessageType()); //$NON-NLS-1$
       for (AeMessagePartTypeInfo part : getPartsMap().values()) {
           sb.append('\n');
           sb.append(part.toString());
       }
      return sb.toString();
   }

   /**
    * Returns a {@link AeMessagePartsMap} for the given WSDL <code>Message</code>.
    *
    * @param aMessage
    * @param aDef
    */
   public static AeMessagePartsMap createMessagePartsMap(Message aMessage, AeBPELExtendedWSDLDef aDef) throws AeException
   {
      AeMessagePartsMap map = new AeMessagePartsMap(aMessage.getQName());

       for (Object o : aMessage.getParts().values()) {
           Part part = (Part) o;
           map.addPartInfo(part, aDef);
       }
      
      return map;
   }
}
 