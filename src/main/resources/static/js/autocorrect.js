var Autocorrect = function(inputID, listID) {
	this.inputElement = document.getElementById(inputID);
	this.listElement = document.getElementById(listID);

	this.displaySuggestions = function(suggestionsList) {
		this.listElement.innerHTML = "";

		if (suggestionsList == null || suggestionsList.length == 0) {
			return;
		}

		for (var i = 0; i < suggestionsList.length; i++) {
			var option = new Option("", suggestionsList[i]);
			this.listElement.appendChild(option);
		}

		return;
	}

	var _this = this; // for functions calls outside of this object (like in event handlers)
    this.inputElement.addEventListener("keyup", function(event) {
		var text = this.value;

		var postParameters = {inputStart: JSON.stringify(text)};
		console.log(postParameters);

		$.post("/suggestions", postParameters, function(responseJSON) {
			console.log("Received suggestions: " + responseJSON);
			responseObject = JSON.parse(responseJSON);
			var suggestionsList = responseObject.startSuggestions;

			_this.displaySuggestions(suggestionsList);
		});
	});
}