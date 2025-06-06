# Тестовое задание для стажёра Android-направления
## Описание задания 
Разработать Android-приложение, которое позволяет:
- Искать музыку через открытую API
- Воспроизводить найденные треки
- Управлять проигрыванием (пауза, продолжение, следующая/предыдущая песня)
- Воспроизводить локальную музыку и искать по ней в том числе

## Технологический стек
- **Язык программирования:** Kotlin
- **Архитектура:** Clean Architecture
- **UI:** XML + Fragments
- **Паттерн проектирования:** MVVM
- **Библиотеки:**
    - Coroutines
    - Retrofit
    - Kotlinx Serialization
    - Glide
    - Media3 ExoPlayer

## Инструкция по запуску
[Скачать apk](https://github.com/vladryanka/MusicPlayer/releases/download/v1.0/app-debug.apk) последней версии и установить на устройство

## Функциональность приложения
### Экран скачанных треков
- Отображение списка локально сохранённых треков с помощью `RecyclerView`
- Поле поиска по названию и исполнителю
- Элемент списка:
    - Обложка трека
    - Название
    - Автор
- По тапу на трек — переход на экран воспроизведения

### Экран треков из [Deezer API](https://developers.deezer.com/)
- [Загрузка чарта](https://api.deezer.com/chart) популярных треков
- [Поиск по запросу](https://api.deezer.com/search?q={query})
- [Трек списка](https://api.deezer.com/track/{id}):
    - Обложка (или заглушка)
    - Название
    - Автор
- При выборе — переход на экран воспроизведения с превью-треком

### Навигация
- Нижняя панель `BottomNavigation` с двумя вкладками:
    - Скачанные треки
    - Онлайн-треки
- Реализовано через **Navigation Component**

### Экран воспроизведения
- Отображает:
    - Обложку трека или альбома
    - Название трека и альбома (если есть)
    - Имя исполнителя
- Прогресс-бар с возможностью перемотки
- Управление:
    - Play / Pause
    - Следующий трек
    - Предыдущий трек
- Тайминг в формате `mm:ss` (текущее время / длительность)

### Фоновый плеер
- Музыка продолжает играть при сворачивании приложения
- Уведомление в статус-баре с контролами:
    - Play / Pause
    - Переключение треков

## Итоги
- Впервые поработала с медиа аудио-контентом при реализации плеера
- Освоила работу с фоновыми задачами с помощью сервиса и broadcast receiver
- Научилась обрабатывать действия из уведомлений
- Следовала принципам Clean Architecture
- Применяла паттерн проектирования MVVM
- Реализовала навигацию с использованием Type-safe Jetpack Navigation
- Настроила пагинацию для загрузки данных

## Перспективы развития / Что не удалось реализовать
- Многомодульная архитектура
- Загрузка данных в уведомлении именно из альбома
- Внедрение Dagger2
- Улучшение UI (дизайна)
- Универсальный экран для списков с возможностью менять UI

## Скриншоты

<img src="https://github.com/user-attachments/assets/b9c83fb5-199b-403a-b07e-40f7cf5fa0a4" width="33%" alt="Image 1">
<img src="https://github.com/user-attachments/assets/24d3be33-bd44-4afc-bed1-46706ceded82" width="33%" alt="Image 2">
<img src="https://github.com/user-attachments/assets/597f6059-39a3-4569-84a3-c0f32a5b5d5d" width="33%" alt="Image 3">
<img src="https://github.com/user-attachments/assets/2f285066-6dc9-4836-98de-aa0e6d7bb187" width="33%" alt="Image 4">
<img src="https://github.com/user-attachments/assets/f573f12f-b2eb-4203-ac10-0ec78b6340f2" width="33%" alt="Image 5">
