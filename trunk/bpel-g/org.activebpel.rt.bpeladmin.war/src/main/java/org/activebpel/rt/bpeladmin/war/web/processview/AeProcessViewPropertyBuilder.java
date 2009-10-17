//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessViewPropertyBuilder.java,v 1.20 2008/03/14 16:48:28 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//                PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.coord.AeCoordinationDetail;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.web.AeWebUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class is responsible for building the list of properties to be displayed by the AePropertViewBean. Any
 * state information that is to be displayed, such as variables, is retrieved by this class.
 */
public class AeProcessViewPropertyBuilder extends AeProcessWebPropertyBuilder
{

   /**
    * The view (bean) used to display the content.
    */
   private AeProcessViewBean mPropertyViewBean;

   /**
    * Constructs the builder.
    */
   public AeProcessViewPropertyBuilder(AeProcessViewBean aPropertyViewBean)
   {
      super(aPropertyViewBean.getBpelObject());
      setPropertyViewBean(aPropertyViewBean);
   }

   /**
    * @return Returns the propertyViewBean.
    */
   protected AeProcessViewBean getPropertyViewBean()
   {
      return mPropertyViewBean;
   }
   /**
    * @param aPropertyViewBean The propertyViewBean to set.
    */
   protected void setPropertyViewBean(AeProcessViewBean aPropertyViewBean)
   {
      mPropertyViewBean = aPropertyViewBean;
   }

   /**
    * Overrides method to add the details to the web property bean which is used to display in the admin console.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessWebPropertyBuilder#addDetails(org.activebpel.rt.bpeladmin.war.web.processview.AePropertyNameValue)
    */
   protected void addDetails(AePropertyNameValue aDetails)
   {
      getPropertyViewBean().addDetails(aDetails);
   }

   /**
    * Overrides method to add the property to the web property bean which is used to display in the admin console.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessWebPropertyBuilder#addProperty(org.activebpel.rt.bpeladmin.war.web.processview.AePropertyNameValue)
    */
   protected void addProperty(AePropertyNameValue aNameValue)
   {
      getPropertyViewBean().addProperty(aNameValue);
   }

   /**
    * Overrides method to remove the named property from the web property bean.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessWebPropertyBuilder#removeProperty(java.lang.String)
    */
   protected void removeProperty(String aName)
   {
      getPropertyViewBean().removeProperty(aName);
   }

   /**
    * Adds additional properties based on the child element, such as PartnerLink end point information.
    * @param aElement current element
    */
   protected void buildDetail(Element aElement)
   {
      Node node = ((Node)aElement).getFirstChild();
      while (node != null)
      {
         if ( node.getNodeType() == Node.ELEMENT_NODE )
         {
            buildDetailInternal( (Element)node );
         }
         node = node.getNextSibling();
      }
   }

   /**
    * Construct the read only <code>AePropertyNaveValue</code> from the the given
    * <code>Element</code>.
    * @param aElement
    */
   protected AePropertyNameValue buildDetailInternal( Element aElement )
   {
      return buildDetailInternal( aElement, false );
   }

   /**
    * Construct the <code>AePropertyNaveValue</code> from the the given
    * <code>Element</code>.
    * @param aElement
    * @param aEditFlag
    */
   protected AePropertyNameValue buildDetailInternal( Element aElement, boolean aEditFlag )
   {
      String name = aElement.getNodeName();
      String value = AeXMLParserBase.documentToString(aElement, true);
      AePropertyNameValue pd = new AePropertyNameValue(name, value, aEditFlag);
      getPropertyViewBean().addDetails(pd);
      return pd;
   }

   /**
    * Overrides method to add process log data if available.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      // remove 'path' per requirement 148.
      removeProperty("path");    //$NON-NLS-1$
      // Get the log
      long processId = getPropertyViewBean().getProcessId();
      if (getPropertyViewBean().isRetrieveLogData())
      {
         String log = AeEngineFactory.getEngineAdministration().getProcessLog(processId);
         if ( AeUtil.isNullOrEmpty(log) )
         {
            // get "Log file not available for Process ID " message.
            log = MessageFormat.format(AeMessages.getString("AeProcessViewPropertyBuilder.1"), //$NON-NLS-1$
                  new Object[] { String.valueOf(processId) });
            getPropertyViewBean().setHasLogData(false);
         }
         else
         {
            getPropertyViewBean().setHasLogData(true);
         }
         AePropertyNameValue pd = new AePropertyNameValue("logs", log, false);//$NON-NLS-1$
         getPropertyViewBean().addDetails(pd);
      }

      // build list of parent/child process ids used in coordination (if any)
      StringBuffer sb = new StringBuffer();
      // to build list of parents, first check to see if this is  a child process (and reduce overhead).
      if (getPropertyViewBean().getBpelProcess().isParticipant())
      {
         try
         {
            // get list of parent process (if coordinated)
            AeCoordinationDetail cd = AeEngineFactory.getEngineAdministration().getCoordinatorForProcessId(processId);
            if (cd != null)
            {
               buildCoordinationDetail(cd, sb, true);
               addProperty("parentProcessId", sb.toString());//$NON-NLS-1$
            }
         }
         catch(Exception e)
         {
            AeException.logError(e,e.getLocalizedMessage());
         }
      }
      sb.setLength(0);

      // build list of child processes only if this process had an subprocess invoke (i.e. a coordinator).
      if (getPropertyViewBean().getBpelProcess().isCoordinator())
      {
         try
         {
            // sub processes (if coordinated)
            List list = AeEngineFactory.getEngineAdministration().getParticipantForProcessId(processId);
            if (list.size() > 0)
            {
               for (int i = 0; i < list.size(); i++)
               {
                  AeCoordinationDetail cd = (AeCoordinationDetail) list.get(i);
                  buildCoordinationDetail(cd, sb, false);
                  if (i < (list.size() - 1))
                  {
                     sb.append(", "); //$NON-NLS-1$
                  }
               }
               addProperty("childProcessId", sb.toString());//$NON-NLS-1$
            }
         }
         catch(Exception e)
         {
            AeException.logError(e,e.getLocalizedMessage());
         }
      }
   }

   /**
    * Build a hyperlink to child or parent process.
    * @param aCd
    * @param aBuffer
    */
   protected void buildCoordinationDetail(AeCoordinationDetail aCd, StringBuffer aBuffer, boolean aLinkPath)
   {
      aBuffer.append("<a href=\"processview_detail" + getLinkFileExtension() + "?pid=" + aCd.getProcessId()); //$NON-NLS-1$ //$NON-NLS-2$
      if (aLinkPath)
      {
         aBuffer.append("&xpath=" + AeWebUtil.escapeSingleQuotes( aCd.getLocationPath() )); //$NON-NLS-1$
      }
      aBuffer.append("\" target=\"ae_pid_" + String.valueOf( aCd.getProcessId() ) + "\" >"); //$NON-NLS-1$ //$NON-NLS-2$
      aBuffer.append(aCd.getProcessId());
      aBuffer.append("</a>"); //$NON-NLS-1$
   }

   /**
    * Extends method to get the value of the correlation set as a property.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
      super.visit(aDef);
      if (isHasState() && aDef.getProperties().size() > 0)
      {
         String loc = getPropertyViewBean().getPath();
         String expression = "//" + IAeImplStateNames.STATE_CORRSET + "[@locationPath=\"" + loc + "\"]";   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         try
         {
            Element corrEle = selectElement(expression);
            if (corrEle != null)
            {
               addProperty(IAeImplStateNames.STATE_INIT, corrEle.getAttribute(IAeImplStateNames.STATE_INIT) );
               StringBuffer value = new StringBuffer();
               for(Node node = corrEle.getFirstChild(); node != null; node = node.getNextSibling())
               {
                  if(node.getNodeType() == Node.ELEMENT_NODE)
                     value.append(AeXMLParserBase.documentToString(node, true));
               }
               if (value.length() > 0)
                  addDetails(new AePropertyNameValue(corrEle.getNodeName(), value.toString(), false));
            }
         }
         catch(Exception e)
         {
            AeException.logError(e, e.getLocalizedMessage());
         }
      }
   }

   /**
    * Overrides method to add variable instance data.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aDef)
   {
      super.visit(aDef);
      if (isHasState())
      {
         AeVariableDetail detail =  getVariableDetail(getLocationPath(), getPropertyViewBean().getProcessId());
         AePropertyNameValue pd = new AePropertyNameValue("variableData", detail.mDisplayValue, false); //$NON-NLS-1$

         addVariableDetail(pd, detail);
         addAttachments(detail);
      }
   }

   /**
    * Adds the variable data.
    * @param aNvPair
    * @param aDetail
    */
   protected void addVariableDetail(AePropertyNameValue aNvPair, AeVariableDetail aDetail)
   {
      addDetails(aNvPair);
   }

   /**
    * Adds the attachment items to the process view bean property
    * @param aDetail
    */
   protected void addAttachments(AeVariableDetail aDetail)
   {
      for(Iterator aItr = aDetail.mAttachmentDetails.iterator(); aItr.hasNext();)
      {
         AeAttachmentViewBean attachment = (AeAttachmentViewBean)aItr.next();
         getPropertyViewBean().addAttachments(attachment);
      }
   }

   /**
    * Returns the variable instance data given the process id and the location path. Separates attachment information.
    * @param aLocationPath
    * @param aProcessId
    */
   protected AeVariableDetail getVariableDetail(String aLocationPath, long aProcessId)
   {
      AeVariableDetail rVal = new AeVariableDetail();
      StringBuffer xml = new StringBuffer();
      try
      {
         Document varDoc = AeEngineFactory.getEngineAdministration().getVariable(aProcessId, aLocationPath);

         Node node = varDoc.getFirstChild();

         NamedNodeMap attributes = node.getAttributes();
         Attr attr = (Attr) (attributes.getNamedItem(IAeImplStateNames.STATE_HASATTACHMENTS));
         boolean hasAttachments = (attr != null) && "true".equals(attr.getValue()); //$NON-NLS-1$
         boolean isType         = attributes.getNamedItem(IAeImplStateNames.STATE_TYPE) != null;
         boolean isElement      = attributes.getNamedItem(IAeImplStateNames.STATE_ELEMENT) != null;

         int nodeIndex = 1;
         node = node.getFirstChild();

         while (node != null)
         {
            if (node.getNodeName().equals(IAeImplStateNames.STATE_ATTACHMENT))
            {
               AeAttachmentViewBean attachmentBean = createAttachmentBean(node, nodeIndex++);
               if (attachmentBean != null)
               {
                  rVal.mAttachmentDetails.add(attachmentBean);
               }
            }
            else if (hasAttachments && (isType || isElement))
            {
               // skip 'value' wrapper tag
               xml.append(AeXMLParserBase.documentToString(node.getFirstChild(), true));
            }
            else
            {
               xml.append(AeXMLParserBase.documentToString(node, true));
            }

            node = node.getNextSibling();
         }
         rVal.mAvailable = true;
         rVal.mEditValue = xml.toString();
      }
      catch (AeBusinessProcessException bpe)
      {
         // No variable to serialize
         xml.append(AeMessages.getString("AeProcessViewPropertyBuilder.2")); //$NON-NLS-1$
      }
      catch (Exception e)
      {
         String msg = MessageFormat.format(AeMessages.getString("AeProcessViewPropertyBuilder.5"), //$NON-NLS-1$
               new Object[] { String.valueOf(aProcessId), aLocationPath } );

         AeException.logError(e,msg);
         xml.append(AeMessages.getString("AeProcessViewPropertyBuilder.4")); //$NON-NLS-1$
      }
      if ( xml.length() == 0 )
      {
         // Not initialized.
         xml.append(AeMessages.getString("AeProcessViewPropertyBuilder.3")); //$NON-NLS-1$
      }
      rVal.mDisplayValue = xml.toString();
      return rVal;
   }

   /**
    * Create {@link AeAttachmentViewBean} from node.
    * @param rVal
    * @param node
    * @return
    */
   private AeAttachmentViewBean createAttachmentBean(Node aNode, int aIndex)
   {
      AeAttachmentViewBean aView;

      String attachmentId = aNode.getAttributes().getNamedItem(IAeImplStateNames.STATE_ID).getNodeValue();
      if (AeUtil.isNullOrEmpty(attachmentId))
      {
         aView = null;
      }
      else
      {
         aView = new AeAttachmentViewBean(Long.parseLong(attachmentId), aIndex);
         Node hNode = aNode.getFirstChild();
         while (hNode != null)
         {
             if(hNode.getNodeName().equals(IAeImplStateNames.STATE_ATTACHMENT_HEADER))
             {
                aView.setHeader(hNode.getAttributes().getNamedItem(IAeImplStateNames.STATE_NAME).getNodeValue(), hNode.getFirstChild().getNodeValue());
             }
             hNode = hNode.getNextSibling();
         }
      }

      return aView;
   }

   /**
    * File extension to use for page links.
    */
   public String getLinkFileExtension()
   {
      // TODO cck - more elegant solution preferred
      String ext = ".jsp"; //$NON-NLS-1$
      if(AeEngineFactory.getEngineAdministration().getEngineConfig().getMapEntry("Dotnet") != null) //$NON-NLS-1$
         ext = ".aspx"; //$NON-NLS-1$
      return ext;
   }

   /**
    * Simple wrapper class to hold the variable instance data.
    */
   public class AeVariableDetail
   {
      public boolean mAvailable = false;
      public String mEditValue = null;
      public String mDisplayValue = null;
      public List mAttachmentDetails = new LinkedList();
   }

   public class AeAttachmentDetail
   {
      public long mValueToken;
      public Map mHeaders = new HashMap();
   }

}
