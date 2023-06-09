package com.master.iot.luzi.ui.utils

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.master.iot.luzi.R

class DialogUtils {
    companion object {
        fun showDialogWithOneButton(context: Context, @StringRes title: Int, @StringRes description: Int) {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(title))
                .setMessage(context.getString(description))
                .setPositiveButton(context.getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        fun showDialogWithOneButton(context: Context, title: String, description: String) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(context.getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                .show()
        }

        fun showDialogWithOneButtonAndAction(context: Context, @StringRes title: Int, @StringRes description: Int, listener: DialogInterface.OnClickListener) {
            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(title))
                .setMessage(context.getString(description))
                .setPositiveButton(context.getString(R.string.ok), listener)
                .show()
        }

        fun showCustomDialogWithOneButton(context: Context, view: View, @StringRes title: Int): AlertDialog =
            MaterialAlertDialogBuilder(context)
                .setView(view)
                .setTitle(context.getString(title))
                .setPositiveButton(context.getString(R.string.action_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
    }
}