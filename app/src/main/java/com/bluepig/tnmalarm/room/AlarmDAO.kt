package com.bluepig.tnmalarm.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.bluepig.tnmalarm.model.Alarm

@Dao
interface AlarmDAO {

    @Query("select * from alarm")
    fun getAll() : LiveData<List<Alarm>>

    @Query("select * from alarm")
    fun getAllList() : List<Alarm>

    @Query("select * from alarm where id is :id")
    fun getId(id : Int) : Alarm

    @Insert
    fun insert(alarm : Alarm) : Long

    @Delete
    fun delete(alarm: Alarm)

    @Update
    fun update(alarm : Alarm)
}