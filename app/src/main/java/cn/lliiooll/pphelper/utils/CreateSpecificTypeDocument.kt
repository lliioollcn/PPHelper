package cn.lliiooll.pphelper.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class CreateSpecificTypeDocument(private val type: String) :
    ActivityResultContracts.CreateDocument() {
    override fun createIntent(context: Context, input: String): Intent {
        return super.createIntent(context, input).setType(type)
    }
}