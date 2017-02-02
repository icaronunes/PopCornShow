# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/icaro/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Signature
-keepclassmembers class br.com.icaro.filme.domain.** {
  *;
}
# Gerar arquivo do proguard usado no firebases
-printmapping mapping.txt
# classes duplicadas.. >>>>
-keep class org.apache.** { *; }
-keep class android.support.v7.widget.SearchView { *; }
#-dontwarn org.apache.commons.**
-dontwarn org.apache.commons.logging.impl.LogFactoryImpl**
-dontwarn org.apache.commons.logging.impl.Jdk14Logger**
-dontwarn com.google.common.collect.** ## são muitas
-dontwarn com.google.common.** ## são muitas
-dontwarn com.fasterxml.jackson.databind.ext.DOMSerializer*
-dontwarn com.google.common.base**
-dontwarn com.google.common.cache.package-info**
-dontwarn com.google.common.cache.Cache**
-dontwarn com.google.common.cache.RemovalNotification**
-dontwarn com.google.common.cache.Striped64**
-dontwarn com.google.common.collect.AbstractBiMap**
-dontwarn com.google.common.collect.AbstractMapBasedMultimap**
-dontwarn com.google.common.collect.AbstractMapBasedMultiset**
-dontwarn com.google.common.collect.AbstractMapBasedMultiset**
-dontwarn com.google.common.collect.AbstractMultiset**
-dontwarn com.google.common.collect.AbstractNavigableMap**
-dontwarn com.google.common.collect.AbstractMapEntry**
-dontwarn com.google.common.collect.AbstractRangeSet**
-dontwarn com.google.common.collect.AbstractSequentialiterator**
-dontwarn com.google.common.collect.AbstractSetMultimap**
-dontwarn com.google.common.collect.AbstractListMultimap**
-dontwarn com.google.common.collect.AbstractMultimap**
-dontwarn com.google.common.collect.ArrayTable**
-dontwarn com.google.common.collect.BiMap**
-dontwarn com.google.common.cache.cacheBuilder**
-dontwarn com.google.common.cache.cacheBuilderSpec**
-dontwarn com.google.common.cache.cacheBuilder**
-dontwarn com.google.common.cache.LocalCache**
-dontwarn com.google.common.cache.ForwardingCache**
-dontwarn com.squareup.picasso.OkHttpDownload**
-dontwarn info.movito.themoviedbapi.tools.HttpClientProxy**
-dontwarn okio.**
-dontwarn okio.DeflaterSink**
# <<<<<<< classes duplicadas..
