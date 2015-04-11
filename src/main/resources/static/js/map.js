var Map = function(id) {
	// constants
	this.STAND_LAT = 0; //standard parallel to use for projection
	this.COS_STAND_LAT = Math.cos(STAND_LAT); // store for efficency

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

	this.latlngToView = function(lat, lng) {
		// x = lat * cos(standard parallel)
		var x = lng * COS_STAND_LAT;
		// y = dP
		var y = lat;

		// This should be some normalized arc length or something, but might need some scaling
		return {x:dP * Math.cos((p1 + p2)/2), y:dL};
	};

	this.viewToLatLng = function(x, y) {
		
	};

	this.loadTiles = function(url) {

	};

	this.translate = function(dx, dy) {

	};
}