# Android News Feed App
Android(Kotlin) News Feed application 

[![Build Status](https://travis-ci.org/mvdan/android-template.svg?branch=master)](https://travis-ci.org/mvdan/android-template)

Minimalist Android app template using Gradle.

This Repositary contains the full implementation of a basic application for
the Android platform( `Kotlin`), demonstrating the basic facilities that applications
will use.  You can run the application using Android studio either directly 
open (File/open) the application using Android Studio or import the application
using Android Studio like below. 
open Android Studio => File => New => Import Project.

## Structure

* `build.gradle` - root gradle config file
* `settings.gradle` - root gradle settings file
* `app` - our only project in this repo
* `app/build.gradle` - project gradle config file
* `app/src` - main project source directory
* `app/src/main` - main project flavour
* `app/src/main/AndroidManifest.xml` - manifest file
* `app/src/main/java` - java source directory
* `app/src/main/res` - resources directory
* `res/layout/*` - This directory contains XML files describing user interface
	view hierarchies.  The activity_main.xml file here is used by
	MainActivity.java to construct its UI.  The base name of each file
	(all text before a '.' character) is taken as the resource name;
	it must be lower-case.
* `res/values/colors.xml , res/values/strings.xml ,res/values/styles.xml` - These XML files describe additional resources included in the application.
	They all use the same syntax; all of these resources could be defined in one
	file, but we generally split them apart as shown here to keep things organized.


