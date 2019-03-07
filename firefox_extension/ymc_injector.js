(function() {
  const id = browser.runtime.id;
  const url = browser.runtime.getURL('ymc_injection.js');
  
  let script = document.querySelector(`script[data-ymc-id='${id}']`);

  if (script) {
    return;
  }

  script = document.createElement('script');
  script.src = url;
  script.setAttribute('data-ymc-id', id);

  document.head.appendChild(script);

  const port = browser.runtime.connect({ name: 'ymc' });

  port.onMessage.addListener(message => {
    window.postMessage({ type: 'YMC_EXT', data: message }, '*');
  });

  window.addEventListener('message', event => {
    if (event.source !== window || !(event.data.type && event.data.type === 'YMC_PAGE')) {
      return;
    }

    port.postMessage(event.data.data);
  });
})();
