import { query } from '../base/base';

import sourceLow from '../../static/video-low.mp4';
import sourceHigh from '../../static/video-high.mp4';

function insertVideo() {
  const videoElement = document.createElement('video');

  videoElement.classList.add('laptop_video');
  videoElement.setAttribute('autoplay', '');
  videoElement.setAttribute('loop', '');
  videoElement.setAttribute('muted', '');
  videoElement.setAttribute('playisinline', '');

  const sourceElement = document.createElement('source');

  sourceElement.setAttribute(
    'src',
    window.innerWidth >= 1600
      ? sourceHigh
      : sourceLow,
  );
  sourceElement.setAttribute('type', 'video/mp4');

  videoElement.appendChild(sourceElement);

  query('.laptop').appendChild(videoElement);
}

document.addEventListener('DOMContentLoaded', () => {
  if (!navigator.connection || !navigator.connection.saveData) {
    setTimeout(() => insertVideo(), 1500);
  }
});
