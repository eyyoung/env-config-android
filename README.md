# Environment Switcher for Android

![GitHub release (latest by date)](https://img.shields.io/github/v/release/eyyoung/env-config-android)

## About Repo

During the development period, we may need to switch different environment variables to support different dev environments.
This repo is focused to support switching the environment variables in a simple way and supporting several features:

* Do not leak variables in the release package
* Support release package variable switches by installing external APK
* Debug package can easily integrate the Environment Switch UI
* Custom Environment Variable Value Editing


## Dependency

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/eyyoung/env-config-android")
    }
}

dependencies {
    implementation 'com.loopnow.envconfig:switcher:$version'
}
```

## Sample and how to use

Sample have for modules

### sample

this is a sample to provide how to switch and read variables.

Usage:

* get Env value

```kotlin
Env.getEnv(com.loopnow.envconfig.switchsample.R.attr.baseUrl)
```

* Start Env Switchment Activity

```kotlin
val componentName =
    ComponentName(this, "com.loopnow.envconfig.switcher.EnvSwitchActivity")
startActivity(Intent().setComponent(componentName))
```

* Only expose variable value in debug package

```gradle
debugImplementation project(':sample-switch-env')
```

### sample-schema

This is a sample schema module to define the environment schema

Usage:

* Schema definition

```xml
<declare-styleable name="EnvironmentVariableSchema">
    <attr name="baseUrl" format="string" />
</declare-styleable>

<style name="EnvironmentValue">
    <item name="baseUrl">http://prod.url</item>
</style>
```

* different product flavors for different value

```xml
<style name="EnvironmentValue">
    <item name="baseUrl">http://dev.url</item>
</style>
```

#### sample-switch-env

this is a sample to provide how to define variable schema and value

Usage:

* Add Dependency

```
implementation 'com.loopnow.envconfig:switcher:$version'
```

* Expose Activity Switchment and Theme (not required if you do not need switchment UI)

```xml
<activity
    android:name="com.loopnow.envconfig.switcher.EnvSwitchActivity"
    android:exported="true"
    android:theme="@style/Theme.EnvConfig" />
```

* Expose Provider to support external switchment(not required if you do not need external switchment fo release package)

```xml
<provider
    android:name="com.loopnow.envconfig.switcher.EnvProvider"
    android:authorities="${applicationId}.env.config"
    android:exported="true"
    android:permission="com.loopnow.envconfig">

    <intent-filter>
        <action android:name="com.loopnow.envconfig" />
    </intent-filter>

</provider>
```

* Assets Json definition(this json is used both for switchment UI and external switch module)

```
{
  "baseUrl": "http://dev.url"
}
```

### app

How to implement an external application to support release package switchment

Usage:

* add dependency for env schema module

```gradle
api project(":sample-switch-env")
```

* Define default behavior of external app and remove useless provider

```xml
<activity
    android:name=".switcher.EnvSwitchActivity"
    android:exported="true"
    android:theme="@style/Theme.EnvConfig">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />

        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<provider
    tools:node="remove"
    android:name="com.loopnow.envconfig.switcher.EnvProvider"
    android:authorities="${applicationId}.env.config"
    android:exported="true"
    android:permission="com.loopnow.envconfig">
</provider>
```

## Implementation

<b>By using the AAPT, we can easily replace styles/attrs with diffrent product flavors, AAPT resource merge function is a standard flow for Gradle Build </b>

* In order to not leak any variables in release package, we use aapt merge function to support dev product flavors.
* In order to support release package variable switchment, we use Content Provider to expose Variable Update

## Libraries

### schema

This module difines the base stylable values so that we can use the definiton to fetch variables

```xml
<declare-styleable name="EnvironmentVariableSchema">
</declare-styleable>

<style name="EnvironmentValue">
</style>
```

### swithcer

This module take charges to :

* Env class to support fetch variable values
* EnvInitializer support to fetch data when application start up
* EnvProvider expose variable update to support external switchment
* EnvSwitchActivity expose an easily way to change environment
