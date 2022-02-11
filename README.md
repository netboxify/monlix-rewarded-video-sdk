# Monlix Rewarded Videos SDK

🚀 The SDK is in its alpha release, 0.1.1b-alpha being the latest version.
[![](https://jitpack.io/v/netboxify/monlix-rewarded-video-sdk.svg)](https://jitpack.io/#netboxify/monlix-rewarded-video-sdk)

## How to deploy/import to your project

This SDK library works with any app that runs on Android versions later than Android 4.3 (API 18).

#### Include the JitPack repository in your project's `build.gradle` :

```kotlin
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

#### Then, add the SDK library's dependency to your MODULE's ```build.gradle```

```kotlin
dependencies {
  implementation 'com.github.netboxify:monlix-rewarded-video-sdk:0.1.1b_a'
}
```

## Usage

The SDK library helps you get ads from our network, and show them on screen for you, and handle all callbacks including clicks, close, errors, as well as impressions etc.
The usage is simple. The SDK library can be triggered through a builder pattern. If you don't have an idea about what that is, a builder pattern means you instantiate a class, configure it, then build it, and voilà, the ad will be displayed.

##### First of all, instantiate this class (it's the builder class) :
```kotlin
  val adBuilder = RewardedAdsBuilder() //You instantiate this first
```

#### Then, if you wanna configure some parts of the ad, you can run this on the adBuilder variable we just created,

```kotlin
  val adConfiguration = adBuilder.configure() //same as RewardedAdsConfiguration()
  //or you can this lane, they return the same thing/object.
  val adConfiguration = RewardedAdsConfiguration() 
  
  //However, you can skip this part if you wanna use the default configuration. This configuration is totally optional.
```

#### After that, build the ad by giving the builder an activity, a fragment container, and IDs for the network (Mandatory)
```kotlin
  adBuilder.build(
    hostActivity = this, //Activity that hosts the ad
    fragmentContainer = R.id.container, //Container that hosts the ad fragment
    customAdConfiguration = adConfiguration //Can be null to use default configuration
    "userid", //This is the user ID to use for the network
    "zoneid", //This is the zone ID to use for the network
    "37337" //This is the publisher ID to use for the network
    )
 ```
 
 As you can see, you need an ```Activity``` which will host the ad (the ad is basically a fragment), and you need a ```fragment container``` which exists within the activity.
 You can pass a null configuration in the third parameter. And that's it, after calling the above code, the ad's information are ready to be used to load the ad.
 
 
#### Finally, load the ad 
Now, just load the ad by calling ```loadAd()``` on the builder. Voilà, your Rewarded Ad will begin loading and be visible right away.

```kotlin
  adBuilder.build(...)
            .loadAd()
```

### Controlling the ad even more through callbacks

If you want to listen to ad callbacks such as 'adLoaded, adClosed, adFailed, adStarted' etc... You need to get the ```fragment``` that is displaying the ad.
This is possible by calling the following line :

```kotlin
  val adfragment = adBuilder.waitForFragment(10000) //Wait for a fragment in 10 secs, 
```

This method is useful because it will wait for an ad fragment to exist within the next 10 seconds, and will return an error if it gets timed out.
So ultimately, you obtain a fragment, on which you can set a listener and listen to callbacks.


#### Setting a listener and listening to callbacks from the ad

So, if you understand so far how to instantiate the ad fragment and build it, even configure it, then you will manage to be able to set a listener and listen to callbacks.
It's very easy, remember the ```adfragment``` we waited for 10 seconds to get, with the previous step? You set a listener on it like this, then override the methods inside the listener.
Here's how it's done in Kotlin :

```kotlin
//The listener will take a 'RewardedAdsCallback' object which is basically an interface that has certain functions to override.
adfragment?.setCallbackListener(object: RewardedAdsCallback {
  override fun adLoaded() {
    Log.e("MyAd", "Loaded")
  }
  override fun adStarted() {
    Log.e("MyAd", "Started")
  }
 override fun adWatched() {
    Log.e("MyAd", "Watched")
 }
 override fun adClicked() {
    Log.e("MyAd", "Clicked")
 }
 override fun adClosed(remainingSec: Long, manually: Boolean) {
    Log.e("MyAd", "Closed, $remainingSec seconds left. By user: $manually")
 }
 override fun noAdAvailable() {
    Log.e("MyAd", "No add available.")
 }
 override fun onAdFetchFailed(error: String) {
    Log.e("MyAd", "Error fetching Ad: $error")
 }
})
```

The ad will be closed and destroyed automatically on close/failure. But you can destroy the fragment itself if you wanna do it manually.

