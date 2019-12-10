package com.caldremch.myannotationprocessor

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.caldremch.annotation.NeedLogin

@NeedLogin
class MainActivity : AppCompatActivity() {

    private lateinit var context: Context

    private var needLog: INeedLoginProcessor = NotLoginProcessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        needLog?.process(this)
    }

}
