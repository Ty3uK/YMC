(function() {
	let page;
	let app;

	const messageQueue = [];

	async function onConnect(port) {
		let tabs = (await queryTabs({ url: '*://*.yandex.ru/*' })) || [];

		tabs = tabs.filter(tab => tab.url.indexOf('music.') !== -1 || tab.url.indexOf('radio.') !== -1);

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
