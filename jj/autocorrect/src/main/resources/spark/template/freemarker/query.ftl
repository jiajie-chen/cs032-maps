<#assign content>
<div id="wrapper">

<div id="header" class="cell">
	<center><h1 class="title">
		Autocorre<span class="insert-outer">k<span class="insert">c</span></span>t
	</h1></center>
</div>

<div id="content">
	<div id="row1" class="cell">
		<input type="text" name="phrase" ng-model="phrase" />
	</div>

	<div id="row2" class="cell">
		<table>
			<tr class="table-head">
				<td> <h3>Results</h3> </td>
			</tr>

			<tr ng-repeat="s in suggestions track by $index">
				<td>{{s}}</td>
			</tr>
		</table>
	</div>
</div>

</div>
<sup id="footer">Database created from: ${db}</sup>
</#assign>
<#include "main.ftl">
