<#assign content>
<div id="wrapper">

<div id="header" class="colcell">
	<h1>
		<span class="glyphicon glyphicon-star"></span>
		Reach for the Stars!
	</h1>
</div>

<div id="content">
	<div id="col1">
		<form name="command" class="colcell" novalidate>
			<h3 title="Search by nearest-neighbor or radius">
				Search Type
			</h3>

			<label ng-init="searchType = 'neighbor'">
				<input type="radio" name="searchType" value="neighbor" ng-model="searchType"/>
				Neighbor
			</label>
			<label>
				<input type="radio" name="searchType" value="radius" ng-model="searchType"/>
				Radius
			</label>

			<div class="type-input" ng-show="searchType == 'neighbor'">
				<label>Number of Neighboring Stars</label><br/>
				<input type="text" name="k" ng-pattern="/^\d+$/" ng-model="k" ng-required="searchType == 'neighbor'"/>
			</div>

			<div class="type-input" ng-show="searchType == 'radius'">
				<label>Radius</label><br/>
				<input type="text" name="r" ng-pattern="/^\+?\d*\.?\d+$/" ng-model="r" ng-required="searchType == 'radius'"/>
			</div>
		</form>

		<form name="location" class="colcell" novalidate>
			<h3 title="Define search location by coordinates or star name">
				Location Type
			</h3>

			<label ng-init="locationType = 'coord'">
				<input type="radio" name="locationType" value="coord" ng-model="locationType"/>
				Coordinates
			</label>
			<label>
				<input type="radio" name="locationType" value="name" ng-model="locationType"/>
				Name
			</label>

			<br/>

			<div class="type-input" ng-show="locationType == 'name'">
				<label>Star Name</label><br/>
				<input type="text" name="starname" ng-model="starname" ng-required="locationType == 'name'"/>
			</div>

			<div class="type-input" ng-show="locationType == 'coord'">
				<label>Coordinates</label><br/>

				<label>X:</label>
				<input type="text" name="sx" ng-pattern="/^[\-\+]?\d*\.?\d+$/" ng-model="sx" ng-required="locationType == 'coord'"/><br/>

				<label>Y:</label>
				<input type="text" name="sy" ng-pattern="/^[\-\+]?\d*\.?\d+$/" ng-model="sy" ng-required="locationType == 'coord'"/><br/>

				<label>Z:</label>
				<input type="text" name="sz" ng-pattern="/^[\-\+]?\d*\.?\d+$/" ng-model="sz" ng-required="locationType == 'coord'"/>
			</div>
		</form>

			
		<form name="submitForm" class="colcell" ng-submit="submit();">
			<button type="submit" class="btn btn-success btn-lg btn-block mybtn" ng-disabled="!validForm()">Search the Skies</button>
		</form>
	</div>

	<div id="col2">
	<div class="colcell left">
		<h3>
			Results
		</h3>

		<table ng-show="noError">
			<tr class="table-head">
				<td>Star Name</td>
				<td>Star Id</td>
			</tr>
			<tr ng-repeat="s in stars">
				<td class="{{s.name | validName}}">{{s.name | nameFilter}}</td>
				<td>{{s.id}}</td>
			</tr>
		</table>

		<table ng-hide="noError">
			<tr class="error">
				<td>Error: {{errMsg}}</td>
			</tr>
		</table>
	</div>
	</div>
</div>

</div>
<sup id="footer">Database created from: ${db}</sup>
</#assign>
<#include "main.ftl">
