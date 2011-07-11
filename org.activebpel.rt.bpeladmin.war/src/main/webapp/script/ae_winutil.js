//
//  Contains utility functions related to manipulating browser windows.
//
//  (c) Active Enpoints Inc.
//

//
// Opens a modal popup window, centered around the parent window.
//
function aePopupModalWindow(aParentWindow, aUri, aWinName, aWidth, aHeight, aResizable, aParams)
{
	return aePopupWindow(aParentWindow, aUri, aWinName, aWidth, aHeight, aResizable, true, aParams);
}

//
// Opens a popup window, centered around the parent window.
//

function aePopupWindow(aParentWindow, aUri, aWinName, aWidth, aHeight, aResizable, aModal, aParams)
{
	var x = aeGetCenterLeft(aParentWindow, aWidth);
	var y = aeGetCenterTop(aParentWindow, aHeight);
	var winParams = "width=" + aWidth + ",height=" + aHeight + ",resizable=";
	if (aResizable)
	{
		winParams = winParams + "yes";		     	
	}
	else
	{
		winParams = winParams + "no";	
	}
	winParams = winParams + ",modal=";	
	if (aModal)
	{
		winParams = winParams + "yes";		     	
	}
	else
	{
		winParams = winParams + "no";	
	}
   winParams = winParams + ",left=" + x + ",top=" + y;
   if (aParams && aParams != null && aParams.length > 0)
   {
	   winParams = winParams + "," + aParams;	
   }
   var childWindow = window.open(aUri,aWinName ,winParams);
   return childWindow;
}

// returns the x coordinate (left) position given parent window and child window width.
// the returned x value is used to center child window.
function aeGetCenterLeft(aParentWindow, aChildWidth)
{
   var x = 0;
   var parentW = aeGetWindowWidth(aParentWindow);
   if (parentW > 0)
   {
      var left = aParentWindow.screenLeft ? aParentWindow.screenLeft : aParentWindow.screenX;
      x = left + (parentW - aChildWidth)/2;
   }
   return x;
}

// returns the y coordinate (top) position given parent window and child window height.
// the returned y value is used to center child window.
function aeGetCenterTop(aParentWindow, aChildHeight)
{
   var y = 0;
   var parentH = aeGetWindowHeight(aParentWindow);
   if (parentH > 0)
   {
      var top = aParentWindow.screenTop ? aParentWindow.screenTop : aParentWindow.screenY;
      y = top + (parentH - aChildHeight)/2;
   }
   return y;
}

// Centers the given window based on the parent window. If the parent window
// is null, then the window will be centered on the screen.
function aeCenterWindow(aWin, aParentWindow)
{
   if (!aWin || aWin == null)
   {
      return;
   }
   var childW = aeGetWindowWidth(aWin);
   var childH = aeGetWindowHeight(aWin);

   var parentW = (aParentWindow && aParentWindow != null) ? aeGetWindowWidth(aParentWindow) : aWin.screen.width;
   var parentH = (aParentWindow && aParentWindow != null) ? aeGetWindowHeight(aParentWindow) : aWin.screen.height;

   var left = (aParentWindow && aParentWindow != null) ? aParentWindow.screenLeft ? aParentWindow.screenLeft : aParentWindow.screenX : 0;
   var top = (aParentWindow && aParentWindow != null) ? aParentWindow.screenTop ? aParentWindow.screenTop : aParentWindow.screenY  : 0;
   var x = left + (parentW - childW)/2;
   var y = top + (parentH - childH)/2;

   aWin.moveTo(x,y);
}


// returns the window width
function aeGetWindowWidth(aWin)
{
   var rVal = -1;
   if (aWin && aWin != null)
   {
      // check various combinations and handle for IE quirks mode,
      if (aWin.innerWidth)
      {
         rVal = aWin.innerWidth;
      }
      else if (aWin.document.documentElement && aWin.document.documentElement.clientWidth)
      {
         rVal = aWin.document.documentElement.clientWidth;
      }
      else if (aWin.document.body)
      {
         rVal = aWin.document.body.clientWidth;
      }
   }
   return rVal;
}

// returns the window height
function aeGetWindowHeight(aWin)
{
   var rVal = -1;
   if (aWin && aWin != null)
   {
      // check various combinations and handle for IE quirks mode,
      if (aWin.innerWidth)
      {
         rVal = aWin.innerHeight;
      }
      else if (aWin.document.documentElement && aWin.document.documentElement.clientWidth)
      {
         rVal = aWin.document.documentElement.clientHeight;
      }
      else if (aWin.document.body)
      {
         rVal = aWin.document.body.clientHeight;
      }
   }
   return rVal;
}
