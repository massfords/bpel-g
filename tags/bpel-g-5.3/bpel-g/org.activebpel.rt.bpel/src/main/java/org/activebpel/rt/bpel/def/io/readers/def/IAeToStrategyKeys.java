// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/IAeToStrategyKeys.java,v 1.4 2008/03/11 21:42:09 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.readers.def;

/**
 * Sstrategies for selecting the "L" value in a copy operation.
 */
public interface IAeToStrategyKeys
{
   /*
    * Some to-spec strategy keys that can be used (no arguments).
    */
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_TYPE = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_TYPE);
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_TYPE_QUERY = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_TYPE_QUERY);
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_MESSAGE_PART_QUERY = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART_QUERY);
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_MESSAGE_PART = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART);
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_MESSAGE = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_MESSAGE);
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_ELEMENT_QUERY = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_ELEMENT_QUERY);
   public static final AeSpecStrategyKey KEY_TO_VARIABLE_ELEMENT = new AeSpecStrategyKey(IAeToStrategyNames.TO_VARIABLE_ELEMENT);
   public static final AeSpecStrategyKey KEY_TO_PROPERTY_TYPE = new AeSpecStrategyKey(IAeToStrategyNames.TO_PROPERTY_TYPE);
   public static final AeSpecStrategyKey KEY_TO_PROPERTY_MESSAGE = new AeSpecStrategyKey(IAeToStrategyNames.TO_PROPERTY_MESSAGE);
   public static final AeSpecStrategyKey KEY_TO_PROPERTY_ELEMENT = new AeSpecStrategyKey(IAeToStrategyNames.TO_PROPERTY_ELEMENT);
   public static final AeSpecStrategyKey KEY_TO_PARTNER_LINK = new AeSpecStrategyKey(IAeToStrategyNames.TO_PARTNER_LINK);
   public static final AeSpecStrategyKey KEY_TO_EXPRESSION = new AeSpecStrategyKey(IAeToStrategyNames.TO_EXPRESSION);
   public static final AeSpecStrategyKey KEY_TO_EXTENSION = new AeSpecStrategyKey(IAeToStrategyNames.TO_EXTENSION);
}
