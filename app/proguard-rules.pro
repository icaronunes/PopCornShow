## Add project specific ProGuard rules here.
## By default, the flags in this file are appended to flags specified
## in /home/icaro/Android/Sdk/tools/proguard/proguard-android.txt
## You can edit the include path and order by changing the proguardFiles
## directive in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## Add any project specific keep options here:
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
-printconfiguration config.txt
-printmapping mapping.txt

-verbose

-keepattributes *Annotation*
-keepattributes *Annotation*,Signature


-keeppackagenames br.com.icaro.**
-keep class br.com.icaro.** {*;}
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
-keep class * implements android.os.AsyncTask {
  public static final android.os.AsyncTask *;
}

-keep class android.support.** {*;}
-keep class android.support.v7** {*;}
-keep class android.content.** {*;}
-keep class java.nio.** {*;}
-keepclassmembers class android.support.**
-keep class android.support.v7.widget.SearchView { *; }
-keep class com.fasterxml.jackson.** {*;}
-keep class com.squareup.** {*;}

-keep class info.movito.** {*;}
-keep class com.facebook.** {*;}
-keep class com.google.** {*;}
-keep class android.** {*;}
-keeppackagenames com.google.**
-keeppackagenames info.movito.**

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class * implements java.io.Serializable {
  public static final java.io.Serializable *;
}

-dontwarn android.support.**
-dontnote android.net.http.**
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-dontnote org.apache.commons.logging.**
-dontnote com.fasterxml.jackson.**
-dontnote com.google.**
-dontnote org.apache.**
-dontnote com.squareup.okhttp.**
-dontwarn org.apache.commons.**
-dontwarn org.w3c.dom.**
-dontwarn com.fasterxml.jackson.**
-dontwarn com.google.**
-dontwarn com.squareup.**
-dontwarn info.movito.**
-dontwarn okio.**
