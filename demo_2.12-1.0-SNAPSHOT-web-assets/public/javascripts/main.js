(async function() {

    const ws = await connectToServer();
    const tweetsDiv = document.getElementById('tweets')

    ws.onmessage = (webSocketMessage) => {
        console.log(webSocketMessage.data);
        const frag = document.createElement("div");

        frag.innerHTML = "<span>"+webSocketMessage.data+"</span>";

        tweetsDiv.appendChild(frag);
    };

    async function connectToServer() {
        const ws = new WebSocket('ws://localhost:8000/feed/stream/lucasrpb');
        return new Promise((resolve, reject) => {
            const timer = setInterval(() => {
                if(ws.readyState === 1) {
                    clearInterval(timer);
                    resolve(ws);
                }
            }, 10);
        });
    }

    /*function getOrCreateCursorFor(messageBody) {
        const sender = messageBody.sender;
        const existing = document.querySelector(`[data-sender='${sender}']`);
        if (existing) {
            return existing;
        }

        const template = document.getElementById('cursor');
        const cursor = template.content.firstElementChild.cloneNode(true);
        const svgPath = cursor.getElementsByTagName('path')[0];

        cursor.setAttribute("data-sender", sender);
        svgPath.setAttribute('fill', `hsl(${messageBody.color}, 50%, 50%)`);
        document.body.appendChild(cursor);

        return cursor;
    }*/

})();