         <div id="processops">
            <form name="ec_form" method="post" action="processview_outline.jsp">
              <input type="hidden" name="pid" value='<%=request.getParameter("pid")%>'/>
              <input type="hidden" name="path" value=''/>
              <input type="hidden" name="ProcessAction" value="" />
             </form>
            <table id="tb20" border="0" cellpadding="1" cellspacing="1">
               <tr>
                  <td>
                      <!-- Suspend process button  -->
                     <ae:IfTrue name="treeViewBean" property="suspendable" >
                           <a href="javascript:procOp('Suspend');" class="button" id="btn_suspend1" title="<ae:GetResource name="suspend" />"><span class="icon">&nbsp;</span></a>
                     </ae:IfTrue>
                     <ae:IfFalse name="treeViewBean" property="suspendable" >
                           <span class="button_dis" id="btn_suspend0" title="<ae:GetResource name="suspend" />"><span class="icon">&nbsp;</span></span>
                     </ae:IfFalse>
                  </td>
                  <td>
                     <span class="tbsep">&nbsp;</span>
                  </td>
                  <td>
                     <ae:IfTrue name="treeViewBean" property="resumable" >
                           <a href="javascript:procOp('Resume');" class="button" id="btn_resume1" title="<ae:GetResource name="resume" />"><span class="icon">&nbsp;</span></a>
                     </ae:IfTrue>
                     <ae:IfFalse name="treeViewBean" property="resumable" >
                           <span class="button_dis" id="btn_resume0" title="<ae:GetResource name="resume" />"><span class="icon">&nbsp;</span></span>
                     </ae:IfFalse>
                  </td>
                  <td>
                     <span class="tbsep">&nbsp;</span>
                  </td>
                  <td>
                     <ae:IfTrue name="treeViewBean" property="terminatable" >
                           <a href="javascript:procOp('Terminate');" class="button" id="btn_term1" title="<ae:GetResource name="terminate" />"><span class="icon">&nbsp;</span></a>
                     </ae:IfTrue>
                     <ae:IfFalse name="treeViewBean" property="terminatable" >
                           <span class="button_dis" id="btn_term0" title="<ae:GetResource name="terminate" />"><span class="icon">&nbsp;</span></span>
                     </ae:IfFalse>
                  </td>
                  <td>
                     <span class="tbsep">&nbsp;</span>
                  </td>
                  <ae:IfTrue name="treeViewBean" property="restartEnabled">
                  <td>
                     <ae:IfTrue name="treeViewBean" property="restartable" >
                           <a href="javascript:procOp('Restart');" class="button" id="btn_restart1" title="<ae:GetResource name="restart" />"><span class="icon">&nbsp;</span></a>
                     </ae:IfTrue>
                     <ae:IfFalse name="treeViewBean" property="restartable" >
                           <span class="button_dis" id="btn_restart0" title="<ae:GetResource name="restart" />"><span class="icon">&nbsp;</span></span>
                     </ae:IfFalse>
                  </td>
                  <td>
                     <span class="tbsep">&nbsp;</span>
                  </td>
                  </ae:IfTrue>
                  <td>
                     <a target="aeprocesslog" href="processview_log.jsp?pid=<%= request.getParameter("pid") %>" class="button" id="btn_view1" title="<ae:GetResource name="viewlog" />"><span class="icon">&nbsp;</span></a>
                  </td>

               </tr>
            </table>
         </div>
