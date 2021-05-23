const {
    RSocketClient,
    JsonSerializer,
    IdentitySerializer
} = require('rsocket-core');
const RSocketWebSocketClient = require('rsocket-websocket-client').default;

const toDataURL = url => fetch(url)
    .then(response => response.blob())
    .then(blob => new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result)
        reader.onerror = reject
        reader.readAsDataURL(blob)
    }))

// Create an instance of a client
const client = new RSocketClient({
    // send/receive objects instead of strings/buffers
    serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer
    },
    setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: 'application/json',
        // format of `metadata`
        metadataMimeType: 'message/x.rsocket.routing.v0',
    },
    transport: new RSocketWebSocketClient({url: 'ws://localhost:8080/rsocket'}),
});

var subscription = null;

// Open the connection
client.connect().subscribe({
    onError: error => console.error(error),
    onSubscribe: cancel => {/* call cancel() to abort */},
    onComplete: socket => {
        socket.requestStream({
            data: null,
            metadata: String.fromCharCode("cars".length) + "cars"
        })
        .subscribe({
            onComplete: () => console.log("requestStream done"),
            onError: error => {
                console.log("got error with requestStream");
                console.error(error);
            },
            onNext: value => {
                console.log("got next value in requestStream..");
                console.log(value.data);
                document.getElementById("rego").innerText = "Registration Number: " + value.data.rego;
                toDataURL("images/" + value.data.image).then(dataUrl => {
                    document.getElementById("image").src = dataUrl;
                    subscription.request(1);
                })
            },
            // Nothing happens until `request(n)` is called
            onSubscribe: sub => {
                console.log("subscribe request Stream!");
                subscription = sub;
                subscription.request(1);
            }
        });
    }
});