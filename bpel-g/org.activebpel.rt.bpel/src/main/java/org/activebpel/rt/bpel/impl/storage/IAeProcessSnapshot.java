// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/IAeProcessSnapshot.java,v 1.2 2005/12/29 20:01:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.w3c.dom.Document;

/**
 * Defines the interface for process snapshots that contain all variables and
 * correlation sets that are live for normal or compensation processing.
 */
public interface IAeProcessSnapshot
{
   /**
    * Returns a <code>Set</code> containing the location paths for all
    * correlation sets.
    */
   public Set getCorrelationSetLocationPaths();

   /**
    * Returns a <code>Set</code> containing the version numbers for all
    * correlation sets with the specified location path.
    */
   public Set getCorrelationSetVersionNumbers(String aLocationPath);

   /**
    * Returns a <code>Set</code> containing the pending invoke instances for
    * the process--those invoke instances that are still in the executing
    * state.
    */
   public Set getPendingInvokes();

   /**
    * Returns a <code>Set</code> containing the location paths for all
    * variables.
    */
   public Set getVariableLocationPaths();

   /**
    * Returns a <code>Set</code> containing the version numbers for all
    * variables with the specified location path.
    */
   public Set getVariableVersionNumbers(String aLocationPath);

   /**
    * Returns the serialization of the correlation set instance corresponding
    * to the specified location path and version number.
    *
    * @param aLocationPath
    * @param aVersionNumber
    */
   public AeFastDocument serializeCorrelationSet(String aLocationPath, int aVersionNumber) throws AeBusinessProcessException;

   /**
    * Returns the serialization of the process.
    *
    * @param aForPersistence <code>true</code> to serialize for persistence.
    */
   public AeFastDocument serializeProcess(boolean aForPersistence) throws AeBusinessProcessException;

   /**
    * Returns the serialization of the variable instance corresponding to the
    * specified location path and version number.
    *
    * @param aVariable
    */
   public AeFastDocument serializeVariable(IAeVariable aVariable) throws AeBusinessProcessException;
   
   /**
    * Gets the variable instance corresponding to the specified location path 
    * and version number.
    * 
    * @param aLocationPath
    * @param aVersionNumber
    * @throws AeBusinessProcessException
    */
   public IAeVariable getVariable(String aLocationPath, int aVersionNumber) throws AeBusinessProcessException;

   /**
    * Sets a correlation set from a previously serialized correlation set
    * document.
    *
    * @param aLocationPath
    * @param aVersionNumber
    * @param aDocument
    * @throws AeBusinessProcessException
    */
   public void setCorrelationSetData(String aLocationPath, int aVersionNumber, Document aDocument) throws AeBusinessProcessException;

   /**
    * Sets a variable from a previously serialized variable document.
    *
    * @param aLocationPath
    * @param aVersionNumber
    * @param aDocument
    * @throws AeBusinessProcessException
    */
   public void setVariableData(String aLocationPath, int aVersionNumber, Document aDocument) throws AeBusinessProcessException;
}
