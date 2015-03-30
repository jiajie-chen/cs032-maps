<#assign content>
<div id="wrapper">

<div id="header" class="colcell">
	<center><h1>
		Bacon
	</h1></center>
</div>

<div id="content">
	<div id="col1">
		<form novalidate name="form1" class="colcell">
			<h3>Starting Actor</h3>
			<input type="text" name="input1" ng-model="input1"/>
			<div ng-repeat="s in suggest1 track by $index">
				<input type="radio" name="actor1" ng-value="s" ng-model="actor1"/>
				{{s}}
			</div>
		</form>

		<form novalidate name="form2" class="colcell">
			<h3>Ending Actor</h3>
			<input type="text" name="input2" ng-model="input2"/>
			<div ng-repeat="s in suggest2 track by $index">
				<input type="radio" name="actor2" ng-value="s" ng-model="actor2"/>
				{{s}}
			</div>
		</form>

		<form novalidate name="form2" class="colcell">
			<button type="submit" class="btn btn-success btn-lg btn-block mybtn" ng-disabled="!validForm(actor1, actor2)">Search</button>
		</form>
	</div>

	<div id="col2">
		<div class="colcell left">
		</div>
	</div>
</div>

</div>
</#assign>
<#include "main.ftl">
