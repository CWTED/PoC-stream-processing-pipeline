
// min chart 
var canvas1 = document.getElementById('minChart');
var minData = {
  labels: ['-', '-', '-', '-', '-', '-', '-', '-', '-', '-'],
  datasets: [
    {
      label: "10 Latest values for Minute NaN Frequency (mans//TRUCK-ID-00//engineOilTemp)",
      fill: false,
      lineTension: 0.1,
      backgroundColor: "rgba(75,192,192,0.4)",
      borderColor: "rgba(75,192,192,1)",
      borderCapStyle: 'butt',
      borderDash: [],
      borderDashOffset: 0.0,
      borderJoinStyle: 'miter',
      pointBorderColor: "rgba(75,192,192,1)",
      pointBackgroundColor: "#fff",
      pointBorderWidth: 1,
      pointHoverRadius: 5,
      pointHoverBackgroundColor: "rgba(75,192,192,1)",
      pointHoverBorderColor: "rgba(220,220,220,1)",
      pointHoverBorderWidth: 2,
      pointRadius: 5,
      pointHitRadius: 10,
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    }
  ]
};
var option = {
  showLines: true
};
var minChart = Chart.Line(canvas1, {
  data: minData,
  options: option
});

//////////////////////////////////////////////////////////////////

var canvas2 = document.getElementById('hourChart');
var hourData = {
  labels: ['-', '-', '-', '-', '-', '-', '-', '-', '-', '-'],
  datasets: [
    {
      label: "10 Latest values for Hour NaN Frequency (mans//TRUCK-ID-00//engineOilTemp)",
      fill: false,
      lineTension: 0.1,
      backgroundColor: "rgba(75,192,192,0.4)",
      borderColor: "rgba(192,75,75,1)",
      borderCapStyle: 'butt',
      borderDash: [],
      borderDashOffset: 0.0,
      borderJoinStyle: 'miter',
      pointBorderColor: "rgba(75,192,192,1)",
      pointBackgroundColor: "#fff",
      pointBorderWidth: 1,
      pointHoverRadius: 5,
      pointHoverBackgroundColor: "rgba(75,192,192,1)",
      pointHoverBorderColor: "rgba(220,220,220,1)",
      pointHoverBorderWidth: 2,
      pointRadius: 5,
      pointHitRadius: 10,
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    }
  ]
};
var option = {
  showLines: true
};
var hourChart = Chart.Line(canvas2, {
  data: hourData,
  options: option
});

////////////////////////////////////////////////////////

document.addEventListener("DOMContentLoaded", () => {
  periodicFetcher();
});

function periodicFetcher() {
  fetcher();
  setTimeout(periodicFetcher, 20);
}

function fetcher() {
  const url = 'http://localhost:3000'; // URL of your Express.js server

  fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json(); // Parse the JSON response
    })
    .then(data => {
      // Update the content of the <div> with the fetched data
      const resultDiv = document.getElementById('result');
      resultDiv.textContent = JSON.stringify(data);
      pushJSON(data)
    })
    .catch(error => {
      console.error('Error fetching data:', error);
    });
}

function pushJSON(jsonData) {

  // print response to console
  console.log(JSON.stringify(jsonData));

  // extract values from JSON
  const minFreqValues = jsonData[0];
  const hourFreqValues = jsonData[1];
  const minFreq = minFreqValues.minFreq;
  const minFreqTS = minFreqValues.__time;
  //THIS IS SAME AS __time
  const minFreqTSe = minFreqValues.eventTime;
  const hourFreqTSe = hourFreqValues.eventTime;
  const hourFreq = hourFreqValues.hourFreq;
  const hourFreqTS = hourFreqValues.__time;

  const kafkaTSm = minFreqValues.kafkaTS;
  const kafkaTSh = hourFreqValues.kafkaTS;


  const current = minFreqValues.current;
  const currentDate = new Date(current);
  const druidIngestion = currentDate.getTime();

  const eventLong = new Date(minFreqTS);
  const eTime = eventLong.getTime();

  const minFreqDate = new Date(minFreqTS);
  const minHours = ("0" + minFreqDate.getHours()).slice(-2);     // Get hours (with leading zero)
  var minMinutes = ("0" + minFreqDate.getMinutes()).slice(-2); // Get minutes (with leading zero)
  var minSeconds = ("0" + (minFreqDate.getSeconds() + 1)).slice(-2); // Get seconds (with leading zero)

  const hourFreqDate = new Date(hourFreqTS);

  const hourHours = ("0" + hourFreqDate.getHours()).slice(-2);     // Get hours (with leading zero)
  var hourMinutes = ("0" + (hourFreqDate.getMinutes() + 1)).slice(-2); // Get minutes (with leading zero)
  var hourSeconds = ("0" + (hourFreqDate.getSeconds() + 1)).slice(-2); // Get seconds (with leading zero)

  if (minSeconds === "60") {
    minSeconds = "00";
  }

  if (hourSeconds === "60") {
    hourSeconds = "00";
  }

  if (minMinutes === "60") {
    minMinutes = "00";
  }

  if (hourMinutes === "60") {
    hourMinutes = "00";
  }

  // Construct the formatted time strings
  const minFormattedTime = `${minHours}:${minMinutes}:${minSeconds}`;
  const hourFormattedTime = `${hourHours}:${hourMinutes}:${hourSeconds}`;

  // push to minChart
  minChart.data.datasets[0].data.shift();
  minChart.data.datasets[0].data.push(minFreq);
  minChart.data.labels.shift();
  minChart.data.labels.push(minFormattedTime);
  minChart.update();
  const minCounter = document.getElementById('minCounter');
  minCounter.textContent = minFreq;

  // push to hourChart
  hourChart.data.datasets[0].data.shift();
  hourChart.data.datasets[0].data.push(hourFreq);
  hourChart.data.labels.shift();
  hourChart.data.labels.push(hourFormattedTime);
  hourChart.update();
  const hourCounter = document.getElementById('hourCounter');
  hourCounter.textContent = hourFreq;

  var add = 60 * 60 * 1000 * 2;
  const minAggEndResult = kafkaTSm - minFreqTSe + add;
  const hourAggEndResult = kafkaTSh - hourFreqTSe + add;

  // push to min latency
  minLatencyChart.data.datasets[0].data.shift();
  minLatencyChart.data.datasets[0].data.push(minAggEndResult);
  minLatencyChart.data.labels.shift();
  minLatencyChart.data.labels.push(minFormattedTime);
  minLatencyChart.update();
  const minLatencyCounter = document.getElementById('minLatencyCounter');
  minLatencyCounter.textContent = minAggEndResult;

  // push to hour latency
  hourLatencyChart.data.datasets[0].data.shift();
  hourLatencyChart.data.datasets[0].data.push(hourAggEndResult);
  hourLatencyChart.data.labels.shift();
  hourLatencyChart.data.labels.push(hourFormattedTime);
  hourLatencyChart.update();
  const hourLatencyCounter = document.getElementById('hourLatencyCounter');
  hourLatencyCounter.textContent = hourAggEndResult;

  const aggEndDiv = document.getElementById('aggEndResult');
  aggEndDiv.textContent = "kafkaTSm: " + kafkaTSm + ", min latency (millis): " + minAggEndResult + ", hour latency (millis):" + hourAggEndResult;

  const value = {
    res: minAggEndResult,
    druidTime: new Date(druidIngestion),
    eTid: new Date(eTime)
  }

  appendValueTextArea(value);
}

/////////////////////////////////////////////////////

var canvas3 = document.getElementById('minLatencyChart');
var minLatencyData = {
  labels: ['-', '-', '-', '-', '-', '-', '-', '-', '-', '-'],
  datasets: [
    {
      label: "10 Latest latency values for Minute NaN Frequency (mans//TRUCK-ID-00//engineOilTemp)",
      fill: false,
      lineTension: 0.1,
      backgroundColor: "rgba(75,192,192,0.4)",
      borderColor: "rgba(74,74,191,1)",
      borderCapStyle: 'butt',
      borderDash: [],
      borderDashOffset: 0.0,
      borderJoinStyle: 'miter',
      pointBorderColor: "rgba(75,192,192,1)",
      pointBackgroundColor: "#fff",
      pointBorderWidth: 1,
      pointHoverRadius: 5,
      pointHoverBackgroundColor: "rgba(75,192,192,1)",
      pointHoverBorderColor: "rgba(220,220,220,1)",
      pointHoverBorderWidth: 2,
      pointRadius: 5,
      pointHitRadius: 10,
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    }
  ]
};
var option = {
  showLines: true
};
var minLatencyChart = Chart.Line(canvas3, {
  data: minLatencyData,
  options: option
});

//////////////////////////////////////////////////////////////////

var canvas4 = document.getElementById('hourLatencyChart');
var hourLatencyData = {
  labels: ['-', '-', '-', '-', '-', '-', '-', '-', '-', '-'],
  datasets: [
    {
      label: "10 Latest latency values for Hour NaN Frequency (mans//TRUCK-ID-00//engineOilTemp)",
      fill: false,
      lineTension: 0.1,
      backgroundColor: "rgba(75,192,192,0.4)",
      borderColor: "rgba(191,191,74,1)",
      borderCapStyle: 'butt',
      borderDash: [],
      borderDashOffset: 0.0,
      borderJoinStyle: 'miter',
      pointBorderColor: "rgba(75,192,192,1)",
      pointBackgroundColor: "#fff",
      pointBorderWidth: 1,
      pointHoverRadius: 5,
      pointHoverBackgroundColor: "rgba(75,192,192,1)",
      pointHoverBorderColor: "rgba(220,220,220,1)",
      pointHoverBorderWidth: 2,
      pointRadius: 5,
      pointHitRadius: 10,
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    }
  ]
};
var option = {
  showLines: true
};
var hourLatencyChart = Chart.Line(canvas4, {
  data: hourLatencyData,
  options: option
});

/////////////////////////////////////////////////////

function appendValueTextArea(value) {
  const eTid = value.eTid.toLocaleTimeString([], { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit', fractionalSecondDigits: 3 });
  const csvTuple = `${eTid},${value.res}\n`;
  const textarea = document.getElementById('csvTextarea');
  textarea.value += csvTuple;
  textarea.scrollTop = textarea.scrollHeight;
}

/////////////////////////////////////////////

