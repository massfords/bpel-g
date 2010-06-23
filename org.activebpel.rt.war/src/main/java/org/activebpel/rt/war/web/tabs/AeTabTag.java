//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/web/tabs/AeTabTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.web.tabs; 

import java.text.MessageFormat;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.war.tags.AeAbstractResourceTag;

/**
 * Initializes an AeTabBean and places it in the page context. The tab bean will
 * contain information about what tabs are on the JSP and is used by the JSP in
 * order to determine the currently selected tab and what JSP should be loaded
 * within the tab container.
 * 
 * The .properties file for the WAR contains a list of the tabs and the JSP that
 * each tab should load when it is active. This list will be read by using the name
 * to produce a key into the resource bundle and keep reading for as many tabs as
 * indicated by the tab.[name].count property.
 * 
 * i.e. If the name is "config" then the following properties will be read to 
 *      produce our tabs:
 * 
 *      tab.config.count=3
 * 
 *      tab.config.1.name=Engine Properties
 *      tab.config.1.page=config-engine.jsp
 * 
 *      tab.config.2.name=Function Contexts
 *      tab.config.2.page=config-fc.jsp
 * 
 *      tab.config.3.name=URN Mappings
 *      tab.config.3.name=config-urn.jsp
 */
public class AeTabTag extends AeAbstractResourceTag
{
   /** name to use for reading the tabs from the resource bundle. */
   private String mName;
   
   /** name of the AeTabBean we're going to populate */
   private String mBeanName;

   /**
    * Creates the AeTabBean with the tabs and places it on the page context.
    * 
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      AeTabBean bean = (AeTabBean) pageContext.findAttribute(getBeanName());      

      MessageFormat formatCount = new MessageFormat("tab.{0}.count"); //$NON-NLS-1$
      MessageFormat formatName = new MessageFormat("tab.{0}.{1}.name"); //$NON-NLS-1$
      MessageFormat formatPage = new MessageFormat("tab.{0}.{1}.page"); //$NON-NLS-1$
      Object[] args = new Object[2];
      args[0] = mName;
      int count = Integer.parseInt(getResourceString(formatCount.format(args)));
      for(int tabNumber = 1; tabNumber <= count; tabNumber++)
      {
         args[1] = new Integer(tabNumber);
         String name = getResourceString(formatName.format(args));
         String page = getResourceString(formatPage.format(args));
         
         AeTab tab = new AeTab();
         tab.setName(name);
         tab.setPage(page);
         
         bean.addTab(tab);
      }
      
      return SKIP_BODY;
   }
   
   /**
    * Setter for the name.
    * 
    * @param aName
    */
   public void setName(String aName)
   {
      mName = aName;
   }
   
   /**
    * Setter for the bean name.
    * @param aBeanName
    */
   public void setBeanName(String aBeanName)
   {
      mBeanName = aBeanName;
   }
   
   /**
    * Getter for the bean name.
    */
   protected String getBeanName()
   {
      return mBeanName;
   }
}
 