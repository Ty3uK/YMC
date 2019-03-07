(function() {
  const scriptElement = document.querySelector('script[data-ymc-id]');
  const id = scriptElement && scriptElement.getAttribute('data-ymc-id');

  if (!id) {
    return;
  }

  let isPlaying;

  externalAPI.on(externalAPI.EVENT_READY, sendCurrentTrack);

  externalAPI.on(externalAPI.EVENT_TRACK, sendCurrentTrack);

  externalAPI.on(externalAPI.EVENT_CONTROLS, () => {
    postMessage('CONTROLS', externalAPI.getControls());
    postMessage('TOGGLE_LIKE', { state: getLikedState() });
  });

  externalAPI.on(externalAPI.EVENT_STATE, () => {
    const playingState = externalAPI.isPlaying();

    if (isPlaying !== playingState) {
      isPlaying = playingState;
      postMessage('PLAYING', { state: isPlaying });
    }
  });

  window.addEventListener('message', event => {
    if (event.source !== window || !(event.data.type && event.data.type === 'YMC_EXT')) {
      return;
    }

    const message = event.data.data;
    const type = message.type;

    if (type === 'PLAY_PAUSE') {
      externalAPI.togglePause();
    } else if (type === 'PREV') {
      externalAPI.prev();
    } else if (type === 'NEXT') {
      externalAPI.next();
    } else if (type === 'TOGGLE_LIKE') {
      externalAPI.toggleLike().then(success => {
        if (!success) {
          return;
        }

        postMessage(type, { state: getLikedState() });
      });
    } else if (type === 'REFRESH') {
      postMessage('PLAYER_STATE', {
        currentTrack: getTrackInfo(),
        controls: externalAPI.getControls(),
        isPlaying: externalAPI.isPlaying()
      });
    }
  });

  function sendCurrentTrack() {
    postMessage('CURRENT_TRACK', getTrackInfo());
  }

  function getTrackInfo() {
    const trackInfo = externalAPI.getCurrentTrack();

    if (!trackInfo) {
      return {};
    }

    return {
      title: trackInfo.title,
      artist: trackInfo.artists.map(it => it.title).join(', '),
      cover: `https://${trackInfo.cover.slice(0, -3)}/400x400`,
      liked: trackInfo.liked,
      link: `${location.origin}${trackInfo.link}`
    }
  }

  function getLikedState() {
    const trackInfo = externalAPI.getCurrentTrack();
    return !!(trackInfo && trackInfo.liked);
  }

  function postMessage(type, data) {
    window.postMessage({ type: 'YMC_PAGE', data: { type: type, data: data }}, '*');
  }
})();
