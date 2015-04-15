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

var TO_RAD = Math.PI / 180;


var MapUtilObject = function() {
	this.PROJ_RADIUS = 100;

	// uses mercator projection to turn latlng to cartesian coordinates (origin at 0deg, 0deg)
	this.latlngToCart = function(lat, lng) {
		// x = R * lng
		var x = this.PROJ_RADIUS * (lng * TO_RAD);
		// y = R * ln(tan(pi/4 + lat/2))
		var y = this.PROJ_RADIUS * Math.log(Math.tan( (Math.PI/4) + ((lat * TO_RAD)/2) ));

		return {x:x, y:y};
	};

	this.cartToLatLng = function(x, y) {
		// lng = x / R
		var lng = (x / this.PROJ_RADIUS) / TO_RAD;
		// lat = 2 * atan((y/R)^e) - pi/2
		var lat = (2 * Math.atan(Math.exp(y / this.PROJ_RADIUS)) - (Math.PI/2)) / TO_RAD;

		return {lat:lat, lng:lng};
	};

	this.screenToCart = function(sx, sy, viewport, screenW, screenH) {
		var x = viewport.origin.x + (x * viewport.size.height / screenW);
		var y = viewport.origin.y - (y * viewport.size.height / screenH);

		return {x:x, y:y};
	};

	this.cartToScreen = function(x, y, viewport, screenW, screenH) {
		var sx = (x - viewport.origin.x) * (screenW / viewport.size.width);
		var sy = -(y - viewport.origin.y) * (screenH / viewport.size.height);

		return {sx:sx, sy:sy};
	};
};

var MapUtil = new MapUtilObject();

var MapData = function() {
	this.tiles = {};
	var TILE_SIZE = 1.00;

	var postSync = function(url, data, success) {
		$.ajax({
			type: 'POST',
			url: url,
			data: data,
			success: success,
			async: false
		});
	};

	var postAsync = function(url, data, success) {
		$.ajax({
			type: 'POST',
			url: url,
			data: data,
			success: success,
			async: true
		});
	};

	var convertWays = function(ways) {
		var cartWays = new Array();

		for (var i = 0; i < ways.length; i++) {
			var cartWay = {};
			cartWay.start = MapUtil.latlngToCart(ways[i].start.lat, ways[i].start.lng);
			cartWay.end = MapUtil.latlngToCart(ways[i].end.lat, ways[i].end.lng);
			cartWays.push(cartWay);
		}

		return cartWays;
	};

	this.queryTiles = function(tileNWs) {
		if (tileNWs.length == 0) {
			return;
		}
		var postParameters = {tiles: JSON.stringify(tileNWs)};
		//console.log(postParameters);

		postSync("/tile", postParameters, function(responseJSON) {
			//console.log("Received tiles: " + responseJSON);
			var results = JSON.parse(responseJSON);
			if (results) {
				var newTiles = results.tiles;
				for (var i = 0; i < newTiles.length; i++) {
					var key = newTiles[i].nw.lat + ":" + newTiles[i].nw.lng;
					
					this.tiles[key] = convertWays(newTiles[i].ways);
				}
				console.log(this.tiles);
			}
		}.bind(this));
	};

	this.pathIDs = new Array();
	// var querySuccess = false;

	this.queryPath = function(url, params) {
		var results;
		postAsync(url, params, function(responseJSON) {
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
	var startPoint = {x: 0, y: 0, active: false};
	var endPoint = {x: 0, y: 0, active: false};

	this.setEndpoint = function(lat, lng) {
		if (currEnd == 0) {
			startPoint.lat = lat;
			startPoint.lng = lng;
			startPoint.active = true;
			currEnd = 1;

			// reset path
			endPoint.active = false;
			this.pathIDs = new Array();
		} else {
			endPoint.lat = lat;
			endPoint.lng = lng;
			endPoint.active = true;
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
	this.cWi = $(this.canvas).width();
	this.cHi = $(this.canvas).height();
	this.canvas.width = this.cWi;
	this.canvas.height = this.cHi;
	// map data
	this.mapData = mData;

	// projection radius for cartesian conversion
	var PROJ_RADIUS = MapUtil.PROJ_RADIUS;
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
			width: 1,
			height: 1,

			minWidth: 10 / PROJ_RADIUS,
			minHeight: 10 / PROJ_RADIUS,

			maxWidth: 10 * PROJ_RADIUS,
			maxHeight: 10 * PROJ_RADIUS
		}
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
		var cart = MapUtil.latlngToCart(lat, lng);
		// convert to screen coords
		var x1 = (cart.x - this.viewport.origin.x) * (this.cWi / this.viewport.size.width);
		var y1 = -(cart.y - this.viewport.origin.y) * (this.cHi / this.viewport.size.height);
		var x2 = x1;
		var y2 = y1 - (LINE_WIDTH * 5);

		this.context.beginPath();
		this.context.lineJoin = "round";
		this.context.moveTo(x1, y1);
		this.context.lineTo(x2, y2);
		this.context.strokeStyle = "black";
		this.context.lineWidth = LINE_WIDTH;
		this.context.stroke();

		this.context.beginPath();
		this.context.arc(x2, y2, LINE_WIDTH * 2, 0, 2 * Math.PI);
		this.context.fillStyle = color;
		this.context.fill();
	};

	this.drawWay = function(x1, y1, x2, y2, color) {
		// formula for road width (the more zoomed out, the smaller the roads)
		var lW = LINE_WIDTH; //Math.max(0.5, LINE_WIDTH * (this.cWi / this.viewport.size.width));

		// this.context.beginPath();
		this.context.lineJoin = "round";
		this.context.moveTo(x1, y1);
		this.context.lineTo(x2, y2);
		this.context.strokeStyle = color;
		this.context.lineWidth = lW;
		this.context.stroke();
	};

	this.drawView = function() {
		this.context.clearRect(0, 0, this.cWi, this.cHi);

		// get all ways in bounding box, and draw them
		var sLatLng = MapUtil.cartToLatLng(this.viewport.origin.x, this.viewport.origin.y);
		var eLatLng = MapUtil.cartToLatLng(this.viewport.origin.x + this.viewport.size.width, this.viewport.origin.y - this.viewport.size.height);
		var ways = this.mapData.getWays(sLatLng.lat, sLatLng.lng, eLatLng.lat, eLatLng.lng);
		for (var i = 0; i < ways.length; i++) {
			var w = ways[i];
			this.drawWay(w.start.x, w.start.y, w.end.x, w.end.y, w.color);
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
		if (startPoint.active) {
			this.drawEndpoints(startPoint.lat, startPoint.lng, "green");
		}
		if (endPoint.active) {
			this.drawEndpoints(endPoint.lat, endPoint.lng, "red");
		}
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
		console.log("scrolling");
		var s = 90/100; // scroll down, scale in
		if (upScroll) {
			s = 110/100; // scroll up, scale out
		}

		drawer.scaleView(s);
	};
	this.mapDrag = function(sX, sY, eX, eY) {
		console.log("dragging");
		// convert to cartesian deltas
		var dx = -(eX - sX);
		var dy = (eY - sY);

		drawer.translateView(dx, dy);
	};
	this.mapClick = function(x, y) {
		console.log("clicking");
		// convert to cartesian, on the map
		var cx = drawer.viewport.origin.x + (x * drawer.viewport.size.height / drawer.cWi);
		var cy = drawer.viewport.origin.y - (y * drawer.viewport.size.height / drawer.cHi);
		// convert to latlng
		var latlng = MapUtil.cartToLatLng(cx, cy);
		console.log(latlng);

		mapData.setEndpoint(latlng.lat, latlng.lng);
	};

	var DRAG_DIST = 1; // drag threshold
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
		var oldClicked = startClick.clicked;
		startClick.clicked = false;
		startClick.started = false;

		if (oldClicked) {
			this.mapClick(startClick.x, startClick.y);
		}
	};
	var mapMouseIn = function(mouseEvent) {
		startClick.clicked = false;
		startClick.started = false;
		overCanvas = true;
	};
	var mapMouseOut = function(mouseEvent) {
		startClick.clicked = false;
		startClick.started = false;
		overCanvas = false;
	};
	var mapMouseWheel = function(mouseEvent) {
		if (overCanvas) {
			if (event.wheelDelta > 0 || event.detail < 0) {
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
		requestAnimationFrame(this.beginUpdating.bind(this));
		mapData.queryTraffic();
		drawer.drawView();
	};

	// move to beginning latlng
	var startOrigin = MapUtil.latlngToCart(43, -71);
	drawer.translateView(startOrigin.x, startOrigin.y);
	// begin drawing/polling loop
	this.beginUpdating();
};