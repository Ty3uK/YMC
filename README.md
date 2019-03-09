<h1 align="center">
  <img src=".github/ym.svg" alt="Yandex Music Logo" width="128">
  <br>
  Yandex.Music Control
</h1>

<h5 align="center">
  Control Yandex.Music from any window in MacOS.
  <br>
  <br>
  <img src=".github/firefox.svg" width="24">&nbsp;
  <img src=".github/chrome.svg" width="24">&nbsp;
  <img src=".github/chromium.svg" width="24">&nbsp;
  <img src=".github/opera.svg" width="24">&nbsp;
  <img src=".github/yandex.svg" width="24">&nbsp;
  <img src=".github/vivaldi.svg" width="24">
</h5>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#todo">TODO</a> •
  <a href="#hotkeys">Hotkeys</a> •
  <a href="#install">Install</a> •
  <a href="#debug">Debug</a> •
  <a href="#external-libraries">External libraries</a> •
  <a href="#license">License</a>
</p>

<p align="center">
  <img src=".github/example.gif" alt="Example GIF">
</p>

## Features

- Control playback from any window or screen through top bar popup
- Control playback with Mac native media keys
- Get current track info with notification
- Share link to track through native sharing menu

## TODO

- [ ] Handle headphones media keys

## Hotkeys

| **Function**                | **Hotkey**                 |
|-----------------------------|----------------------------|
| **Play**                    | Play/Pause track           |
| **Next**                    | Switch to next track       |
| **Prev**                    | Switch to previous track   |
| **&#8984;** &#43; **Play**  | Show current track info    |
| **&#8984;** &#43; **Next**  | Like/Unlike current track  |
| **&#8984;** &#43; **Prev**  | Copy current track link    |

## Install

- Download and install extension for your favorite browser from [release page](https://github.com/Ty3uK/YMC/releases)
- Download application from [release page](https://github.com/Ty3uK/YMC/releases) and copy to `Applications` folder
- Run application and wait for message `Manifest updated`, click `Got it`
- When you visit `music.yandex.ru` application will be started automatically

If you want to use native media keys - enable your browser in accessibility settings (`System Preferences → Security & Privacy → Accessibility`) and reload yandex music tab in browser

## Debug

When `Enable debugging` checkbox is checked, application will write log file to `/tmp/ymc.log`. Attach log file in issue, please.

## External libraries

- [RxSwift](https://github.com/ReactiveX/RxSwift)
- [SwiftyBeaver](https://github.com/SwiftyBeaver/SwiftyBeaver)
- [MediaKeyTap](https://github.com/nhurden/MediaKeyTap) ([fork](https://github.com/Ty3uK/MediaKeyTap))
- [Files](https://github.com/JohnSundell/Files)

## License

[MIT](LICENSE)
