(function() {
	let page;
	let app;

	const messageQueue = [];

	async function onConnect(port) {
		const tabs = await browser.tabs.query({ url: '*://music.yandex.ru/*' });

		if (tabs.length > 1) {
			return;
		}

		page = port;
		app = browser.runtime.connectNative('ymc');

		port.onMessage.addListener(message => {

			messageQueue.push(message);

			const messageFromQueue = messageQueue.shift();

			if (messageFromQueue) {
				app.postMessage(message);
			}
		});

		port.onDisconnect.addListener(() => app.disconnect());

		app.onMessage.addListener(message => page.postMessage(message));
	}

	browser.runtime.onConnect.addListener(onConnect);
})();
