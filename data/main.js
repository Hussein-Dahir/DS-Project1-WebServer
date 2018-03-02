function openLink(evt, animName) {
    var i, x, tablinks;
    x = document.getElementsByClassName("author");
    for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";
    }
    document.getElementById(animName).style.display = "block";
    document.getElementById(animName).style.opacity = 1;
    // document.getElementById(animName).style.visibility = "visible";
}

function moveDown() {
    var elem = document.getElementById("animate");
    var pos = 0;
    var id = setInterval(frame, 5);

    function frame() {
        if (pos == 350) {
            clearInterval(id);
        } else {
            pos++;
            elem.style.top = pos + 'px';

        }
    }
}

function moveUp() {
    var elem = document.getElementById("animate");
    var pos = 350;
    var id = setInterval(frame, 5);

    function frame() {
        if (pos == 0) {
            clearInterval(id);
        } else {
            pos--;
            elem.style.top = pos + 'px';

        }
    }
}