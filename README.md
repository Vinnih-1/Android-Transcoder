# Android Audio Transcoder

**Android Transcoder** Android Transcoder is an easy way to convert audio files between formats on Android, without ffmpeg-kit.

- [Installation](https://github.com/Vinnih-1/Android-Transcoder?tab=readme-ov-file#installation)
- [Quickstart](https://github.com/Vinnih-1/Android-Transcoder?tab=readme-ov-file#installation)
- [Supported formats](https://github.com/Vinnih-1/Android-Transcoder?tab=readme-ov-file#supported-formats)

## Installation

You can add Android Transcoder to your project using Gradle:

### Gradle (Kotlin DSL)
```
Not implemented yet.
```

## Quickstart
```kotlin
val file = AndroidTranscoder(file, AudioType.WAV, context) {
    Log.d(TAG, "Progress%: $it")
}.convert()
```

## Supported formats
- MP3
- WAV
- AAC // TODO
- M4A // TODO
- FLAC // TODO