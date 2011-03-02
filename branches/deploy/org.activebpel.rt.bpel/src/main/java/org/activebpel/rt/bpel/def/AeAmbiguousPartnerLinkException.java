// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

import java.util.Set;

import org.activebpel.rt.AeException;


/**
 * This exception is thrown when an ambiguous partner link name is used to resolve a partner link.
 * Since partner links can be defined at the scope level, names can be re-used and therefore shadow
 * earlier declarations.  When that happens, the full path to the partner link must be used.  If
 * the partner link name alone is used in that case, this exception is thrown.
 */
public class AeAmbiguousPartnerLinkException extends AeException
{
   /** The list of locations that map to the partner link name. */
   private Set mPartnerLinkLocations;

   /**
    * Constructs the exception.
    *
    * @param aPartnerLinkLocations
    */
   public AeAmbiguousPartnerLinkException(Set aPartnerLinkLocations)
   {
      super();
      setPartnerLinkLocations(aPartnerLinkLocations);
   }

   /**
    * @return Returns the partnerLinkLocations.
    */
   public Set getPartnerLinkLocations()
   {
      return mPartnerLinkLocations;
   }

   /**
    * @param aPartnerLinkLocations The partnerLinkLocations to set.
    */
   protected void setPartnerLinkLocations(Set aPartnerLinkLocations)
   {
      mPartnerLinkLocations = aPartnerLinkLocations;
   }
}
