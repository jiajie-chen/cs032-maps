 $(document).ready(function() {
    var sb = document.getElementById("startbox");
    var eb = document.getElementById("endbox");

    console.log("Script started!");

    sb.addEventListener("keyup", function(event) {
      var text = document.getElementById("startbox").value;

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
      var text = document.getElementById("endbox").value;

      var postParameters = {inputEnd: JSON.stringify(text)};
      console.log(postParameters);

      $.post("/suggestions", postParameters, function(responseJSON) {
        console.log("Received suggestions: " + responseJSON);
        responseObject = JSON.parse(responseJSON);
        var suggestionsList = responseObject.endSuggestions;

        displayEnd(suggestionsList);
      });
    });

    eb.addEventListener("keydown", function(event) {
      if (event.keyCode != 13) {
        return;
      }

      event.preventDefault();
      var startText = document.getElementById("startbox").value;
      var endText = document.getElementById("endbox").value.trim();

      if (startText == endText) {
        displayError("Those are the same actor, silly!");
        return;
      }

      var postParameters = {inputStart: JSON.stringify(startText), inputEnd: JSON.stringify(endText)};
      console.log("SUBMITTING: ");
      console.log(postParameters);

      $.post("/results", postParameters, function(responseJSON) {
        responseObject = JSON.parse(responseJSON);
        console.log("RESULTS: ");
        console.log(responseObject);

        var resultsList = responseObject.results;
        if (resultsList == null) {
          displayError(responseObject);
          return;
        }

        console.log(resultsList);

        displayResults(resultsList);
      });
    });
  });

 function displayResults(resultsList) {
  var displayParagraph = document.getElementById('results');
  displayParagraph.innerHTML = "";

  if (resultsList.length == 0) {
    displayParagraph.innerHTML = "No connection between those actors."
  }
  
  for (var i = 0; i < resultsList.length; i++) {
    var arrow = document.createTextNode(" -> ");
    var colon = document.createTextNode(" : ");

    var compactEdge = resultsList[i];
    console.log(compactEdge);

    var line = document.createElement('p'); 

    var firstLink = document.createElement('a');
    var firstLinkText = document.createTextNode(compactEdge.firstActorName);
    firstLink.appendChild(firstLinkText);
    firstLink.href = "/actor" + compactEdge.firstActorId;
    line.appendChild(firstLink);
    line.appendChild(arrow);

    var secondLink = document.createElement('a');
    var secondLinkText = document.createTextNode(compactEdge.secondActorName);
    secondLink.appendChild(secondLinkText);
    secondLink.href = "/actor" + compactEdge.secondActorId;
    line.appendChild(secondLink);
    line.appendChild(colon);

    var filmLink = document.createElement('a');
    var filmLinkText = document.createTextNode(compactEdge.filmName);
    filmLink.appendChild(filmLinkText);
    filmLink.href = "/film" + compactEdge.filmId;
    line.appendChild(filmLink);

    displayParagraph.appendChild(line);
  };
 }

  function displayStart(suggestionsList) {
    // var hide = $("#hide").is(":checked");
    if (suggestionsList == null || suggestionsList.length == 0) {
      $('.sbox').each(function(i, obj) {
        $(this).hide();
      });

      return;
    }

    $('.sbox').each(function(i, obj) {
      var hide = false;
      obj.value = suggestionsList[i];
      if (hide || i >= suggestionsList.length) {
        $(this).hide();
      } else {
        $(this).show();
      }
    });
  }

  function displayEnd(suggestionsList) {
    // var hide = $("#hide").is(":checked");
    if (suggestionsList == null || suggestionsList.length == 0) {
      $('.ebox').each(function(i, obj) {
        $(this).hide();
      });

      return;
    }

    $('.ebox').each(function(i, obj) {
      var hide = false;
      obj.value = suggestionsList[i];
      if (hide || i >= suggestionsList.length) {
        $(this).hide();
      } else {
        $(this).show();
      }
    });
  }

  function displayError(error) {
    var displayParagraph = document.getElementById('results');
    displayParagraph.innerHTML = error;
  }