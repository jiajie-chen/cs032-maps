var initMap = function(id) {
	// init raphael to use given element with id
	var element = document.getElementById(id);
	var cWi = element.width(); // context width, height
	var cHi = element.height();
	var r = Raphael(element, w, h);

	// define constants for viewport
	var toRad = Math.PI / 180;
	var vLat = 10 * toRad;
	var vLng = 10 * toRad;
	var vWi = 10 * toRad; // view width and height by radians of lat/lng
	var vHi = vWi * (cHi/cWi); // use formula to roughly match context ratio

	// create clear rect for drag operations
	r.rect(0, 0, cWi, cHi).drag(function (dx, dy) {

	});
}

// requires an origin reference point (the top left of the viewport)
var latlngToView = function(vLat, vLng, lat, lng) {
	// phi is lat, lambda is lng
    var p1 = vLat; // phi 1
    var p2 = lat; // phi 2
    var dP = p2 - p1; // delta of phi
    var dL = lng - vLng; // delta of lambda
    
    // x = dL * cos(avg(p1, p2))
    // y = dP
    return {x:dP * Math.cos((p1 + p2)/2), y:dL};
}

var viewToLatLng = function(x, y) {

}

var loadTiles = function() {

}