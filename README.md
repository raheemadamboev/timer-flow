# timer-flow

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![](https://jitpack.io/v/raheemadamboev/timer-flow.svg)](https://jitpack.io/#raheemadamboev/timer-flow)

Light library to use Timer in Android. The library is implemented via Kotlin Coroutines and Kotlin Flows.

## How to use

Add it in your root **build.gradle** at the end of repositories:
```groovy
allprojects {
  repositories {
	  maven { url 'https://jitpack.io' }
  }
}
```  

Include below dependency in build.gradle of application and sync it:
```groovy
implementation 'com.github.raheemadamboev:timer-flow:1.0.1'
```

Create an object of Timer:

```kotlin
val timer = Timer()
```

Set timer duration in milliseconds (default: 10 000):

```kotlin
timer.timerDuration = 5_000L
```

Start timer:

```kotlin
timer.start()
```

Observe time of timer:

```kotlin
lifecycleScope.launch {
    timer.time.collectLatest { time ->
      // update UI, do something
      println(time.toString())
    }
}
```

Pause timer:

```kotlin
timer.pause()
```

Resume timer:

```kotlin
timer.resume()
```

Reset timer:

```kotlin
timer.reset()
```

You can also set checkpoint time (default: 3 000) that you will get notified once via Kotlin Channel when it is reached:

```kotlin
timer.timerCheckpoint = 2_500L

lifecycleScope.launch {
  timer.event.collectLatest { event ->
    when(event) {
      Started -> Unit // timer started
      Checkpoint -> Unit // checkpoint reached
      Finished -> Unit // timer finished
    }
  }
}
```
