//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/attachment/AePairSerializer.java,v 1.2 2008/02/17 21:38:55 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.attachment;

import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;

/**
 * Utility class to convert between a java.util.Map containing name/value pairs and an AeFastDocument
 * Restrictions on the map: key and value are assumed to be Strings
 */
public class AePairSerializer
{

   /** xml tag name for name pairs  */
   protected static final String ROOT_ELEMENT = "pairs"; //$NON-NLS-1$

   /** xml tag name for a single name pair */
   protected static final String PAIR_ELEMENT = "pair"; //$NON-NLS-1$

   /** Attribute name of a pair */
   protected static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

   /**
    * Convert a map of name pairs into a fast document xml representation
    * @param aNamePair
    *
    */
   public static AeFastDocument serialize(Map aNamePair)
   {
      AeFastElement root = new AeFastElement(ROOT_ELEMENT);

      for (Iterator pairs = aNamePair.entrySet().iterator(); pairs.hasNext();)
      {
         Map.Entry pair = (Map.Entry)pairs.next();
         AeFastElement pairElement = new AeFastElement(PAIR_ELEMENT);
         pairElement.setAttribute(NAME_ATTRIBUTE, (String)pair.getKey());
         AeFastText value = new AeFastText((String)pair.getValue());
         pairElement.appendChild(value);
         root.appendChild(pairElement);
      }
      AeFastDocument document = new AeFastDocument();
      document.setRootElement(root);
      return document;
   }
}
