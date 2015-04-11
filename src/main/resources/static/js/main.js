 $(document).ready(function() {
  /*
    var sb = document.getElementById("start");
    var eb = document.getElementById("end");
    var scb = document.getElementById("sCross");
    var ecb = document.getElementById("eCross");
  */

    console.log("Script started!");

    var sb = new Autocorrect("start", "startList");
    var eb = new Autocorrect("end", "endList");
    var scb = new Autocorrect("sCross", "sCrossList");
    var ecb = new Autocorrect("eCross", "eCrossList");
    
  /*
    sb.addEventListener("keyup", function(event) {
      var text = sb.value;

      var postParameters = {inputStart: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.startSuggestions;

        displaySuggestions(suggestionsList, "startList");
      });
    });

    eb.addEventListener("keyup", function(event) {
      var text = eb.value;

      var postParameters = {inputEnd: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.endSuggestions;

        displaySuggestions(suggestionsList, "endList");
      });
    });

    scb.addEventListener("keyup", function(event) {
      var text = scb.value;

      var postParameters = {inputStart: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.startSuggestions;

        displaySuggestions(suggestionsList, "sCrossList");
      });
    });

    ecb.addEventListener("keyup", function(event) {
      var text = ecb.value;

      var postParameters = {inputStart: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.startSuggestions;

        displaySuggestions(suggestionsList, "eCrossList");
      });
    });

    function displaySuggestions(suggestionsList, listID) {
      document.getElementById(listID).innerHTML = "";

      if (suggestionsList == null || suggestionsList.length == 0) {
        return;
      }

      for (var i = 0; i < suggestionsList.length; i++) {
        var option = new Option("", suggestionsList[i]);
        document.getElementById(listID).appendChild(option);
      }

      return;
    }
  */
});