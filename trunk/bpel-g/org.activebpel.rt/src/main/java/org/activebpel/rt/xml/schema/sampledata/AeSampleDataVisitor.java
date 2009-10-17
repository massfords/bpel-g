//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/sampledata/AeSampleDataVisitor.java,v 1.15 2008/03/20 14:27:22 kpease Exp $
////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema.sampledata;

import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractType;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAll;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAny;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAnyAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAnyTypeElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeBaseAttribute;
import org.activebpel.rt.xml.schema.sampledata.structure.AeBaseElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeChoice;
import org.activebpel.rt.xml.schema.sampledata.structure.AeComplexElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeGroup;
import org.activebpel.rt.xml.schema.sampledata.structure.AeSequence;
import org.activebpel.rt.xml.schema.sampledata.structure.AeSimpleElement;
import org.activebpel.rt.xml.schema.sampledata.structure.AeStructure;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *  A visitor class for generating XML sample data for a given Schema Element (of simpleType or
 *  complexType) as represented by an AeStructure object.
 */
public class AeSampleDataVisitor implements IAeSampleDataVisitor
{
   /** The generated sample data document. */
   private Document mDoc = AeXmlUtil.newDocument();
   /** The current node being processed. */
   private Node mCurrent = mDoc;
   /** Sample data preferences. */
   private IAeSampleDataPreferences mPreferences;

   /**
    * Constructor.
    *
    * @param aPreferences
    */
   public AeSampleDataVisitor(IAeSampleDataPreferences aPreferences)
   {
      this(aPreferences, new Date());
   }

   /**
    * Constructor.
    *
    * @param aPreferences
    * @param aDate a specific date instance.
    */
   public AeSampleDataVisitor(IAeSampleDataPreferences aPreferences, Date aDate)
   {
      setPreferences(aPreferences);
   }

   /**
    * Gets the generated sample data document.
    * @return Document.
    */
   public Document getDocument()
   {
      return mDoc;
   }

   /**
    * @param aElementName
    * @param aStructure
    */
   protected void traverse(String aElementName, AeStructure aStructure)
   {
      for (Iterator iter = aStructure.getChildren().iterator(); iter.hasNext();)
      {
         AeStructure str = (AeStructure) iter.next();
         str.accept(this);
      }
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAll)
    */
   public void visit(AeAll aAll)
   {
      visitAllChildren(aAll);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAny)
    */
   public void visit(AeAny aAny)
   {
      // do nothing for any's
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAttribute)
    */
   public void visit(AeAttribute aAttribute)
   {
      // If this is an optional attrib check if we should create it.
      if (aAttribute.isOptional()
            && (!getPreferences().isCreateOptionalAttributes() ||
                !getPreferences().isCreateOptionalAttribute(aAttribute.getName())))
         return;

      QName parent = ((AeBaseElement)aAttribute.getParent()).getName();
      String sampleData = null;
      if (!getPreferences().isGenerateAttributeData())
      {
         sampleData = ""; //$NON-NLS-1$
      }
      else
      {
         if ( aAttribute.getDefaultValue() != null )
         {
            sampleData = aAttribute.getDefaultValue();
         }
         else if ( aAttribute.getFixedValue() != null )
         {
            sampleData = aAttribute.getFixedValue();
         }
         else if ( ! aAttribute.getEnumRestrictions().isEmpty() )
         {
            sampleData = getPreferences().selectAttributeValue(parent, aAttribute.getName(), aAttribute.getEnumRestrictions());
         }
         else
         {
            sampleData = getPreferences().getAttributeData(parent, aAttribute.getName(), aAttribute.getDataType());
         }
      }

      addAttribute(aAttribute.getName(), sampleData);
   }

   /**
    * Adds the attribute to the element with the given value. This accounts for
    * qualified and unqualified attribute names.
    * @param aAttribute
    * @param aValue
    */
   protected void addAttribute(QName aAttribute, String aValue)
   {
      if ( AeUtil.notNullOrEmpty(aAttribute.getNamespaceURI()) )
      {
         // Qualified attibute.
         String qname = getEncodedQName(aAttribute);
         Attr attr = getDocument().createAttributeNS(aAttribute.getNamespaceURI(), qname );
         attr.setValue(aValue);
         ((Element)mCurrent).setAttributeNodeNS(attr);
      }
      else
      {
         // Unqualified attribute.
         ((Element)mCurrent).setAttribute(aAttribute.getLocalPart(), aValue);
      }
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAnyAttribute)
    */
   public void visit(AeAnyAttribute aAnyAttribuite)
   {
      //todo do nothing with anyAttributes for now.  Generate a comment in the DOM?
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeChoice)
    */
   public void visit(AeChoice aChoice)
   {
      int choiceStyle = getPreferences().getChoiceStyle();
      int repeatCount = getRepeatCount(aChoice);

      while ( repeatCount-- > 0)
      {
         switch(choiceStyle)
         {
            case IAeSampleDataPreferences.CHOICE_STYLE_FIRST:
            {
               // Generate the 1st choice only.
               if ( aChoice.getChildren().size() > 0 )
               {
                  AeStructure choice = (AeStructure)aChoice.getChildren().get(0);
                  choice.accept(this);
               }
              break;
            }
            default:
            {
               // Generate all instances, leaving all but the first commented out.
               int count = 0;
               for(Iterator it=aChoice.getChildren().iterator(); it.hasNext(); count++)
               {
                  AeStructure choice = (AeStructure)it.next();
                  choice.accept(this);

                  // if we're past the first choice AND the last child is an element
                  // then we should change it to a comment. If the choice was
                  // an <any> then it's likely that we didn't add a child so the
                  // last node is probably a comment.
                  if (count > 0 && mCurrent.getLastChild() instanceof Element)
                  {
                     Element lastChild = (Element) mCurrent.getLastChild();
                     makeComment(lastChild);
                  }
               }
            }
         }
      }
   }

   /**
    * Replaces the element with a Comment node.
    * @param aElement
    */
   protected void makeComment(Element aElement)
   {
      if (aElement != null)
      {
         // fixme (MF) how do we handle nested comments?
         String commentedOut = convertToString(aElement);
         Comment comment = mDoc.createComment(commentedOut.trim());
         mCurrent.replaceChild(comment, aElement);
      }
   }

   /**
    * Converts the element to a string.
    * @param aElement
    */
   protected String convertToString(Element aElement)
   {
      String commentedOut = AeXMLParserBase.documentToString(aElement, true);
      return commentedOut;
   }

   /**
    * Adds the comment
    * @param aComment
    */
   protected void addComment(String aComment)
   {
      Comment comment = mDoc.createComment(aComment);
      mCurrent.appendChild(comment);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeComplexElement)
    */
   public void visit(AeComplexElement aComplexElement)
   {
      int repeatCount = getRepeatCount(aComplexElement.getName(), aComplexElement);

      while ( repeatCount-- > 0)
         processElement(aComplexElement);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAnyTypeElement)
    */
   public void visit(AeAnyTypeElement aAnyTypeElement)
   {
      int repeatCount = getRepeatCount(aAnyTypeElement.getName(), aAnyTypeElement);

      while ( repeatCount-- > 0)
         processElement(aAnyTypeElement);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeSimpleElement)
    */
   public void visit(AeSimpleElement aSimpleElement)
   {
      int repeatCount = getRepeatCount(aSimpleElement.getName(), aSimpleElement);

      while ( repeatCount-- > 0)
         processElement(aSimpleElement);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeGroup)
    */
   public void visit(AeGroup aGroup)
   {
      visitAllChildren(aGroup);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeSequence)
    */
   public void visit(AeSequence aSequence)
   {
      visitAllChildren(aSequence);
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractElement)
    */
   public void visit(AeAbstractElement aAbstractElement)
   {
      int repeatCount = getRepeatCount(aAbstractElement.getName(), aAbstractElement);
      while ( repeatCount-- > 0)
      {
         if (aAbstractElement.getChildren().isEmpty())
         {
            // if the abstract element's children are empty then we didn't find any
            // sg elements for this element. Since it's abstract, we shouldn't create
            // an element for it. Instead, we'll create a commented out element which
            // informs the user that there were no sg elements found.
            pushElement(aAbstractElement);
            pop();
            if (mCurrent != mDoc)
            {
               makeComment((Element) mCurrent.getLastChild());
            }
         }
         else
         {
            // add the first one
            AeBaseElement element = (AeBaseElement) aAbstractElement.getChildren().get(0);
            element.accept(this);

            // ask the prefs which sg element we should create
            if (mCurrent != mDoc && getPreferences().getChoiceStyle() == IAeSampleDataPreferences.CHOICE_STYLE_FIRST_WITH_COMMENT && aAbstractElement.getChildren().size() > 1)
            {
               int count = 0;
               for(Iterator it=aAbstractElement.getChildren().iterator(); it.hasNext(); count++)
               {
                  AeBaseElement tmp = (AeBaseElement) it.next();
                  if(count == 0)
                     continue;
                  tmp.accept(this);
                  makeComment((Element) mCurrent.getLastChild());
               }
            }
         }
      }
   }

   /**
    * @see org.activebpel.rt.xml.schema.sampledata.IAeSampleDataVisitor#visit(org.activebpel.rt.xml.schema.sampledata.structure.AeAbstractType)
    */
   public void visit(AeAbstractType aAbstractType)
   {
      // if the abstract type doesn't have any children then we either didn't
      // find any concrete types during the traverse of the schemas or we're
      // recursing on an abstract type and we hit our limit.
      // In either case, it's invalid xml to add an element for this type w/o
      // having an xsi:type attr that identifies the concrete type for the
      // element. In this case, we'll push/pop the element and then comment it out.
      if (aAbstractType.getChildren().isEmpty())
      {
         pushElement(aAbstractType);
         pop();
         makeComment((Element) mCurrent.getLastChild());
         return;
      }

      int repeatCount = getRepeatCount(aAbstractType.getName(), aAbstractType);
      while ( repeatCount-- > 0)
      {
         // the element name is determined by the qname of the element with the
         // abstract type. As such, we can push here. Assuming that we have one or
         // more concrete types, we'll have to add an xsi:type attribute to the
         // element in order to indicate which concrete type the element belongs too.
         pushElement(aAbstractType);
         try
         {
            AeBaseElement element = (AeBaseElement) aAbstractType.getChildren().get(0);
            addConcreteElementChoice(element);
         }
         finally
         {
            pop();
         }
      }
      // at this point, we've added one element, check to see if the preference
      // is to add the other possibilities commented out and do so.
      // fixme (MF) not doing this if the current node is the document node.
      // Perhaps change to use a DocumentFragment which allows multiple root nodes.
      // If so, there are other changes that need to be made with respect to
      // declaring ns prefixes. (see getOrCreatePrefix() calls)
      if (mCurrent != mDoc && getPreferences().getChoiceStyle() == IAeSampleDataPreferences.CHOICE_STYLE_FIRST_WITH_COMMENT && aAbstractType.getChildren().size() > 1)
      {
         int count = 0;
         for (Iterator it=aAbstractType.getChildren().iterator(); it.hasNext(); count++)
         {
            AeBaseElement element = (AeBaseElement) it.next();
            if (count == 0)
               continue;

            // add all of the other ones as comments

            pushElement(aAbstractType);
            try
            {
               addConcreteElementChoice(element);
            }
            finally
            {
               pop();
            }
            makeComment((Element) mCurrent.getLastChild());
         }
      }
   }

   /**
    * Adds the concrete element
    * @param aElement
    */
   protected void addConcreteElementChoice(AeBaseElement aElement)
   {
      String xsiType = getEncodedQName(aElement.getName());

      // add the xsi:type attr to the parent based on our selection
      addAttribute(new QName(IAeConstants.W3C_XML_SCHEMA_INSTANCE, "type"), xsiType); //$NON-NLS-1$
      if (aElement instanceof AeComplexElement)
      {
         // add any other attributes for this type
         for(Iterator it=((AeComplexElement)aElement).getAttributes().iterator(); it.hasNext();)
         {
            AeBaseAttribute attrib = (AeBaseAttribute) it.next();
            attrib.accept(this);
         }
      }
      visitAllChildren(aElement);
   }

   /**
    * A convenience method for visiting all the children of the given structure.
    *
    * @param aStructure
    */
   private void visitAllChildren(AeStructure aStructure)
   {
      int repeatCount = getRepeatCount(aStructure);

      while ( repeatCount-- > 0)
      {
         for(Iterator it=aStructure.getChildren().iterator(); it.hasNext();)
         {
            AeStructure struct = (AeStructure)it.next();
            struct.accept(this);
         }
      }
   }

   /**
    * Creates a DOM Element for the given Schema element model.
    * @param aElement
    */
   protected void pushElement(AeBaseElement aElement)
   {
      Element elem = createElement(aElement);

      mCurrent.appendChild(elem);
      mCurrent = elem;
   }

   /**
    * Creates a DOM element from the base element
    * @param aElement
    */
   protected Element createElement(AeBaseElement aElement)
   {
      String name = aElement.getName().getLocalPart();
      String namespace = aElement.getName().getNamespaceURI();

      Element elem;
      if ( mCurrent == mDoc)
      {
         // Processing the root element.

         if ( AeUtil.notNullOrEmpty(namespace) )
         {
            String prefix = getPreferences().getPreferredPrefix(namespace);
            elem = getDocument().createElementNS(namespace, prefix + ":" + name); //$NON-NLS-1$
            elem.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, namespace); //$NON-NLS-1$
         }
         else
         {
            elem = getDocument().createElement(name);
         }
      }
      else
      {
         if ( AeUtil.notNullOrEmpty(namespace) )
            elem = getDocument().createElementNS( namespace, getEncodedQName(aElement.getName()) );
         else
            elem = getDocument().createElement(name);
      }
      return elem;
   }

   /**
    * Generates a QName String (i.e. generates a prefex) for the given QName object.
    * If this operation introduces a new namespace URI then that namespace is declared in
    * the root document element.
    *
    * @param aName QName
    * @return String a QName string, e.g. "ns1:blah".
    */
   protected String getEncodedQName(QName aName)
   {
      return AeXmlUtil.encodeQName(aName, mDoc.getDocumentElement(), getPreferences().getPreferredPrefix(aName.getNamespaceURI()));
   }

   /**
    * Gets the current nodes parent, an effective pop operation.
    */
   protected void pop()
   {
      mCurrent = mCurrent.getParentNode();
   }

   /**
    * Process the given complex or simple element.
    *
    * @param aElement
    */
   private void processElement(AeBaseElement aElement)
   {
      if (aElement.getMinOccurs() == 0
            && (!getPreferences().isCreateOptionalElements() ||
                !getPreferences().isCreateOptionalElement(aElement.getName())))
         return;

      pushElement(aElement);
      try
      {
         if ( aElement.isNillable() && !getPreferences().isGenerateNillableContent() )
         {
            handleNillable(mCurrent);
         }
         else
         {
            if ( aElement instanceof AeComplexElement )
            {
               AeComplexElement complexElement = (AeComplexElement) aElement;

               // add the xsi:type attribute if required
               if (complexElement.getXsiType() != null)
               {
                  String value = getEncodedQName(complexElement.getXsiType());
                  addAttribute(new QName(IAeConstants.W3C_XML_SCHEMA_INSTANCE, "type"), value); //$NON-NLS-1$
               }

               // visit attributes (regular and wildcard) defined by this element.
               for ( Iterator itr = complexElement.getAttributes().iterator(); itr.hasNext(); )
               {
                  AeBaseAttribute attrib = (AeBaseAttribute)itr.next();
                  attrib.accept(this);
               }
               if ( complexElement.isMixed() )
               {
                  Text text = getDocument().createTextNode( getPreferences().getMixedContent(aElement.getName()) );
                  mCurrent.appendChild(text);
               }
               else if ( complexElement.isSimpleContentType() )
               {
                  String sampleData = getPreferences().getElementData(complexElement.getName(), complexElement.getDataType());

                  Text text = getDocument().createTextNode( sampleData );
                  mCurrent.appendChild(text);
               }
            }
            else if (aElement instanceof AeSimpleElement)
            {
               AeSimpleElement simpleElem = (AeSimpleElement)aElement;
               String sampleData = null;
               if ( ! getPreferences().isGenerateElementData() )
               {
                  sampleData = ""; //$NON-NLS-1$
               }
               else
               {
                  if ( simpleElem.getDefaultValue() != null )
                     sampleData = simpleElem.getDefaultValue();
                  else if ( simpleElem.getFixedValue() != null )
                     sampleData = simpleElem.getFixedValue();
                  else if ( AeUtil.notNullOrEmpty(simpleElem.getEnumRestrictions()) )
                     sampleData = getPreferences().selectElementValue(simpleElem.getName(), simpleElem.getEnumRestrictions());
                  else if ( simpleElem.getMinInclusive() != null || 
                            simpleElem.getMaxInclusive() != null ||
                            simpleElem.getMinExclusive() != null ||
                            simpleElem.getMaxExclusive() != null) 
                     sampleData = getPreferences().getElementData(simpleElem.getName(), simpleElem.getDataType(), 
                                                                  simpleElem.getMinInclusive(), simpleElem.getMaxInclusive(), 
                                                                  simpleElem.getMinExclusive(), simpleElem.getMaxExclusive());
                  else
                     sampleData = getPreferences().getElementData(simpleElem.getName(), simpleElem.getDataType());

                  Text text = getDocument().createTextNode(sampleData);
                  mCurrent.appendChild(text);
               }
            }
            else if (aElement instanceof AeAnyTypeElement)
            {
               AeAnyTypeElement anyTypeElem = (AeAnyTypeElement)aElement;
               String sampleData = null;
               if ( ! getPreferences().isGenerateElementData() )
               {
                  sampleData = ""; //$NON-NLS-1$
               }
               else
               {
                  sampleData = getPreferences().getElementData(anyTypeElem.getName(), new QName(IAeConstants.W3C_XML_SCHEMA, "anyType")); //$NON-NLS-1$
               }

               Text text = getDocument().createTextNode(sampleData);
               mCurrent.appendChild(text);
            }

            // visit the children of this element.
            for(Iterator it=aElement.getChildren().iterator(); it.hasNext();)
            {
               AeStructure struct = (AeStructure)it.next();
               struct.accept(this);
            }
         }
      }
      finally
      {
         pop();
      }
   }

   /**
    * Add a nil attribute to the current element.
    *
    * @param aNode the node we're adding the nill attribute to.
    */
   private void handleNillable(Node aNode)
   {
      QName xsiNil = new QName(IAeConstants.W3C_XML_SCHEMA_INSTANCE, "nil"); //$NON-NLS-1$
      String encodedValue = AeXmlUtil.encodeQName(xsiNil, mDoc.getDocumentElement(), "xsi"); //$NON-NLS-1$
      Attr attr = getDocument().createAttributeNS(xsiNil.getNamespaceURI(), encodedValue);
      attr.setValue("true"); //$NON-NLS-1$
      ((Element)aNode).setAttributeNodeNS(attr);
   }

   /**
    * Gets the preferred number of times to repeat instances of the given structure.
    * Note that the results are guarenteed to be within the range of the structures
    * specified minOccurs and maxOccurs range.
    *
    * @param aStructure
    * @return int
    */
   private int getRepeatCount(AeStructure aStructure)
   {
      return getRepeatCount(null, aStructure);
   }

   /**
    * Gets the preferred number of times to repeat instances of the given structure.
    * Note that the results are guarenteed to be within the range of the structures
    * specified minOccurs and maxOccurs range.  If the structure being potentially
    * repeated is an element, then the element qname is passed, otherwise null is
    * passed.
    *
    * @param aStructure
    * @return int
    */
   private int getRepeatCount(QName aElementName, AeStructure aStructure)
   {
      int preferred = getPreferences().getNumberOfRepeatingElements();
      if (aElementName != null)
         preferred = getPreferences().getNumberOfRepeatingElements(aElementName);

      int min = aStructure.getMinOccurs();
      int max = aStructure.getMaxOccurs(); // note: -1 indicates unbounded.

      // Check if preferred count is in range.
      if ( preferred < min )
         preferred = min;
      else if ( max>0 && preferred>max )
         preferred = max;

      return preferred;
   }

   /**
    * @return the mPreferences
    */
   protected IAeSampleDataPreferences getPreferences()
   {
      return mPreferences;
   }

   /**
    * @param aPreferences
    */
   private void setPreferences(IAeSampleDataPreferences aPreferences)
   {
      mPreferences = aPreferences;
   }
}
