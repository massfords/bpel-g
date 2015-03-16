## Comparison ##

The following is a quick comparison between bpel-g and other BPEL engines. I'll add more engines as I become familiar with them as well as more criteria.

<table border='1'>
<blockquote><tr border='1'><th align='center'>Project Details</th></tr>
<tr>
<blockquote><th>Feature</th>
<th>bpel-g 5.1</th>
<th>ActiveBPEL 5.0.2</th>
<th>Apache ODE 1.x</th>
<th>Apache ODE 2.0</th>
</blockquote></tr>
<tr>
<blockquote><td>Licensing</td>
<td> GPL</td>
<td> GPL</td>
<td> Apache</td>
<td> Apache</td>
</blockquote></tr>
<tr>
<blockquote><td> Developer community</td>
<td> barely active</td>
<td> dormant</td>
<td> very active</td>
<td> very active</td>
</blockquote></tr>
<tr>
<blockquote><td> Stable</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> Build</td>
<td> Maven2</td>
<td> ANT</td>
<td> Ruby/buildr</td>
<td> Ruby/buildr</td>
</blockquote></tr>
<tr>
<blockquote><td> WAR Packaging</td>
<td> yes</td>
<td> yes</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> JBI Packaging</td>
<td> yes</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr border='1'>
<blockquote><th align='center'>Spec Compliance</th>
</blockquote></tr>
<tr>
<blockquote><th>Feature</th>
<th>bpel-g 5.1</th>
<th>ActiveBPEL 5.0.2</th>
<th>Apache ODE 1.x</th>
<th>Apache ODE 2.0</th>
</blockquote></tr>
<tr>
<blockquote><td> Inline Variable Initialization</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> Element style variables for web service activities</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> fromPart/toPart for web service activities</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> no variable for empty messages</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> BPEL faults</td>
<td> yes</td>
<td> yes</td>
<td> no conflictingReceive/conflictingRequest</td>
<td> no conflictingReceive/conflictingRequest</td>
</blockquote></tr>
<tr>
<blockquote><td> multi-start processes</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> atomic assigns</td>
<td> yes</td>
<td> yes</td>
<td> no (copy operations are atomic)</td>
<td> no (copy operations are atomic)</td>
</blockquote></tr>
<tr>
<blockquote><td> explicit variable validation</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> optional implicit message validation</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
<tr>
<blockquote><td> isolated scopes</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> yes</td>
</blockquote></tr>
<tr border='1'>
<blockquote><th align='center'>Extensions</th>
</blockquote></tr>
<tr>
<blockquote><th>Feature</th>
<th>bpel-g 5.1</th>
<th>ActiveBPEL 5.0.2</th>
<th>Apache ODE 1.x</th>
<th>Apache ODE 2.0</th>
</blockquote></tr>
<tr>
<blockquote><td> XQuery expression language</td>
<td> yes</td>
<td> yes</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> XSLT 2.0</td>
<td> yes</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> HTTP Bindings</td>
<td> no</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> REST</td>
<td> no</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> Access to message headers</td>
<td> no</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> Engine correlation</td>
<td> yes, with standard WS-Addressing</td>
<td> yes, with standard WS-Addressing</td>
<td> yes, with custom WS-Addressing headers</td>
<td> yes, with custom WS-Addressing headers</td>
</blockquote></tr>
<tr>
<blockquote><td> Sub-process</td>
<td> yes</td>
<td> yes</td>
<td> process-process communication, but no lifecycle</td>
<td> process-process communication, but no lifecycle</td>
</blockquote></tr>
<tr border='1'>
<blockquote><th align='center'> Tools</th>
</blockquote></tr>
<tr>
<blockquote><th>Feature</th>
<th>bpel-g 5.1</th>
<th>ActiveBPEL 5.0.2</th>
<th>Apache ODE 1.x</th>
<th>Apache ODE 2.0</th>
</blockquote></tr>
<tr>
<blockquote><td> Admin Console</td>
<td> extensive, with visual rendering of deployed and running/completed processes.</td>
<td> extensive, with visual rendering of deployed and running/completed processes.</td>
<td> partial, basic listings only</td>
<td> unknown</td>
</blockquote></tr>
<tr>
<blockquote><td> Static Analysis</td>
<td> extensive, most errors reported at deployment time</td>
<td> extensive, most errors reported at deployment time</td>
<td> some, but errors still make it through to runtime</td>
<td> unknown</td>
</blockquote></tr>
<tr>
<blockquote><td> Standalone Compiler</td>
<td> none</td>
<td> none apart from commercial design tool</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr border='1'>
<blockquote><th align='center'>API</th>
</blockquote></tr>
<tr>
<blockquote><th>Feature</th>
<th>bpel-g 5.1</th>
<th>ActiveBPEL 5.0.2</th>
<th>Apache ODE 1.x</th>
<th>Apache ODE 2.0</th>
</blockquote></tr>
<tr>
<blockquote><td> Web Service API</td>
<td> yes</td>
<td> yes</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> Process events</td>
<td> planned</td>
<td> not documented</td>
<td> yes with optional filtering in deployment artifact</td>
<td> yes with optional filtering in deployment artifact</td>
</blockquote></tr>
<tr border='1'>
<blockquote><th align='center'> WS-<code>*</code></th>
</blockquote></tr>
<tr>
<blockquote><th>Feature</th>
<th>bpel-g 5.1</th>
<th>ActiveBPEL 5.0.2</th>
<th>Apache ODE 1.x</th>
<th>Apache ODE 2.0</th>
</blockquote></tr>
<tr>
<blockquote><td> WS-Security</td>
<td> yes (w/in SMX)</td>
<td> no</td>
<td> yes</td>
<td> yes</td>
</blockquote></tr>
<tr>
<blockquote><td> WS-RM</td>
<td> yes (w/in SMX)</td>
<td> no</td>
<td> yes (w/in SMX)</td>
<td> ?</td>
</blockquote></tr>
<tr>
<blockquote><td> WS-Addressing ReplyTo</td>
<td> yes</td>
<td> yes</td>
<td> no</td>
<td> no</td>
</blockquote></tr>
</table>