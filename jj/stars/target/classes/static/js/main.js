var mainApp = angular.module('mainApp', [])

.controller('mainControl', ['$scope', '$http',
	function($scope, $http) {
		$scope.noError = true;

		$scope.validForm = function() {
			var search =
				($scope.searchType == 'neighbor' && $scope.command.k.$valid) ||
				($scope.searchType == 'radius' && $scope.command.r.$valid);
			var location =
				($scope.locationType == 'name' && $scope.location.starname.$valid) ||
				($scope.locationType == 'coord' 
					&& $scope.location.sx.$valid
					&& $scope.location.sy.$valid
					&& $scope.location.sz.$valid);
			return search && location;
		};

		$scope.submit = function() {
			// validate forms
			if (!$scope.validForm()) {
				$scope.noError = false;
				$scope.errMsg = "invalid form input";
				return;
			}

			// send API get request
			var base = $scope.locationType;
			var cmd = $scope.searchType;
			var apiUrl = '/' + base + '/' + cmd + '/';

			if (cmd == 'neighbor') {
				apiUrl += $scope.k;
			} else if (cmd == 'radius') {
				apiUrl += $scope.r;
			}

			apiUrl += '/'

			if (base == 'name') {
				apiUrl += $scope.starname;
			} else if (base == 'coord') {
				apiUrl += $scope.sx + '/' + $scope.sy + '/' + $scope.sz;
			}

			console.log(apiUrl);
			$http({
				method : 'GET',
				url : apiUrl
			}).success(function(data) {
				if (data.success) {
					$scope.noError = true;
					$scope.stars = data.stars;
				} else {
					$scope.noError = false;
					$scope.errMsg = data.error;
				}
			});
		};
	}])

.filter('validName',
	function($sce) {
		return function(name) {
			if (name == '') {
				return 'no-name';
			} else {
				return 'has-name';
			}
		};
	})

.filter('nameFilter',
	function() {
		return function(name) {
			if (name == '') {
				return 'no name';
			} else {
				return name;
			}
		};
	});