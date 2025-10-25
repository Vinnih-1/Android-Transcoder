# Android Audio Transcoder

**Android Transcoder** Android Transcoder is an easy way to convert audio files between formats on Android, without ffmpeg-kit.

- [Installation](https://github.com/Vinnih-1/Android-Transcoder/edit/develop/README.md#installation)
- [Quickstart](https://github.com/Vinnih-1/Android-Transcoder/edit/develop/README.md#quickstart)
- [Supported formats](https://github.com/Vinnih-1/Android-Transcoder/edit/develop/README.md#supported-formats)

## Installation

You can add Android Transcoder to your project using Gradle:

### Gradle (Kotlin DSL)
```
Not implemented yet.
```

## Quickstart
```kotlin
AndroidTranscoder(
    file = file,
    to = AudioType.WAV,
    output = "teste1",
).converter(
    progress = { // Optional
        Log.d(TAG, "Progress: $it%")
    },
    success = { // Optional
        Log.d(TAG, "File created successfully: ${it.name}")
    },
    error = { // Optional
        Log.d(TAG, "Error while converting this file.")
    }
)
```

## Supported formats
- AAC // TODO
- MP3 // TODO
- M4A // TODO
- WAV
- FLAC // TODO