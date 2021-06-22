package com.bluepig.tnmalarm.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bluepig.tnmalarm.model.Alarm

@Database(entities = [Alarm::class], version = 2)
@TypeConverters(AlarmConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDAO

    companion object {
        //해당 변수를 cpu cache 에서가 아닌 main Memory 에 저장 및 읽기를 하겠다는 명시의 어노테이션
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            // synchronized 은 객체에 대한 모든 동기화 블록은 한 시점에 오직 한 쓰레드만이 블록 안으로 접근하도록 실행하도록 한다
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "Alarm_dataBase"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }
}
