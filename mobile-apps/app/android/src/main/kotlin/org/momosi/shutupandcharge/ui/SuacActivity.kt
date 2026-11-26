package org.momosi.shutupandcharge.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.momosi.shutupandcharge.app.SuacApp
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory

class SuacActivity : AppCompatActivity() {

    private val codeAuthFlowFactory: AndroidCodeAuthFlowFactory by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeAuthFlowFactory.registerActivity(this)
        enableEdgeToEdge()
        setContent {
            SuacApp()
        }
    }
}
