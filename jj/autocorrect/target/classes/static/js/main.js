var mainApp = angular.module('mainApp', [])

.controller('mainControl', ['$scope', '$http',
	function($scope, $http) {
		var autocorrect = function() {
			var phrase = $scope.phrase;

			// validate empty string
			if (!phrase || phrase.length === 0) {
				$scope.suggestions = [];
				return;
			}
			// validate is user is still typing
			if (/[\s]+$/i.test(phrase)) {
				$scope.suggestions = [phrase];
				return;
			}

			// send API get request
			var apiUrl = '/autocorrect/' + phrase + '/';

			console.log(apiUrl);

			$http({
				method : 'GET',
				url : apiUrl
			}).success(function(data) {
				console.log(data.suggestions);

				$scope.suggestions = data.suggestions;
			});
		};

		$scope.$watch("phrase", autocorrect);
	}]);