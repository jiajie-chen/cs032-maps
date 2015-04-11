var Map = function(id) {
	// get reference to canvas
	this.canvas = document.getElementById(id);
	var cWi = this.canvas.width(); // context width, height
	var cHi = this.canvas.height();

	// projection radius for cartesian conversion
	this.PROJ_RADIUS = 100;

	//viewport (canvas), in terms of cartesian coordinates
	this.viewport = {
		origin: { // top left of viewport
			x: 0,
			y: 0,

			minX: -Math.PI * this.PROJ_RADIUS,
			minY: -Math.PI * this.PROJ_RADIUS,

			maxX: Math.PI * this.PROJ_RADIUS,
			maxY: Math.PI * this.PROJ_RADIUS
		},
		size: { //size is width and height, or bottom right coordinate for latlng
			width: 200,
			height: 200 * (cHi/cWi), // use formula to roughly match context ratio

			minWidth: 100,
			minHeight: 100,

			maxWidth: 1000,
			maxHeight: 1000
		}
	};

	/*				CONVERSION METHODS					*/

	// uses mercator projection to turn latlng to cartesian coordinates (origin at 0deg, 0deg)
	this.latlngToCart = function(lat, lng) {
		// x = R * lng
		var x = this.PROJ_RADIUS * lng;
		// y = R * ln(tan(pi/4 + lat/2))
		var y = this.PROJ_RADIUS * Math.log(Math.tan( (Math.PI/4) + (lat/2) ));

		return {x:x, y:y};
	};

	this.cartToLatLng = function(x, y) {
		// lng = x/R
		var lng = x / this.PROJ_RADIUS;
		// lat = 2 * atan((y/R)^e) - pi/2
		var lat = 2 * Math.atan(Math.exp(y / this.PROJ_RADIUS)) - (Math.PI/2);

		return {lat:lat, lng:lng};
	};

	this.translateView = function(dx, dy) {
		var oldX = this.viewport.origin.x;
		var oldY = this.viewport.origin.y;
		this.viewport.origin.x =
			Math.max(
				Math.min(
					oldX + dx,
					this.viewport.origin.maxX - this.viewport.size.width),
				this.viewport.origin.minX);
		this.viewport.origin.y =
			Math.max(
				Math.min(
					oldY + dY,
					this.viewport.origin.maxY - this.viewport.size.height),
				this.viewport.origin.minY);
	};

	this.scaleView = function(s) {
		var oldW = this.viewport.size.width;
		var oldH = this.viewport.size.height;
		this.viewport.size.width =
			Math.max(
				Math.min(
					oldW * s,
					this.viewport.size.maxWidth),
				this.viewport.size.minWidth);
		this.viewport.size.height =
			Math.max(
				Math.min(
					oldH * s,
					this.viewport.size.maxHeight),
				this.viewport.size.minHeight);

		// translate so scaling is from center out
		var dx = (this.viewport.size.width - oldW)/2;
		var dy = (this.viewport.size.height - oldH)/2;
		this.translateView(dx, -dy);
	}

	/*				DRAWING METHODS					*/
}