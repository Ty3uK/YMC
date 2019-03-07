(function() {
	let page;
	let app;

	const messageQueue = [];

	async function onConnect(port) {
		const tabs = await queryTabs({ url: '*://music.yandex.ru/*' });

		if (tabs.length > 1) {
			return;
		}

		page = port;
		app = chrome.runtime.connectNative('ymc');

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

	chrome.runtime.onConnect.addListener(onConnect);

	async function queryTabs(options) {
		return new Promise(resolve => chrome.tabs.query(options, resolve));
	}
})();
