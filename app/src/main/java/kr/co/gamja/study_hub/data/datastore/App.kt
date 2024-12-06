package kr.co.gamja.study_hub.data.datastore

import android.app.Application
import java.io.File

// DataStore 싱글톤
class App:Application() {
    private lateinit var datastore:DataStoreModule

    companion object{
        private lateinit var app:App
        fun getInstance():App= app
    }

    override fun onCreate() {
        super.onCreate()

        app=this
        datastore=DataStoreModule(this)

        val dexOutputDir: File =codeCacheDir
        dexOutputDir.listFiles()?.forEach {
            file->file.setReadOnly()

        }

    }
    fun getDataStore():DataStoreModule=datastore
}