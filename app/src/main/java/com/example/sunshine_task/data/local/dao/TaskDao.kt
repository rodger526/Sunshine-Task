package com.example.sunshine_task.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sunshine_task.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Lecturas reactivas: Room vuelve a emitir cada vez que cambia la tabla.
    @Query("SELECT * FROM tasks ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeById(taskId: String): Flow<TaskEntity?>

    // Lectura única, para reprogramar recordatorios al abrir la app.
    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<TaskEntity>

    // Escrituras: funciones suspend, Room las ejecuta fuera del hilo principal.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: String)
}