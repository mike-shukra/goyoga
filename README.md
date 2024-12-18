# GoYoga Android Application

## Описание
**GoYoga** — это современное Android-приложение, разработанное для удобного взаимодействия пользователей с сервером и предоставления передового пользовательского интерфейса. Оно использует самые современные технологии, такие как Jetpack Compose, Hilt, Firebase, а также интеграцию с REST API через Retrofit.

## Стек технологий

### Основные языки и инструменты
- **Kotlin**: Основной язык разработки, обеспечивающий безопасность типов и лаконичность кода.
- **Gradle**: Инструмент для сборки проекта и управления зависимостями.

### Android SDK
- **Минимальная версия SDK**: 31
- **Целевая версия SDK**: 34

### Архитектура
- **MVVM**: Организация кода на основе модели-представления-представления модели.
- **Jetpack Compose**: Современный инструмент для декларативного построения пользовательских интерфейсов.
- **Navigation Component**: Для навигации между экранами.

### Зависимости и технологии
#### Интерфейс пользователя
- **Jetpack Compose**: Построение UI с использованием:
  - `androidx.compose.material` для стилизованных компонентов.
  - `ConstraintLayout` для сложных компоновок.
  - `Navigation Compose` для работы с навигацией.

#### Управление зависимостями
- **Dagger Hilt**: Инструмент для внедрения зависимостей, который упрощает управление жизненным циклом компонентов.

#### Работа с сетью
- **Retrofit2**: Для выполнения REST-запросов.
- **Moshi**: Для преобразования JSON-данных в Kotlin-объекты.
- **OkHttp3**: Для управления запросами и логирования.

#### База данных
- **Room**: Локальная база данных для хранения данных с поддержкой Coroutines и LiveData.

#### Firebase
- **Firebase Authentication**: Для авторизации пользователей.
- **Firebase Analytics**: Для аналитики.
- **Firebase UI**: Для упрощенной настройки экранов входа.

#### Оплата
- **Google Play Billing**: Для работы с подписками и внутриигровыми покупками.

#### Загрузка изображений
- **Picasso**: Для загрузки и кэширования изображений.

#### Корутины
- **Kotlin Coroutines**: Для асинхронной работы.

#### Тестирование
- **JUnit**: Для модульного тестирования.
- **Mockito**: Для создания mock-объектов.
- **Espresso**: Для тестирования пользовательского интерфейса.
- **AndroidX Testing**: Для инструментального тестирования.

## Установка и сборка
1. Клонируйте репозиторий:

    ```bash
    git clone https://github.com/yourusername/goYogaAndroidApp.git
    ```

2. Перейдите в директорию проекта:

    ```bash
    cd goYogaAndroidApp
    ```

3. Настройте файл `google-services.json` для интеграции Firebase. Добавьте его в директорию `app/`.

4. Запустите приложение с помощью Android Studio.

## Функциональность
- Авторизация через Firebase (включая Google Sign-In).
- Управление локальной базой данных с помощью Room.
- Асинхронная загрузка данных через Retrofit.
- Поддержка Jetpack Compose для быстрого обновления UI.

