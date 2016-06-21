function render(queryConfig, div) {
    plotDiv = d3.select(div);

    // Generate query and get data
    queryConfig.success = onSuccess;
    queryConfig.error = onError;

    LABKEY.Query.GetData.getRawData(queryConfig);
}

function onSuccess(results) {
    require([
        "esri/map",
        "dojo/_base/array",
        "esri/geometry/Point",
        "esri/symbols/SimpleMarkerSymbol",
        "esri/graphic",
        "esri/layers/GraphicsLayer",
        "dojo/dom",
        "dojo/parser",
        "dojo/ready"
    ], function(
        Map, array, Point, SimpleMarkerSymbol, Graphic, GraphicsLayer, dom, parser, ready
    ) {
        parser.parse();

        var map, strain, scale;

        ready(function() {
            map = new Map("map", {
                basemap: "gray",
                center: [-71.0636, 42.3581],
                zoom: 4
            });
            addPoints();
        });

        function addPoints() {
            var gl = GraphicsLayer();

            var counts = results.rows.map(function(row) {
                return row.Count.value;
            });
            var minCount = Math.min.apply(Math, counts);
            var maxCount = Math.max.apply(Math, counts);

            scale = d3.scale.linear().domain([minCount, maxCount]).range([5, 25]);
            strain = d3.scale.ordinal().domain(["A", "B"]).range(["red", "blue"]);

            results.rows.map(function(row) {
                lat = row.Latitude.value;
                lon = row.Longitude.value;

                var p = new Point(lon, lat);
                var s = new SimpleMarkerSymbol();

                s.setSize(scale(row.Count.value));

                var color = new esri.Color(strain(row.Strain.value));
                color.a = 0.5;
                s.setColor(color);

                var g = new Graphic(p, s);
                gl.add(g);

                map.addLayer(gl);
            })

            createLegend();
        }

        function createLegend() {
            var swatchTemplate =
                '<div>' +
                '<svg width="24" height="24" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
                '<path d="M 11 11 L 12 11 L 12 12 L 11 12 Z" style="fill:none; stroke: ${color}; stroke-width: 8; stroke-opacity: 0.7; stroke-linecap: round;stroke-linejoin: round;" />' +
                '</svg>' +
                '<span>${label}</span>' +
                '</div>';

            var html = "",
                inverted, data, legend = dom.byId("legend");

            array.forEach(strain.domain(), function(domainVal) {
                // Returns the extent of values in the input domain [x0, x1] for the corresponding value in the output range y
                color = strain(domainVal);

                data = {
                    label: domainVal,
                    color: color
                };
                html += esri.substitute(data, swatchTemplate);
            });
            legend.innerHTML = legend.innerHTML + html;
        }
    });
}

function onError(errorInfo) {
    plotDiv.innerHTML = errorInfo.exception;
}