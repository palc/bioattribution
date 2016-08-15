function render(queryConfig, div) {
  plotDiv = d3.select(div);

  queryConfig.success = onSuccess;
  queryConfig.error = onError;

  LABKEY.Query.GetData.getRawData(queryConfig);
}

function onSuccess(results) {
  var map;
  // Cache locations to save on geocoding API calls
  var _cache = {};
  var _boundingBox;

  // Initial coordinates for map
  var _lat = -97;
  var _lon = 38;

  var featureVector = new ol.layer.Vector();
  // Cluster points by coordinates
  var _locationClusters = {};
  var _locationIdx = 0;
  var _locationFeatures = [];

  if (typeof(Storage) !== "undefined") {
    // in IE9, you cannot use local storage for a file system webpage (file://),
    // so this is a workaround to keep it from crashing
    var storage = {};
    if (localStorage) {
      storage = localStorage;
    }

    _cache = storage;
  }

  map = new ol.Map({
    target: 'map',
    layers: [vector],
    target: 'map',
    view: new ol.View2D({
      center: ol.proj.transform([_lat, _lon], 'EPSG:4326', 'EPSG:3857'),
      zoom: 4
    })
  });

  // Parse results, mapping addresses into queres of the geocoding service
  results.rows.map(processLocation);
  mapLocations();

  function processLocation(location) {
    if (location.Address) {
      geoCodeLookup(location);
    }
  };

  function geoCodeLookup(location) {
    var address = location.Address.value;
    var geoCode = lookupGeocode(address);

    if (geoCode) {
      clusterLocation(geoCode);
    } else {
      $.ajax({
        url: 'http://nominatim.openstreetmap.org/search',
        type: 'GET',
        dataType: 'jsonp',
        jsonp: false,
        jsonpCallback: 'json_callback' + location.Key.value,
        data: {
          format: 'json',
          q: address,
          limit: 1,
          json_callback: 'json_callback' + location.Key.value
        },
        success: function(data, textStatus, xhr) {
          var coords = null;
          // if there was a response, then cache the [location name,geocoordinate] pair
          if (data && data.length && data.length > 0) {
            coords = data[0].lat + ',' + data[0].lon;
            addGeocode(address, coords);
          }
          clusterLocation(coords);
        }
      });
    }
  };

  function clusterLocation(geoCode) {
    if (!_locationClusters[geoCode]) {
      _locationClusters[geoCode] = 1;
    } else {
      _locationClusters[geoCode] += 1;
    }
  }

  // is the location name in the geocode cache?
  function lookupGeocode(address) {
    return (!_cache[address]) ? null : _cache[address];
  }

  // add the [location, geocoordinate] pair to the cache
  function addGeocode(address, value) {
    _cache[address] = value;
  }

  function mapLocations() {
    for (var key in _locationClusters) {
      addFeature(key, _locationClusters[key]);
    };

    var parser = new ol.parser.GeoJSON();

    vector.addFeatures(parser, _locationFeatures);
  }

  function addFeature(geoCode, size) {
    if (geoCode) {
      var pos = ol.proj.transform([geoCode.split(',')[1], geoCode.split(',')[0]], 'EPSG:4326', 'EPSG:3857');
      var feature = {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: pos
        },
        properties: {
          foo: 'bar'
        }
      };
      _locationFeatures.push(feature);
    }
  }
}

function onError(errorInfo) {
  plotDiv.innerHTML = errorInfo.exception;
}