
var date = new Date()
var timeout_for_screensaver = date.getTime() + 15*60*1000; //add 15 minutes;


function startHorloge() {
    var today = new Date()
    var h = today.getHours();
    var m = today.getMinutes();
    var s = today.getSeconds();

    s = checkTime(s);
    m = checkTime(m);
    h = checkTime(h);

    document.getElementById('horloge').innerHTML = h + ":" + m + ":" + s;

    var t = setTimeout(startHorloge, 1000);
}

function checkTime(i) {
    if (i < 10) {i = "0" + i};  // add zero in front of numbers < 10
    return i;
}