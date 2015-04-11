var Map = function(id) {

	// init raphael to use given element with id
	this.element = document.getElementById(id);
	var cWi = this.element.width(); // context width, height
	var cHi = this.element.height();
	var r = Raphael(this.element, w, h);

	// define constants for viewport
	var toRad = Math.PI / 180;
	var vLat = 10 * toRad;
	var vLng = 10 * toRad;
	var vWi = 10 * toRad; // view width and height by radians of lat/lng
	var vHi = vWi * (cHi/cWi); // use formula to roughly match context ratio

	this.viewport = {
		origin: {lat: vLat, lng: vLng},
		width: vWi,
		height: vHi
	};

	// uses 
	this.latlngToView = function(lat, lng) {
		// x = lng * cos(standard parallel)
		var x = lng * COS_STAND_LAT;
		// y = lat
		var y = lat;

		return {x:x, y:y};
	};

	this.viewToLatLng = function(x, y) {
		
	};

	this.loadTiles = function(url) {

	};

	this.translate = function(dx, dy) {

	};
}