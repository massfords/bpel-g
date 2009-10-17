//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/attachment/AeDeleteAttachmentBean.java,v 1.1 2007/08/13 19:36:34 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview.attachment;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;

/**
 * Bean responsible for deleteing a process variable attachment from the engine by request from the BpelAdmin
 * console.
 */
public class AeDeleteAttachmentBean extends AeAttachmentBeanBase
{
   /**
    * Delete the attachment item number from the process variable
    * @param aItemNumber
    */
   public void setDeleteItem(String aItemNumber)
   {
      try
      {
         int[] itemNumber = new int[1];
         // Adjust to internal offset
         itemNumber[0] = AeUtil.getNumeric(aItemNumber) - 1;

         getAdmin().removeVariableAttachments(getPidAsLong(), getPath(), itemNumber);
      }
      catch (AeException ex)
      {
         setError(ex);
      }
   }
}
