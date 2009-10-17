      <div style="margin:10px">
         <ul class="tabnav">
            <ae:IfPropertyMatches name="graphBean" property="partId" value="0" classType="int" >
               <li class="seltab"><span>&nbsp; <ae:GetResource name="process_graph_tab_process" /> &nbsp;</span></li>
               <ae:IfTrue name="graphBean" property="hasFaultHandlers" >
                  <li><a href="javascript:showTab('1');">&nbsp; <ae:GetResource name="process_graph_tab_faults" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasEventHandlers" >
                  <li><a href="javascript:showTab('2')">&nbsp; <ae:GetResource name="process_graph_tab_events" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasCompensationHandler" >
                  <li><a href="javascript:showTab('3')">&nbsp; <ae:GetResource name="process_graph_tab_compensate" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasTerminationHandler" >
                  <li><a href="javascript:showTab('4')">&nbsp; <ae:GetResource name="process_graph_tab_termination" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li><a href="javascript:showTab('5')">&nbsp; <ae:GetResource name="process_graph_tab_bpel" /> &nbsp;</a></li>
            </ae:IfPropertyMatches>
            <ae:IfPropertyMatches name="graphBean" property="partId" value="1" classType="int" >
               <li><a href="javascript:showTab('0')">&nbsp; <ae:GetResource name="process_graph_tab_process" /> &nbsp;</a></li>
               <li class="seltab"><span>&nbsp; <ae:GetResource name="process_graph_tab_faults" /> &nbsp;</span></li>
               <ae:IfTrue name="graphBean" property="hasEventHandlers" >
                  <li><a href="javascript:showTab('2')">&nbsp; <ae:GetResource name="process_graph_tab_events" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasCompensationHandler" >
                  <li><a href="javascript:showTab('3')">&nbsp; <ae:GetResource name="process_graph_tab_compensate" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasTerminationHandler" >
                  <li><a href="javascript:showTab('4')">&nbsp; <ae:GetResource name="process_graph_tab_termination" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li><a href="javascript:showTab('5')">&nbsp; <ae:GetResource name="process_graph_tab_bpel" /> &nbsp;</a></li>
            </ae:IfPropertyMatches>
            <ae:IfPropertyMatches name="graphBean" property="partId" value="2" classType="int" >
               <li><a href="javascript:showTab('0')">&nbsp; <ae:GetResource name="process_graph_tab_process" /> &nbsp;</a></li>
               <ae:IfTrue name="graphBean" property="hasFaultHandlers" >
                  <li><a href="javascript:showTab('1')">&nbsp; <ae:GetResource name="process_graph_tab_faults" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li class="seltab"><span>&nbsp; <ae:GetResource name="process_graph_tab_events" /> &nbsp;</span></li>
               <ae:IfTrue name="graphBean" property="hasCompensationHandler" >
                  <li><a href="javascript:showTab('3')">&nbsp; <ae:GetResource name="process_graph_tab_compensate" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasTerminationHandler" >
                  <li><a href="javascript:showTab('4')">&nbsp; <ae:GetResource name="process_graph_tab_termination" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li><a href="javascript:showTab('5')">&nbsp; <ae:GetResource name="process_graph_tab_bpel" /> &nbsp;</a></li>
            </ae:IfPropertyMatches>
            <ae:IfPropertyMatches name="graphBean" property="partId" value="3" classType="int" >
               <li><a href="javascript:showTab('0')">&nbsp; <ae:GetResource name="process_graph_tab_process" /> &nbsp;</a></li>
               <ae:IfTrue name="graphBean" property="hasFaultHandlers" >
                  <li><a href="javascript:showTab('1')">&nbsp; <ae:GetResource name="process_graph_tab_faults" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasEventHandlers" >
                  <li><a href="javascript:showTab('2')">&nbsp; <ae:GetResource name="process_graph_tab_events" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li class="seltab"><span>&nbsp; <ae:GetResource name="process_graph_tab_compensate" /> &nbsp;</span></li>
               <ae:IfTrue name="graphBean" property="hasTerminationHandler" >
                  <li><a href="javascript:showTab('4')">&nbsp; <ae:GetResource name="process_graph_tab_termination" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li><a href="javascript:showTab('5')">&nbsp; <ae:GetResource name="process_graph_tab_bpel" /> &nbsp;</a></li>
            </ae:IfPropertyMatches>
            <ae:IfPropertyMatches name="graphBean" property="partId" value="4" classType="int" >
               <li><a href="javascript:showTab('0')">&nbsp; <ae:GetResource name="process_graph_tab_process" /> &nbsp;</a></li>
               <ae:IfTrue name="graphBean" property="hasFaultHandlers" >
                  <li><a href="javascript:showTab('1');">&nbsp; <ae:GetResource name="process_graph_tab_faults" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasEventHandlers" >
                  <li><a href="javascript:showTab('2')">&nbsp; <ae:GetResource name="process_graph_tab_events" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasCompensationHandler" >
                  <li><a href="javascript:showTab('3')">&nbsp; <ae:GetResource name="process_graph_tab_compensate" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li class="seltab"><span>&nbsp; <ae:GetResource name="process_graph_tab_termination" /> &nbsp;</span></li>
               <li><a href="javascript:showTab('5')">&nbsp; <ae:GetResource name="process_graph_tab_bpel" /> &nbsp;</a></li>
            </ae:IfPropertyMatches>
            <ae:IfPropertyMatches name="graphBean" property="partId" value="5" classType="int" >
               <li><a href="javascript:showTab('0')">&nbsp; <ae:GetResource name="process_graph_tab_process" /> &nbsp;</a></li>
               <ae:IfTrue name="graphBean" property="hasFaultHandlers" >
                  <li><a href="javascript:showTab('1');">&nbsp; <ae:GetResource name="process_graph_tab_faults" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasEventHandlers" >
                  <li><a href="javascript:showTab('2')">&nbsp; <ae:GetResource name="process_graph_tab_events" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasCompensationHandler" >
                  <li><a href="javascript:showTab('3')">&nbsp; <ae:GetResource name="process_graph_tab_compensate" /> &nbsp;</a></li>
               </ae:IfTrue>
               <ae:IfTrue name="graphBean" property="hasTerminationHandler" >
                  <li><a href="javascript:showTab('4')">&nbsp; <ae:GetResource name="process_graph_tab_termination" /> &nbsp;</a></li>
               </ae:IfTrue>
               <li class="seltab"><span>&nbsp; <ae:GetResource name="process_graph_tab_bpel" /> &nbsp;</span></li>
            </ae:IfPropertyMatches>
         </ul>
         <div class="tabcontainer" id="tabimgcontainer">
				<ae:IfParamMatches property="part" value="5">
					<div style="padding:5px;margin:5px 0 0 0;">
			          <br/>
			          <textarea  style="width:100%; height=100%" rows="20" wrap="off" readonly="readonly"><jsp:getProperty name="graphBean" property="bpelXmlSource" /></textarea>
					</div>
				</ae:IfParamMatches>
				<ae:IfParamNotMatches property="part" value="5">
	            <ae:IfTrue name="graphBean" property="hasImage" >
	               <div id="aeprocessdiv">
		               <img class="aeimageoverlay" id="aeimgoverlay" src="../images/processview/trans10x10.gif" usemap="#bpelgraph-image-map" />
		               <map id="bpelgraph-image-map" name="bpelgraph-image-map">
	                  <jsp:getProperty name="graphBean" property="graphImageMapArea" />
	   	            </map>
	      	         <span id="graphselection" class="gsoff" onClick="onSelectionClick();"></span>
	         	      <span id="currselection" class="gsoff"></span>
		            </div>
	            </ae:IfTrue>
	            <ae:IfFalse name="graphBean" property="hasImage" >
	               <div class="gsstatus">
	                  Graph Not Available.
	               </div>
	            </ae:IfFalse>
				</ae:IfParamNotMatches>
         </div>
      </div>
