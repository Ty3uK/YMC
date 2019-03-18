<p align="right">
  <a href="README.md">English version</a>
</p>

<h1 align="center">
  <img src=".github/ym.svg" alt="Yandex Music Logo" width="128">
  <br>
  Yandex.Music Control
</h1>

<h5 align="center">
  Управляйте Яндекс.Музыкой из любого окна macOS
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
  <a href="#возможности">Возможности</a> •
  <a href="#todo">TODO</a> •
  <a href="#горячие-клавиши">Горячие клавиши</a> •
  <a href="#установка">Установка</a> •
  <a href="#отладка">Отладка</a> •
  <a href="#сторонние-библиотеки">Сторонние библиотеки</a> •
  <a href="#лицензия">Лицензия</a>
</p>

<p align="center">
  <img src=".github/example.gif" alt="Example GIF">
</p>

## Возможности

- Управляйте воспроизведением из любого окна или экрана при помощи виджета в верхней панели
- Управляйте воспроизведением при помощи стандартных медиа-клавиш Мак-устройства
- Получайте информацию о текущей композиции при помощи уведомления
- Делитесь ссылкой на текущую композицию через системное меню "Поделиться"

## TODO

- [ ] Обрабатывать нажатия на кнопки гарнитур
- [ ] Обрабатывать другие домены (не только music.yandex.ru)

## Горячие клавиши

| **Функции**                 | **Горячие клавиши**                                     |
|-----------------------------|---------------------------------------------------------|
| **Play**                    | Воспроизведение/Пауза                                   |
| **Next**                    | Включить следующую композицию                           |
| **Prev**                    | Включить предыдущую композицию                          |
| **&#8984;** &#43; **Play**  | Показать информацию о текущей композиции                |
| **&#8984;** &#43; **Next**  | Поставить/Снять отмеку "Нравится" с текущей композиции  |
| **&#8984;** &#43; **Prev**  | Скопировать ссылки на текущую композицию                |

## Установка

- Скачайте и установите расширение для Вашего любимого браузера со [страницы релизов](https://github.com/Ty3uK/YMC/releases)
- Скачайте приложение со [страницы релизов](https://github.com/Ty3uK/YMC/releases) и скопируйте в папку `Программы`
- Запустите приложение и дождитесь сообщения `Manifest updated`, нажмите `Got it`
- Приложение запустится автоматически при посещении `music.yandex.ru`

Если Вы хотите управлять воспроизведением при помощи стандартных медиа-клавиш - включите браузер в настройках универсального доступа (`Системные настройки → Защита и безопасность → Универсальный доступ`) и обновите вкладку яндекс.музыки в браузере.

#### Note

If you get errors like `The application can't be opened` or `App is damaged and can't be opened` run this command in terminal:

```bash
sudo spctl --master-disable
```

## Отладка

При включенной настройке `Enable debugging` приложение записывает лог в файл `/tmp/ymc.log`. Пожалуйста, прикладывайте файл лога при создании ишью.

## Сторонние библиотеки

- [RxSwift](https://github.com/ReactiveX/RxSwift)
- [SwiftyBeaver](https://github.com/SwiftyBeaver/SwiftyBeaver)
- [MediaKeyTap](https://github.com/nhurden/MediaKeyTap) ([fork](https://github.com/Ty3uK/MediaKeyTap))
- [Files](https://github.com/JohnSundell/Files)
- [BinUtils](https://github.com/nst/BinUtils)

## Лицензия

[MIT](LICENSE)
