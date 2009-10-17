/**
* PROPRIETARY RIGHTS STATEMENT
* The contents of this file represent confidential information that is the
* proprietary property of Active Endpoints, Inc.  Viewing or use of
* this information is prohibited without the express written consent of
* Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
* is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
*
* ae_util.js:
* Defines general static method script utilities.
*
* Dependencies:

* jquery.js (http://www.jquery.com, jQuery 1.1.2)
* jquery.form.js
*/

// Utility constructor required for static functions
function AeUtil() {};

// return true when oject is initialized, otherwise return false
AeUtil.isNullOrUndefined = function(aObject)
{
    if (aObject == null || typeof(aObject) == 'undefined')
    {
        return true;
    }
    else
    {
       return false;
    }
};

// return false when oject is initialized, otherwise return true
AeUtil.isNotNullOrUndefined = function(aObject)
{
 return AeUtil.isNullOrUndefined(aObject) == false;
};

// Assert object null or undefined with an alert message
AeUtil.assertNotNull = function(aObject, aMessage)
{
    if (typeof(aObject) == 'undefined')
    {
        this.showErrorMessage("[undefined object]: " + aMessage);
    }
    else if (aObject == null)
    {
        this.showErrorMessage("[null object]: " + aMessage);
    }
};

// Open an error window with aMessage
AeUtil.showErrorMessage = function(aMessage)
{    
    alert("ERROR:\n" + aMessage);
};

// Open a failure window with aMessage
AeUtil.showFailureMessage = function(aMessage)
{
    alert("FAILURE:\n" + aMessage);
}