package com.dmia.bioAttendance.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.dmia.bioAttendance.R
import java.io.StringWriter
import java.util.UUID

fun Activity.showDialog(
    context: Context?,
    title: String?,
    message: String?,
    onClickListener: DialogInterface.OnClickListener?
) {
    val dialog = AlertDialog.Builder(context)
    dialog.setIcon(R.mipmap.ic_launcher)
    dialog.setTitle(title)
    dialog.setMessage(message)
    dialog.setPositiveButton("OK", onClickListener)
    //dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
    dialog.show()
}

fun Activity.locationPermissionSettingPopup(
    positiveCallback: () -> Unit
) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setContentView(R.layout.dialog_location_permission_setting_popup)

    val tvPositive = dialog.findViewById(R.id.tvPositive) as AppCompatTextView

    tvPositive.setOnClickListener {
        dialog.dismiss()
        positiveCallback.invoke()

    }

    dialog.show()
}

fun Activity.getUniqueIMEIId(context: Context): String {
    return try {
        if (Build.VERSION.SDK_INT >= 29) {
            val androidId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val androidId_UUID = UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8")))
            androidId_UUID.toString()
        } else {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                }
            }
            telephonyManager.deviceId
        }
    } catch (e: Exception) {
        val errors = StringWriter()
        //            e.printStackTrace(new PrintWriter(errors));
        "00000000000000000000"
        //e.printStackTrace();
    }

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun Activity.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}