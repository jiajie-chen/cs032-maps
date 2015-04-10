var initMap = function(id) {
	// init raphael to use given element with id
	var element = document.getElementById(id);
	var cWi = element.width(); // context width, height
	var cHi = element.height();
	var r = Raphael(element, w, h);

	// define constants for viewport
	var toRad = Math.PI / 180;
	var vWi = 10 * toRad; // view width and height by radians of lat/lng
	var vHi = vWi * (cHi/cWi); // use formula to roughly match context ratio

	// create clear rect for drag operations
	r.rect(0, 0, cWi, cHi).drag(function (dx, dy) {
		
	});
}

var latlngToView = function(lat, lng) {

}

var viewToLatLng = function(x, y) {

}

var loadTiles = function() {

}