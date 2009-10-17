<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<head>
   <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
   <title>Date Chooser</title>
   <style type="text/css">

      body
      {
         font-size: 1.0em;
      }

      #aedatetimechooser
      {
         padding: 1px;
         margin: 1px;
      }
      #caltable
      {
         text-align:center;
         padding:0px;
         margin:0px;
      }
      #aedttable
      {
         padding: 2px;
      }

      #aedtcalendar
      {
         text-align:center;
         padding: 2px;
      }

      #aedttime
      {
         padding: 2px;
      }

      #aedtcurrselectioninfo
      {
         font-size: 0.75em;
         color: #006;
      }

      select, button, input
      {
         font-size: 0.75em;
      }

      #aedtbuttons
      {
         padding: 2px;
         border: 1px dotted #888
      }

      .aeweekname
      {
         text-align:center;
         font-size: 0.8em;
         padding: 2px;
         border-bottom: 1px solid #000;
         margin-botton: 1px;
      }

      #aedatetimechooser a, #aedatetimechooser a:active, #aedatetimechooser a:hover, #aedatetimechooser a:visited
      {
         display:block;
         color: #000;
         text-decoration: none;
      }

      .aedaycell
      {
         text-align:center;
         background: #fff;
      }

      .aeday
      {
         padding: 1px;
         color: #000;
         border:1px solid #ccc;
      }

      .aetoday
      {
         padding: 2px;
         color: #000;
         background: #ff6;
      }

      .aedaysel
      {
         padding: 1px;
         color: #000;
         background: #cff;
         border:1px solid #009;
      }

      #aedatetimechooser a.aeday:hover, #aedatetimechooser a.aedaysel:hover, #aedatetimechooser a.aetoday:hover
      {
         padding: 1px;
         display:block;
         color: #009;
         border:1px solid #009;
      }
   </style>

   <script language="javascript">

      var amLabel = "<ae:GetResource name="am" />";
      var pmLabel = "<ae:GetResource name="pm" />";
      var daysInMonth = new Array();
      var weekdayNames = new Array();
      var monthNames = new Array();
      var currYear = 0;
      var currMonth = 0;
      var currDay = 0;
      var currDayCellId = -1;


      function log(s)
      {
         d = document.getElementById('debuginfo');
         if (d) {
            d.innerHTML  = d.innerHTML  + "<br/>" + s;
         }
      }

      function logDate()
      {
         log("Curr d=" + currDay + "; m=" + currMonth + "; y=" + currYear);
      }

      function init()
      {
         initDaysInMonth();
         initWeekdayNames();
         initMonthNames();
         initControls();
         clearTime();
         setToday();
         checkEnableTime();
         checkEnableCalendar();
         updateCalendarControlsUi();
      }
      
      function checkEnableCalendar()
      {
         var s = "<%= request.getParameter("calendar")%>";
         if (s && s == "no")
         {
            hideControl('aecalendarcontrol');
            hideControl('aecalendarnavcontrol1');
            hideControl('aecalendarnavcontrol2');
            hideControl('aedtcurrselection');
         }
      }
            
      function checkEnableTime()
      {
         var s = "<%= request.getParameter("time")%>";
         if (s && s == "no")
         {
            hideControl('aetimecontrol');
         }
      }

      function hideControl(controlId)
      {
         d = document.getElementById(controlId);
         if (d)
         {
            d.style.display = "none";
         }
      }
      
      function initControls()
      {
         // initialize months.
         var ctrl = getMonthControl();
         ctrl.options.length = monthNames.length;
         for (idx = 0; idx < monthNames.length; idx++)
         {
            ctrl.options[idx].value = idx;
            ctrl.options[idx].innerHTML = monthNames[idx];
         }
         var val = "";
         // hour
         ctrl = getHourControl();
         ctrl.options.length = 14;
         ctrl.options[0].value = -1;
         ctrl.options[0].innerHTML = "--";
         for (i = 1; i < 14; i++)
         {
            idx = i - 1
            ctrl.options[i].value = idx;
            val = (idx < 10) ? "0" + idx : "" + idx;
            ctrl.options[i].innerHTML = val;
         }
         // min
         ctrl = getMinuteControl();
         ctrl.options.length = 61;
         ctrl.options[0].value = -1;
         ctrl.options[0].innerHTML = "--";

         for (i = 1; i < 61; i++)
         {
            idx = i -1
            ctrl.options[i].value = idx;
            val = (idx < 10) ? "0" + idx : "" + idx;
            ctrl.options[i].innerHTML = val;
         }
         // am pm
         ctrl = getAmPmControl();
         ctrl.options.length = 2;
         ctrl.options[0].value = 0;
         ctrl.options[0].innerHTML = amLabel;
         ctrl.options[1].value = 1;
         ctrl.options[1].innerHTML = pmLabel;
      }


      function initDaysInMonth()
      {
         daysInMonth[0] = 31;
         daysInMonth[1] = 28;
         daysInMonth[2] = 31;
         daysInMonth[3] = 30;
         daysInMonth[4] = 31;
         daysInMonth[5] = 30;
         daysInMonth[6] = 31;
         daysInMonth[7] = 31;
         daysInMonth[8] = 30;
         daysInMonth[9] = 31;
         daysInMonth[10] = 30;
         daysInMonth[11] = 31;
      }

      function initWeekdayNames()
      {
         weekdayNames[0] = "<ae:GetResource name="su" />";
         weekdayNames[1] = "<ae:GetResource name="mo" />";
         weekdayNames[2] = "<ae:GetResource name="tu" />";
         weekdayNames[3] = "<ae:GetResource name="we" />";
         weekdayNames[4] = "<ae:GetResource name="th" />";
         weekdayNames[5] = "<ae:GetResource name="fr" />";
         weekdayNames[6] = "<ae:GetResource name="sa" />";
      }

      function initMonthNames()
      {
         monthNames[0] = "<ae:GetResource name="january" />";
         monthNames[1] = "<ae:GetResource name="february" />";
         monthNames[2] = "<ae:GetResource name="march" />";
         monthNames[3] = "<ae:GetResource name="april" />";
         monthNames[4] = "<ae:GetResource name="may" />";
         monthNames[5] = "<ae:GetResource name="june" />";
         monthNames[6] = "<ae:GetResource name="july" />";
         monthNames[7] = "<ae:GetResource name="august" />";
         monthNames[8] = "<ae:GetResource name="september" />";
         monthNames[9] = "<ae:GetResource name="october" />";
         monthNames[10] = "<ae:GetResource name="november" />";
         monthNames[11] = "<ae:GetResource name="december" />";
      }

      function setToday()
      {
         var now   = new Date();
         var day   = now.getDate();
         var month = now.getMonth(); // 0 based index
         var year  = now.getFullYear();
         setDate(day, month, year);
      }

      function setNow()
      {
         var now   = new Date();
         updateTimeControlUi( get12Hour(now.getHours()), now.getMinutes(), isAm(now.getHours()) );
      }

      function clearTime()
      {
         updateTimeControlUi(-1,-1,true);
      }

      function get12Hour(hour24)
      {
         var hour12;
         if (hour24 > 12)
         {
            hour12 = hour24 - 12;
         }
         else
         {
            hour12 = hour24;
         }
         return hour12;
      }

      function isAm(hour24)
      {
         if (hour24 < 12)
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      function setDate(aDay, aMonth, aYear)
      {
         // value of -1 = no change.
         if (aDay != -1)
         {
            currDay = aDay;
         }
         if (aMonth != -1)
         {
            currMonth = aMonth;
         }
         if (aYear != -1)
         {
            currYear = aYear;
         }
         updateCalendarUi(currDay, currMonth, currYear);
         updateSelectionUi();
      }

      function getCurrentDay()
      {
         return currDay;
      }

      function setCurrentDay(aDay)
      {
         if (aDay == currDay)
         {
            return;
         }
         var todayDay = getTodayDay(currMonth, currYear);
         // revert previous selection css
         var clazzName = (todayDay == currDay) ? "aetoday" : "aeday";
         applyDayClassName(currDay, clazzName);
         // apply new selection css.
         clazzName = "aedaysel"
         currDay = aDay;
         applyDayClassName(currDay, clazzName);
      }

      function applyDayClassName(aDay, clazzName)
      {
         var ele = document.getElementById('aedayid' + aDay);
         if (ele != null)
         {
            ele.className = clazzName;
         }
      }

      function updateCalendarControlsUi()
      {
         updateMonthControlUi();
         updateYearControlUi();
      }

      function updateMonthControlUi()
      {
         // month
         var ctrl = getMonthControl();
         ctrl.selectedIndex = currMonth;
      }

      function updateYearControlUi()
      {
         // year
         getYearControl().value = "" + currYear;
      }

      function formatDoubleDigit(aNum)
      {
         if (aNum < 10)
         {
            return "0" + aNum;
         }
         else
         {
            return "" + aNum;
         }
      }

      function getCurrentDateSelection()
      {
         // returns date as a string.
         // date
         var yStr = "" + currYear;
         var mStr = formatDoubleDigit( parseInt(currMonth) + 1 );
         var dStr = formatDoubleDigit( parseInt(currDay) );
         var s = yStr + "/" + mStr + "/" + dStr;
         return s;
      }

      function getCurrentTimeSelection()
      {
         // returns time as a string or null if time is not selected.
         // time
         var hour = getHourFromUi(); // 0 - 23
         var min = getMinuteFromUi();
         var s = null;
         if (hour != -1 && min != -1)
         {
            s = "";
            s = s + formatDoubleDigit( get12Hour(hour) ) + ":";
            s = s + formatDoubleDigit(min) + " ";
            s = s + ( isAm(hour) ? amLabel : pmLabel );
         }
         return s;
      }

      function updateSelectionUi()
      {
         // date
         var s = getCurrentDateSelection();
         // time
         var t = getCurrentTimeSelection();
         if (t != null)
         {
            s = s + " " + t;
         }
         d = document.getElementById('aedtcurrselectioninfo');
         if (d) {
            d.innerHTML  = s;
         }

      }

      function getHourFromUi()
      {
         // return current selection from UI as a 24 hour time. (0-23)
         var ctrl = getHourControl();
         var rVal = -1;
         if (ctrl.selectedIndex > 0)
         {
            // 0 - 12
            var h = parseInt(ctrl.options[ctrl.selectedIndex].value)
            ctrl = getAmPmControl();
            var pm = (ctrl.selectedIndex == 1) ? true : false;
            if (pm && h != 12)
            {
               h = h + 12;
            }
            rVal = h;
         }
         return rVal;
      }

      function getMinuteFromUi()
      {
         // return current selection from UI  (0-59)
         var ctrl = getMinuteControl();
         var rVal = -1;
         if (ctrl.selectedIndex > 0)
         {
            // 0 - 59
            rVal = parseInt(ctrl.options[ctrl.selectedIndex].value)
         }
         return rVal;

      }

      function updateTimeControlUi(h,m, am)
      {
         var ctrl = getHourControl();
         if (h >=0 && h <= 12)
         {
            ctrl.selectedIndex = h + 1;
         }
         else
         {
            ctrl.selectedIndex = 0;
         }

         ctrl = getMinuteControl();
         if (m >=0 && m <= 59)
         {

            ctrl.selectedIndex = m + 1;
         }
         else
         {
            ctrl.selectedIndex = 0;
         }
         ctrl = getAmPmControl();
         ctrl.selectedIndex = am ? 0 : 1;
      }

      function updateCalendarUi(aDay, aMonth, aYear)
      {
         var todayDay = getTodayDay(aMonth, aYear); // set actual date if today day should be highlighted, else set to -1.

         var nDays = getDaysInMonth(aMonth, aYear);
         var fomDate = new Date(aYear, aMonth, 1);
         var firstDay = (new Date(aYear, aMonth, 1)).getDay(); // 0 based index
         var lastDay = firstDay + nDays - 1;
         //log("date=" + aYear + "/" + aMonth + "; nDays=" + nDays + "; 1st=" + firstDay + "; last=" + lastDay + "; td=" + todayDay);
         var i = 0;
         var selectedDay = getCurrentDay();
         var clazzName = "";
         var html = "";
         html += '<table id="caltable" border="0" cellpadding="1" cellspacing="0" >';
         // weekday names
         html += "<tr>";
         for (i = 0; i < weekdayNames.length; i++)
         {
            html += "<td class=\"aeweekname\">" + weekdayNames[i] + "</td>";
         }
         html += "</tr>";
         // calendar

         for (i = 0; i < 42; i++)
         {
            if (i % 7 == 0 )
            {
               html += "<tr>";
            }

            if (i >= firstDay && i <= lastDay)
            {
               var d = i + 1 - firstDay
               html += '<td class="aedaycell" >';

               clazzName = (d == todayDay) ? "aetoday" : (d == selectedDay) ? "aedaysel" : "aeday";
               html += '<a  class="' + clazzName + '" id="aedayid' + d + '" href="javascript:void();" onclick="onDayClick(' + d + '); return false;" ondblclick="onDayDblClick(' + d + ');; return false;" >';
               html +=  d;
               html += "</a>";
               html += "</td>";
            }
            else
            {
               html += "<td>";
               html +=  "&nbsp;";
               html += "</td>";
            }

            if (i % 7 == 6 )
            {
               html += "</tr>";
            }

         }
         // reset prev day cell.
         prevDayCell = null;

         html += "</table>";
         d = document.getElementById('aedtcalendar');
         if (d) {
            d.innerHTML  = "" + html;
         }
         // update year control value
         getYearControl().value = "" + aYear;
      }

      function onNow()
      {
         setNow();
         updateSelectionUi();
      }

      function onClearTime()
      {
         clearTime();
         updateSelectionUi();
      }


      function onToday()
      {
         setToday();
         var d = getCurrentDay();
         applyDayClassName(d, "aedaysel");
         updateSelectionUi();
      }

      function onYearChange()
      {
         var year = getYearFromUi();
         if (year != -1)
         {
            setDate(-1,-1, year);
         }
         else
         {
            reportYearError();
         }
      }

      function getYearFromUi()
      {
         // return the year from the text input field or -1 if invalid.
         var s = getYearControl().value;
         var year = -1;
         if (s && s != null && s.length == 4)
         {
            try
            {
               year = parseInt(s);
            }
            catch(e)
            {
            }
         }
         if (year != NaN && year >= 1900)
         {
            return year;
         }
         else
         {
            return -1;
         }
      }

      function reportYearError()
      {
         getYearControl().focus();
         getYearControl().select();
      }

      function handleSelectonControlDelta(aCtrl, aDelta)
      {
         //adds or substracts and returns the new selection control index.
         var idx = aCtrl.selectedIndex;
         idx = idx + aDelta;
         if (idx < 0)
         {
            idx = aCtrl.options.length - 1;
         }
         else if (idx >= aCtrl.options.length)
         {
            idx = 0;
         }
         aCtrl.selectedIndex = idx;
         return idx;
      }

      function onDeltaYear(aDelta)
      {
         var y = getYearFromUi();
         if (y != -1)
         {
             y = y + aDelta;
             if (y < 1900)
             {
                y = 1900;
             }
             else if (y > 2100)
             {
                y = 2100;
             }
             setDate(-1,-1, y);
         }
      }


      function onDeltaMonth(aDelta)
      {
         var ctrl = getMonthControl();
         var idx = handleSelectonControlDelta(ctrl, aDelta);
         var month = ctrl.options[idx].value;
         setDate(-1,month, -1);
      }

      function onMonthChange()
      {
         var ctrl = getMonthControl();
         var idx = ctrl.selectedIndex;
         var month = ctrl.options[idx].value
         setDate(-1,month, -1);
         //log("OnMonth " + idx + "; " + month);
      }

      function onTimeChange()
      {
         var hCtrl = getHourControl();
         var hIdx = hCtrl.selectedIndex;
         var mCtrl = getMinuteControl();
         var mIdx = mCtrl.selectedIndex;
         var amCtrl = getAmPmControl();
         var amIdx = amCtrl.selectedIndex;

         if (hIdx == 0 && mIdx > 0)
         {
            hCtrl.selectedIndex = 1;
         }
         else if (hIdx > 0 && mIdx == 0)
         {
            mCtrl.selectedIndex = 1;
         }

         if (hIdx == 1 && amIdx != 0)
         {
            // force am
            amCtrl.selectedIndex = 0;
         }
         if (hIdx == 13 && amIdx != 1)
         {
            // force pm
            amCtrl.selectedIndex = 1;
         }

         updateSelectionUi();

      }

      function onDayClick(aDay)
      {
         setCurrentDay(aDay);
         updateSelectionUi();
         //log("Click " + aDay);

      }

      function onDayDblClick(aDay)
      {
         //log("DoubleClick " + aDay);
         onOk();
      }

      function onOk()
      {
         if (window.opener && window.opener.aeCalendarCallback)
         {
            var year = parseInt(currYear);
            var month = parseInt(currMonth) + 1;
            var day = parseInt(currDay);
            var hour24 = getHourFromUi();
            var min = getMinuteFromUi();
            // date
            var dateStr = getCurrentDateSelection();
            // time
            var timeStr = getCurrentTimeSelection();

            window.opener.aeCalendarCallback(year, month, day, hour24, min, dateStr, timeStr)
         }
         window.close();
      }


      function onCancel()
      {
         window.close();
      }

      function getForm()
      {
         return document.aedtform;
      }

      function getMonthControl()
      {
         return getForm().aedtmonthcontrol;
      }

      function getYearControl()
      {
         return getForm().aedtyearcontrol;
      }

      function getHourControl()
      {
         return getForm().aedthourcontrol;
      }

      function getMinuteControl()
      {
         return getForm().aedtmincontrol;
      }

      function getAmPmControl()
      {
         return getForm().aedtampmcontrol;
      }



      function getTodayDay(aMonth, aYear)
      {
         // returns todays day if the current month and year is equal to now.
         // else returns -1.
         var now = new Date();
         if (now.getMonth() == aMonth && now.getFullYear() == aYear)
         {
            return now.getDate();
         }
         else
         {
            return -1;
         }
      }

      function getDaysInMonth(aMonth, aYear)  {
         // aMonth is 0 based index.
         var nDays = daysInMonth[aMonth];
         // check for leap year if month is Feb.
         if (aMonth == 1 && isLeapYear(aYear))
         {
            nDays = 29;
         }
         return nDays;
      }


      function isLeapYear(aYear)
      {
          if (((aYear % 4)==0) && ((aYear % 100)!=0) || ((aYear % 400)==0))
          {
            return true;
          }
          else
          {
            return false;
          }
      }

   </script>
</head>
<body onload="init();">
   <div id="aedatetimechooser">
      <form style="padding:0px; margin:0px" name="aedtform" onsubmit="return false;">
         <div id="aedtyearmonth">
            <table id="aedttable" width="100%" cellpadding="0" cellspacing="0" border="0">
            <tr id="aecalendarnavcontrol1">
               <td colspan="3">
                  <select style="width:100%" id="aedtmonthcontrol" name="month" onchange="onMonthChange();"></select>
               </td>
               <td colspan="2">
                  <input size="5" maxlength="4" type="text" style="width:95%"
                     id="aedtyearcontrol" name="year" onchange="onYearChange();"  />
               </td>
             </tr>
             <tr style="text-align:center" id="aecalendarnavcontrol2">
               <td><button name="aebtnprevyear" onclick="onDeltaYear(-1);">&lt;&lt;</button></td>
               <td><button name="aebtnprevmonth" onclick="onDeltaMonth(-1);">&nbsp;&lt&nbsp;</button></td>
               <td><button name="aebtntoday" onclick="onToday();"><ae:GetResource name="today" /></button></td>
               <td><button name="aebtnnextmonth"onclick="onDeltaMonth(1);">&nbsp;&gt;&nbsp;</button></td>
               <td><button name="aebtnnextyear" onclick="onDeltaYear(1);">&gt;&gt;</button></td>
             </tr>
             <tr  id="aecalendarcontrol">
               <td colspan="5" >
                  <div id="aedtcalendar">
                     calendar date selection
                  </div>
               </td>
             </tr>
             <tr width="100%" id="aetimecontrol" >
               <td colspan="3">
                  <select id="aedthourcontrol" name="hour" onchange="onTimeChange();">
                  </select>:<select id="aedtmincontrol" name="minute" onchange="onTimeChange();">
                  </select><select id="aedtampmcontrol" name="ampm" onchange="onTimeChange();"></select>
               </td>
               <td colspan="2">
                  <button name="aebtnnow" onclick="onNow();"><ae:GetResource name="now" /></button> <button name="aebtnclear" onclick="onClearTime();"><ae:GetResource name="clear" /></button>
               </td>
             </tr>
             <tr id="aedtcurrselection">
               <td colspan="2"><ae:GetResource name="selection" />:</td>
               <td colspan="3"><div id="aedtcurrselectioninfo"></div></td>
             </tr>

             <tr style="text-align:right;">
               <td colspan="5" style="padding-top:2px; border-top:1px solid #000;">
                  <button name="aebtnok" onclick="onOk();">&nbsp;<ae:GetResource name="ok" />&nbsp;</button>&nbsp;
                  <button name="aebtncancel" onclick="onCancel();">&nbsp;<ae:GetResource name="cancel" />&nbsp;</button>
               </td>
             </tr>
            </table>
         </div>
      </form>
   </div>
  </body>
</html>