//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/IAeErrorAwareBean.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags; 

/**
 * Interface for tags to report errors when setting properties on beans. The likely
 * cause of these errors would be data entry errors by the user in a web form.
 * This allows the bean to be aware of the error and provide details of the error
 * on the page as opposed to a JspException. 
 */
public interface IAeErrorAwareBean
{
   /**
    * Called by the custom tag when it couldn't set a property on the bean.
    * 
    * @param aPropertyName The name of the property.
    * @param aValue The value of the property that caused the problem.
    * @param aErrorMessage The error message.
    */
   public void addError(String aPropertyName, String aValue, String aErrorMessage);
   
   /**
    * Returns a value that was added previously by a call to <code>addError</code>.
    * If present, this value represents a failed attempt to set a value on a bean
    * due to an invalid format or some other runtime parsing error. This method
    * allows tags to display the offending value in place of a null or default
    * value.
    * 
    * i.e. If a user enters an invalid date format, then the SetDate tag will catch
    * the error and report it on the bean. There will likely be a form field on the
    * JSP which displays the date for the bean. The DateFormatter tag should check
    * the bean to see if it's an instanceof IAeErrorAwareBean, and if it is, it 
    * will check to see if there is an error value for that property. If there is,
    * it will display the error value. If not, it will display the formatted value for
    * the bean's property.
    * 
    * @param aPropertyName
    */
   public String getErrorValue(String aPropertyName);
}
 