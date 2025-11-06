# ami-app-android

This is an [Android Studio](https://developer.android.com/studio) project.

## Prerequisites

Make sure that you installed the latest [java](https://www.oracle.com/java/technologies/downloads).
This documentation has been tested with "Java SE Development Kit 21.0.9".

**WARNING**: at the time of this writing, "Java SE Development Kit 25.0.1" did **NOT** work,
because of some incompatibility with the latest version of Kotlin packaged with Android Studio
which couldn't parse the version "25.0.1" properly.

## Fastlane

We're using [fastlane](https://fastlane.tools/) to help with the automation.

### Setting up

Follow the steps on [Fastlane's setup doc](https://docs.fastlane.tools/getting-started/android/setup/).
For now, ignore the "Supply" steps, they may be configured later using:

```sh
fastlane supply init
```

### Running tests

```sh
fastlane test
```
