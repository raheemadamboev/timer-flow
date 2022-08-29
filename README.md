<h1 align="center">Timer Flow</h1>

<p align="center">
  <a href="http://developer.android.com/index.html"><img alt="Android" src="https://img.shields.io/badge/platform-android-green.svg"/></a>
  <a href="https://jitpack.io/#raheemadamboev/timer-flow"><img alt="Version" src="https://img.shields.io/badge/platform-android-green.svg"/></a>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
</p>

<p align="center">
⏳️ Light library to use Timer in Android. The library is implemented via Kotlin Coroutines and Kotlin Flows.
</p>

# Setup

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
implementation 'com.github.raheemadamboev:timer-flow:1.0.2'
```

# Implementation

**Create an object of Timer:**

```kotlin
val timer = Timer()
```

**Set timer duration in milliseconds (default: 10 000):**

```kotlin
timer.timerDuration = 5_000L
```

**Start timer:**

```kotlin
timer.start()
```

**Observe time of timer:**

```kotlin
lifecycleScope.launch {
    timer.time.collectLatest { time ->
      // update UI, do something
      println(time.toString())
    }
}
```

**Pause timer:**

```kotlin
timer.pause()
```
_If you pause timer, do not forget to stop() it. Otherwise, it runs forever._

**Resume timer:**

```kotlin
timer.resume()
```

**Reset timer:**

```kotlin
timer.reset()
```

**Set checkpoint time (default: 3 000) that you will get notified when it is reached. It is implemented via Kotlin Channel and received as Kotlin Flow. So you will only get notified once:**

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

**Observe timer states. It is implemented via Kotlin StateFlow so you always get the current timer state:**

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

**After finished using Timer, please do not forget to stop(). Otherwise, it won't get garbage collected!**

```kotlin
timer.stop()
```

# Demo

Very simple Jetpack Compose demo. <a href="https://github.com/raheemadamboev/timer-flow/blob/master/app-debug.apk">Download demo</a>

<img src="https://github.com/raheemadamboev/timer-flow/blob/master/banner.gif" alt="Italian Trulli" width="200" height="400">

# Projects using this library

**GoTest** 150 000+ downloads in <a href="https://play.google.com/store/apps/details?id=xyz.teamgravity.gotest">Google Play Store</a>

**Buxgalteriya schyotlar rejasi** 20 000+ downloads in <a href="https://play.google.com/store/apps/details?id=xyz.teamgravity.uzbekistanaccountingcode">Google Play Store</a>

**Irregular Verbs**  20 000+ downloads in <a href="https://play.google.com/store/apps/details?id=xyz.teamgravity.irregularverbs">Google Play Store</a>

# License

```xml
Designed and developed by raheemadamboev (Raheem) 2022.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
