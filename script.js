
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
    return x.Engine == engine;
  });
  return xs.map(function(x) {
    return x.Mean;
  });
}

function get_versions(points) {
  var versionsSet = new Set(points.map(function(x) {
    return x.Version;
  }));
  var versions = [];
  versionsSet.forEach(function(obj) {
    versions.push(obj);
  });

  versions.sort(compare_versions);
  return versions;
}

function plot_chart(div_id, title, points) {
  var data = {
    labels: get_versions(points)
  };
  data['datasets'] = ["V8", "JSC", "SM"].map(function(e) {
    return {
      data: get_points(points, e),
      label: e,
      fill: false,
      borderColor: colors[e],
      backgroundColor: backgroundColors[e],
      lineTension: 0
    };
  });

  var options = {
    title: {
      display: false,
      text: title
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
  var ctx = document.getElementById(div_id).getContext('2d');

  new Chart(ctx, {
    type: 'line',
    data: data,
    options: options
  });
}

function get_json(f_name, cb) {
  var r = new XMLHttpRequest();
  r.open("GET", f_name, true);
  r.onreadystatechange = function () {
    if (r.readyState != 4 || r.status != 200) return;
    var rows = JSON.parse(r.responseText);
    cb(rows);
  };
  r.send();
}

function process_stats(f_name) {
  get_json(f_name, function(rows) {
    document.querySelectorAll("#benchmarks li").forEach(function(obj, idx) {
      var title = obj.firstChild.textContent;
      var data_for_chart = rows[title];
      var div_id = "chart-" + (idx + 1);
      plot_chart(div_id, title, data_for_chart);
    });
    document.getElementById("loader").className = "";
  });
}

function process_sizes(f_name) {
  get_json(f_name, function(rows) {
    var ctx = document.getElementById("filesize").getContext('2d');

    var options = {
      title: {
        display: false,
        text: "File Size"
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
            labelString: 'Size (in bytes)'
          }
        }]
      }
    };
    var data = {
      labels: get_versions(rows)
    };
    var chart_data = rows.map(function(x) {
      return x.FileSize;
    });
    data['datasets'] = [{data: chart_data, fill: false, label: "Minified size", lineTension: 0}];
    new Chart(ctx, {
      type: 'line',
      data: data,
      options: options
    });
  });
}
