// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeXmlUtil.java,v 1.101.4.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.xml.AeElementBasedNamespaceContext;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Utility methods for working with XML.
 */
public class AeXmlUtil
{
   /** Some patterns for doing XML regular expression matching. */
   public static final String LETTER_PATTERN = "\u0041-\u005A\u0061-\u007A\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u0131\u0134-\u013E\u0141-\u0148\u014A-\u017E\u0180-\u01C3\u01CD-\u01F0\u01F4-\u01F5\u01FA-\u0217\u0250-\u02A8\u02BB-\u02C1\u0386\u0388-\u038A\u038C\u038E-\u03A1\u03A3-\u03CE\u03D0-\u03D6\u03DA\u03DC\u03DE\u03E0\u03E2-\u03F3\u0401-\u040C\u040E-\u044F\u0451-\u045C\u045E-\u0481\u0490-\u04C4\u04C7-\u04C8\u04CB-\u04CC\u04D0-\u04EB\u04EE-\u04F5\u04F8-\u04F9\u0531-\u0556\u0559\u0561-\u0586\u05D0-\u05EA\u05F0-\u05F2\u0621-\u063A\u0641-\u064A\u0671-\u06B7\u06BA-\u06BE\u06C0-\u06CE\u06D0-\u06D3\u06D5\u06E5-\u06E6\u0905-\u0939\u093D\u0958-\u0961\u0985-\u098C\u098F-\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09DC-\u09DD\u09DF-\u09E1\u09F0-\u09F1\u0A05-\u0A0A\u0A0F-\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32-\u0A33\u0A35-\u0A36\u0A38-\u0A39\u0A59-\u0A5C\u0A5E\u0A72-\u0A74\u0A85-\u0A8B\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2-\u0AB3\u0AB5-\u0AB9\u0ABD\u0AE0\u0B05-\u0B0C\u0B0F-\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32-\u0B33\u0B36-\u0B39\u0B3D\u0B5C-\u0B5D\u0B5F-\u0B61\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99-\u0B9A\u0B9C\u0B9E-\u0B9F\u0BA3-\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB5\u0BB7-\u0BB9\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C33\u0C35-\u0C39\u0C60-\u0C61\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CDE\u0CE0-\u0CE1\u0D05-\u0D0C\u0D0E-\u0D10\u0D12-\u0D28\u0D2A-\u0D39\u0D60-\u0D61\u0E01-\u0E2E\u0E30\u0E32-\u0E33\u0E40-\u0E45\u0E81-\u0E82\u0E84\u0E87-\u0E88\u0E8A\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAA-\u0EAB\u0EAD-\u0EAE\u0EB0\u0EB2-\u0EB3\u0EBD\u0EC0-\u0EC4\u0F40-\u0F47\u0F49-\u0F69\u10A0-\u10C5\u10D0-\u10F6\u1100\u1102-\u1103\u1105-\u1107\u1109\u110B-\u110C\u110E-\u1112\u113C\u113E\u1140\u114C\u114E\u1150\u1154-\u1155\u1159\u115F-\u1161\u1163\u1165\u1167\u1169\u116D-\u116E\u1172-\u1173\u1175\u119E\u11A8\u11AB\u11AE-\u11AF\u11B7-\u11B8\u11BA\u11BC-\u11C2\u11EB\u11F0\u11F9\u1E00-\u1E9B\u1EA0-\u1EF9\u1F00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u2126\u212A-\u212B\u212E\u2180-\u2182\u3041-\u3094\u30A1-\u30FA\u3105-\u312C\uAC00-\uD7A3\u4E00-\u9FA5\u3007\u3021-\u3029"; //$NON-NLS-1$
   public static final String DIGIT_PATTERN = "\u0030-\u0039\u0660-\u0669\u06F0-\u06F9\u0966-\u096F\u09E6-\u09EF\u0A66-\u0A6F\u0AE6-\u0AEF\u0B66-\u0B6F\u0BE7-\u0BEF\u0C66-\u0C6F\u0CE6-\u0CEF\u0D66-\u0D6F\u0E50-\u0E59\u0ED0-\u0ED9\u0F20-\u0F29"; //$NON-NLS-1$
   public static final String COMBINING_CHAR_PATTERN = "\u0300-\u0345\u0360-\u0361\u0483-\u0486\u0591-\u05A1\u05A3-\u05B9\u05BB-\u05BD\u05BF\u05C1-\u05C2\u05C4\u064B-\u0652\u0670\u06D6-\u06DC\u06DD-\u06DF\u06E0-\u06E4\u06E7-\u06E8\u06EA-\u06ED\u0901-\u0903\u093C\u093E-\u094C\u094D\u0951-\u0954\u0962-\u0963\u0981-\u0983\u09BC\u09BE\u09BF\u09C0-\u09C4\u09C7-\u09C8\u09CB-\u09CD\u09D7\u09E2-\u09E3\u0A02\u0A3C\u0A3E\u0A3F\u0A40-\u0A42\u0A47-\u0A48\u0A4B-\u0A4D\u0A70-\u0A71\u0A81-\u0A83\u0ABC\u0ABE-\u0AC5\u0AC7-\u0AC9\u0ACB-\u0ACD\u0B01-\u0B03\u0B3C\u0B3E-\u0B43\u0B47-\u0B48\u0B4B-\u0B4D\u0B56-\u0B57\u0B82-\u0B83\u0BBE-\u0BC2\u0BC6-\u0BC8\u0BCA-\u0BCD\u0BD7\u0C01-\u0C03\u0C3E-\u0C44\u0C46-\u0C48\u0C4A-\u0C4D\u0C55-\u0C56\u0C82-\u0C83\u0CBE-\u0CC4\u0CC6-\u0CC8\u0CCA-\u0CCD\u0CD5-\u0CD6\u0D02-\u0D03\u0D3E-\u0D43\u0D46-\u0D48\u0D4A-\u0D4D\u0D57\u0E31\u0E34-\u0E3A\u0E47-\u0E4E\u0EB1\u0EB4-\u0EB9\u0EBB-\u0EBC\u0EC8-\u0ECD\u0F18-\u0F19\u0F35\u0F37\u0F39\u0F3E\u0F3F\u0F71-\u0F84\u0F86-\u0F8B\u0F90-\u0F95\u0F97\u0F99-\u0FAD\u0FB1-\u0FB7\u0FB9\u20D0-\u20DC\u20E1\u302A-\u302F\u3099\u309A"; //$NON-NLS-1$
   public static final String EXTENDER_PATTERN = "\u00B7\u02D0\u02D1\u0387\u0640\u0E46\u0EC6\u3005\u3031-\u3035\u309D-\u309E\u30FC-\u30FE"; //$NON-NLS-1$
   public static final String NCNAME_CHAR_PATTERN = "\u002d\u005f\u002e" //$NON-NLS-1$
         + LETTER_PATTERN + DIGIT_PATTERN + COMBINING_CHAR_PATTERN + EXTENDER_PATTERN;
   public static final String NCNAME_PATTERN = MessageFormat.format("[{0}_][{1}]*",  //$NON-NLS-1$
      new Object [] { LETTER_PATTERN, NCNAME_CHAR_PATTERN });

   /** A regexp pattern to use to match an NCName. */
   public static final Pattern sNCNamePattern = Pattern.compile(NCNAME_PATTERN);

   /** The doc builder map. */
   private static final Map<String, DocumentBuilder> sDocumentBuilderMap = new HashMap<String, DocumentBuilder>();
   /** A default AE prefix. */
   private static final String PREFIX = "aeaaanstmp";  //$NON-NLS-1$

   /**
    * Default constructor.
    */
   private AeXmlUtil()
   {
   }

   /**
    * Parses the given String into a DOM Document.  Parse validation is disabled.
    *
    * @param aXmlString the XML String.
    * @return Document.
    * @throws AeException
    */
   public static Document toDoc(String aXmlString) throws AeException
   {
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setValidating(false);
      return parser.loadDocument(new StringReader(aXmlString), null);
   }

   /**
    * Creates and returns a Document given a handle to the xml file.
    * @param aXmlFile File to the xml document.
    * @param aSchemas schema, if present will validate the document.
    * @return Document object.
    */
   public static Document toDoc(File aXmlFile, Iterator<Schema> aSchemas) throws AeException
   {
      try
      {
         return toDoc(new InputSource( new FileInputStream(aXmlFile)), aSchemas);
      }
      catch(FileNotFoundException fnfe)
      {
         throw new AeException(fnfe);
      }
   }

   /**
    * Creates and returns a Document given a handle to the xml file.
    * @param aInputSource Input source xml document.
    * @param aSchemas schema, if present will validate the document.
    * @return Document object.
    */
   public static Document toDoc(InputSource aInputSource, Iterator<Schema> aSchemas) throws AeException
   {
      Document rDoc;
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setNamespaceAware(true);
      parser.setValidating( aSchemas != null);
      rDoc = parser.loadDocument(aInputSource, aSchemas);
      return rDoc;
   }

   /**
    * Extracts the prefix from a QName value. This is the value that comes before
    * the colon in a qname. i.e. foo:x, this method returns 'foo'
    * @param aQNameValue can be null
    * @return String or null if there is no prefix or the param is null
    */
   public static String extractPrefix(String aQNameValue)
   {
      if (aQNameValue == null)
         return null;

      int colonOffset = aQNameValue.indexOf(':');

      if (colonOffset == -1)
         return null;

      return aQNameValue.substring(0, colonOffset).trim();
   }

   /**
    * Extracts the localname from a QName value. This is the value that comes
    * after the colon in a qname. i.e. foo:x, this method returns 'x'
    * @param aQNameValue can be null
    * @return String or null the input is null or aQNameValue if there is no colon.
    */
   public static String extractLocalPart(String aQNameValue)
   {
      if (aQNameValue == null)
         return null;

      int colonOffset = aQNameValue.indexOf(':');
      if (colonOffset == -1)
         return aQNameValue.trim();

      if (colonOffset == aQNameValue.length()-1)
         return null;

      return aQNameValue.substring(colonOffset+1).trim();
   }

   /**
    * Returns true if there is a colon in the string.
    * @param aQNameValue
    * @return true if there is a colon in the string
    */
   public static boolean hasColon(String aQNameValue)
   {
      if (aQNameValue == null)
         return false;

      return aQNameValue.indexOf(':') != -1;
   }

   /**
    * Removes the children and all attributes for the given source node.
    * @param aSource the source of the remove operation.
    * @param aRemoveAttrs True if attributes are to be removed False otherwise
    */
   public static void removeNodeContents(Node aSource, boolean aRemoveAttrs)
   {
      if (aRemoveAttrs && aSource.hasAttributes())
      {
         // Remove any attributes the Element may have (Only Elements have attributes)
         NamedNodeMap attrMap = aSource.getAttributes();
         int skip = 0;
         for (int i=0, len=attrMap.getLength(); i< len; i++)
         {
            if(attrMap.item(skip).getNodeName().startsWith("xmlns:")) //$NON-NLS-1$
               skip++;
            else
               ((Element)aSource).removeAttribute(attrMap.item(skip).getNodeName());
         }
      }

      // Remove all of the children from the node
      for (Node child = aSource.getFirstChild(); child != null; child = aSource.getFirstChild())
         aSource.removeChild(child);
   }

   /**
    * Serialize the element to a string.
    * @param aElement
    */
   public static String serialize( Element aElement )
   {
      return AeXMLParserBase.documentToString( createCopyAsDocumentFragment( aElement ) );
   }

   /**
    * Copy the element contents to a <code>DocumentFragment</code>.
    * @param aElement
    */
   public static DocumentFragment createCopyAsDocumentFragment( Element aElement )
   {
      Document doc = getOwnerDocument( aElement );
      DocumentFragment frag = doc.createDocumentFragment();

      String tagName = aElement.getTagName();
      String prefix = extractPrefix( tagName );
      String ns = null;

      if( AeUtil.notNullOrEmpty(prefix) )
      {
         ns = getNamespaceForPrefix( aElement, prefix );
      }

      String tagNameWithoutPrefix = extractLocalPart( tagName );
      Element fragRoot = doc.createElementNS( ns, tagNameWithoutPrefix );
      frag.appendChild( fragRoot );
      copyNodeContents( aElement, fragRoot );

      if( AeUtil.notNullOrEmpty(ns) )
      {
         fragRoot.setPrefix( prefix );
      }

      return frag;
   }

   /**
    * Makes a copy of the Element in a new document
    * @param aElement
    */
   public static Element cloneElement(Element aElement)
   {
      Document doc = newDocument();
      Element copy = cloneElement(aElement, doc);
      doc.appendChild(copy);
      return copy;
   }

   /**
    * Makes a copy of the Element in the context of the given Document.
    *
    * @param aElement
    * @param aDocument
    */
   public static Element cloneElement(Element aElement, Document aDocument)
   {
      Element copy = aDocument.createElementNS(aElement.getNamespaceURI(), aElement.getNodeName());
      AeXmlUtil.copyNodeContents(aElement, copy);
      return copy;
   }

   /**
    * Copies the contents beneath a source node and makes them children
    * of the target node. Any attributes of the source root will be moved
    * to the target node as well.
    *
    * The one unsafe situation in regard to namespace prefix remapping is
    * if we've remapped a prefix and the target element has attributes that
    * may contain embedded qname strings or other content that depends on
    * a specific prefix mapping
    *
    * @param aSource the source of the copy operation
    * @param aTarget the target of the copy operation
    */
   public static void copyNodeContents(Node aSource, Node aTarget)
   {
      int prefixCounter = 0;
      // flag that gets set if we need to fully qualify the source nodes when
      // they're copied over to the target. This gets set to true if the source
      // and target have different default namespaces
      boolean addXmlnsDeclToChildNodes = false;
      // adjust namespaces for nodes
      if(aSource.getNodeType() == Node.ELEMENT_NODE &&
         aTarget.getNodeType() == Node.ELEMENT_NODE)
      {
         HashMap<String, String> newNamespaceAttrs = new HashMap<String, String>();
         HashMap<String, String> existingNamespaceAttrs = new HashMap<String, String>();
         HashMap<String, String> changedNamespaceAttrs = new HashMap<String, String>();
         getDeclaredNamespaces((Element)aSource, newNamespaceAttrs);
         getDeclaredNamespaces((Element)aTarget, existingNamespaceAttrs);

         // add a default namespace to each map if not already declared
         addDefaultNamespace(newNamespaceAttrs);
         addDefaultNamespace(existingNamespaceAttrs);

         Element tgtElement = (Element)aTarget;
         String tgtElementPrefix = extractPrefix(tgtElement.getNodeName());
         for(Iterator iter = newNamespaceAttrs.entrySet().iterator(); iter.hasNext(); )
         {
            Map.Entry es = (Map.Entry)iter.next();
            String prefix = (String)es.getKey();
            String namespace = (String)es.getValue();
            String oldNamespace = (String) existingNamespaceAttrs.get(prefix);

            // add a namespace declaration
            if(!namespace.equals(oldNamespace))
            {
               // need to remap the old namespace declaration only if there's a conflict
               if (existingNamespaceAttrs.containsKey(prefix))
               {
                  // add a changed prefix to the map so we can update any existing children
                  changedNamespaceAttrs.put(prefix, oldNamespace);

                  // if the target elements namespace/prefix conflicts reassign elements prefix to temporary one
                  if(prefix.equals(tgtElementPrefix))
                  {
                     int counter = getUniquePrefix(prefixCounter, newNamespaceAttrs);
                     tgtElementPrefix = PREFIX + counter;
                     prefixCounter++;
                     tgtElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + tgtElementPrefix, tgtElement.getNamespaceURI()); //$NON-NLS-1$
                     tgtElement.setPrefix(tgtElementPrefix);
                  }
               }
               // add a new namespace decl
               if("".equals(prefix)) //$NON-NLS-1$
               {
                  // only set the default namespace on the target element if
                  // it matches the element's current namespace. You can't change
                  // the namespace of an element in DOM.
                  if (namespace.equals(tgtElement.getNamespaceURI()))
                  {
                     prefix = "xmlns"; //$NON-NLS-1$
                     tgtElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, namespace);
                  }
                  else
                  {
                     addXmlnsDeclToChildNodes = true;
                  }
               }
               else
               {
                  prefix = "xmlns:" + prefix;  //$NON-NLS-1$
                  tgtElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, namespace);
               }
            }
         }

         // add a local declaration to any existing children if prefixes were changed
         if (!AeUtil.isNullOrEmpty(changedNamespaceAttrs) && tgtElement.hasChildNodes())
         {
            NodeList nodes = tgtElement.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++)
            {
               if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
               {
                  Element child = (Element) nodes.item(i);

                  for (Iterator it = changedNamespaceAttrs.entrySet().iterator(); it.hasNext();)
                  {
                     Map.Entry entry = (Map.Entry) it.next();
                     String prefix = (String) entry.getKey();
                     String namespace = (String) entry.getValue();

                     if("".equals(prefix)) //$NON-NLS-1$
                        prefix = "xmlns"; //$NON-NLS-1$
                     else
                        prefix = "xmlns:" + prefix; //$NON-NLS-1$

                     // add a new namespace decl if one hasn't been locally defined
                     if (AeUtil.isNullOrEmpty(child.getAttribute(prefix)))
                     {
                        child.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, namespace);
                     }
                  }
               }
            }
         }
      }

      // Import the node and append all of its children to the target
      Node srcNode = aTarget.getOwnerDocument().importNode(aSource, true);
      while (srcNode.getFirstChild() != null)
      {
         Node child = srcNode.getFirstChild();
         // Note: appending the child to the target node will effectively remove
         //       if from the src node. Don't explicitly remove the child from
         //       the srcNode since not all DOM impls behave well when you do
         //       this. (Axis is one example).
         aTarget.appendChild(child);
         // if the child is an element and we need to fully qualify it then check
         // to see if it already has a default ns declaration and if not, add one.
         if (child instanceof Element && addXmlnsDeclToChildNodes)
         {
            Element e = (Element) child;

            if (!e.hasAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns")) //$NON-NLS-1$
            {
               e.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", aSource.getNamespaceURI()); //$NON-NLS-1$
            }
         }
      }

      // Copy attributes if we have any
      if (srcNode.hasAttributes() && aTarget instanceof Element)
      {
         NamedNodeMap attrMap = srcNode.getAttributes();
         for (int i=0, len=attrMap.getLength(); i < len; i++)
         {
            Attr attr = (Attr)attrMap.item(i);
            if(! XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI()))
            {
               if (AeUtil.isNullOrEmpty(attr.getNamespaceURI()))
               {
                  ((Element)aTarget).setAttributeNS(null, attr.getNodeName(), attr.getNodeValue());
               }
               else
               {
                  ((Element)aTarget).setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue());
               }
            }
         }
      }
   }

   /**
    * Creates a org.w3c.dom.element
    * @param aName qualified name of the element
    * @param aNameSpace namespace of the element
    */
   public static Element createElement(String aNameSpace, String aPrefix, String aName)
   {
      Element elem = AeXmlUtil.newDocument().createElementNS(aNameSpace, aPrefix+":"+aName); //$NON-NLS-1$
      elem.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:"+aPrefix, aNameSpace); //$NON-NLS-1$
      return elem;
   }

   /**
    * Adds the default namespace to the map if it's not already declared.
    * @param aNsMap
    */
   private static void addDefaultNamespace(Map<String, String> aNsMap)
   {
      if (!aNsMap.containsKey("")) //$NON-NLS-1$
      {
         aNsMap.put("", ""); //$NON-NLS-1$ //$NON-NLS-2$
      }
   }

   /**
    * Create a new document for the given part name with no namespace (per WS-I BP 1).
    *
    * @param aPartName
    * @param aPartData
    */
   public static Document createMessagePartTypeDocument(String aPartName, Element aPartData)
   {
      Document result = newDocument();
      Element root = createMessagePartTypeElement(result, aPartName, aPartData);
      result.appendChild(root);
      return result;
   }

   /**
    * Create a new element for the given part name with no namespace (per WS-I BP 1).
    *
    * @param aDocument
    * @param aPartName
    * @param aPartData
    */
   public static Element createMessagePartTypeElement(Document aDocument, String aPartName, Element aPartData)
   {
      // Create element from part name without namespace URI.
      Element result = aDocument.createElementNS(null, aPartName);

      // Copy node contents.
      copyNodeContents(aPartData, result);

      // Remove default namespace declaration.
      String xmlns = result.getAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns"); //$NON-NLS-1$
      if (!AeUtil.isNullOrEmpty(xmlns))
      {
         for (Node node = result.getFirstChild(); node != null; node = node.getNextSibling())
         {
            if (node instanceof Element)
            {
               Element child = (Element) node;

               // Copy default namespace declaration to this child unless child
               // already has its own default namespace declaration.
               if (AeUtil.isNullOrEmpty(child.getAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns"))) //$NON-NLS-1$
               {
                  child.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", xmlns); //$NON-NLS-1$
               }
            }
         }

         result.removeAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns"); //$NON-NLS-1$
      }

      return result;
   }

   /**
    * Returns a prefix that is not currently in use in the node being copied.
    * @param aStartingValue
    * @param aPrefixToNamespaceMap
    */
   private static int getUniquePrefix(int aStartingValue, Map<String, String> aPrefixToNamespaceMap)
   {
      int counter = aStartingValue;

      while(aPrefixToNamespaceMap.containsKey(PREFIX + counter))
      {
         counter++;
      }

      return counter;
   }

   /**
    * Recursively get all the declared namespaces for the passed element
    * and return in the passed hashmap.
    *
    * @param aElement Element to scan for namespace declarations in.
    * @param aNamespaceAttrs HashMap to return declarations in.
    */
   public static void getDeclaredNamespaces(Element aElement, Map<String, String> aNamespaceAttrs)
   {
      // Loop through and add all attributes which are part of the xmlns to the map
      NamedNodeMap attrNodes = aElement.getAttributes();
      for (int i = 0, length = attrNodes.getLength(); i < length; i++)
      {
         Attr attr = (Attr) attrNodes.item(i);
         if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI()))
         {
            String prefix = attr.getLocalName();
            String namespaceURI = attr.getNodeValue();
            if ("xmlns".equals(prefix)) //$NON-NLS-1$
               prefix = ""; //$NON-NLS-1$
            if(aNamespaceAttrs.get(prefix) == null)
               aNamespaceAttrs.put(prefix, namespaceURI);
         }
      }
      if(aElement.getParentNode() != null)
      {
         if(aElement.getParentNode().getNodeType() == Node.ELEMENT_NODE)
            getDeclaredNamespaces((Element)aElement.getParentNode(), aNamespaceAttrs);
      }
   }

   /**
    * Walks the element's ancestors looking for the namespace declaration given
    * a prefix name.
    *
    * @param aElement the element context to find the namespace in
    * @param aPrefix the prefix we want the namespace for
    */
   public static String getNamespaceForPrefix(Element aElement, String aPrefix)
   {
      if (aElement == null)
         return null;

      String namespace = aElement.getAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, aPrefix);
      // namespace shouldn't be null but a MessageElement in axis-SOAP is returning
      // a null when the attribute isn't declared so I'm handling that here.
      if (AeUtil.isNullOrEmpty(namespace))
      {
         Node parentNode = aElement.getParentNode();
         if ( !(parentNode instanceof Element) )
            return null;

         return getNamespaceForPrefix((Element)parentNode, aPrefix);
      }

      return namespace;
   }

   /**
    * Walks the element's ancestors looking for the prefix in use for the given
    * namespace declaration.
    * @param aElement the element context to find the namespace in
    * @param aNamespace the namespace we ant the prefix for
    * @param aAllowDefault true if we allow default namespaces to be returned
    * @return String the prefix, which is the prefix or an empty string if default namespace, or null if not found
    */
   public static String getPrefixForNamespace(Element aElement, String aNamespace, boolean aAllowDefault)
   {
      String prefix = null;
      if (aElement.hasAttributes())
      {
         NamedNodeMap attrNodes = aElement.getAttributes();
         for (int i=0, len=attrNodes.getLength(); i < len && prefix == null; i++)
         {
            Node attr = attrNodes.item(i);
            if (attr.getNodeName().startsWith("xmlns:")) //$NON-NLS-1$
            {
               if (attr.getNodeValue().equals(aNamespace))
                  prefix = extractLocalPart(attr.getNodeName());
            }
            else if(aAllowDefault && attr.getNodeName().equals("xmlns")) //$NON-NLS-1$
            {
               if (attr.getNodeValue().equals(aNamespace))
                  prefix = ""; //$NON-NLS-1$
            }
         }
      }

      if (prefix == null)
      {
         Node parentNode = aElement.getParentNode();
         if (parentNode instanceof Element)
            prefix = getPrefixForNamespace((Element)parentNode, aNamespace, aAllowDefault);
      }

      return prefix;
   }

   /**
    * Walks the element's ancestors looking for the prefix in use for the given
    * namespace declaration.  Note this calls assumes that you will allow default
    * namespaces to be returned (empty prefix - "").
    * @param aElement the element context to find the namespace in
    * @param aNamespace the namespace we ant the prefix for
    * @return String the prefix, which is the prefix or an empty string if default namespace, or null if not found
    */
   public static String getPrefixForNamespace(Element aElement, String aNamespace)
   {
      return getPrefixForNamespace(aElement, aNamespace, true);
   }

   /**
    * Declare all of the visible namespace prefixes locally on this element.
    * @param aElement
    */
   public static void declareNamespacePrefixesLocally(Element aElement)
   {
      Map<String, String> namespaceMap = new HashMap<String, String>();
      AeXmlUtil.getDeclaredNamespaces(aElement, namespaceMap);
      declareNamespacePrefixes(aElement, namespaceMap);
   }

   /**
    * Declare all of the namespaces from the map on the element.
    * @param aElement
    * @param aNamespaceMap
    */
   public static void declareNamespacePrefixes(Element aElement, Map aNamespaceMap)
   {
      // declare each of the namespaces locally in the element
      for (Iterator iter = aNamespaceMap.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         if (AeUtil.notNullOrEmpty((String) entry.getKey()))
         {
            aElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + entry.getKey(), (String)entry.getValue()); //$NON-NLS-1$
         }
         else
         {
            aElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", (String)entry.getValue()); //$NON-NLS-1$
         }
      }
   }

   /**
    * Returns a QName object given the element context and namespace qualified string
    * to be transformed into a QName object.
    * @param aElement the element context
    * @param aQNameStr the Qname string to be transformed
    */
   public static QName createQName(Element aElement, String aQNameStr)
   {
      String prefix    = extractPrefix(aQNameStr);
      String namespace = getNamespaceForPrefix(aElement, prefix);

      return new QName(namespace, extractLocalPart(aQNameStr));
   }

   /**
    * Gets an encoded QName from the Element.
    *
    * @param aElement
    */
   public static QName getQName(Element aElement)
   {
      QName qname = null;

      String qnameStr = getText(aElement);
      if (qnameStr != null)
      {
         qname = createQName(aElement, qnameStr.trim());
      }

      return qname;
   }

   /**
    * Utility method to get a single sub element with the given tag name. Null will
    * be returned if the desired sub element does not exist
    * @param aParent the parent node to search from
    * @param aName the desired element name or * to match on the first one
    */
   public static Element findSubElement(Node aParent, String aName)
   {
      Element element = null;
      for (Node node=aParent.getFirstChild(); node != null && element == null; node=node.getNextSibling())
      {
         if (node.getNodeType() == Node.ELEMENT_NODE && (aName.equals("*") || aName.equals(getLocalName((Element) node)))) //$NON-NLS-1$
            element = (Element)node;
      }

      return element;
   }

   /**
    * Utility method to get a single sub element with the given qname. Null will
    * be returned if the desired sub element does not exist
    * @param aParent the parent node to search from
    * @param aName the desired element name
    */
   public static Element findSubElement(Node aParent, QName aName)
   {
      Element element = null;
      for (Node node=aParent.getFirstChild(); node != null && element == null; node=node.getNextSibling())
      {
         if (node.getNodeType() == Node.ELEMENT_NODE && AeUtil.compareObjects(aName.getLocalPart(), node.getLocalName())
                                                     && AeUtil.compareObjects(aName.getNamespaceURI(), node.getNamespaceURI()) )
            element = (Element)node;
      }

      return element;
   }

   /**
    * Gets the local name of the element or returns the node name if the local
    * name was null.
    * @param aElement
    */
   public static String getLocalName(Element aElement)
   {
      if (aElement.getLocalName() != null)
         return aElement.getLocalName();
      return AeXmlUtil.extractLocalPart(aElement.getNodeName());
   }

   /**
    * Gets a new Document instance, rethrowing any exception during create as an
    * InternalError since that would signal something horribly wrong with the
    * class path or other configuration issues.
    */
   public static Document newDocument()
   {
      try
      {
         return getDocumentBuilder().newDocument();
      }
      catch (AeException e)
      {
         throw new InternalError(AeMessages.getString("AeXmlUtil.ERROR_14")); //$NON-NLS-1$
      }
   }

   /**
    * Returns a document builder which is closest in the classpath, in our case
    * this is from Xerces. This is a workaround because eclipse serves up Crimson
    * as the default parser which is not as robust.
    */
   public static DocumentBuilder getDocumentBuilder() throws AeException
   {
      return getDocumentBuilder(true, false);
   }

   /**
    * Returns a document builder which is closest in the classpath, in our case
    * this is from Xerces. This is a workaround because eclipse serves up Crimson
    * as the default parser which is not as robust.
    * @param aNamespaceAwareFlag
    * @param aValidationFlag
    */
   public static DocumentBuilder getDocumentBuilder(boolean aNamespaceAwareFlag, boolean aValidationFlag) throws AeException
   {
      synchronized (sDocumentBuilderMap)
      {
         // Generate key from method arguments.
         String key = "" + aNamespaceAwareFlag + "; " + aValidationFlag; //$NON-NLS-1$ //$NON-NLS-2$

         // Check map of DocumentBuilder objects for matching request.
         DocumentBuilder builder = (DocumentBuilder) sDocumentBuilderMap.get(key);
         if (builder == null)
         {
            ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
               // Set class loader to that which loaded us, to ensure we load the xerces parser
               Thread.currentThread().setContextClassLoader(AeXmlUtil.class.getClassLoader());

               // Create factory for parsing and turn on validation and namespaces
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

               factory.setNamespaceAware(aNamespaceAwareFlag);
               factory.setValidating(aValidationFlag);

               // Create the document builder and parse the filename
               builder = factory.newDocumentBuilder();
            }
            catch(Exception e)
            {
               throw new AeException(AeMessages.getString("AeXmlUtil.ERROR_17"), e); //$NON-NLS-1$
            }
            finally
            {
               Thread.currentThread().setContextClassLoader(previousClassLoader);
            }

            // Save this new DocumentBuilder for next matching request.
            sDocumentBuilderMap.put(key, builder);
         }

         return builder;
      }
   }

   /**
    * Utility method to find the first child Element node for a given parent node.
    * Null will be returned if there are no child Element nodes.
    * @param aParent the parent node to search from
    */
   public static Element getFirstSubElement(Node aParent)
   {
      Element element = null;
      for (Node node=aParent.getFirstChild(); node != null && element == null; node=node.getNextSibling())
      {
         if (node.getNodeType() == Node.ELEMENT_NODE)
            element = (Element)node;
      }

      return element;
   }

   /**
    * Returns a concatenation of all of the text nodes and CDATA nodes that are
    * immediate children of this element.
    * @param aElement
    */
   public static String getText(Element aElement)
   {
      // short return if no element has been passed
      if(aElement == null)
         return null;

      StringBuffer buffer = new StringBuffer();

      NodeList nl = aElement.getChildNodes();
      for (int i=0; nl.item(i) != null; i++)
      {
         Node child = nl.item(i);
         switch(child.getNodeType())
         {
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
               buffer.append(child.getNodeValue());
         }
      }

      return buffer.toString();
   }

   /**
    * Gets the owner document for this node. If the node is a document then it returns
    * the node.
    * @param aNode
    */
   public static Document getOwnerDocument(Node aNode)
   {
      if (aNode instanceof Document)
         return (Document) aNode;
      else
         return aNode.getOwnerDocument();
   }

   /**
    * Helper method for creating an element and adding to a parent.
    * @param aParent The parent of the node, either a Document, Element, or DocumentFragment
    * @param aNamespace The namespace uri of the element
    * @param aLocalName The local name of the element
    * @param aData Text to get added if not null.
    */
   public static Element addElementNS(Node aParent, String aNamespace, String aLocalName, String aData)
   {
      return addNillableElementNS(aParent, aNamespace, aLocalName, aData, false);
   }
   
   /**
    * Helper method for creating an <strong>nillable</strong> element and adding to a parent.
    * @param aParent The parent of the node, either a Document, Element, or DocumentFragment
    * @param aNamespace The namespace uri of the element
    * @param aLocalName The local name of the element
    * @param aData Text to get added if not null.
    * @param aNillable if true and aData is null, xsi:nill is set to true.
    */
   public static Element addNillableElementNS(Node aParent, String aNamespace, String aLocalName, String aData, boolean aNillable)
   {
      Document doc = getOwnerDocument(aParent);
      Element e = doc.createElementNS(aNamespace, aLocalName);
      aParent.appendChild(e);
      if (aNillable && aData == null)
      {
         setNil(e);
      }
      else if (!AeUtil.isNullOrEmpty(aData))
      {
         e.appendChild(doc.createTextNode(aData));
      }
      return e;
   }   

   /**
    * Helper method for creating an element.
    *
    * @param aParent
    * @param aNamespace
    * @param aLocalName
    */
   public static Element addElementNS(Node aParent, String aNamespace, String aLocalName)
   {
      return addElementNS(aParent, aNamespace, aLocalName, null);
   }

   /**
    * Given an XML element, returns the element's schema type.  It does this by first
    * checking for an attribute of "xsi:type".  If this is found then that value is
    * used.  If no such attribute is found, then returns null.
    *
    * @param aElem
    */
   public static QName getXSIType(Element aElem)
   {
      String complexType = aElem.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"); //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(complexType))
      {
         String sampleTypePrefix = extractPrefix(complexType);
         String sampleTypeNS = getNamespaceForPrefix(aElem, sampleTypePrefix);
         String sampleTypeLP = extractLocalPart(complexType);
         return new QName(sampleTypeNS, sampleTypeLP);
      }
      return null;
   }

   /**
    * Given an XML element, returns the element type.  This method is typically used when
    * determining the type of a message or sample data.
    *
    * @param aElem
    */
   public static QName getElementType(Element aElem)
   {
      return new QName(AeUtil.getSafeString(aElem.getNamespaceURI()), getLocalName(aElem));
   }

   /**
    * Insert the xsi:type attribute into the document.
    *
    * @param aDoc
    * @param aTypeName
    */
   public static void declarePartType(Document aDoc, QName aTypeName)
   {
      Element docElement = aDoc.getDocumentElement();
      declareXsiType(docElement, aTypeName);
   }

   /**
    * Insert the xsi:type attribute into the element.
    *
    * @param aElement
    * @param aTypeName
    */
   public static void declareXsiType(Element aElement, QName aTypeName)
   {
      String xsiPrefix = "xsi"; //$NON-NLS-1$

      // Set the schema instance namespace if not already set
      Attr currentXsiNS = aElement.getAttributeNodeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xsi"); //$NON-NLS-1$
      if (currentXsiNS == null)
      {
         aElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI); //$NON-NLS-1$
      }
      else if (!XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(currentXsiNS.getValue()))
      {
         // If they are using the xsi prefix for something else...
         aElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:aeXSI", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI); //$NON-NLS-1$
         xsiPrefix = "aeXSI"; //$NON-NLS-1$
      }

      // Remove the xsi:type attribute if it exists
      aElement.removeAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"); //$NON-NLS-1$

      // If a prefix is declared for this namespace we should use it, otherwise declare our own
      String prefix = AeXmlUtil.getPrefixForNamespace(aElement, aTypeName.getNamespaceURI());
      if (AeUtil.isNullOrEmpty(prefix))
      {
         prefix = "aensTYPE"; //$NON-NLS-1$
         aElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix, aTypeName.getNamespaceURI()); //$NON-NLS-1$
      }

      // Set the xsi:type attribute
      String typeSpec = prefix + ":" + aTypeName.getLocalPart(); //$NON-NLS-1$
      aElement.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, xsiPrefix + ":type", typeSpec); //$NON-NLS-1$
   }

   /**
    * Encodes a QName object as a string (of the form prefix:localPart) and
    * returns the encoded String value.  Creates a prefix if one does not
    * already exist for the namespace.
    *
    * @param aQName
    * @param aElementContext
    */
   public static String encodeQName(QName aQName, Element aElementContext, String aPreferredPrefix)
   {
      AeElementBasedNamespaceContext nsContext = new AeElementBasedNamespaceContext(aElementContext);
      return encodeQName(aQName, nsContext, aPreferredPrefix);
   }

   /**
    * Encodes a QName object as a string (of the form prefix:localPart) and
    * returns the encoded String value.  Creates a prefix if one does not
    * already exist for the namespace.
    *
    * @param aQName
    * @param aNamespaceContext
    * @param aPreferredPrefix
    */
   public static String encodeQName(QName aQName, IAeMutableNamespaceContext aNamespaceContext,
         String aPreferredPrefix)
   {
      if (AeUtil.isNullOrEmpty(aQName.getNamespaceURI()))
      {
         return aQName.getLocalPart();
      }
      else
      {
         if (aPreferredPrefix == null)
            aPreferredPrefix = "ns"; //$NON-NLS-1$
         
         String prefix = null;
         
         if (XMLConstants.XML_NS_URI.equals( aQName.getNamespaceURI() ))
         {
            prefix = "xml"; //$NON-NLS-1$
         }
         else
         {
            prefix = aNamespaceContext.getOrCreatePrefixForNamespace(aPreferredPrefix, aQName.getNamespaceURI());
         }
         String localPart = aQName.getLocalPart();
         return prefix + ":" + localPart; //$NON-NLS-1$
      }
   }

   /**
    * Utility method to determine if the give String is a valid XML NCName. For a name to be a valid
    * NCName it must begin with a letter or underscore character, followed by zero or more letters,
    * digits, ".", "-" or "_" characters. This method returns true if the argument name is a valid
    * NCName, otherwise it returns false.
    *
    * @param aName the name to be checked.
    * @return returns true if name is a valid NCName, otherwise returns false.
    */
   public static boolean isValidNCName(String aName)
   {
      return sNCNamePattern.matcher(aName).matches();
   }

   /**
    * Gets the prefix to use for the given namespace. If none is found, then a unique prefix is added to the
    * root element.
    *
    * @param aParentElement
    * @param aNamespace
    */
   public static String getOrCreatePrefix(Element aParentElement, String aNamespace)
   {
      return getOrCreatePrefix(aParentElement, aNamespace, "ext", true); //$NON-NLS-1$
   }

   /**
    * Gets the prefix to use for the given namespace. If none is found, then a unique prefix is added to the
    * root element.
    *
    * @param aParentElement
    * @param aNamespace
    * @param aPreferredPrefix
    * @param aDeclareRootFlag
    */
   public static String getOrCreatePrefix(Element aParentElement, String aNamespace, String aPreferredPrefix, boolean aDeclareRootFlag)
   {
      return getOrCreatePrefix(aParentElement, aNamespace, aPreferredPrefix, aDeclareRootFlag, true);
   }

   /**
    * Gets the prefix to use for the given namespace. If none is found, then a
    * unique prefix is added to the element.
    * @param aParentElement - element to begin search for prefix.
    * @param aNamespace - value of the ns that you're searching for
    * @param aPreferredPrefix - preferred value to use for the prefix
    * @param aDeclareRootFlag - if true a newly created prefix is defined on the root element
    * @param aAllowDefaultNS - if true then we'll consider a default ns
    * @return existing prefix, or newly created one, or empty string if default ns is in scope
    */
   public static String getOrCreatePrefix(Element aParentElement, String aNamespace, String aPreferredPrefix, boolean aDeclareRootFlag, boolean aAllowDefaultNS)
   {
      String prefix = AeXmlUtil.getPrefixForNamespace(aParentElement, aNamespace, aAllowDefaultNS);
      if (prefix == null)
      {
         // we need to add a prefix
         prefix = aPreferredPrefix;

         // find all of the prefixes in scope so we don't step on them
         Map<String, String> existingPrefixesInScope = new HashMap<String, String>();
         AeXmlUtil.getDeclaredNamespaces(aParentElement, existingPrefixesInScope);
         int counter = 1;
         while (existingPrefixesInScope.containsKey(prefix))
         {
            prefix = aPreferredPrefix + counter++;
         }

         // we now have a unique prefix for the element add it on the root element for others to use
         Element root = aParentElement;
         if (aDeclareRootFlag)
         {
            while(root.getParentNode() != null && root.getParentNode() instanceof Element)
            {
               root = (Element) root.getParentNode();
            }
         }
         root.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix, aNamespace); //$NON-NLS-1$
      }
      return prefix;
   }

   /**
    * Returns true if the passed type is non-null and not an anyType or complexType
    */
   public static boolean isComplexOrAny(XMLType aType)
   {
      return aType != null && (aType.isComplexType() || aType.isAnyType());
   }


   /**
    * Returns true if the element is null or if it has xsi:nil attribute value of "true".
    * @param aElement element to test.
    * @return if element is null or nil.
    */
   public static boolean isNil(Element aElement)
   {
      if (aElement != null)
      {
         return "true".equals( aElement.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil") );  //$NON-NLS-1$//$NON-NLS-2$
      }
      else
      {
         return true;
      }
   }

   /**
    * Sets the element xsi:nil to true.
    * @param aElement
    */
   public static void setNil(Element aElement)
   {
      if (aElement == null || isNil(aElement))
      {
         return;
      }
      String prefix = getOrCreatePrefix(aElement, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi", false); //$NON-NLS-1$
      aElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI); //$NON-NLS-1$
      aElement.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, prefix + ":nil", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Returns the value of the specified attribute from the given Element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the attribute.
    * @return String
    */
   public static String getAttribute(Element aElement, String aAttributeName)
   {
      return aElement.getAttribute(aAttributeName);
   }

   /**
    * Retrieves the given attribute from the given Element.  If the attribute had an empty String
    * value then the default argument is returned.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttr The name of the attribute.
    * @param aDefault The default value if the attribute value is empty.
    */
   public static String getAttribute(Element aElement, String aAttr, String aDefault)
   {
      String str = getAttribute(aElement, aAttr);
      return str.equals("") ? aDefault : str; //$NON-NLS-1$
   }

   /**
    * Returns the <code>boolean</code> value of the specified attribute from the given Element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the attribute.
    * @return boolean if the attribute has the value of "true" then true, else false
    */
   public static boolean getAttributeBoolean(Element aElement, String aAttributeName)
   {
      return "true".equals(getAttribute(aElement, aAttributeName)); //$NON-NLS-1$
   }

   /**
    * Returns the <code>boolean</code> value of the specified attribute from the given Element.
    * If the attribute is empty then the default value is returned.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttr The name of the attribute.
    * @param aDefault The default value if the attribute value is empty.
    * @return boolean
    */
   public static boolean getAttributeBoolean(Element aElement, String aAttr, boolean aDefault)
   {
      String boolStr = aElement.getAttribute(aAttr);
      return boolStr.equals("") ? aDefault : getAttributeBoolean(aElement, aAttr); //$NON-NLS-1$
   }

   /**
    * Returns the <code>int</code> value of the specified attribute of the given Element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the attribute.
    * @return int
    */
   public static int getAttributeInt(Element aElement, String aAttributeName)
   {
      return Integer.parseInt(getAttribute(aElement, aAttributeName));
   }

   /**
    * Returns the <code>int</code> value of the specified attribute of the given Element.
    * If the attribute has an empty string value then the default argument is returned.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttr The name of the attribute.
    * @param aDefault this value is return if the attribute's value is an empty String.
    * @return int
    */
   public static int getAttributeInt(Element aElement, String aAttr, int aDefault)
   {
      String intStr = aElement.getAttribute(aAttr);
      return intStr.equals("") ? aDefault : getAttributeInt(aElement, aAttr); //$NON-NLS-1$
   }

   /**
    * Facilitates setting a QName attribute within the element node which was passed in.
    *
    * @param aElement The element to create the attribute within
    * @param aAttributeName the attribute name to be created
    * @param aQName The qualified name to be written if null, then no attribute is created.
    */
   public static void setAttributeQName(Element aElement, String aAttributeName, QName aQName)
   {
      if(aQName != null && AeUtil.notNullOrEmpty(aQName.getLocalPart()))
      {
         String encoded = encodeQName(aQName, aElement, null);
         aElement.setAttribute(aAttributeName, encoded);
      }
   }

   /**
    * Sets the given attribute value.
    *
    * @param aElement
    * @param aAttributeName
    * @param aValue
    */
   public static void setAttribute(Element aElement, String aAttributeName, String aValue)
   {
      if (aValue != null)
      {
         aElement.setAttribute(aAttributeName, aValue);
      }
   }

   /**
    * Appends a text node to the given element.
    *
    * @param aElement
    * @param aValue
    */
   public static void addText(Element aElement, String aValue)
   {
      if (AeUtil.notNullOrEmpty(aValue))
         aElement.appendChild(aElement.getOwnerDocument().createTextNode(aValue));
   }

   /**
    * Replaces a text nodes in element with given text.
    *
    * @param aElement
    * @param aValue
    */
   public static void replaceText(Element aElement, String aValue)
   {
      // remove any text nodes.
      for (Node child = aElement.getFirstChild(); child != null; child = child.getNextSibling())
      {
         // remove text nodes as well as cdata nodes (to mirror getText(Element) method)
         if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE)
         {
            aElement.removeChild(child);
         }
      }
      addText(aElement, aValue);
   }
   
   /**
    * Creates a new element of type xsd:dateTime given date object.
    * @param aParentEle container element
    * @param aNamespace element namespace
    * @param aPrefix element namespace prefix
    * @param aEleName element name with prefix.
    * @param aDate
    * @param aNillable true if the element is nillable.
    * @return element containing dateTime.
    */
   public static Element addElementNSDate(Element aParentEle, String aNamespace, String aPrefix, String aEleName, Date aDate, boolean aNillable)
   {
      String data = aDate != null ? new AeSchemaDateTime( aDate ).toString() : null;
      Element ele = addNillableElementNS(aParentEle, aNamespace, aPrefix + ":" + aEleName, data, aNillable); //$NON-NLS-1$
      ele.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + aPrefix, aNamespace); //$NON-NLS-1$
      return ele;
   }
   
   /**
    * Creates a new element of type xsd:QName given QName object.
    * @param aParentEle container element
    * @param aNamespace element namespace
    * @param aPrefix element namespace prefix
    * @param aEleName element name with prefix.
    * @param aQName QName data.
    * @param aNillable true if the element is nillable.
    * @return element containing QName.
    */
   public static Element addElementNSQName(Element aParentEle, String aNamespace, String aPrefix, String aEleName, QName aQName, boolean aNillable)
   {
      String data = null;
      if (aQName != null)
      {
         data = encodeQName(aQName, aParentEle, null);
      }
      Element ele = addNillableElementNS(aParentEle, aNamespace, aPrefix + ":" + aEleName, data, aNillable); //$NON-NLS-1$
      ele.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + aPrefix, aNamespace); //$NON-NLS-1$
      return ele;
   }   

   /**
    * Returns a QName for a specified attribute name in an element.
    *
    * @param aElement
    * @param aAttrName
    * @return a QName
    */
   public static QName getAttributeQName(Element aElement, String aAttrName)
   {
      QName qname = null;
      if (aElement.hasAttribute(aAttrName))
      {
         String name = aElement.getAttribute(aAttrName);
         String prefix = extractPrefix(name);
         String ns = getNamespaceForPrefix(aElement, prefix);
         qname = new QName(ns, extractLocalPart(name));
      }
      return qname;
   }

   /**
    * Returns the default namespace that is currently in-scope for the given Element.
    * This method will walk up the DOM hierarchy looking for xmlns declarations.
    *
    * @param aElement
    */
   public static String findDefaultNamespace(Element aElement)
   {
      String defaultNS = null;
      Element elem = aElement;
      do
      {
         defaultNS = elem.getAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns"); //$NON-NLS-1$
         Node node = elem.getParentNode();
         if (node instanceof Element)
            elem = (Element) node;
         else
            elem = null;
      }
      while (AeUtil.isNullOrEmpty(defaultNS) && elem != null);
      return defaultNS;
   }

   /**
    * Removes unnecessary namespace declarations from the given
    * Element.  This is accomplished by iterating over the xmlns:prefix
    * attributes in the Element and removing any declaration that
    * already exists somewhere in the parent hierarchy.  In other words,
    * walk up the DOM hierarchy looking for declarations of the same
    * prefix to the same namespace.
    *
    * @param aElement
    */
   public static void removeDuplicateNSDecls(Element aElement)
   {
      // Do nothing if the element has no parent or if the element's
      // parent is not an element (might be a Document).
      if (aElement.getParentNode() == null || aElement.getParentNode().getNodeType() != Node.ELEMENT_NODE)
         return;

      // List to hold the attributes we are going to remove - this is to
      // avoid changing the Node while we are iterating over it via the
      // NamedNodeMap object (some DOM impls simply proxy the source node).
      List<Attr> attributesToRemove = new ArrayList<Attr>();

      // Iterate through all of the xmlns: attributes and save the ones
      // that are not necessary (because some parent node already declares
      // the same prefix/ns pair).
      NamedNodeMap attrNodes = aElement.getAttributes();
      if (attrNodes != null && attrNodes.getLength() > 0)
      {
         Map<String, String> declaredNamespaces = new HashMap<String, String>();
         // Get all in-scope NS declarations from the parent.
         getDeclaredNamespaces((Element) aElement.getParentNode(), declaredNamespaces);

         for (int i = 0, length = attrNodes.getLength(); i < length; i++)
         {
            Attr attr = (Attr) attrNodes.item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI()))
            {
               String prefix = attr.getLocalName();
               if ("xmlns".equals(prefix)) //$NON-NLS-1$
                  prefix = ""; //$NON-NLS-1$
               String namespaceURI = attr.getNodeValue();
               // If the parent has the exact same prefix-NS declaration
               // in scope, then we can remove it from the element.
               if (declaredNamespaces.containsKey(prefix) &&
                     AeUtil.compareObjects(namespaceURI, declaredNamespaces.get(prefix)))
               {
                  attributesToRemove.add(attr);
               }
            }
         }
      }

      // Now actually remove the attributes from the Element.
      for (Attr attr : attributesToRemove)
      {
         aElement.removeAttributeNode(attr);
      }
   }

   /**
    * Touches the XML DOM nodes reachable from the given collection
    *
    * @param aCollection
    */
   public static void touchXmlNodes(Collection aCollection)
   {
      if (!AeUtil.isNullOrEmpty(aCollection))
      {
         for (Iterator i = aCollection.iterator(); i.hasNext(); )
         {
            Object node = i.next();
            if (node instanceof Node)
            {
               touchXmlNodes((Node) node);
            }
         }
      }
   }

   /**
    * Touches the given XML DOM <code>Node</code> and its children.
    *
    * @param aNode
    */
   public static void touchXmlNodes(Node aNode)
   {
      // Touch the node.
      aNode.getNodeName();
      aNode.getNodeValue();

      // Touch the node's children.
      for (Node child = aNode.getFirstChild(); child != null; child = child.getNextSibling())
      {
         touchXmlNodes(child);
      }

      // If the node is an element with attributes, then touch the attributes.
      NamedNodeMap attributes = aNode.getAttributes();
      if (attributes != null)
      {
         for (int i = attributes.getLength(); --i >= 0; )
         {
            touchXmlNodes(attributes.item(i));
         }
      }
   }

   /**
    * Transforms the xml source using the provided stylesheet
    * @param aXslSource
    * @param aXmlSource
    * @param aParams
    * @throws TransformerException
    */
   public static Node doTransform(Source aXslSource, Source aXmlSource, Map aParams) throws TransformerException //throws Exception
   {
      return doTransform(aXslSource, aXmlSource, aParams, null);
   }

   /**
    * Transforms the xml source using the provided stylesheet
    * @param aXslSource
    * @param aXmlSource
    * @param aParams
    * @param aResolver
    * @throws TransformerException
    */
   public static Node doTransform(Source aXslSource, Source aXmlSource, Map aParams, URIResolver aResolver) throws TransformerException //throws Exception
   {

      // Do the transform.
      TransformerFactory factory = TransformerFactory.newInstance();
//      String factoryName = factory.getClass().getName();
      // Check for the JDK 1.5 or Saxon transformer.
//      if (factoryName.startsWith("com.sun.org.apache.xalan")   //$NON-NLS-1$
//            || factoryName.startsWith("net.sf.saxon") || factoryName.startsWith("weblogic.xml.jaxp") ) //$NON-NLS-1$ //$NON-NLS-2$
//      {
//         // Instantiate the Xalan transformer directly, since the impl we use
//         // actually matters (the JDK1.5 transformer has some problems with Elements
//         // as stylesheet parameters; the Saxon transformer works, but emits
//         // spurious messages to stdout).
//
//         // 9/10/2007/PJ: Defect 3112. Also added check for classname starts with 'weblogic.xml.jaxp'.
//         // When using the aetasks.war app in weblogic, the weblogic uses an older
//         // version of the xsl transformer which its parser lexer causes NPE when it
//         // handles a comment. See http://issues.apache.org/jira/browse/XALANJ-2023.
//         // Work around is to use included xalan transformer factory.
//
//         factory = new TransformerFactoryImpl();
//      }
      return doTransform(factory, aXslSource, aXmlSource, aParams, aResolver);
   }


   /**
    * Transforms the xml source using the provided stylesheet using the transformers implementations
    * created by the given transformer factory.
    * @param aTransformerFactory
    * @param aXslSource
    * @param aXmlSource
    * @param aParams
    * @param aResolver
    * @throws TransformerException
    */
   public static Node doTransform(TransformerFactory aTransformerFactory, Source aXslSource, Source aXmlSource, Map aParams, URIResolver aResolver) throws TransformerException //throws Exception
   {
      DOMResult result = new DOMResult();
      // Do the transform.

      if(aResolver != null)
         aTransformerFactory.setURIResolver(aResolver);

      Transformer trans = aTransformerFactory.newTransformer(aXslSource);
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
      trans.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
      trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3"); //$NON-NLS-1$ //$NON-NLS-2$

      // Set up the global parameters based on the arguments to the function.
      if (aParams != null)
      {
         for (Iterator iter = aParams.keySet().iterator(); iter.hasNext(); )
         {
            String key = (String) iter.next();
            Object value = aParams.get(key);
            trans.setParameter(key, value);
         }
      }
      trans.transform(aXmlSource, result);

      return result.getNode();
   }

}
