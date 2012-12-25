// Script to open the process view when the user types in the PID and presses the Go button.
// Requires the form name to be 'process_status_form'. This script is invoked by this
// form (located in header_nav.jsp).
function openProcessView()
{
   var pid = document.process_status_form.pid.value;
   if (pid && pid != '')
   {
      var url = 'processview/processview_detail.jsp?pid=' + pid
      window.open(url, "aeprocessview");
   }
   else
   {
      alert("Please enter Process ID.");
      if (document.process_status_form.pid)
      {
         document.process_status_form.pid.focus();
      }
   }
   return false;
}
