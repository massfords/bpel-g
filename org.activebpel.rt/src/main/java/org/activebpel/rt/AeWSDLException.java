// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/AeWSDLException.java,v 1.2 2004/07/08 13:09:43 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt;

/**
 * Ae WSDL Related Exception class.
 */
public class AeWSDLException extends AeException
{
  /**
   * Constructor for AeWSDLExcepton.
   */
  public AeWSDLException()
  {
     super();
  }

  /**
   * Constructor for AeWSDLExcepton.
   * @param aInfo
   */
  public AeWSDLException(String aInfo)
  {
     super(aInfo);
  }

  /**
   * Constructor for AevWSDLException.
   * @param aRootCause
   */
  public AeWSDLException(Throwable aRootCause)
  {
     super(aRootCause);
  }
  
  /**
   * Constructor for AeWSDLExcepton.
   * @param aInfo
   * @param aRootCause
   */
  public AeWSDLException(String aInfo, Throwable aRootCause)
  {
     super(aInfo, aRootCause);
  }

}
