//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeJsTreeBuilder.java,v 1.10 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//                 PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.AeException;

/**
 * This class generates the Java Script needed to display a Tree View control.
 * Specifically, the tree view used is the LGPLed NanoTree (http://nanotree.sourceforge.net/).
 */
public class AeJsTreeBuilder
{

   /** BPEL process model to use the build the tree.*/
   private AeBpelProcessObject mProcessObject = null;
   
   /** The unique id which will be assigned to each node in the Java Script tree view model. */
   private int mNextNodeId = 2; // start with 2, since 1 is reserved for the root .
   
   /** Set to hold image (icon) names. */
   private Set mImageNames = new HashSet();
   
   /** Base path to image resources. */
   private AeProcessViewImageResources mImagePaths = null;
   
   /**
    * Constructs a AeJsTreeBuilder.
    * @param aProcessObject BPEL process model
    * @param aImagePaths base path to the images.
    */
   public AeJsTreeBuilder(AeBpelProcessObject aProcessObject, AeProcessViewImageResources aImagePaths)   
   {
     mProcessObject = aProcessObject;
     mImagePaths =  aImagePaths;
   }
   
   /** 
    * @return Next (unique) id to be assigned to a JavaScript tree model node.
    */
   private int getNextId()
   {
      int id = mNextNodeId;
      mNextNodeId++;
      return id;      
   }
   
   /**
    * Builds the Java Script node model needed to display the tree control.
    * @return Java Script treeview
    */
   public String build() throws AeException 
   {     
      StringBuffer sb = new StringBuffer();
      synchronized(sb)
      {
         sb.append("//begin AEI tv \n"); //$NON-NLS-1$
         sb.append("\n"); //$NON-NLS-1$
         sb.append("iconPath = '"); //$NON-NLS-1$ 
         sb.append(mImagePaths.getImagePath());
         sb.append("';");//$NON-NLS-1$ 
         sb.append("\n"); //$NON-NLS-1$
         buildJSTree(mProcessObject, sb, 1);
         sb.append("\r\n"); //$NON-NLS-1$
         sb.append("//end AEI tv \n"); //$NON-NLS-1$
         return sb.toString();
      }      
   }
   
   private void buildJSTree(AeBpelObjectBase aBpelObj, StringBuffer sb, int depth) throws AeException
   {
      synchronized(sb)
      {
         int nodeId = getNextId();
         String tvParentNodeName = "aeNode" + (depth - 1); //$NON-NLS-1$
         String tvNodeName = "aeNode" + depth; //$NON-NLS-1$
         String tvName = ""; //$NON-NLS-1$
        
         String tvIcon = "img" + aBpelObj.getControllerType(); //$NON-NLS-1$
         
         // Use a Set to maintain the list of images we have already 'used'
         // to avoid duplicates.
         if (!mImageNames.contains(aBpelObj.getControllerType()))
         {
            mImageNames.add(aBpelObj.getControllerType());
            sb.append("\n"); //$NON-NLS-1$
            sb.append("var "); //$NON-NLS-1$ 
            sb.append(tvIcon);
            sb.append(" = '");//$NON-NLS-1$ 
            sb.append(mImagePaths.getBpelObjectImagePath(aBpelObj.getIconName()));
            sb.append("';");//$NON-NLS-1$ 
         }
         
         if (!"".equals(aBpelObj.getName())) //$NON-NLS-1$
         {
            tvName = " " + aBpelObj.getName(); //$NON-NLS-1$
         }
         else
         {
            tvName = " " + aBpelObj.getTagName(); //$NON-NLS-1$
         }
         
         sb.append("\n"); //$NON-NLS-1$
         indent(sb, depth);      
         
         if (depth == 1)
         {
            // root
            tvParentNodeName = "rootNode"; //$NON-NLS-1$
            sb.append("rootNode = new TreeNode(1,'RootNode');"); //$NON-NLS-1$
            sb.append("\n"); //$NON-NLS-1$                  
         }
         
         String param = aBpelObj.getLocationPath();
         
   
         String stateImage = mImagePaths.getStateImagePath( aBpelObj.getDisplayState());
         if (stateImage != null)
         {
            tvName += " <img border=0 src=\"" + stateImage + "\" />";   //$NON-NLS-1$ //$NON-NLS-2$ 
         }
         
         if (aBpelObj instanceof AeBpelObjectContainer 
               && ((AeBpelObjectContainer) aBpelObj).getChildren().size() > 0)
         {  
            // node
            // Eg: rootNode = new TreeNode(1,'RootNode');
            // Eg: node2 = new TreeNode(3,'subpage 2',new Array(closedGif,openGif));
            sb.append("var "); //$NON-NLS-1$         
            sb.append(tvNodeName);
            sb.append(" = new TreeNode("); //$NON-NLS-1$
            sb.append(nodeId);
            sb.append(",'"); //$NON-NLS-1$
            sb.append(tvName);
            sb.append("',"); //$NON-NLS-1$
            sb.append(tvIcon);
            sb.append(",\""); //$NON-NLS-1$
            sb.append(param);
            sb.append("\");"); //$NON-NLS-1$
            List children = ((AeBpelObjectContainer) aBpelObj).getChildren();
            for (int i = 0; i < children.size(); i++)
            {
               AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
               buildJSTree(child, sb, depth + 1);
            }
         }
         else 
         {
            // leaf
            // Eg: node2 = new TreeNode(8,'Some child 7',pageIcon);         
            sb.append("var "); //$NON-NLS-1$
            sb.append(tvNodeName);
            sb.append( " = new TreeNode("); //$NON-NLS-1$
            sb.append(nodeId);
            sb.append(",'" ); //$NON-NLS-1$
            sb.append(tvName);
            sb.append("',"); //$NON-NLS-1$
            sb.append(tvIcon);
            sb.append(",\""); //$NON-NLS-1$
            sb.append(param);
            sb.append("\");"); //$NON-NLS-1$
         }
         
         if (!tvParentNodeName.equals(tvNodeName))
         {
            sb.append("\n"); //$NON-NLS-1$
            indent(sb, depth);
            // Eg: node2.addChild(node2a);
            sb.append(tvParentNodeName);    
            sb.append(".addChild(");//$NON-NLS-1$
            sb.append(tvNodeName);
            sb.append( ");");//$NON-NLS-1$
         }
      }// end sync.  
   }

   /**
    * Pads the string buffer.
    * @param aSb
    * @param aDepth
    */
   private void indent(StringBuffer aSb, int aDepth)
   {
      synchronized(aSb)
      {
         for (int i = 0; i < aDepth; i++)
         {
            aSb.append("   "); //$NON-NLS-1$
         }
      }
   }   

}
