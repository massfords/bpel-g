<!-- window centering scripts -->
<script type="text/javascript" language="JavaScript" src="script/ae_winutil.js"></script>
<script>

   <!-- begin calendar popup -->
   var aeCalendarCallbackField = null;
   var aeCalendarMode = true;
   var ae24ModeMode = false;

   function displayCalendar(aDateField)
   {
     aeCalendarMode = true;
     aeCalendarCallbackField  = aDateField;
     var calWin = aePopupModalWindow(self, 'calendar.jsp?time=no','AeCalendar', 255, 335, true,null);
     calWin.focus();
   }

   function displayTime(aDateField)
   {
     aeCalendarMode = false;
     aeCalendarCallbackField  = aDateField;
     var calWin = aePopupModalWindow(self, 'calendar.jsp?calendar=no','AeCalendar', 265, 120, true,null);
     calWin.focus();
   }

   function aeCalendarCallback(aYear, aMonth, aDay, aHour24, aMinute, aDateStr, aTimeStr)
   {
      if (aeCalendarCallbackField && aTimeStr != null && !aeCalendarMode)
      {
         aeCalendarCallbackField.value = aTimeStr;
      }   
      else if (aeCalendarCallbackField && aDateStr != null && aeCalendarMode)
      {
         aeCalendarCallbackField.value = aDateStr;
      }
   }
   <!-- end calendar popup -->

   function gotoLast( aTotalRows, aRowCount )
   {
      var lastRows = aTotalRows%aRowCount;
      if( lastRows == 0 )
      {
         lastRows = aRowCount;
      }
      document.forms["filterForm"].rowOffset.value=aTotalRows-lastRows;
      document.forms["filterForm"].submit();
   }
</script>
