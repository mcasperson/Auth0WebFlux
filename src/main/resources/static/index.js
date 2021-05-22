const {
    RSocketClient,
    JsonSerializers,
} = require('rsocket-core');
const RSocketWebSocketClient = require('rsocket-websocket-client').default;

// Create an instance of a client
const client = new RSocketClient({
    // send/receive objects instead of strings/buffers
    serializers: JsonSerializers,
    setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: 'application/json',
        // format of `metadata`
        metadataMimeType: 'application/json',
    },
    transport: new RSocketWebSocketClient({url: 'wss://localhost:7000'}),
});

// Open the connection
client.connect().subscribe({
    onComplete: socket => {
        socket.requestStream({
            data: { message: "request - stream from javascript!" },
            metadata: ""
        })
        .subscribe({
            onComplete: () => console.log("requestStream done"),
            onError: error => {
                console.log("got error with requestStream");
                console.error(error);
            },
            onNext: value => {
                // console.log("got next value in requestStream..");
                console.log(value.data);
            },
            // Nothing happens until `request(n)` is called
            onSubscribe: sub => {
                console.log("subscribe request Stream!");
                sub.request(7);
            }
        });
    },
    onError: error => console.error(error),
    onSubscribe: cancel => {/* call cancel() to abort */}
});