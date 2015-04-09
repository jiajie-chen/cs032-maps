<!DOCTYPE html>
<html>
  <head>
   
    <meta charset="utf-8">
    <link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/main.css">
    <link href="http://fonts.googleapis.com/css?family=Merienda+One" rel="stylesheet" type="text/css">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="/js/main.js"></script>
    
    <title>${title}</title>
  </head>
  
  <body>

    <div id="searchDiv">

    <form method="POST" action="/suggestion" class="searchForm">
      <textarea id="startbox" class="searchbar" rows="1" cols="40" placeholder="Start"></textarea></br>

      <textarea id="suggestion1" class="sbox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion2" class="sbox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion3" class="sbox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion4" class="sbox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion5" class="sbox" rows="1" cols="40" readonly></textarea></br>
    </form> 

    <form method="POST" action="/suggestion" class="searchForm">
      <textarea id="endbox" class="searchbar" rows="1" cols="40" placeholder="End"></textarea></br>

      <textarea id="suggestion1" class="ebox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion2" class="ebox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion3" class="ebox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion4" class="ebox" rows="1" cols="40" readonly></textarea></br>
      <textarea id="suggestion5" class="ebox" rows="1" cols="40" readonly></textarea></br>
    </form> 

  </div>

  <div id="mainDiv">

    <canvas id="mainCanvas"></canvas>

  </div>

  </body>
</html>