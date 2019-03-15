import { query } from '../base/base';

import sourceLow from '../../static/video-low.mp4';
import sourceHigh from '../../static/video-high.mp4';

function insertSource(videoElement) {
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
  if (navigator.connection && navigator.connection.saveData) {
    return;
  }

  const videoElement = query('.laptop_video');
  const startTime = Date.now();

  insertSource(videoElement);

  videoElement.addEventListener('canplay', () => {
    const delta = Date.now() - startTime;
    const animate = () => query('.laptop_video').classList.add('animated');

    if (delta >= 1500) {
      animate();
    } else {
      setTimeout(animate.bind(this), 1500 - delta);
    }
  });
});
