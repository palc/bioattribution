var margin = {
    top: 150,
    right: 150,
    bottom: 75,
    left: 300
},
    cellSize = 18,
    colNumber = 33,
    rowNumber = 52,
    width = cellSize * colNumber,
    height = cellSize * rowNumber,
    legendElementWidth = cellSize * 2.5,
    colorBuckets = 9,
    colors = ['rgb(255,255,204)', 'rgb(255,237,160)',
        'rgb(254,217,118)', 'rgb(254,178,76)',
        'rgb(253,141,60)', 'rgb(252,78,42)',
        'rgb(227,26,28)', 'rgb(189,0,38)',
        'rgb(128,0,38)'
    ];
rowvar = 'serovar',
colvar = 'collection_year';

var columns = [];
var query_results = [];
var exportDataButton;
var exportPdfButton;
var refreshButton;
var dropDown1;
var dropDown2;
var rowFilterCount = 20;
var colFilterCount = 20;
var rowElems;
var colElems;

function render(queryConfig, div) {
    jsDiv = d3.select(div);

    plotDiv = d3.select("#plotControls");
    varDiv = d3.select("#form");

    varDiv.html("");

    // Buttons for tabular data and pdf export
    exportDataButton = varDiv.append("input")
        .attr("type", "button")
        .attr("value", "Export Data")

    exportPdfButton = varDiv.append("input")
        .attr("type", "button")
        .attr("value", "Save Plot")
        .on("click", exportPdf);

    refreshButton = varDiv.append("input")
        .attr("type", "button")
        .attr("value", "Refresh Plot");

    dropDiv = varDiv.append("div");

    dropDiv.append("label")
        .attr("for", "row-list")
        .text("Row variable:");

    dropDown1 = dropDiv.append("select")
        .attr("name", "row-list");

    dropDiv.append("label")
        .attr("for", "col-list")
        .text("Column variable:");

    dropDown2 = dropDiv.append("select")
        .attr("name", "col-list");

    sortDiv = varDiv.append("div");

    rowFilterForm = sortDiv.append("form");
    rowFilterForm.text("Number of rows to keep in plot: ")
    rowFilterInput = rowFilterForm.append("input")
        .attr("type", "text")
        .attr("id", "rowFilterCount")
        .attr("value", rowFilterCount);

    filterForm = sortDiv.append("form");
    filterForm.text("Number of columns to keep in plot: ")
    filterInput = filterForm.append("input")
        .attr("type", "text")
        .attr("id", "colFilterCount")
        .attr("value", colFilterCount);

    queryConfig.success = onSuccess;
    queryConfig.error = onError;

    // Generate query and get data
    console.log(queryConfig);
    LABKEY.Query.GetData.getRawData(queryConfig)
};

// Functions to enable us to find unique x and y axis elements
// from raw data (http://stackoverflow.com/questions/11246758/how-to-get-unique-values-in-a-array)
Array.prototype.contains = function(v) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] === v) return true;
    }
    return false;
}

Array.prototype.unique = function() {
    var arr = [];
    for (var i = 0; i < this.length; i++) {
        if (!arr.contains(this[i])) {
            arr.push(this[i]);
        }
    }
    return arr;
}



function onRefresh() {
    plotDiv.select("svg").remove();

    var badVals = [
        "Unknown",
        "missing",
        "null",
        "unspecified",
        "unknown",
        "not applicable"
    ];

    data = query_results.rows.map(
        function(a) {
            var rowElem = a[rowvar].value;
            var colElem = a[colvar].value;

            if (badVals.contains(rowElem) || rowElem == null) {
                rowElem = "Unknown";
            }

            if (badVals.contains(colElem) || colElem == null) {
                colElem = "Unknown";
            }

            return {
                row: rowElem,
                col: colElem
            }
        });

    // Order row labels by total count
    var orderedRowLabels = d3.nest()
        .key(function(d) {
            return d.row;
        })
        .rollup(function(leaves) {
            return leaves.length;
        })
        .entries(data)
        .sort(function(a, b) {
            return b.values - a.values;
        })
        .map(function(d) {
            return d.key;
        })
        .slice(0, parseInt(document.getElementById('rowFilterCount').value, 10));
    rowElems = orderedRowLabels;

    // Order column labels by total count
    var orderedColLabels = d3.nest()
        .key(function(d) {
            return d.col;
        })
        .rollup(function(leaves) {
            return leaves.length;
        })
        .entries(data)
        .sort(function(a, b) {
            return b.values - a.values;
        })
        .map(function(d) {
            return d.key;
        })
        .slice(0, parseInt(document.getElementById('colFilterCount').value, 10));
    colElems = orderedColLabels;


    var nestData = d3.nest()
        .key(function(d) {
            return d.row;
        })
        .key(function(d) {
            return d.col;
        })
        .rollup(function(leaves) {
            return leaves.length;
        })
        .entries(data)
        .map(function(d) {
            return d.values.map(function(e) {
                return {
                    row: d.key,
                    col: e.key,
                    count: e.values
                }
            })
        })

    countData = nestData.reduce(function(d1, d2) {
        return d1.concat(d2)
    }, []);
    countData = countData.filter(function(d) {
        return rowElems.contains(d.row);
    });
    generatePlot(countData);
}

function onError(errorInfo) {
    jsDiv.innerHTML = errorInfo.exception;
}

function generatePlot(data) {
    var colNumber = colElems.length,
        rowNumber = rowElems.length,
        width = cellSize * colNumber,
        height = cellSize * rowNumber;

    var colorScale = d3.scale.quantile()
        .domain([0, colorBuckets - 1, d3.max(data, function(d) {
            return d.count;
        })])
        .range(colors);

    var svg = plotDiv.append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var x = d3.scale.ordinal().rangeRoundBands([0, width]);
    var xAxis = d3.svg.axis().scale(x).orient("bottom");
    x.domain(colElems.sort());

    var y = d3.scale.ordinal().rangeRoundBands([0, height]);
    var yAxis = d3.svg.axis().scale(y).orient("left");
    y.domain(rowElems);

    var rowLabels = svg.append("g")
        .selectAll(".rowLabelg")
        .data(rowElems)
        .enter().append("text")
        .text(function(d) {
            return d;
        })
        .attr("x", 0)
        .attr("y", function(d) {
            return y(d);
        })
        .style("text-anchor", "end")
        .attr("transform", "translate(-6," + cellSize / 1.5 + ")")
        .attr("class", function(d) {
            return "rowLabel mono " + d;
        })
        .on("mouseout", function(d) {
            d3.select(this).classed("text-hover", false);
        })
        .on("mouseover", function(d) {
            d3.select(this).classed("text-hover", true);
            d3.selectAll(".cell").classed("cell-hover", function(c) {
                return c.row == d;
            });
        })
        .on("mouseout", function() {
            d3.select(this).classed("text-hover", false);
            d3.selectAll(".cell").classed("cell-hover", false);
        });

    var colLabels = svg.append("g")
        .selectAll(".colLabelg")
        .data(colElems)
        .enter()
        .append("text")
        .text(function(d) {
            return d;
        })
        .attr("x", 0)
        .attr("y", function(d) {
            return x(d);
        })
        .style("text-anchor", "left")
        .attr("transform", "translate(" + cellSize / 2 + ",-6) rotate (-90)")
        .attr("class", function(d) {
            return "colLabel mono " + d;
        })
        .on("mouseover", function(d) {
            d3.select(this).classed("text-hover", true);
            d3.selectAll(".cell").classed("cell-hover", function(c) {
                return c.col == d;
            });
        })
        .on("mouseout", function() {
            d3.select(this).classed("text-hover", false);
            d3.selectAll(".cell").classed("cell-hover", false);
        });

    var heatMap = svg.append("g").attr("class", "g3").selectAll(".cellg")
        .data(data, function(d) {
            return d.row + ":" + d.col;
        })
        .enter()
        .append("rect")
        .attr("x", function(d) {
            return x(d.col);
        })
        .attr("y", function(d) {
            return y(d.row);
        })
        .attr("width", cellSize)
        .attr("height", cellSize)
        .attr("class", function(d, i) {
            return "cell cell-border cr" + (i - 1) + " cc" + (i - 1);
        })
        .style("fill", function(d) {
            return colorScale(d.count);
        })
        .on("mouseover", function(d) {
            d3.select(this).classed("cell-hover", true);
            d3.selectAll(".rowLabel").classed("text-highlight", function(r) {
                return r == d.row;
            });
            d3.selectAll(".colLabel").classed("text-highlight", function(c) {
                return c == d.col;
            });

            d3.select("#tooltip")
                .style("left", (d3.event.pageX + 10) + "px")
                .style("top", (d3.event.pageY - 10) + "px")
                .select("#value")
                .text(rowvar + ": " + d.row + " " + colvar + ": " + d.col + " Count:" + d.count);

            d3.select("#tooltip").classed("hidden", false);
        })
        .on("mouseout", function() {
            d3.select(this).classed("cell-hover", false);
            d3.selectAll(".rowLabel").classed("text-highlight", false);
            d3.selectAll(".colLabel").classed("text-highlight", false);
            d3.select("#tooltip").classed("hidden", true);
        });

    var legend = svg.selectAll(".legend")
        .data([0].concat(colorScale.quantiles()), function(d) {
            return d;
        })
        .enter().append("g")
        .attr("class", "legend");

    legend.append("rect")
        .attr("width", legendElementWidth)
        .attr("height", cellSize)
        .style("fill", function(d, i) {
            return colors[i];
        })
        .attr("x", width + cellSize)
        .attr("y", function(d, i) {
            return cellSize * i;
        });

    legend.append("text")
        .attr("class", "mono")
        .text(function(d) {
            return ">= " + Math.round(d);
        })
        .attr("x", width + cellSize + legendElementWidth)
        .attr("y", function(d, i) {
            return cellSize * (i + 1);
        });

    svg.append("text")
        .attr("class", "mono")
        .text("Counts")
        .attr("x", width + cellSize + legendElementWidth)
        .attr("y", 0);

    var exportCallback = function() {
        exportData(data);
    }
    exportDataButton.on("click", exportCallback);
}

function generateLists() {
    var options1 = dropDown1.selectAll("option")
        .data(columns)
        .enter().append("option");

    var options2 = dropDown2.selectAll("option")
        .data(columns)
        .enter().append("option");

    dropDown1.on("change", menuChanged1);
    dropDown2.on("change", menuChanged2);
    refreshButton.on("click", onRefresh);

    options1.text(function(d) {
        return d;
    })
        .attr("value", function(d) {
            return d;
        })
        .property("selected", function(d) {
            return d == rowvar;
        });

    options2.text(function(d) {
        return d;
    })
        .attr("value", function(d) {
            return d;
        })
        .property("selected", function(d) {
            return d == colvar;
        });

    options1.select(rowvar).attr("selected", "selected");
    options2.select(rowvar).attr("selected", "selected");
}

function menuChanged1() {
    var selectedValue = d3.event.target.value;
    rowvar = selectedValue;
}

function menuChanged2() {
    var selectedValue = d3.event.target.value;
    colvar = selectedValue;
}

function exportData(data) {
    // After http://stackoverflow.com/questions/17836273/export-javascript-data-to-csv-file-without-server-interaction
    csvRows = data.map(function(f) {
        return [f.row, f.col, f.count].join(',');
    });
    csvRows.unshift([rowvar, colvar, "count"].join(','));

    var csvString = csvRows.join("%0A");
    var a = document.createElement('a');
    a.href = 'data:attachment/csv,' + csvString;
    a.target = '_blank';
    a.download = 'count_data.csv';

    document.body.appendChild(a);
    a.click();
}

function exportPdf() {
    var svg = $('svg').get(0);
    var svgStr = svgToStr(svg);

    // Generate form to post to SVG to PDF creator
    var form = document.createElement('form');
    form.setAttribute('method', 'post');
    form.setAttribute('action', LABKEY.ActionURL.buildURL('visualization', 'exportPDF'));

    var hiddenField = document.createElement('input');
    hiddenField.setAttribute('type', 'hidden');
    hiddenField.setAttribute('name', 'svg');
    hiddenField.setAttribute('value', svgStr);
    form.appendChild(hiddenField);

    document.body.appendChild(form);
    form.submit();
}

function svgToStr(E) {
    var A;
    var D = "http://www.w3.org/2000/svg";
    var H = new RegExp("xmlns=[\"']" + D + "[\"']");
    var G = new RegExp("xmlns=[\"']" + D + "[\"']", "g");

    if (typeof XMLSerializer != "undefined") {
        A = (new XMLSerializer().serializeToString(E))
    } else {
        A = E.xml
    }
    var F = "";
    if (A.indexOf("xmlns=") == -1) {
        F = 'xmlns="' + D + '" '
    }
    A = A.replace(/<([^ ]+)/, "<$1 " + F + " ");
    var C = A.match(G);
    if (C.length > 1) {
        for (var B = 1; B < C.length; B++) {
            A = A.replace(H, "")
        }
    }
    return A
};