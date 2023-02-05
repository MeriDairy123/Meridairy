# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android_soft\new\sdknew/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
  -keepattributes Signature
  -keepattributes *Annotation*
 -keep class b2infosoft.milkapp.com.customer_app.** { *; }
 -keep class b2infosoft.milkapp.com.Model.** { *; }
 -keepnames class org.jsoup.nodes.Entities
 #-------Retrofit
 -keepattributes Signature
 -keepattributes *Annotation*
 -dontwarn retrofit.**
 -keep class retrofit.** { *; }
  #----okHttp3

  -keep class okhttp3.** { *; }
  -keep interface okhttp3.** { *; }
  -dontwarn okhttp3.**
  -dontnote okhttp3.**
  #------GSOn
  -keep class sun.misc.Unsafe { *; }
  -keep class com.google.gson.stream.** { *; }
  #--- Firebase
 -keep class com.firebase.** { *; }
 #---- Volly
 -keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.NetworkDispatcher {
     void processRequest();
 }
 -keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.CacheDispatcher {
     void processRequest();
 }
#----Itext PDF
-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.**

-keep class com.itextpdf.** { *; }

-keep class javax.xml.crypto.dsig.** { *; }
-dontwarn javax.xml.crypto.dsig.**

-keep class org.apache.jcp.xml.dsig.internal.dom.** { *; }
-dontwarn org.apache.jcp.xml.dsig.internal.dom.**

-keep class javax.xml.crypto.dom.** { *; }
-dontwarn javax.xml.crypto.dom.**

-keep class org.apache.xml.security.utils.** { *; }
-dontwarn org.apache.xml.security.utils.**

-keep class javax.xml.crypto.XMLStructure
-dontwarn javax.xml.crypto.XMLStructure
