package np.ict.mad.wackamole.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppDao {

    // Sign up
    @Insert
    suspend fun insertUser(user: UserEntity)

    // Sign in
    @Query(
        "SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1"
    )
    suspend fun login(username: String, password: String): UserEntity?

    // Save score
    @Insert
    suspend fun insertScore(score: ScoreEntity)

    // Personal best
    @Query(
        "SELECT MAX(score) FROM scores WHERE userId = :userId"
    )
    suspend fun getPersonalBest(userId: Int): Int?
}


