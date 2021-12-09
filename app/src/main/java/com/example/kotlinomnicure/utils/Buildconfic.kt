package com.example.kotlinomnicure.utils

object Buildconfic {
    var appname = ""
    fun value(): String {
        if (BuildConfig.SERVER.equalsIgnoreCase("ext_test")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("exttesting")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("test")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccn")) {
            appname = "Netccn"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccndev")) {
            appname = "Netccndev"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccnautotest")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("staging_iam")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("production")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("demo")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("qa")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("qa_omnicure")) {
//            appname="Omnicure Application Name Testing";
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("omnicurepilot")) {
            appname = "Omnicure Now"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("netccndemo")) {
            appname = "Netccndemo"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("training")) {
            appname = "Netccntraining"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("Omnicure-netccnDEV2")) {
            appname = "Netccndev2"
        } else if (BuildConfig.SERVER.equalsIgnoreCase("Omnicure-netccnDEV3")) {
            appname = "Netccndev3"
        } else {
            appname = "Omnicure Now"
        }
        return appname
    }
}