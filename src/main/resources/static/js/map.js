// requestAnimationFrame shim
var requestAnimationFrame =  
	window.requestAnimationFrame ||
	window.webkitRequestAnimationFrame ||
	window.mozRequestAnimationFrame ||
	window.msRequestAnimationFrame ||
	window.oRequestAnimationFrame ||
	function(callback) {
	    return setTimeout(callback, 16);
	};

var toRad = Math.PI / 180;

var MapData = function() {
	this.tiles = {};
	var TILE_SIZE = 0.10;

	this.queryTiles = function(tileNWs) {
		var postParameters = {tiles: JSON.stringify(tileNWs)};
		console.log(postParameters);

		$.post("/tile", postParameters, function(responseJSON) {
			console.log("Received tiles: " + responseJSON);
			var results = JSON.parse(responseJSON);
			if (results) {
				var newTiles = results.tiles;
				for (ids in newTiles) {
					this.tiles[ids] = newTiles[ids].ways;
				}
			}
		}.bind(this));
	};

	this.pathIDs = new Array();
	// var querySuccess = false;

	this.queryPath = function(url, params) {
		var results;
		$.post(url, params, function(responseJSON) {
			console.log("Received path: " + responseJSON);
			results = JSON.parse(responseJSON);
			if (results) {
				this.pathIDs = new Array();
				var path = results.path;
				if (path && path.path) {
					var ways = path.path;
					for (var i = 0; i < ways.length; i++) {
						var w = ways[i];
						this.pathIDs.push(w.id);
					}
				}
			}
		}.bind(this));
	};

	this.queryIntersection = function(sStreet, sCross, eStreet, eCross) {
		var postParameters = {
			startStreet: JSON.stringify(sStreet),
			startCross: JSON.stringify(sCross),
			endStreet: JSON.stringify(eStreet),
			endCross: JSON.stringify(sCross)
		};

		this.queryPath("/intersection", postParameters);
	};

	this.queryPoint = function(sLat, sLng, eLat, eLng) {
		var postParameters = {
			startLat: JSON.stringify(sLat),
			startLng: JSON.stringify(sLng),
			endLat: JSON.stringify(eLat),
			endLng: JSON.stringify(eLng)
		};

		this.queryPath("/point", postParameters);
	};

	var trafficIDs = {};

	this.queryTraffic = function() {

	};

	var currEnd = 0;
	var startPoint = {lat: 0, lng: 0};
	var endPoint = {lat: 0, lng: 0};

	this.setEndpoints = function(lat, lng) {
		if (currEnd == 0) {
			startPoint.lat = lat;
			startPoint.lng = lng;
			currEnd = 1;

			// reset path
			this.pathIDs = new Array();
		} else {
			endPoint.lat = lat;
			endPoint.lng = lng;
			currEnd = 0;

			// query for path
			this.queryPoint(startPoint.lat, startPoint.lng, endPoint.lat, endPoint.lng);
		}
	};

	this.getEndpoint = function(num) {
		if (num == 0) {
			return startPoint;
		} else {
			return endPoint;
		}
	};

	this.getTiles = function(sLat, sLng, eLat, eLng) {
		// init needed tiles by going through all tiles in bounding box and seeing if cached
		var needed = new Array();
		var toReturn = new Array();
		var lat = Math.ceil(sLat / TILE_SIZE) * TILE_SIZE; // snap to tile lat boundary
		for (; lat > eLat; lat -= TILE_SIZE) {
			var lng = Math.floor(sLng / TILE_SIZE) * TILE_SIZE; // snap to tile lng boundary
			for (; lng < eLng; lng += TILE_SIZE) {
				var key = lat + ":" + lng;

				// not already in cache
				if (!(key in this.tiles)) {
					needed.push({lat:lat, lng:lng});
				}

				toReturn.push(key); // add id to returned array
			}
		}

		// query and cache needed tiles
		this.queryTiles(needed);
		return toReturn;
	};

	this.getWayColor = function(wID) {
		if (this.pathIDs.indexOf(wID) != -1) {
			return "cyan";
		}
		if (wID in trafficIDs) {
			var tLevel = trafficIDs[wID];
			if (tLevel > 5) {
				return "red";
			}
			if (tLevel > 1) {
				return "orange";
			}
			if (tLevel < 1) {
				return "green";
			}
		}
		return "black";
	};

	this.getWays = function(sLat, sLng, eLat, eLng) {
		console.log(sLat);
		console.log(sLng);
		var toReturn = new Array();
		var tileIDs = this.getTiles(sLat, sLng, eLat, eLng);
		for (var i = 0; i < tileIDs.length; i++) {
			var ways = this.tiles[tileIDs[i]];
			for (var j = 0; j < ways.length; j++) {
				var w = ways[j];
				w.color = this.getWayColor(w.id); // add color property
				toReturn.push(w);
			}
		}

		console.log(toReturn);

		return toReturn;
	};

	/*
	this.getPath = function() {
		if (path == undefined) {
			return undefined;
		}

		return path.path;
	};
	*/

	this.getPathBounds = function() {

	};
};

var MapDrawer = function(canvasElement, mData) {
	this.canvas = canvasElement;
	this.context = this.canvas.getContext("2d");
	// context width, height
	var cWi = this.canvas.width;
	var cHi = this.canvas.height;
	// map data
	this.mapData = mData;

	// projection radius for cartesian conversion
	var PROJ_RADIUS = 1;
	// road line radius (when viewport width/height matches)
	var LINE_WIDTH = 2;

	//viewport (canvas), in terms of cartesian coordinates
	this.viewport = {
		origin: { // top left of viewport
			x: 0,
			y: 0,

			minX: -Math.PI * PROJ_RADIUS,
			minY: -Math.PI * PROJ_RADIUS,

			maxX: Math.PI * PROJ_RADIUS,
			maxY: Math.PI * PROJ_RADIUS
		},
		size: { //size is width and height, or bottom right coordinate for latlng
			width: cWi,
			height: cHi,

			minWidth: cWi / 10,
			minHeight: cHi / 10,

			maxWidth: cWi * 10,
			maxHeight: cHi * 10
		}
	};

	/*				CONVERSION METHODS					*/

	// uses mercator projection to turn latlng to cartesian coordinates (origin at 0deg, 0deg)
	this.latlngToCart = function(lat, lng) {
		// x = R * lng
		var x = PROJ_RADIUS * lng;
		// y = R * ln(tan(pi/4 + lat/2))
		var y = PROJ_RADIUS * Math.log(Math.tan( (Math.PI/4) + (lat/2) ));

		return {x:x, y:y};
	};

	this.cartToLatLng = function(x, y) {
		// lng = x/R
		var lng = x / PROJ_RADIUS;
		// lat = 2 * atan((y/R)^e) - pi/2
		var lat = 2 * Math.atan(Math.exp(y / PROJ_RADIUS)) - (Math.PI/2);

		return {lat:lat, lng:lng};
	};

	// used for auto-draw optimization (don't auto-redraw if nothing is changed)
	// this.viewChanged = true;

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
					oldY + dy,
					this.viewport.origin.maxY - this.viewport.size.height),
				this.viewport.origin.minY);

		// this.viewChanged = (this.viewport.origin.x != oldX) || (this.viewport.origin.y != oldY);
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

		// this.viewChanged =  (dx != 0) || (dy != 0); // if scaling doesn't change, then no translation happens
	};

	/*				DRAWING METHODS					*/

	this.drawEndpoints = function(lat, lng, color) {
		var cart = this.latlngToCart(lat, lng);
		// convert to screen coords
		var x1 = (cart.x - this.viewport.origin.x) * (cWi / this.viewport.size.width);
		var y1 = -(cart.y - this.viewport.origin.y) * (cHi / this.viewport.size.height);
		var x2 = x1;
		var y2 = y1 - (LINE_WIDTH * 3);

		this.context.arc(x2, y2, LINE_WIDTH, 0, 2 * Math.PI, false);
		this.context.fillStyle = color;
		this.context.fill();

		this.context.lineJoin = "round";
		this.context.moveTo(x1, y1);
		this.context.lineTo(x2, y2);
		this.context.strokeStyle = "black";
		this.context.lineWidth = LINE_WIDTH;
		this.context.stroke();
	};

	this.drawWay = function(sLat, sLng, eLat, eLng, color) {
		var sCart = this.latlngToCart(sLat, sLng);
		var eCart = this.latlngToCart(eLat, eLng);

		// convert to screen coords
		var x1 = (sCart.x - this.viewport.origin.x) * (cWi / this.viewport.size.width);
		var y1 = -(sCart.y - this.viewport.origin.y) * (cHi / this.viewport.size.height);
		var x2 = (eCart.x - this.viewport.origin.x) * (cWi / this.viewport.size.width);
		var y2 = -(eCart.y - this.viewport.origin.y) * (cHi / this.viewport.size.height);
		// formula for road width (the more zoomed out, the smaller the roads)
		var lW = Math.max(0.5, LINE_WIDTH * (cWi / this.viewport.size.width));

		this.context.lineJoin = "round";
		this.context.moveTo(x1, y1);
		this.context.lineTo(x2, y2);
		this.context.strokeStyle = color;
		this.context.lineWidth = lW;
		this.context.stroke();
	};

	this.drawView = function() {
		this.context.clearRect(0, 0, cWi, cHi);

		// get all ways in bounding box, and draw them
		var sLatLng = this.cartToLatLng(this.viewport.origin.x, this.viewport.origin.y);
		var eLatLng = this.cartToLatLng(this.viewport.origin.x + this.viewport.size.height, this.viewport.origin.y - this.viewport.size.height);
		var ways = this.mapData.getWays(sLatLng.lat, sLatLng.lng, eLatLng.lat, eLatLng.lng);
		for (var i = 0; i < ways.length; i++) {
			var w = ways[i];
			this.drawWay(w.start.lat, w.start.lng, w.end.lat, e.end.lng, w.color);
		}

		/*
		// draw the solution path, if exists
		var path = this.mapData.getPath();
		if (path != undefined) {
			for (var i = 0; i < ways.length; i++) {
				var p = path[i];
				this.drawWay(p.start.lat, p.start.lng, p.end.lat, p.end.lng, "cyan");
			}
		}
		*/

		// draw the endpoints
		var startPoint = this.mapData.getEndpoint(0);
		var endPoint = this.mapData.getEndpoint(1);
		this.drawEndpoints(startPoint.lat, startPoint.lng, "green");
		this.drawEndpoints(endPoint.lat, endPoint.lng, "red");
	};
};

var Map = function(canvasId) {
	var mapData = new MapData();
	var drawer = new MapDrawer(document.getElementById(canvasId), mapData);
	this.offX = drawer.canvas.offsetLeft;
	this.offY = drawer.canvas.offsetTop;

	this.mapQueryIntersection = function(sStreet, sCross, eStreet, eCross) {
		mapData.queryIntersection(sStreet, sCross, eStreet, eCross);
		var bounds = mapData.getPathBounds();
	};

	this.mapScroll = function(upScroll) {
		var s = 90/100; // scroll down, scale in
		if (upScroll) {
			s = 110/100; // scroll up, scale out
		}

		drawer.scaleView(s);
	};
	this.mapDrag = function(sX, sY, eX, eY) {
		// convert to cartesian deltas
		var dx = eX - sX;
		var dy = -(eY - sY);

		drawer.translateView(dx, dy);
	};
	this.mapClick = function(x, y) {
		// convert to cartesian, on the map
		var cx = drawer.viewport.origin.x + x;
		var cy = drawer.viewport.origin.y - y;
		// convert to latlng
		var latlng = drawer.cartToLatLng(cx, cy);

		mapData.setEndpoint(latlng.lat, latlng.lng);
	};

	var DRAG_DIST = 5; // drag threshold
	var overCanvas = false;
	var startClick = {started: false, clicked: false, x:0, y:0};
	var mapMouseStart = function(mouseEvent) {
		startClick.clicked = true;
		startClick.started = true;

		startClick.x = mouseEvent.pageX - this.offX;
		startClick.y = mouseEvent.pageY - this.offY;
	};
	var mapMouseDrag = function(mouseEvent) {
		if (startClick.started) {
			var mouseX = mouseEvent.pageX - this.offX;
			var mouseY = mouseEvent.pageY - this.offY;

			var dist = Math.pow(startClick.x - mouseX, 2) + Math.pow(startClick.y - mouseY, 2);
			if (dist > Math.pow(DRAG_DIST, 2)) { // only register drag if enough distance has passed
				startClick.clicked = false;

				this.mapDrag(startClick.x, startClick.y, mouseX, mouseY);

				startClick.x = mouseX; // update the mouse start postion
				startClick.y = mouseY;
			}
		}
	};
	var mapMouseStop = function(mouseEvent) {
		if (startClick.clicked) {
			this.mapClick(startClick.x, startClick.y);
		}
		startClick.clicked = false;
		startClick.started = false;
	};
	var mapMouseIn = function(mouseEvent) {
		overCanvas = true;
	};
	var mapMouseOut = function(mouseEvent) {
		overCanvas = false;
	};
	var mapMouseWheel = function(mouseEvent) {
		if (overCanvas) {
			if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
				// scroll up
				this.mapScroll(true);
			} else {
				// scroll down
				this.mapScroll(false);
			}
		}
	};
	$(drawer.canvas) // bind mouse events to actions
	.mousedown(mapMouseStart.bind(this))
	.mousemove(mapMouseDrag.bind(this))
	.mouseup(mapMouseStop.bind(this))
	.mouseenter(mapMouseIn.bind(this))
	.mouseleave(mapMouseOut.bind(this))
	.bind('mousewheel DOMMouseScroll', mapMouseWheel.bind(this));

	this.beginUpdating = function() {
		// requestAnimationFrame(this.beginUpdating.bind(this));
		mapData.queryTraffic();
		drawer.drawView();
	};

	// move to beginning latlng
	var startOrigin = drawer.latlngToCart(41 * toRad, -71 * toRad);
	drawer.translateView(startOrigin.x, startOrigin.y);
	// begin drawing/polling loop
	this.beginUpdating();
};