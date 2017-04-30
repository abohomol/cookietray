# CookieTray
Securely persist cookies in private storage on Android

[![Build Status](https://travis-ci.org/abohomol/cookietray.svg?branch=master)](https://travis-ci.org/abohomol/cookietray) [![](https://jitpack.io/v/abohomol/cookietray.svg)](https://jitpack.io/#abohomol/cookietray) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CookieTray-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5670) [![Apache 2.0 License](https://img.shields.io/hexpm/l/plug.svg) ](https://github.com/abohomol/cookietray/blob/master/LICENSE)

## Usage

Default usage:

    CookieStore cookieStore = new CookieTray(context);
    CookieHandler cookieHandler = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
    builder.cookieJar(new JavaNetCookieJar(cookieHandler));
    OkHttpClient httpClient = builder.build();

Construct CookieTray using existing SharedPreferences instance:

    SharedPreferences preferences = ...
    CookieStore cookieStore = new CookieTray(preferences);
    ...

## ProGuard

    -keepnames class * implements java.io.Serializable
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

## Download

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        compile 'com.github.abohomol:cookietray:1.0'
	}

## License

    Copyright 2017 Anton Bohomol

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.