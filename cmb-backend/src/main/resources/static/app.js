let stc;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

async function connect() {
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "username": "dehemi",
        "password": "qwertyuiop"
    });

    const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
    };

    let res = await fetch("http://localhost:8080/user/token", requestOptions);
    let tokens = await res.json();

    stc = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/events',
        connectHeaders: {
            passcode: tokens.accessToken
        }
    });

    stc.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stc.subscribe('/topic/transactions', (greeting) => {
            showGreeting(greeting.body);
        });
    };

    stc.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stc.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    stc.activate();
}

function disconnect() {
    stc.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stc.publish({
        destination: "/app/hello",
        body: JSON.stringify({'name': $("#name").val()})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});