var canvas = document.getElementById("mainCanvas");
canvas.width = window.innerWidth - 4;
canvas.height =  window.innerHeight - 70;
var context = canvas.getContext("2d");

var myId;
var sprites = [];
var blocks = [];

window.addEventListener("beforeunload", function () {
    pageUnload();
});

function pageLoad () {
    fetch("/game/new", {method: 'POST'}).then(response => response.json()).then(data => {
        myId = data.id;
        document.getElementById("id").innerHTML = "my id: " + myId;
        setInterval(updateSpriteList, 10);
        setInterval(drawCanvas, 10);
    });

    fetch("/game/getBlocks", {method: 'GET'}).then(response => response.json()).then(data => {
        for (let d of data) {
            blocks.push({x: d.x, y: d.y, size: d.size});
        }
    });
}

function pageUnload () {
    var formData = new FormData();
    alert();
    formData.append("id", myId);
    fetch("game/delete", {method: 'POST', body: formData}).then(response => response.json()).then(data => {
        alert("Deleted myself :(");
    });
}

function updateSpriteList () {
    fetch("/game/list", {method: 'GET'}).then(response => response.json()).then(data => {
        var newSprite = false;
        for (let d of data) {
            var newSprite = true;
            for (let s of sprites) {
                if (s.id == d.id) {
                    newSprite = false;
                    s.x = d.x;
                    s.y = d.y;
                    s.size = d.size;
                }
            }

            if (newSprite) {
                sprites.push({id: d.id, x: d.x, y: d.y, size: d.size});
            }
        }
    });
}

function drawCanvas () {
    requestAnimationFrame(drawCanvas);
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.fillStyle = "#888888";
    for (var i = 0; i < sprites.length; i++) {
        context.fillRect(sprites[i].x, sprites[i].y, sprites[i].size, sprites[i].size);
    }
    for (var i = 0; i < blocks.length; i++) {
        context.fillRect(blocks[i].x, blocks[i].y, blocks[i].size, blocks[i].size);
    }
}