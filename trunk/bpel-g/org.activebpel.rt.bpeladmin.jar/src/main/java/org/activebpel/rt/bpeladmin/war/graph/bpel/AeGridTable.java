//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/AeGridTable.java,v 1.2 2005/06/28 17:19:01 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel;


import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpeladmin.war.graph.bpel.controller.AeBpelControllerBase;
import org.activebpel.rt.bpeladmin.war.graph.bpel.figure.AeBpelSimpleActivityFigure;
import org.activebpel.rt.bpeladmin.war.graph.ui.figure.AeGraphFigure;
import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;


/**
 * Grid to hold BPEL objects while performing the auto layout.
 */
public class AeGridTable
{
   /** Holds layout prefs used to set margins during layout */
   private AeLayoutPrefs mLayoutPrefs;

   /** Holds all members of the grid table */
   private ArrayList mGridData;

   /** Buckets which are used to group siblings together */
   private ArrayList mBuckets[];   

   /**
    * Constructs the table with the given layout preferences.
    */
   public AeGridTable(AeLayoutPrefs aPrefs)
   {
      mLayoutPrefs = aPrefs;
      mGridData = new ArrayList(10);      
      mBuckets  = new ArrayList[10];       
   }

   /**
    * Adds an element to the grid table. Returns a status indicating if the
    * element was added successfully.  An add will fail if a component already 
    * exists at the desired location. The input params aRow or aCol may be set
    * to -1 to indicated auto insert mode, but at least one must be set to a 
    * non negative number.
    * 
    * @param aNode the node element we are adding 
    * @param aParents a list of the parent nodes for this element
    * @param aRow the row we would like to be added to (-1 for auto append)
    * @param aCol the column we would like to be added to (-1 for auto append)
    * 
    * @return boolean status indicator (true = success, false = failure)
    */
   public boolean addGridElement(AeBpelControllerBase aNode, List aParents, int aRow, int aCol)
   {   
      // Row or column may be -1 but not both
      boolean status = false;
      if (aRow < 0 && aCol < 0)
         return status;
      
      // If row or column is -1 then we are auto appending, so find next available slot
      if (aCol < 0)
         aCol = getNextColumnForRow(aRow);
      else if (aRow < 0)
         aRow = getNextRowForColumn(aCol);

      // Grid location should be emtpy
      if (getGridElement(aRow, aCol) == null)
      {
         status = mGridData.add(new AeGridElement(aNode, aParents, aRow, aCol));
      }
      return status;
   }
   
   /**
    * Removes an element from the grid table at the specified location.
    *  
    * @param aRow the row we are looking for
    * @param aCol the column we are looking for
    */
   public void removeGridElement(int aRow, int aCol)
   {
      AeGridElement element = getGridElement(aRow, aCol);
      if (element != null)
         mGridData.remove(element);
   }

   /**
    * Finds the next available slot for a given row.
    * 
    * @param aRow the row we are interested in
    * @return the next available slot.
    */
   public int getNextColumnForRow(int aRow)
   {
      int slot = -1;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getRow() == aRow)
            slot = Math.max(slot, gridElement.getColumn());
      }
         
      return slot + 1;
   }
   
   /**
    * Finds the next available slot for a given column.
    * 
    * @param aCol the column we are interested in
    * @return the next available slot.
    */
   public int getNextRowForColumn(int aCol)
   {
      int slot = -1;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getColumn() == aCol)
            slot = Math.max(slot, gridElement.getRow());
      }
         
      return slot + 1;
   }
   
   /**
    * Finds the grid element we are looking for given the row and column.
    * 
    * @param aRow the row we are looking for
    * @param aCol the column we are looking for
    * @return AeGridElement object if found, null otherwise.
    */
   public AeGridElement getGridElement(int aRow, int aCol)
   {
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getRow() == aRow && gridElement.getColumn() == aCol)
            return gridElement;
      }
         
      return null;
   }
   
   /**
    * Finds the grid element for the given node edit part or returns null if not
    * found.
    * 
    * @param aNode the edit part we are looking for
    * @return AeGridElement object if found, null otherwise.
    */
   public AeGridElement findGridElement(AeBpelControllerBase aNode)
   {
      AeGridElement gridElement = null;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         gridElement = (AeGridElement)iter.next();
         if (gridElement.getNode() == aNode)
            break;
      }
         
      return gridElement;
   }
   
   /**
    * Finds the grid element for the given node model or returns null if not found.
    * 
    * @param aNode the node model we are looking for
    * @return AeGridElement object if found, null otherwise.
    */
   public AeGridElement findGridElement(AeBpelObjectBase aNode)
   {
      AeGridElement gridElement = null;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         gridElement = (AeGridElement)iter.next();
         if (gridElement.getNode().getBpelModel() == aNode)
            break;
      }
         
      return gridElement;
   }
   
   
   /**
    * Once all grid elements have been placed in the table, this routine will
    * adjust the grid element locations. The objective is to first position 
    * the child elements next to one another. Next we will attempt to center the 
    * parent above the children. 
    */
   public void adjustLayout()
   {
      int maxRows = getMaxRows();
      int maxCols = getMaxCols();
  
      for (int i=1; i < maxRows; i++)
      {
         // loop through columns and add elements to appropriate buckets
         for (int j=0; j < maxCols; j++)
         {
            AeGridElement gridElement = getGridElement(i,j);
            if (gridElement != null)
            {
               addToBucket(gridElement);
            }
         }
         // Now assign the elements in the buckets new column ids, and release 
         // buckets for next use.
         assignColumnsToBuckets();
         emptyBuckets(); 
      }

      // Now that the siblings are grouped together, try to center the columns
      // within the rows
      for (int i=1; i < maxRows; i++)
      {
         maxCols = centerRowElements(i, maxCols);
      }     
   }
   
   /**
    * Used to center parents of a sibling group for a given row. 
    *  
    * @param aRowLevel the current row level we are processing
    * @param aMaxCols the maximum columns in the grid
    * @return maximum columns in the grid, which may change if elements are added
    */
   private int centerRowElements(int aRowLevel, int aMaxCols)
   {
      AeGridElement gridElement, firstElement = null, lastElement = null;
      
      // Process all elements for the given row
      for (int i=0; i < aMaxCols; i++)
      {
         // Locate the grid element and only process ones which are not root nodes
         gridElement = getGridElement(aRowLevel, i);
         if (gridElement != null && !gridElement.getParents().isEmpty())
         {
            if (firstElement == null)
            {
               firstElement = lastElement = gridElement;
            }
            else if (getCommonParent(firstElement, gridElement) != null)
            {
               lastElement = gridElement;
            }
            else
            {
               // Center the parent and reset first/last elements for next iteration.
               // If max cols was modified, need to account for this in our grid selection index.
               int maxCols = centerElementRange(firstElement, lastElement, aMaxCols);
               if (maxCols > aMaxCols)
               {
                  i += (maxCols - aMaxCols);
                  aMaxCols = maxCols;
               }
               
               firstElement = lastElement = gridElement;
            }
         }               
      }
      
      // Process the final iteration from the loop
      if (firstElement != null && lastElement != null)
      {
         aMaxCols = centerElementRange(firstElement, lastElement, aMaxCols);
      }
      return aMaxCols;
   }

   /**
    * Used to center the parent of a group of elements within the major axis. If 
    * the group range represents a single element, then the element will be 
    * centered beneath it's parent. Otherwise the parent is centered above the
    * element group.
    * 
    * @param aFirst the first element in the range
    * @param aLast the last element in the range (may be same as first)
    * @param aMaxCols maximum number of columns in grid
    * @return number of columns in the grid, which may have changed. 
    */
   private int centerElementRange(AeGridElement aFirst, AeGridElement aLast, int aMaxCols)
   {
      // if first and last element are the same, adjust ourselves beneath the 
      // parent otherwise adjust parent beneath the group 
      if (aFirst == aLast)
      {
         int targetCell = 0;
         int parentSize = Math.max(1, aFirst.getParents().size());
         
         for (Iterator iter = aFirst.getParents().iterator(); iter.hasNext();)
         {
            targetCell += findGridElement(((AeBpelObjectBase)iter.next())).getColumn();
         }
         targetCell /= parentSize;
         aMaxCols = shiftRowData(aFirst, targetCell, aMaxCols);
      }
      else
      {
         int groupSize, targetCell;
         
         // Determine target cell for sibling group.
         groupSize  = aLast.getColumn() - aFirst.getColumn() + 1;
         targetCell = aFirst.getColumn() + (groupSize >> 1);
         
         // We need to perform an insert if group size is even
         if (groupSize % 2 == 0)
         {
            aMaxCols++;
            insertColumn(targetCell);
         }

         // locate the parent grid element so we can reposition it   
         AeGridElement parentElement = findGridElement(getCommonParent(aFirst, aLast));
         if (parentElement != null)
         {
            if (parentElement.getColumn() != targetCell)
            {
               aMaxCols = shiftRowData(parentElement, targetCell, aMaxCols);
            }
         }
      }               
      
      return aMaxCols;
   }
   
   /**
    * Given a grid element and the new target column, this method will shift the 
    * remaining columns within a row to the right. The width of the row is returned 
    * in case this operation affects the row width.
    *   
    * @param aElement the grid element to be shifted
    * @param aTgtCol the target column the grid element will be shifted to 
    * @param aMaxCols the maximum column width of the grid
    * @return the width of the grid, which may have been modified
    */
   private int shiftRowData(AeGridElement aElement, int aTgtCol, int aMaxCols)
   {
      AeGridElement tmpElement;
      
      // Slide the element and remainder of nodes at this level to the right
      int shift = Math.max(0, aTgtCol - aElement.getColumn());
      for (int i=aMaxCols - 1 ; i >= aElement.getColumn(); i--)
      {
         if ((tmpElement = getGridElement(aElement.getRow(), i)) != null)
         {
            tmpElement.setColumn(tmpElement.getColumn() + shift);
         }
      }

      // If we extended the grid, reflect that in the value we will return
      for (int i=aMaxCols; i < aMaxCols + shift; i++)
      {
         if ((tmpElement = getGridElement(aElement.getRow(), i)) != null)
         {
            aMaxCols++;
         }
      }          
      
      return aMaxCols;
   }

   /**
    * Given two grid elements, returns the common parent if found.
    * 
    * @param aGridA first element to compare
    * @param aGridB second element to compare
    * @return common parent if one was found
    */
   private AeBpelObjectBase getCommonParent(AeGridElement aGridA, AeGridElement aGridB)
   {
      AeBpelObjectBase commonParent = null;      
      for (Iterator iter = aGridA.getParents().iterator(); iter.hasNext() && commonParent == null;)
      {
         Object node = iter.next();
         if (aGridB.getParents().indexOf(node) >= 0)
         {
            commonParent = (AeBpelObjectBase)node;
         }
      }      
      return commonParent;
   }
   
   /**
    * Adds a grid element element to a bucket with common siblings. The bucket
    * management is such that bucket 0 is reserved for root nodes, but all others 
    * are available for grouping. If we run out of buckets, the bucket array is
    * resized to contain 50% more than before and a new bucket is started for the
    * siblings.
    *  
    * @param aElement the element to be added.
    */
   private void addToBucket(AeGridElement aElement)
   {
      boolean elementAdded = false;

      // If element has no parents then add to first bucket which is reserved
      // for root nodes only.      
      if (aElement.getParents().isEmpty())
      {
         if (mBuckets[0] == null)
         {
            mBuckets[0] = new ArrayList(10);
         }  
         mBuckets[0].add(aElement);
         elementAdded = true;
      }
      else
      {
         // Loop through all buckets except first which is reserved for root nodes.
         // Look for a parental group to associate element with
         for (int i=1; i < mBuckets.length && !elementAdded; i++)
         {
            if (mBuckets[i] != null)
            {
               // Get sample sibling to check parental association
               AeGridElement sibling = (AeGridElement)mBuckets[i].get(0);
               for (Iterator iter = sibling.getParents().iterator(); iter.hasNext();)
               {
                  if (aElement.getParents().indexOf(iter.next()) >= 0)
                  {
                     mBuckets[i].add(aElement);
                     elementAdded = true;
                  }                  
               }
            }            
         }
         
         // If we didn't find a proper bucket, create one now 
         if (!elementAdded)
         {
            int freeBucket = -1;
            
            // Try and grab a bucket near my parent to help with alignment later
            AeGridElement parent = findGridElement((AeBpelObjectBase)aElement.getParents().get(0));
            if (parent != null)
            {
               int desiredBucket = parent.getColumn() + 1;
               if (desiredBucket < mBuckets.length && mBuckets[desiredBucket] == null)
               {
                   freeBucket = desiredBucket;
               }
            }
            
            // Couldn't get our desired bucket, so look for any bucket
            for (int i=1; i < mBuckets.length && freeBucket < 0; i++)
            {
               if (mBuckets[i] == null)
               {
                  freeBucket = i;
               }
            }
            
            // If we didn't find a free bucket, we need to create more
            if (freeBucket < 0)
            {
               // The next free bucket index is the bucket size, since we are zero based
               freeBucket = mBuckets.length;
               
               // Create a new array of buckets 50% greater than the previous one
               // Then copy the old bucket contents to the newly created bucket array
               ArrayList newBuckets[] = new ArrayList[(int)(mBuckets.length * 1.5)];               
               System.arraycopy(mBuckets, 0, newBuckets, 0, mBuckets.length);
               mBuckets = newBuckets;
            }
            
            // Now create the list for the bucket and add the element to it
            mBuckets[freeBucket] = new ArrayList(10);
            mBuckets[freeBucket].add(aElement);            
         }
      }      
   }
   
   /**
    * Method to assign cell indices to the buckets of children we have gathered
    */
   private void assignColumnsToBuckets()
   {
      for (int i=0, col=0; i < mBuckets.length; i++)
      {
         if (mBuckets[i] != null)
         {
            for (Iterator iter = mBuckets[i].iterator(); iter.hasNext();)
            {
               AeGridElement sibling = (AeGridElement)iter.next();
               sibling.setColumn(col++);
            }            
         }         
      }
   }

   /**
    * Method to empty out all buckets, so they may be used in next iteration.
    */
   private void emptyBuckets()
   {
      for (int i=0; i < mBuckets.length; i++)
         mBuckets[i] = null;
   }
   
   
   /**
    * Once all grid elements have been placed in the table and the layout has 
    * been adjusted, this routine will assign new coordinates to the components 
    * based on their grid location. This layout is for a vertical orientation.
    * 
    * @return the new size of the container
    */
   public Dimension repositionVertical()
   {
      final int maxRows = getMaxRows();
      final int maxCols = getMaxCols();
      int rowHeight[] = new int[maxRows];
      int rowDelta[]  = new int[maxRows];
      int colWidth[]  = new int[maxCols];
      int colDelta[]  = new int[maxCols];

      // Calculate the heights and deltas for each row
      for (int i = 0; i < maxRows; i++)
      {
         rowHeight[i] = getMaxRowHeight(i);
         if (i == 0)
         {
            rowDelta[i] = mLayoutPrefs.getMarginTop();
         }
         else
         {
            rowDelta[i] = rowHeight[i-1] + rowDelta[i-1] + mLayoutPrefs.getInterLevelSpacing();
         }
      }

      // Calculate the widths and deltas of each column
      for (int i = 0; i < maxCols; i++)
      {
         colWidth[i] = getMaxColWidth(i);
         if (i == 0)
         {
            colDelta[i] = mLayoutPrefs.getMarginLeft();
         }
         else
         {
            colDelta[i] = colWidth[i-1] + colDelta[i-1] + mLayoutPrefs.getInterNodeSpacing();
         }
      }
      
      // if all simple standard icon w/text east or west activities they will not be centered
      boolean simpleGrid = isSimpleGrid();

      // Process all container elements within grid to assign proper coordinates 
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         AeGraphFigure nodeFig = gridElement.getNode().getFigure();
         int col = gridElement.getColumn();
         int colCenter = 0;
         if(! simpleGrid)
         {
            colCenter = Math.max(0, (colWidth[col] -  nodeFig.getPreferredSize().width)) + getAnchorWidthDelta(gridElement);
         }
         int row = gridElement.getRow();
         int rowCenter = Math.max(0, rowHeight[row] - nodeFig.getPreferredSize().height);
         int figX = colDelta[col] + (colCenter/2);
         int figY = rowDelta[row] + (rowCenter/2);
         nodeFig.setLocation(figX, figY);
      }
      
      // Calculate and return the new size which is based on delta of the last
      // row or column plus the height and width of the elements at that location
      Dimension sz = new Dimension(colDelta[maxCols-1] + colWidth[maxCols-1], 
                            rowDelta[maxRows-1] + rowHeight[maxRows-1]);      
      return sz;
   }
   
   public Dimension repositionHorizontal()
   {
      final int maxRows = getMaxRows();
      final int maxCols = getMaxCols();
      int colDelta[]  = new int[maxRows];
      int colWidth[]  = new int[maxRows];
      int rowDelta[]  = new int[maxCols];      
      int rowHeight[] = new int[maxCols];

      // Determine the row deltas, which are based on the grid column elements
      // since we are inverting the grid. 
      for (int i=maxCols-1; i >= 0; i--)
      {
         rowHeight[i]  = getMaxColHeight(i);
         if (i == maxCols-1)
            rowDelta[i] = mLayoutPrefs.getMarginTop();
         else       
            rowDelta[i] = rowHeight[i+1] + rowDelta[i+1] + mLayoutPrefs.getInterNodeSpacing();       
      }

      // Determine the column deltas, which are based on the grid row elements
      // since we are inverting the grid. 
      for (int i=0; i < maxRows; i++)
      {
         colWidth[i] = getMaxRowWidth(i);
         if (i == 0)
            colDelta[i] = mLayoutPrefs.getMarginLeft();
         else       
            colDelta[i] = colWidth[i-1] + colDelta[i-1] + mLayoutPrefs.getInterLevelSpacing();       
      }

      // Process all container elements within grid to assign proper coordinates 
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         AeGraphFigure nodeFig = gridElement.getNode().getFigure();
         
         int col = gridElement.getColumn();
         int row = gridElement.getRow();

         int colCenter = Math.max(0, (colWidth[row]  - nodeFig.getSize().width)  / 2); 
         int rowCenter = Math.max(0, (rowHeight[col] - nodeFig.getSize().height) / 2); 
          
         nodeFig.setLocation(new Point(colDelta[row] + colCenter, rowDelta[col] + rowCenter));                  
      }
      
      // Calculate and return the new size which is based on delta of the last
      // row or column plus the height and width of the elements at that location
      Dimension sz =  new Dimension(colDelta[maxRows-1] + colWidth[maxRows-1], 
                            rowDelta[0] + rowHeight[0]);
      
      return sz;
   }   
   
   /**
    * Obtains the maximum number of columns in our grid. 
    * 
    * @return representing the maximum number of columns in the grid
    */
   public int getMaxCols()
   {
      int maxCols = 0;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         maxCols = Math.max(maxCols, ((AeGridElement)iter.next()).getColumn());
      }  
      return maxCols + 1;
   }

   /**
    * Obtains the maximum number of rows in our grid. 
    * 
    * @return representing the maximum number of rows in the grid
    */
   public int getMaxRows()
   {
      int maxRows = 0;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         maxRows = Math.max(maxRows, ((AeGridElement)iter.next()).getRow());
      }  
      return maxRows + 1;
   }

   /**
    * Determine the maximum height of components in the given row.
    * 
    * @param aRow the target row for the operation.
    * @return the height of the component with the greatest height in pixels
    */
   public int getMaxRowHeight(int aRow)
   {
      int maxHeight = 0;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getRow() == aRow)
         {
            maxHeight = Math.max(maxHeight, gridElement.getNode().getFigure().getSize().height);
         }
      }         
      return maxHeight;
   }

   /**
    * Determine the maximum width of components in the given row.
    * 
    * @param aRow the target row for the operation.
    * @return the widtht of the component with the greatest width in pixels
    */
   public int getMaxRowWidth(int aRow)
   {
      int maxWidth = 0;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getRow() == aRow)
         {
            maxWidth = Math.max(maxWidth, gridElement.getNode().getFigure().getSize().width);
         }
      }
         
      return maxWidth;
   }

   /**
    * Determine the maximum width for components in the given column.
    * 
    * @param aColumn the target column for the operation.
    * @return the width of the component with the largest size in pixels
    */
   public int getMaxColWidth(int aColumn)
   {
      boolean simpleGrid = isSimpleGrid();
      int maxWidth = 0;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getColumn() == aColumn)
         {
            int nodeWidth = gridElement.getNode().getFigure().getSize().width;
            if(! simpleGrid)
            {
               nodeWidth += getAnchorWidthDelta(gridElement);
            }
            maxWidth = Math.max(maxWidth, nodeWidth);
         }
      }
         
      return maxWidth;
   }

   /**
    * Returns the figure if true the element passed is associated with a simple activities with icons 
    * and left or right text orientation. Otherwise returns null.
    * @param aGridElement
    */
   protected AeBpelSimpleActivityFigure getSimpleFigure(AeGridElement aGridElement)
   {
      AeBpelSimpleActivityFigure fig = null;
      // since simple activity anchors are to the icon adjust the center as neccesary
      if(aGridElement.getNode().getFigure() instanceof AeBpelSimpleActivityFigure)
      {
         fig = (AeBpelSimpleActivityFigure)aGridElement.getNode().getFigure();
         if (fig.getLabel() != null && fig.getLabel().getIcon() == null)
         { 
            fig = null;
         }
      }
      return fig;
   }

   /**
    * Returns true if the grid data contains all simple figure as defined by <code>getSimpleFigure</code>.
    */
   protected boolean isSimpleGrid()
   {
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if(getSimpleFigure(gridElement) == null)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * If passed element is a simple figure as defined by <code>getSimpleFigure</code> then it returns the text width.
    * for adjustment of the column width.
    * @param aGridElement
    * @return The width adjustment
    */
   protected int getAnchorWidthDelta(AeGridElement aGridElement)
   {
      AeBpelSimpleActivityFigure fig = getSimpleFigure(aGridElement);
      int width = 0;
      if(fig != null)
      {
         width = fig.getLabel().getPreferredSize().width;
         if (fig.getLabel().getIcon() != null)
         {
            width = width - fig.getLabel().getIcon().getIconWidth();
         }
      }
      return width;
   }

   /**
    * Determine the maximum height for components in the given column.
    * 
    * @param aColumn the target column for the operation.
    * @return the height of the component with the largest size in pixels
    */
   public int getMaxColHeight(int aColumn)
   {
      int maxHeight = 0;         
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getColumn() == aColumn)
         {
            maxHeight = Math.max(maxHeight, gridElement.getNode().getFigure().getSize().height);
         }
      }
         
      return maxHeight;
   }

   /**
    * Insert a new column at the the specified location, shifting any existing 
    * columns to the right. The column locations are zero based, to shift over 
    * the first column specify aCol as 0.
    *  
    * @param aCol the column position to insert from
    */   
   public void insertColumn(int aCol)
   {
      for (Iterator iter = mGridData.iterator(); iter.hasNext();)
      {
         AeGridElement gridElement = (AeGridElement)iter.next();
         if (gridElement.getColumn() >= aCol)
         {
            gridElement.setColumn(gridElement.getColumn() + 1);
         }
      }
   }
   
}


/**
 * Class used to associate a node with a row and column of a grid table. 
 */
class AeGridElement
{
   private AeBpelControllerBase mNode;
   private List mParents;
   private int mRow, mCol;   

   /**
    * Constructor for grid element object. Once a node and parent list have 
    * been set they may not be modfied.  Only it's location within the grid may 
    * be modified.
    * 
    * @param aNode the node being added to the grid location
    * @param aParents a list of the parent nodes for this element
    * @param aRow the row within the grid for the node
    * @param aCol the column within the grid for the node
    */
   public AeGridElement(AeBpelControllerBase aNode, List aParents, int aRow, int aCol)
   {
      mNode    = aNode;
      mParents = aParents;
      mRow     = aRow;
      mCol     = aCol;
   }

   /**
    * Getter method for the node object. 
    * @return node associated with this grid location
    */
   public AeBpelControllerBase getNode()
   {
      return mNode;
   }

   /**
    * Getter method for the parent list. 
    * @return list of parents for this node
    */
   public List getParents()
   {
      return mParents;
   }

   /**
    * Getter method for the row position of the node within the table.
    * @return the current row position
    */
   public int getRow()
   {
      return mRow;
   }

   /**
    * Setter method to change the row position of a node within the table.
    * @param aRow the new row assignment
    */
   public void setRow(int aRow)
   {
      mRow = aRow;
   }

   /**
    * Getter method for the column position of the node within the table.
    * @return the current column position
    */
   public int getColumn()
   {
      return mCol;
   }

   /**
    * Setter method to change the column position of a node within the table.
    * @param aCol the new column assignment
    */
   public void setColumn(int aCol)
   {
      mCol = aCol;
   }
} 

/**
 * Class which maintains layout information which will be used by the undo logic. 
 */
class AeLayoutData
{
   private AeBpelControllerBase mNode;
   private Dimension       mSize;
   private Point           mLocation;
   private HashMap         mConstraints;
   
   /**
    * Constructor which stores layout data for the visual model.
    * 
    * @param aNode the node we are working with
    * @param aSize the size of the node
    * @param aLocation the location of the node
    * @param aConstraints constraints for container, if any
    */
   public AeLayoutData(AeBpelControllerBase aNode, Dimension aSize, Point aLocation, HashMap aConstraints)
   {
      mNode = aNode;
      mSize = aSize;
      mLocation = aLocation;
      mConstraints = aConstraints;
   }
   
   /**
    * Getter method to obtain the node.
    * @return the node element 
    */
   public AeBpelControllerBase getNode()
   {
      return mNode;
   }

   /**
    * Getter method to return the size of the component.
    * @return size of the component
    */
   public Dimension getSize()
   {
      return mSize;
   }

   /**
    * Getter method to return the location of the component
    * @return location of the component
    */
   public Point getLocation()
   {
      return mLocation;
   }
   
   /**
    * Getter method to return the component constraints, if any.
    * @return hashmap of constraints, or null if not applicable
    */
   public HashMap getConstraints()
   {
      return mConstraints;
   }
}

