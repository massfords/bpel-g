// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/query/AeXPathQueryValidator.java,v 1.9 2006/11/01 16:59:15 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.query;

import java.util.Stack;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ContentModelGroup;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.XMLType;
import org.jaxen.saxpath.Axis;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.helpers.XPathReaderFactory;

/**
 * This class validates xpath queries against the types declared for their contexts.
 */
public class AeXPathQueryValidator extends AeXPathSyntaxValidator
{
   /** The type associated with the context. */
   protected XMLType mRootContext;
   /** The root name of the query context. */ 
   protected QName mRootName;
   /** The current parent within the context (null at start of validation). */   
   protected XMLType mCurrentParent = null;
   /** Stack of contexts for queries which use dot-dot (..) type constructs. */
   protected Stack mContextStack = new Stack();
   /** Flag indicating whether we should validate the root. */
   protected boolean mValidateRoot = false;
   /** Namespace context for validating qualified elements. */
   protected IAeNamespaceContext mNamespaceContext;
   
   /**
    * Constructor for the validator.
    */
   public AeXPathQueryValidator()
   {
   }

   /** 
    * Call to perform validation.  This is the main entry point for validation
    * and can be called multiple times by a process to validate its queries.
    * However this entry point is not reentrant and should only be called again 
    * after it returns. 
    * 
    * @param aNamespaceContext
    * @param aExpr
    * @param aContext
    * @param aRoot
    * @throws AeException
    */
   public void validate(IAeNamespaceContext aNamespaceContext, String aExpr, XMLType aContext, QName aRoot)
         throws AeException
   {
      try
      {
         // ns context
         mNamespaceContext = aNamespaceContext;

         // set new context
         mRootContext = aContext;
         mRootName = aRoot;
         
         // reset instance data 
         mValidateRoot = false;
         mStopValidate = false;
         mCurrentParent = null;
         mContextStack.clear();

         // parse the passed xpath and validate through our handler implementation         
         XPathReader reader = XPathReaderFactory.createReader();
         reader.setXPathHandler( this );
         reader.parse( aExpr );
      }
      catch (Exception e)
      {
         throw new AeException(e.getMessage(), e);
      }
   }

   /**
    * Checks the name passed for applicability at the current level.
    * @see org.jaxen.saxpath.XPathHandler#startNameStep(int, java.lang.String, java.lang.String)
    */
   public void startNameStep(int axis, String prefix, String localName) throws SAXPathException
   {
      // if we have stopped trying to validate then do a short return
      if(mStopValidate)
         return;

      switch(axis)
      {
         case Axis.CHILD:
            if(mCurrentParent == null)
            {
               if (mValidateRoot)
               {
                  validateRootName(prefix, localName);
                  mCurrentParent = mRootContext; 
                  return;
               }
               else
               {
                  mCurrentParent = mRootContext;
               }
            }

            mContextStack.push(mCurrentParent);
            if(mCurrentParent.isSimpleType())
            {
               throw new SAXPathException(AeMessages.format(
                     "AeXPathQueryValidator.2",  //$NON-NLS-1$
                     new Object[] { localName } ));
            }
            else if(mCurrentParent.isComplexType())
            {
               if ("*".equals(localName)) //$NON-NLS-1$
               {
                  mStopValidate = true;
               }
               else
               {
                  if(! findChild((ComplexType)mCurrentParent, prefix, localName))
                     throw new SAXPathException(AeMessages.format(
                           "AeXPathQueryValidator.2",  //$NON-NLS-1$
                           new Object[] { formatQualifiedName(prefix, localName) } ));
               }
            }
            else
            {
               mStopValidate = true;
            }
            break;
         
         case Axis.ATTRIBUTE:
         // TODO validate attributes
         //         if(aType.isComplexType())
         //         {
         //            ComplexType complexType = (ComplexType)aType;
         //            for(Enumeration en=complexType.getAttributeDecls(); en.hasMoreElements();)
         //               aParent.addChild(new AevSchemaTypeModel(aParent, aPart, (AttributeDecl)en.nextElement()));
            mStopValidate = true;
         break;
      
         default:
            // axis passed which we are not handing so stop validating   
            mStopValidate = true;
         break;
      }
   }

   /**
    * Validates that the given prefix and local name match the root name.
    * 
    * @param aPrefix
    * @param aLocalName
    * @throws SAXPathException
    */
   protected void validateRootName(String aPrefix, String aLocalName) throws SAXPathException
   {
      // Validate the local part
      if(! AeUtil.compareObjects(aLocalName, mRootName.getLocalPart()))
      {
         throw new SAXPathException(AeMessages.getString("AeXPathQueryValidator.ERROR_0") + aLocalName); //$NON-NLS-1$
      }

      // Validate the namespace.
      String ns = ""; //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aPrefix))
         ns = mNamespaceContext.resolvePrefixToNamespace(aPrefix);
      if (!AeUtil.compareObjects(AeUtil.getSafeString(ns), AeUtil.getSafeString(mRootName.getNamespaceURI())))
      {
         throw new SAXPathException(AeMessages.format("AeXPathQueryValidator.InvalidNamespaceInNameStep", //$NON-NLS-1$ 
               new String[] { mRootName.getNamespaceURI(), ns }));
      }
   }

   /**
    * Checks the schema model to see if the passed child element name is applicable
    * for the current type.  Note this is recursive as one schema group can be 
    * defined as extending another group and a type can have a base type.
    */
   protected boolean findChild(ComplexType type, String prefix, String localName) throws SAXPathException
   {
      boolean found = findChild((ContentModelGroup)type, prefix, localName);
      
      if(! found && type.getBaseType() != null)
          found = findChild((ComplexType)type.getBaseType(), prefix, localName);
      
      return found;
   }

   /**
    * Checks the schema model to see if the passed child element name is applicable
    * for the current level.  Note this is recursive as one schema group can be 
    * defined as extending another group. 
    */
   protected boolean findChild(ContentModelGroup group, String prefix, String localName) throws SAXPathException
   {
      boolean found = false;
      for(int i=0; !found && i < group.getParticleCount(); ++i)
      {
         Particle particle = group.getParticle(i);
         if (particle.getStructureType() == Particle.ELEMENT)
         {
            if (nameMatchesElementDecl(prefix, localName, (ElementDecl) particle))
            {
               mCurrentParent = ((ElementDecl) particle).getType();
               found = true;
            }
         }
         else if (particle.getStructureType() == Particle.GROUP)
         {
            found = findChild((ContentModelGroup) particle, prefix, localName);
         }
         else
         {
            // Wildcard (extensibility elements) assume we are good, but stop validating
            mStopValidate = true;
            found = true;
         }
      }
      return found;
   }
   
   /**
    * @see org.jaxen.saxpath.XPathHandler#startAbsoluteLocationPath()
    */
   public void startAbsoluteLocationPath() throws SAXPathException
   {
      mCurrentParent = null;
      mContextStack.clear();
      mValidateRoot = true;
   }
   
   /**
    * Returns true if the given qualified name matches the given Element declaration.
    * 
    * @param aPrefix
    * @param aLocalName
    * @param aElementDecl
    */
   protected boolean nameMatchesElementDecl(String aPrefix, String aLocalName, ElementDecl aElementDecl)
   {
      boolean rval = AeUtil.compareObjects(aLocalName, aElementDecl.getName());

      String ns = ""; //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aPrefix))
         ns = mNamespaceContext.resolvePrefixToNamespace(aPrefix);
      String elemNS = AeSchemaUtil.getNamespaceURI(aElementDecl);
      rval = rval && AeUtil.compareObjects(AeUtil.getSafeString(ns), AeUtil.getSafeString(elemNS));
      
      return rval;
   }
   
   /**
    * Formats a prefix + local name properly.
    * 
    * @param aPrefix
    * @param aLocalName
    */
   protected String formatQualifiedName(String aPrefix, String aLocalName)
   {
      if (AeUtil.notNullOrEmpty(aPrefix))
         return aPrefix + ":" + aLocalName; //$NON-NLS-1$
      else
         return aLocalName;
   }
}
