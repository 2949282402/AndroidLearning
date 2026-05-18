# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Android project. The app module lives in `app/`, with Kotlin source under `app/src/main/java/hejulian/ai/myapplication/`. UI code is organized by feature under `ui/`, shared UI components under `ui/components/`, app models under `core/model/`, and logging support under `core/logging/`. Android resources are in `app/src/main/res/`. Unit tests belong in `app/src/test/`, and instrumented Android tests belong in `app/src/androidTest/`.

## Build, Test, and Development Commands

Use the Gradle wrapper from the repository root:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

`assembleDebug` builds a debug APK. `compileDebugKotlin` is the fastest check for Kotlin and Compose compile errors. `testDebugUnitTest` runs local JVM tests. `connectedDebugAndroidTest` requires a running emulator or connected device.

## Coding Style & Naming Conventions

Use Kotlin with 4-space indentation and idiomatic Compose patterns. Name composables with PascalCase nouns or screens, such as `LogsScreen` or `HomeRoute`. Name ViewModels with the `ViewModel` suffix, such as `LogsViewModel`. Keep package names lowercase and aligned with their directory paths. Prefer small feature-scoped files over large mixed-purpose files. Avoid broad refactors when making targeted changes.

## Testing Guidelines

Use JUnit for local tests in `app/src/test/` and AndroidX/JUnit instrumentation tests in `app/src/androidTest/`. Name test classes after the subject under test, for example `LogsViewModelTest`. Add focused tests for state transformations, filtering logic, and non-trivial ViewModel behavior. Run `.\gradlew.bat :app:testDebugUnitTest` before submitting code that changes business logic.

## Commit & Pull Request Guidelines

Recent commits are short and imperative, often using a prefix such as `add:`. Follow that style with concise messages, for example `add: logs page filtering` or `fix: inject logs view model dependency`. Pull requests should include a clear summary, the commands run, screenshots for visible UI changes, and linked issues when applicable. Mention any known limitations or follow-up work.

## Security & Configuration Tips

Do not commit local machine settings, secrets, keystores, or generated build output. Keep environment-specific values in local files such as `local.properties`. Prefer Gradle version catalog updates in `gradle/libs.versions.toml` for dependency changes.
