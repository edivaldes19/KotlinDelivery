package com.manuel.delivery.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.manuel.delivery.R

class TextWatchers {
    companion object {
        fun validateFieldsAsYouType(
            context: Context,
            extendedFloatingActionButton: ExtendedFloatingActionButton,
            vararg textInputEditText: TextInputEditText
        ) {
            textInputEditText.forEach { text ->
                text.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s.isNullOrBlank()) {
                            text.error = context.getString(R.string.this_field_is_required)
                            extendedFloatingActionButton.isEnabled = false
                        } else {
                            text.error = null
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        textInputEditText.forEach { editText ->
                            extendedFloatingActionButton.isEnabled =
                                !s.isNullOrBlank() && !editText.text.isNullOrBlank()
                        }
                    }
                })
            }
        }

        fun clearAllTextFields(vararg textInputEditText: TextInputEditText) {
            textInputEditText.forEach { editText ->
                editText.apply {
                    text?.clear()
                    error = null
                }
            }
        }

        fun isEmailValid(email: String): Boolean =
            email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}