<!DOCTYPE html>
<html>
  <head>
   
    <meta charset="utf-8">
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/main.css">
    <link href="http://fonts.googleapis.com/css?family=Merienda+One" rel="stylesheet" type="text/css">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="/js/autocorrect.js"></script>
    <script src="/js/map.js"></script>
    <script src="/js/main.js"></script>
    
    <title>${title}</title>
  </head>
  
  <body>

    <div id="searchDiv">

      <input type="text" class="searchBar" name="start" id="start" list="startList" placeholder="Starting Street">
      <datalist class="suggestionList" id="startList">
      </datalist>

      <input type="text" class="searchBar" name="sCross" id="sCross" list="sCrossList" placeholder="Starting Cross Street">
      <datalist class="suggestionList" id="sCrossList">
      </datalist>

      <input type="text" class="searchBar" name="end" id="end" list="endList" placeholder="Ending Street">
      <datalist class="suggestionList" id="endList">
      </datalist>

      <input type="text" class="searchBar" name="eCross" id="eCross" list="eCrossList" placeholder="Ending Cross Street">
      <datalist class="suggestionList" id="eCrossList">
      </datalist>

      <button id="submit">Find</button>

    </div>

    <div id="mainDiv">
      <canvas id="mapCanvas"></canvas>
    </div>

  </body>
</html>