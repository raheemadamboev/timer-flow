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

*Create an object of Timer:*

```kotlin
val timer = Timer()
```

*Set timer duration in milliseconds (default: 10 000):*

```kotlin
timer.timerDuration = 5_000L
```

*Start timer:*

```kotlin
timer.start()
```

*Observe time of timer:*

```kotlin
lifecycleScope.launch {
    timer.time.collectLatest { time ->
      // update UI, do something
      println(time.toString())
    }
}
```

*Pause timer:*

```kotlin
timer.pause()
```
If you pause timer, do not forget to stop() it. Otherwise, it runs forever.

*Resume timer:*

```kotlin
timer.resume()
```

*Reset timer:*

```kotlin
timer.reset()
```

*Set checkpoint time (default: 3 000) that you will get notified when it is reached. It is implemented via Kotlin Channel and received as Kotlin Flow. So you will only get notified once:*

```kotlin
timer.timerCheckpoint = 2_500L

lifecycleScope.launch {
  timer.event.collectLatest { event ->
    when(event) {
      Started -> Unit // timer started
      Checkpoint -> Unit // checkpoint reached
      Finished -> Unit // timer finished naturally, not programmatically
    }
  }
}
```

*Observe timer states. It is implemented via Kotlin StateFlow so you always get the current timer state:*

```kotlin
lifecycleScope.launch {
 timer.state.collectLatest { state ->
  when(state) {
   Idle -> Unit // timer is in idle position, not running
   Ticking -> Unit // timer is ticking, running
   Paused -> Unit // timer is paused, not running
   Finished -> Unit // timer is finished naturally, not programmatically finished, not running
   Stopped -> Unit // timer is stoped programmatically by calling stop() function, not running
  }
 }
}
```

*After finished using Timer, please do not forget to stop(). Otherwise, it won't get garbage collected!*

```kotlin
timer.stop()
```
