package com.sgtech.freevices.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.sgtech.freevices.R

object ItemsUtils {
    fun showAddAlertDialog(context: Context, rootView: View) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.add_habit))
            .setView(dialogView)

        val dialog = builder.create()

        val optionTobacco = dialogView.findViewById<View>(R.id.optionTobacco)
        val optionAlcohol = dialogView.findViewById<View>(R.id.optionAlcohol)
        val optionParties = dialogView.findViewById<View>(R.id.optionParties)
        val optionOthers = dialogView.findViewById<View>(R.id.optionOthers)

        optionTobacco.setOnClickListener {
            dialog.dismiss()
            showExpenseDialog(context, context.getString(R.string.tobacco), rootView)
        }

        optionAlcohol.setOnClickListener {
            dialog.dismiss()
            showExpenseDialog(context, context.getString(R.string.alcohol), rootView)
        }

        optionParties.setOnClickListener {
            dialog.dismiss()
            showExpenseDialog(context, context.getString(R.string.parties), rootView)
        }

        optionOthers.setOnClickListener {
            dialog.dismiss()
            showExpenseDialog(context, context.getString(R.string.others), rootView)
        }

        builder.setPositiveButton(context.getString(R.string.add)) { _, _ ->
            Snackbar.make(rootView, context.getString(R.string.added_text), Snackbar.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showExpenseDialog(context: Context, option: String, rootView: View) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_expense, null)

        val editText = dialogView.findViewById<TextInputEditText>(R.id.amount_edit_text)
        editText.hint = "$"

        builder.setTitle(context.getString(R.string.how_many))
        builder.setView(dialogView)
        builder.setPositiveButton("Add") { _, _ ->
            val expenseAmount = editText.text.toString()
            Snackbar.make(rootView, "Added expense for $option: $ $expenseAmount", Snackbar.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Snackbar.make(rootView, "Cancelled", Snackbar.LENGTH_SHORT).show()
        }

        builder.show()
    }
}
