 $(document).ready(function() {
    var sb = document.getElementById("start");
    var eb = document.getElementById("end");

    console.log("Script started!");

    sb.addEventListener("keyup", function(event) {
      var text = document.getElementById("start").value;

      var postParameters = {inputStart: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.startSuggestions;

        displayStart(suggestionsList);
      });
    });

    eb.addEventListener("keyup", function(event) {
      var text = document.getElementById("end").value;

      var postParameters = {inputEnd: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.endSuggestions;

        displayEnd(suggestionsList);
      });
    });

  function displayStart(suggestionsList) {
    // var hide = $("#hide").is(":checked");
    if (suggestionsList == null || suggestionsList.length == 0) {
      document.getElementById('startList').innerHTML = "";
      return;
    }

    var options = "";

    for (var i = 0; i < suggestionsList.length; i++) {
      options += '<option value="' + suggestionsList[i] + '" />';
    }

    document.getElementById('startList').innerHTML = options;
  }

  function displayEnd(suggestionsList) {
    // var hide = $("#hide").is(":checked");
    if (suggestionsList == null || suggestionsList.length == 0) {
      document.getElementById('endList').innerHTML = "";
      return;
    }

    var options = "";

    for (var i = 0; i < suggestionsList.length; i++) {
      options += '<option value="' + suggestionsList[i] + '" />';
    }

    document.getElementById('endList').innerHTML = options;
  }

});