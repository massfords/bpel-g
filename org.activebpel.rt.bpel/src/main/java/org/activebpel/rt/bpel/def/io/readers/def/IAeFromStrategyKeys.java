// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/IAeFromStrategyKeys.java,v 1.3 2007/11/10 03:36:08 mford Exp $
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
 * The from strategies .
 */
public interface IAeFromStrategyKeys
{
   /*
    * Some from-spec strategy keys that can be used (no arguments).
    */
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_TYPE = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_TYPE);
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_TYPE_QUERY = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_TYPE_QUERY);
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_MESSAGE_PART_QUERY = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE_PART_QUERY);
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_MESSAGE_PART = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE_PART);
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_MESSAGE = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE);
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_ELEMENT_QUERY = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_ELEMENT_QUERY);
   public static final AeSpecStrategyKey KEY_FROM_VARIABLE_ELEMENT = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_VARIABLE_ELEMENT);
   public static final AeSpecStrategyKey KEY_FROM_PROPERTY_TYPE = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_PROPERTY_TYPE);
   public static final AeSpecStrategyKey KEY_FROM_PROPERTY_MESSAGE = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_PROPERTY_MESSAGE);
   public static final AeSpecStrategyKey KEY_FROM_PROPERTY_ELEMENT = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_PROPERTY_ELEMENT);
   public static final AeSpecStrategyKey KEY_FROM_PARTNER_LINK = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_PARTNER_LINK);
   public static final AeSpecStrategyKey KEY_FROM_LITERAL = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_LITERAL);
   public static final AeSpecStrategyKey KEY_FROM_EXPRESSION = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_EXPRESSION);
   public static final AeSpecStrategyKey KEY_FROM_EXTENSION = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_EXTENSION);
}
