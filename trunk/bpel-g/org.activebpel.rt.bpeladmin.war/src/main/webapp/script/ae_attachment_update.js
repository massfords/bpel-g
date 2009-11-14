/**
* PROPRIETARY RIGHTS STATEMENT
* The contents of this file represent confidential information that is the
* proprietary property of Active Endpoints, Inc.  Viewing or use of
* this information is prohibited without the express written consent of
* Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
* is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
*
* ae_attachment_update.js:
* Defines BpelAdmin add attachment related script.
*
* Dependencies:
* jquery.js (http://www.jquery.com, jQuery 1.1.2)
* jquery.form.js
* ae_util.js
*/

// Window manager
var mControl;

// This is run once the form is loaded (equivalent to <body onLoad="...")
$(document).ready(function() { 	
	mControl  = new AeController();
	mControl.init();
});

// Property object
function AeProperty()
{
    /* mode indicator: true- edit, false - add */
    this.mEdit;
    /* index of selected property (number starting from zero)*/
    this.mSelection;
    /* The property name (String) */
    this.mName;
    /* The property value */
    this.mValue;
    
    // setup object function prototypes
    if (typeof(_AeProperty_prototype_called) == 'undefined')
    {
        _AeProperty_prototype_called = true;
            
        // sets mode 
        // aMode - true: edit, false: add
        AeProperty.prototype.setEdit = function(aMode)
        {
            this.mEdit = aMode;
        };
        
        // returns true if in edit mode
        AeProperty.prototype.isEdit = function()
        {
            return this.mEdit;
        }
        
        // sets the selected property index
        AeProperty.prototype.setSelection = function(aIndex)
        {
            this.mSelection = aIndex;
        };
        
        // returns the selected property index
        AeProperty.prototype.getSelection = function()
        {
            return this.mSelection;
        };
        
        // sets the property name
        AeProperty.prototype.setName = function(aName)
        {
            this.mName = aName;
        };
        
        // get property name
        AeProperty.prototype.getName = function()
        {
            return this.mName;
        };
        
        // set the property value
        AeProperty.prototype.setValue = function(aValue)
        {
            this.mValue = aValue;
        };
         
        // returns the property value     
        AeProperty.prototype.getValue = function()
        {
            return this.mValue;
        };
        
        // clears the property (reset)
        AeProperty.prototype.clear = function()
        {
            this.setEdit(false);
            this.setSelection(null);
            this.setName("");
            this.setValue("");
        };

	    // dump this object properties.
        AeProperty.prototype.toString = function()
        {
            var aVars = "";
            aVars += 'mEdit=' + this.isEdit() + "\n";
            aVars += 'mSelection=' + this.getSelection() + "\n";
            aVars += 'mName=' + this.getName() + "\n";
            aVars += 'mValue=' + this.getValue() + "\n";
            return aVars;
        };
    }
}

// Window controller object
function AeController()
{
    // Buttons
    // main window buttons
    this.addAttachmentBtn      = new AeButton("addAttachmentBtn", AeHandler.postAttachment);
    this.cancelAttachmentBtn   = new AeButton("cancelAttachmentBtn", AeHandler.closeAttachmentWindow);
    
    this.property     = new AeProperty();
    this.propertyView = new AePropertyView();
    this.propertyEdit = new AePropertyEdit();
    this.location;

     // setup object function prototypes
    if (typeof(_AeController_prototype_called) == 'undefined')
    {
        _AeController_prototype_called = true;

        // Init object 
        AeController.prototype.init = function()
        {
            // Show variable path in the window title
            $('#titleLocation').text('(' + mPath + ')');
            this.refreshPropertyView();  
            this.addAttachmentBtn.disable();
            $('#busywait').hide();
            this.location = new AeLocation();
            this.location.init();
            this.nextState();     
        };
        
        // Refresh property view frame.
        AeController.prototype.refreshPropertyView = function()
        {
           var url = "processview_properties.jsp?" + mQs;
           window.opener.location=url;
        };
        
        AeController.prototype.nextState = function()
        {
            if (mBeanStatus == 0)
            {
                // successful add - nothing to do - bye bye
                AeHandler.closeAttachmentWindow();
                return;
            }
            else if (mBeanStatus == 1)
            {
                // failure
                AeUtil.showFailureMessage(mBeanStatusDetail);
            }	
            else if (mBeanStatus == 2)
            {
                AeUtil.showErrorMessage(mBeanStatusDetail);
            }
            // ignore, error, failure come here
            this.openLocation();
        };
        
        // Focus on location 
        AeController.prototype.openLocation = function()
        {
            $('.propertyedit').hide();
            this.propertyView.disable();
            this.addAttachmentBtn.disable();
            this.property.clear();
            this.location.getFile();  
        };
        
        // Remove focus on location 
        AeController.prototype.closeLocation = function()
        {
            this.propertyView.enable();
            this.addAttachmentBtn.enable();
        };
        
        // Open property for edits
    	AeController.prototype.openEdit = function()
    	{
    	    // hide accept, location, disable attribute view begin property edit
    	    this.propertyView.disable();
    	    $('.locationview').hide();
            $('.aeAcceptButtons').hide();
            $('.propertyedit').show();
    	    this.propertyEdit.begin(this.property);
    	};
    	
    	// accept edited property
    	AeController.prototype.acceptEdit = function()
    	{
    	    this.closeEdit();
            this.property.setName($('#editName').val());
            this.property.setValue($('#editValue').val());	 
            this.propertyView.accept(this.property);
    	};
    	
    	// Close property edits
    	AeController.prototype.closeEdit = function()
    	{
    	    $('.propertyedit').hide(); 
            $('.locationview').show();
            $('.aeAcceptButtons').show();
            this.propertyView.enable();
    	};

    	// Convert displayed attributes to xml
    	AeController.prototype.serialize = function()
    	{
    	    var xml = '<attributes>';
    	    $('.property').each(function(i) {
                var name = $(this).children('td.propertyname').text();
                var value = $(this).children('td.propertyvalue').text();
                xml = xml +  "<attribute name='" +  name +"' value='" +  value  + "'/>";
    	    });
    	    var xml = xml + '</attributes>';
    	    return xml;
    	};
    	
    	// Wait for server response
    	AeController.prototype.wait = function()
    	{
            this.addAttachmentBtn.disable();
            this.cancelAttachmentBtn.disable();
            this.propertyView.disable();
            this.location.disable();
            $('#busywait').show();
    	}
    	
    	// dump 
        AeController.prototype.toString = function()
    	{
    	    var aVars = "";
    	    return aVars;
        };
    }
}

// attachment file location object
function AeLocation()
{
    // setup object function prototypes
    if (typeof(_AeLocation_prototype_called) == 'undefined')
    {
        _AeLocation_prototype_called = true;
        
        // init: bind change handler
    	AeLocation.prototype.init = function()
    	{
    	    $('#attachmentFilePath').unbind();
    	 
             $('#attachmentFilePath').click(function() {
                AeHandler.acceptLocation();
            });
            $('#attachmentFilePath').change(function() {
                AeHandler.acceptLocation();
            });
   
    	};
    	
        // validate attachment file path
    	AeLocation.prototype.valid = function()
    	{
    	    var path = this.getFilePath();
            if (path == undefined || path.length == 0 || path.match(/^[\s]+$/))
            {
              return false;
            }
            return true;
        };
          
        // attachment file focus
        AeLocation.prototype.getFile = function()
        {
            $('#attachmentFilePath').focus();
        };
        
        // return the qualified attachment file path
        AeLocation.prototype.getFilePath = function()
        {
           return $('#attachmentFilePath').val();
        };
        
        AeLocation.prototype.disable = function()
    	{
    	   $('#attachmentFilePath').attr("disabled", "disabled");
    	};
    }
}

// Property view object
function AePropertyView()
{
    // property view buttons
    this.addPropertyBtn        = new AeButton("addPropertyBtn", AeHandler.newProperty);
    this.editPropertyBtn       = new AeButton("editPropertyBtn", AeHandler.editProperty);
    this.deletePropertyBtn     = new AeButton("deletePropertyBtn",AeHandler.deleteProperty);
    
    // setup object function prototypes
    if (typeof(_AePropertyView_prototype_called) == 'undefined')
    {
        _AePropertyView_prototype_called = true;
        
         /* template used to add/update the html DOM to view the property */
        AePropertyView.prototype.mTemplate =
              "<tr style='display:none;' class='updatingproperty'>" +
       		  "<td class='propertyname'></td>" +
       		  "<td class='propertyvalue'></td>" +
       		  "<td><input type='checkbox' name='index'\></td>" +
    		  "</tr>";
    		  
        
        AePropertyView.prototype.disable = function()
    	{
            this.addPropertyBtn.disable();
            this.editPropertyBtn.disable();
            this.deletePropertyBtn.disable();
            $('.property input[@type="checkbox"]').each(function(i) {
    	          $(this).attr('disabled','disabled');
    	      });
        };
        
        AePropertyView.prototype.enable = function()
    	{
            $('.property input[@type="checkbox"]').each(function(i) {
    	      $(this).removeAttr('disabled');
    	    });
    	    this.addPropertyBtn.enable();
    	    this.register();
    	};
    	
    	// callback, registers the click of a property checkbox
    	AePropertyView.prototype.register = function()
    	{

           var selectionCount = $('.property input[@type="checkbox"]').filter(function(index){ return this.checked ;}).length;
	
    	   // enable/disable property action buttons
    	   switch(selectionCount)
    	   {
    	       case 0:
    	         this.editPropertyBtn.disable();
    	         this.deletePropertyBtn.disable();
    	       	 break;
    	       case 1:
    	         this.editPropertyBtn.enable();
    	         this.deletePropertyBtn.enable();
    	       	 break;
    	       default:
    	         this.editPropertyBtn.disable();
    	         this.deletePropertyBtn.enable();
    	         break;
    	   }
    	 };
    	 
    	// Add property to document DOM for visualization
        AePropertyView.prototype.add = function(aName,aValue)
        {
            var newIdx = $('.property').length;
            $('.addabove').before(this.mTemplate);
            $('.updatingproperty input[@name="index"]').attr('value', newIdx);
            $('.updatingproperty .propertyname').text(aName);
            $('.updatingproperty .propertyvalue').text(aValue);
            $('.updatingproperty input[@type="checkbox"]').bind("click", function() {
                AeHandler.checkBoxRegister();
            });
            $('.updatingproperty').addClass('property').removeClass('updatingproperty').removeAttr('style');
        };
	   
        // Update the property selected in the list for visualization
        AePropertyView.prototype.update = function(aName,aValue,aIdx)
        {
           var parent = $('.property input[@name="index"][@value="' + aIdx + '"]').attr('checked', false).parent();
            $(parent).siblings('.propertyname').text(aName);
            $(parent).siblings('.propertyvalue').text(aValue);
            this.register();
        };
        
        // Accept edited property
        AePropertyView.prototype.accept = function(aProperty)
        {
            // if duplicate then update; otherwise add
            var idx = -1;
            
            // jQuery does ot have and equals() only contains().
            // As a work around, loop though all 'propertyname' to match the content.
            // When a match is found the index of the  duplicate is returned.
            $.each( $('.propertyname'), function(i, n){ 
	            if( $(n).text() == aProperty.getName()) idx = i;
            });
            
            if (idx >= 0)
            {
                aProperty.setEdit(true);
                aProperty.setSelection(idx);
            }
            
            if (aProperty.isEdit())
            {
                this.update(aProperty.getName(),aProperty.getValue(),aProperty.getSelection());
            }
            else
            {
               this.add(aProperty.getName(),aProperty.getValue()); 
            }
        };
        
        // Delete selected properties 
        AePropertyView.prototype.remove = function()
        {
            $('tr.property').find('td input[@type="checkbox"]').filter(function(){ return this.checked ;}).parent().parent().remove();   
            this.register();     
        };
    }
}


// Property edit object
function AePropertyEdit()
{
    // property edit buttons
    this.cancelEditPropertyBtn = new AeButton("cancelEditPropertyBtn",  AeHandler.cancelPropertyEdit);
    this.acceptEditPropertyBtn = new AeButton("acceptEditPropertyBtn", AeHandler.acceptPropertyEdit);
    
    // setup object function prototypes
    if (typeof(_AePropertyEdit_prototype_called) == 'undefined')
    {
        _AePropertyEdit_prototype_called = true;
        
        // Open property for edit
        AePropertyEdit.prototype.begin = function(aProperty)
    	{  	   
            $('#editName').val(aProperty.getName());
            $('#editValue').val(aProperty.getValue());
            $('#editPropertyForm :input')[0].focus();
        } 
    }
}


// Button object
function AeButton(aButtonId, aEvtHandler)
{
    this.mButton = '#' + aButtonId;
    
    // setup object function prototypes
    if (typeof(_AeButton_prototype_called) == 'undefined')
    {
        _AeButton_prototype_called = true;
        
        AeButton.prototype.register = function(aEvtHandler)
        {
            if (AeUtil.isNotNullOrUndefined(aEvtHandler))
            {
                var btnEle = $(this.mButton);
                btnEle.unbind( "click" );
                btnEle.click(function(){
                    aEvtHandler();
                    return false;
	            });
	        }
        };
        
        // Enable Button
        AeButton.prototype.enable = function()
        {
            $(this.mButton).attr("disabled", "");
        };
        
        // Disable button
        AeButton.prototype.disable = function()
        {
           $(this.mButton).attr("disabled", "disabled");
        };
    }
    
    this.register(aEvtHandler);
}

// Static handler's constructor
function AeHandler() {};

// Post attachment to remote ending handler
AeHandler.postAttachment = function()
{
   if (mControl.location.valid())
   {
       // Setup form and submit
       $('#processid').val(mPid);
       $('#variablexpath').val(mPath);
       $('#xmloutput').val( mControl.serialize());
       $('#filepath').val(mControl.location.getFilePath());
       $('#attachmentAcceptForm').submit();
       mControl.wait();
   }
   else
   {
      AeUtil.showErrorMessage(mInvalidFileMsg);
      mControl.openLocation();
   }
};

// Close add attachment modal window 
AeHandler.closeAttachmentWindow = function()
{
    mControl.refreshPropertyView();
    mControl = null;
    window.close();
};

// Accept the attachment file path location handler
AeHandler.acceptLocation = function()
{
     mControl.closeLocation(); 
    if (mControl.location.valid())
    {
      mControl.property.clear();
      mControl.property.setName("Content-Location");
     
      var re = mControl.location.getFilePath().match(/(.*)[\/\\]([^\/\\]+)$/)
      mControl.property.setValue(re[2]);
      mControl.propertyView.accept(mControl.property);
    }
    else
    {
      mControl.openLocation();
    }
   return false;
}

// Register property check box selections
AeHandler.checkBoxRegister = function()
{
   mControl.propertyView.register();
};

// Add a new property event handler
AeHandler.newProperty = function()
{
   if (mControl.location.valid() == true)
   {
       mControl.property.clear();
       mControl.openEdit();
   }
   else
   {
       mControl.openLocation();
   }
}

 // Prepare and submit an attribute for edit
AeHandler.editProperty = function()
{
    var idx = $('.property input[@type="checkbox"][@checked]').attr("value");
    var thisProperty = $('tr.property input[@type="checkbox"][@checked]').parent();   

    mControl.property.clear();
    mControl.property.setEdit(true);
    mControl.property.setSelection(idx);
    mControl.property.setName($(thisProperty).siblings('.propertyname').text());
    mControl.property.setValue($(thisProperty).siblings('.propertyvalue').text());
   
    mControl.openEdit();
}

// Delete property handler
AeHandler.deleteProperty = function()
{
    var answer = confirm(mAttributeConfirmDelete);
	if(answer)
	{
       mControl.propertyView.remove();
    }
   
};

// Abort editing of a property handler
AeHandler.cancelPropertyEdit = function()
{
   mControl.closeEdit();
};

// Accept property edit handler
AeHandler.acceptPropertyEdit = function()
{
   mControl.acceptEdit();
};
