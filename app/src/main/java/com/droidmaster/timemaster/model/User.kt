package com.droidmaster.timemaster.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

  @Parcelize
    data class User(var uuid: String?, var name: String?, var email: String?, var phoneNumber: String?)  :
        Parcelable