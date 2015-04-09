var mainApp = angular.module('mainApp', [])

.controller('mainControl', ['$scope', '$http',
	function($scope, $http) {
		var autocorrect = function(input, field, model) {
			var phrase = $scope[input];

			// validate empty string
			if (!phrase || phrase.length === 0) {
				return;
			}
			// validate is user is still typing
			if (/[\s]+$/i.test(phrase)) {
				return;
			}

			// send API get request
			var apiUrl = '/autocorrect/' + phrase + '/';

			//console.log(apiUrl);

			$http({
				method : 'GET',
				url : apiUrl
			}).success(function(data) {
				//console.log(data.suggestions);

				$scope[field] = data.suggestions;
			});

			$scope[model] = null;
		};

		$scope.$watch("input1", function() {
				autocorrect("input1", "suggest1", "actor1");
			});

		$scope.$watch("input2", function() {
				autocorrect("input2", "suggest2", "actor2");
			});

		$scope.validForm = function(actor1, actor2) {
			console.log(actor1);
			return actor1 != null && actor2 != null;
		}
	}]);