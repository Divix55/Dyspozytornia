function load() {
    var map = new google.maps.Map(document.getElementById("map"), {
        center: new google.maps.LatLng(54.371765, 18.611966),
        zoom: 13,
        mapTypeId: 'roadmap'
    });
    var info = new google.maps.InfoWindow;

    function dodajMarker(lat, lng, txt) {
        var opcjeMarkera =
            {
                position: new google.maps.LatLng(lat, lng),
                map: map
            }
        var marker = new google.maps.Marker(opcjeMarkera);
        marker.txt = txt;

        google.maps.event.addListener(marker, "click", function () {
            info.setContent(marker.txt);
            info.open(map, marker);
        });
        return marker;
    }

    $(function () {
        var n = document.getElementsByClassName('name');
        var x = document.getElementsByClassName('lon');
        var y = document.getElementsByClassName('lat');
        var i = 0;
        $('.wiersz').each(function(){
            var marker = dodajMarker(x[i].innerHTML, y[i].innerHTML, n[i].innerHTML);
            i++;
        });
    });
}