 $(document).ready(function() {

    console.log("Autocorrect started!");
    var sb = new Autocorrect("start", "startList");
    var eb = new Autocorrect("end", "endList");
    var scb = new Autocorrect("sCross", "sCrossList");
    var ecb = new Autocorrect("eCross", "eCrossList");

    console.log("Map started!");
    var map = new Map("mapCanvas");

    document.getElementById("submit").onclick = function() {
      map.queryIntersection(
        sb.getInput(), scb.getInput(),
        eb.getInput(), ecb.getInput());
    };
});