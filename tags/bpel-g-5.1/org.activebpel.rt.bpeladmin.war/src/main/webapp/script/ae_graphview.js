//
// ActiveBpelEngine - Administration - Deployed and Active Process Detail Graphical View
//

var processId =0;
var scrollX = -1;
var scrollY = -1;
var scOffsetX = -1;
var scOffsetY = -1;
var winWidth = -1;
var winHeight = -1;

var lastPath;
// current active or deployed process id
var currId;
// part or tab
var currPart;
// activity location xpath
var currPath;
// pivot path
var currPivotPath;
// init path - passed via pathid.
var initialPath;
// image tile width and height
var tileWidth = 100;
var tileHeight = 100;
// full process image width and height 
var imageWidth = 1;
var imageHeight = 1;
// tile grid data - number of rows and num. of cols.
var nCols = 0;
var nRows = 0;
// base uri to the process image servlet Eg: "graphimage?pid=1&part=1"
var processImageUriBase;
// process image requestion session id (such as current time ms)
var processImageSid;
// pivot path selection used in parallel instances (as in forEach parallel)
var processImagePivot;
// base uri the reload the jsp page Eg: processview_graph.jsp?pid=1
var reloadUriBase;
// base uri to the properties page. Eg: "processview_properties.jsp?pid=1"
var propertiesUriBase;

//
// Higlight the activity under the mouse location
//
function onActivityMouseOver(param)
{
   lastPath = param;
   s = "" + param;
   var theArea = findArea(param);
   if (theArea != null)
   {
      applyClass('graphselection', 'gson');
      reshapeEle('graphselection','aeprocessdiv', theArea.coords);
   }
   window.status = s;
}

//
// UnHiglight the last selection.
//
function onActivityMouseOut(param)
{
   window.status = '';
   applyClass('graphselection', 'gsoff');
}

//
// Highlight the given activity for selection.
//
function highlightActivity(path)
{
   if (!path || path == "" || path == "null") {
      applyClass('currselection', 'gsoff');
      return;
   }
   scrollX = -1;
   scrollY = -1;
   var theArea = findArea(path);

   if (theArea != null)
   {
      applyClass('currselection', 'gstrans');
      reshapeEle('currselection','aeprocessdiv', theArea.coords);
      scrollSelectionToView();
   }
   else
   {
      // clear the current selection
      applyClass('currselection', 'gsoff');
   }
}

//  
// Reload current view (tab)
//
function refreshView()
{
	showTabWithPath(currPart, currPath, currPivotPath);
	return false;
}

//
// Reload the page with the given tab as the selected tab.
//
function showTab(tab)
{
   return showTabWithPath(tab,"", "");
}

// Reload the page with the given tab as the selected tab. If an activity location path
// is given, then it will be appended to the query parameter.
function showTabWithPath(tab, path, pivot)
{
   url = reloadUriBase + "&part=" + tab;
   if (path && path != "") {
		url = url + "&path=" + encodeURI(path);
   }
   if (pivot && pivot != "") {
		url = url + "&pivot=" + encodeURI(pivot);
   }
   window.location = url;
   return true;
}

//
// Event callback when an activity is selected from the Outline tree view.
//
function onOutlineSelect(path)
{
   var newPart = getPartFromPath(path); // tab selection, if any
   var newPivot = getScopeInstancePath(path); // forEach parallel selection, if any.
   var reload = reloadGraphTabOrPivotSelection(currPart, newPart, currPivotPath, newPivot);
   if (reload)
   {
		showTabWithPath(newPart, path, newPivot);
		return false;
   }
   else
   {
		highlightActivity(path);
		return false;
   }
}

//
// Event handler when the user clicks on an activity (in the graph).
// This handler reloads the property view, which in turn will update the outline view.
//
function onActivitySelect(path)
{
   var propWindow = parent.pvproperties;
   if (path && propWindow)
   {
		url =  propertiesUriBase + "&path=" + encodeURI(path);
		propWindow.location = url;
		// highlight (shade) item  selected by the user
		highlightActivity(path)
   }
}

//
// Event handler when an item is selected.
//
function onSelectionClick()
{
   if (lastPath != "")
   {
		onActivitySelect(lastPath);
   }
}
 
//
// Creates a <img> elements for each tile and lays out row-col grid.    
//
function layoutImages()
{
	nCols = Math.ceil(imageWidth / tileWidth);
   nRows = Math.ceil(imageHeight / tileHeight);
   var processDiv = getProcessDivElement()
   var overlayImgEle = getOverlayImgElement()
   if (!processDiv)
   {
      return;
   }
   initOverlayImg();
   createTiles();
   // resize tab container to fit image tiles
   var ele = document.getElementById('tabimgcontainer');
   if (ele)
   {
		ele.style.width = "" + (nCols*tileWidth+10) + "px";
		ele.style.height = "" + (nRows*tileHeight+10) + "px";
   }            
}
  
//
// Event handler when a tile is loaded.
//    
function onTileLoad(evt)
{
   if (!currPath || currPath == null || currPath == "" || currPath == "null")
   {
		currPath = initialPath;
   }
   highlightActivity(currPath);
}

//
// Event handler if a tile failed to load.
//
function onTileError(evt)
{
}      
 
//
// Creates a <img> elements for each tile and lays out row-col grid.    
//
function createTiles()
{
   var parentEle = getProcessDivElement();
   for (r = 1; r <= nRows; r++)
   {
      for (c = 1; c <= nCols; c++)
      {
		  var imgId = "imgr" + r + "c" + c;  // eg imgr1c1
		  var imgClass = "aeimagetile";
		  // todo: delete ele if already exists
		  var imgEle = parentEle.ownerDocument.createElement("img");
		  var leftPx = (c-1) * tileWidth;
		  var topPx = (r-1) * tileHeight;
		  var rightPx = leftPx + tileWidth;
		  var bottomPx = topPx + tileHeight;
		  var imgClip = "rect(" + topPx + "," + rightPx + "," + bottomPx + "," + leftPx + ")";
		  imgEle.setAttribute("id", imgId);
		  imgEle.className = imgClass;
		  imgEle.style.top = "" + topPx + "px";
		  imgEle.style.left = "" + leftPx + "px";
		  imgEle.style.width = "" + tileWidth + "px";
		  imgEle.style.height = "" + tileHeight + "px";
		  if (nRows == r && nCols == c)
		  {
			imgEle.onload = onTileLoad;
		  }
		  imgEle.onerror = onTileError;
		  parentEle.appendChild(imgEle);
		  var imgUri = processImageUriBase;
		  imgUri = imgUri + "&w=" + tileWidth + "&h=" + tileHeight + "&r=" + r + "&c=" + c;
		  imgUri = imgUri + "&sid=" + processImageSid;
		  imgUri = imgUri + "&pivot=" + encodeURI(processImagePivot);
		  imgEle.setAttribute("src", imgUri);
      } // end for col
   } // end for row
}         

//
// Initializes the overlay so that the width and height is the same as the complete image.
//
function initOverlayImg()
{
   // initialize the overlay image and return it.
   var overlayImgEle = getOverlayImgElement()
   if (!overlayImgEle)
   {
      return null;
   }
   overlayImgEle.style.width = "" + imageWidth + "px";
   overlayImgEle.style.height = "" + imageHeight + "px";
   overlayImgEle.style.top = "0px";
   overlayImgEle.style.left = "0px";
   return overlayImgEle;
} 

//
// Returns the process img container DIV
// 
function getProcessDivElement()
{
   var ele = document.getElementById('aeprocessdiv');
   return ele;
}

//
// Returns the overlay img element.
//
function getOverlayImgElement()
{
   var ele = document.getElementById('aeimgoverlay');
   return ele;
}        

//
// Returns true if the graph should be reloaded due to a tab selection
// or selection change on a forEach parallel instance.
//
function reloadGraphTabOrPivotSelection(currPartId, newPartId, currPivotPath, newPivotPath)
{
    var reloadOnTabChange = (newPartId != -1 && newPartId != currPartId);
    // Check for forEach parellel pivot path selections
    var reloadOnPivotChange = false;
    if ( isPivotPath(newPivotPath) )
    {
      if ( !pivotEquals(currPivotPath, newPivotPath) )
      {
         if ( !isChildPivot(newPivotPath, currPivotPath) )
         {
          	// no direct parent child relationship
          	reloadOnPivotChange = true;
         }
      }
    }
    return (reloadOnTabChange || reloadOnPivotChange);
}

//
// Return true if the path contains an instance scope of forEach parallel child.
//
function isPivotPath(path)
{
	var pivotPath = getScopeInstancePath(path);
	return (pivotPath && pivotPath != null && pivotPath != '');
}

//
// Compares two pivot paths and returns true if they are the same.
//
function pivotEquals(pivot1, pivot2)
{
 return (pivot1 && pivot1 != null && pivot2 && pivot2 != null && pivot1 == pivot2);
}

//
// Returns true if the one pivot path is a child of another.
//
function isChildPivot(parentPivot, childPivot)
{
  var rVal = false;
  if (parentPivot && parentPivot != null 
        && childPivot && childPivot != null 
        && childPivot.length > parentPivot.length)
  {
    rVal = ( parentPivot == childPivot.substring(0, parentPivot.length) );
  }
  return rVal;
}

//
// Returns the (deepest) scope instance path of a parallel forEach.
// If an instance scope is not found this method returns an empty string.
function getScopeInstancePath(path)
{
   // locate the last indexOf [instance()=N] string and return the string upto the end of the predicate.
	 var pivot = '';
	 if (path && path != null)
	 {
		 var predicate = "[instance()=";
		 var idx1 = path.lastIndexOf(predicate);
		 var idx2 = -1;
		 if (idx1 != -1)
		 {
		  idx2 = path.indexOf("]", idx1 + 1);
		 }
		 if (idx2 != -1)
		 {
		  pivot = path.substring(0,idx2 + 1);
		 }
    }
	 return pivot;
}

//
// Returns the part (tab #) given the activity location path.
// The return value is  0: process tab, 1: fault handler tab, 2: event handler tab, 3: comp Handler.
function getPartFromPath(path)
{
    var part = -1;
    if (path) {
       if ( /^\/process\/flow/.test(path) ) {
          part = 0;
       }
       else if ( /^\/process\/scope/.test(path) ) {
          part = 0;
       }
       else if ( /^\/process\/sequence/.test(path) ) {
          part = 0;
       }
       else if ( /^\/process\/receive/.test(path) ) {
          part = 0;
       }       
       else if ( /^\/process\/faultHandlers/.test(path) ) {
          part = 1;
       }
       else if ( /^\/process\/eventHandlers/.test(path) ) {
          part = 2;
       }
       else if ( /^\/process\/compensationHandler/.test(path) ) {
          part = 3;
       }
       else if ( /^\/process\/terminationHandler/.test(path) ) {
          part = 4;
       }
    }
    return part;
}

//
// Calculates the scroll offset
// 
function calcScrollOffset()
{

   var x,y;
   if (self.pageYOffset) // all except Explorer
   {
      x = self.pageXOffset;
      y = self.pageYOffset;
   }
   else if (document.documentElement && document.documentElement.scrollTop)
      // Explorer 6 Strict
   {
      x = document.documentElement.scrollLeft;
      y = document.documentElement.scrollTop;
   }
   else if (document.body) // all other Explorers
   {
      x = document.body.scrollLeft;
      y = document.body.scrollTop;
   }
   scOffsetX = x;
   scOffsetY = y;
}

//
// Calculates the browser window size.
//
function calcDimensions()
{
   var x,y;
   if (self.innerHeight) // all except Explorer
   {
      x = self.innerWidth;
      y = self.innerHeight;
   }
   else if (document.documentElement && document.documentElement.clientHeight)
      // Explorer 6 Strict Mode
   {
      x = document.documentElement.clientWidth;
      y = document.documentElement.clientHeight;
   }
   else if (document.body) // other Explorers
   {
      x = document.body.clientWidth;
      y = document.body.clientHeight;
   }
   winWidth = x;
   winHeight = y;
}

//
// Scroll the window client area so that it is visible.
//
function scrollSelectionToView()
{
   if (scrollX < 0 || scrollY < 0)
   {
      return;
   }
   calcScrollOffset();
   calcDimensions();
   var x = scrollX;
   var y = scrollY;
   var shouldScroll = false;
   if (scrollY < scOffsetY || scrollY >  (scOffsetY + winHeight))
   {
      shouldScroll = true;
      y = y - winHeight/2;
   }
   if (scrollX < scOffsetX || scrollX >  (scOffsetX + winWidth))
   {
      shouldScroll = true;
      x = x - winWidth/2;
   }

   if (shouldScroll)
   {
      window.scrollTo(x,y);
   }
}

//
// Finds image map <area> element given the param (location path)
//
function findArea(param)
{
   var mapEle = document.getElementById('bpelgraph-image-map');
   if (mapEle == null)
   {
      return null;
   }
   var areaNL = null;
   areaNL = mapEle.getElementsByTagName('area');
   if (areaNL == null)
   {
      return null;
   }
   var theArea = null;
   for (i = 0; i < areaNL.length; i++)
   {
      areaEle = areaNL[i];
      if (areaEle.title == param)
      {
         theArea = areaEle;
         break;
      }
   }
   return theArea;
}

//
// Resized and moves the targetId element to the given coordinates.
// The coordiates are relative to the refId element container.
//
function reshapeEle(targetId, refId, coords)
{
   var nl = coords.split(',');
   if (nl == null || nl.length != 4)
   {
      return;
   }
   var x1 = parseInt(nl[0]);
   var y1 = parseInt(nl[1]);
   var x2 = parseInt(nl[2]);
   var y2 = parseInt(nl[3]);
   var w = x2 - x1;
   var h = y2 - y1;
   targetEle = document.getElementById(targetId);
   refEle = document.getElementById(refId);
   if (!targetEle || !refEle)
   {
      return;
   }

   targetEle.style.left = "" + x1 + "px";
   targetEle.style.top = "" + y1 + "px";
   targetEle.style.width =  w + "px";
   targetEle.style.height = h + "px";

   var x = refEle.offsetLeft + x1;
   var y = refEle.offsetTop + y1;
   var offsetEle = refEle;

   while (offsetEle.offsetParent)
   {
      offsetEle = offsetEle.offsetParent;
      x += offsetEle.offsetLeft;
      y += offsetEle.offsetTop;
   }
   scrollX = x;
   scrollY = y;

}

//
// Apply the class name to the given element (by id)
//
function applyClass(id, theClassName)
{
   ele = document.getElementById(id);
   if (ele && theClassName)
   {
      ele.className = theClassName;
   }
}
