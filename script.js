
var pad = "00000";

function left_pad(s) {
  return pad.substring(0, pad.length - s.length) + s;
}

function get_ver(x) {
  var parts = x.split('.')
    .map(function(x) {
      return left_pad(parseInt(x))
    });
  return parts.join("-");
}

function compare_versions(x, y) {
  var parts1 = get_ver(x);
  var parts2 = get_ver(y);
  if (parts1 < parts2) {
    return -1;
  }

  if (parts1 > parts2) {
    return 1;
  }
  return 0;
}

function get_points(points, engine) {
  var xs = points.filter(function(x) {
    return x.engine == engine
  });
  return xs.map(function(x) {
    return x.mean;
  });
}

function get_versions(points) {
  var versionsSet = new Set(points.map(function(x) {
    return x.version
  }));
  var versions = [];
  versionsSet.forEach(function(obj) {
    versions.push(obj);
  });

  versions.sort(compare_versions);
  return versions;
}

// Colors from http://colorbrewer2.org/
var colors = {
  V8: '#d7191c',
  JSC: '#fdb863',
  SM: '#2c7bb6'
};
// d3.rgb("#2c7bb6").darker().toString();
var backgroundColors = {
  V8: '#961113',
  JSC: "#b18045",
  SM: '#1e567f'
};

function plot_chart(id, title, points) {
  var data = {
    labels: get_versions(points)
  };
  data['datasets'] = ["V8", "JSC", "SM"].map(function(e) {
    return {
      data: get_points(points, e),
      label: e,
      fill: false,
      borderColor: colors[e],
      backgroundColor: backgroundColors[e]
    };
  });

  var options = {
    title: {
      display: false,
      text: 'Chart.js Line Chart'
    },
    scales: {
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Version'
        }
      }],
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Time per iteration (in seconds)'
        }
      }]

    }
  };
  var ctx = document.getElementById("chart-" + id).getContext('2d');

  new Chart(ctx, {
    type: 'line',
    data: data,
    options: options
  });
}

d3.csv("data.csv")
  .row(function(d) {
    return {
      version: d.Version,
      engine: d.Engine,
      name: d.Name,
      mean: +d.Mean
    };
  })
  .get(function(error, rows) {
    d3.selectAll('#benchmarks li')[0].forEach(function(obj, idx) {
      var title = d3.select(obj).text();
      var data_for_chart = rows.filter(function(x) {
        return x.name == title;
      });
      plot_chart(idx + 1, title, data_for_chart);
    });
    document.getElementById("loader").className = "";
  });