package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun inserts(elections: List<Election>)

    @Update()
    fun update(election: Election)

    //TODO: Add select all election query
    /**
     * @return all elections.
     */
    @Query("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>>

    /**
     * @return all saved elections.
     */
    @Query("SELECT * FROM election_table WHERE saved = 1")
    fun getElectionsSaved(): LiveData<List<Election>>

    //TODO: Add select single election query
    /**
     * @return election.
     */
    @Query("SELECT * FROM election_table WHERE id=:id")
    fun getElection(id: Int): Election?

    //TODO: Add delete query
    @Delete
    fun delete(election: Election)

    //TODO: Add clear query
    /**
     * Delete all election.
     */
    @Query("DELETE FROM election_table")
    fun deleteAllElections()
}