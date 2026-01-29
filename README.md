# MAD_Wackamole_S10262840_Daphne
This is a development done to satisfy the assignment portion for module “Mobile App development” under Ngee Ann Polytechnic for AY24/25

# LLM Usage Declaration
Tool Used: ChatGPT
The tool was mainly used for debugging support tool and was not used to generate the whole application.
The LLM was mainly used to
-clarify error messages 
-debugging roomDB and couroutine issues

# Example Prompt:
1."Why does my roomDB keep indicating "db is closed"
2.“Why is my Compose theme showing unresolved references for colour constants?”

# Changes Made:
1. Leaderboard Feature Design (Advanced Extension)
   
Before:

Scores were stored per user, but there was no way to compare results across users.

After:
    SELECT users.username, MAX(scores.score) AS bestScore
    FROM users
    INNER JOIN scores ON users.userId = scores.userId
    GROUP BY users.userId
    ORDER BY bestScore DESC

Reason for change:
    The LLM helped suggest an SQL aggregation approach to retrieve each user’s personal best score, enabling the implementation of a leaderboard that compares multiple users.

2. UI & Theme Debugging
Issue:
    Unresolved references to colour constants in Theme.kt.

Resolution:
    The LLM helped identify missing imports and package alignment issues between Color.kt and Theme.kt.

Lesson:
    Correct package structure and explicit imports are required for Compose theme files to compile properly.

3. Coroutine Usage in Compose (Database Stability)
Before (problematic approach):
    CoroutineScope(Dispatchers.IO).launch {
        db.dao().insertUser(user)
    }

After (improved approach):
    val scope = rememberCoroutineScope()

   scope.launch(Dispatchers.IO) {
        db.dao().insertUser(user)
    }

Reason for change:
    The LLM helped identify that manually creating a CoroutineScope inside a Composable could cause lifecycle issues and database access errors. The revised approach uses a Compose-aware coroutine     scope, improving stability and preventing “database is closed” errors.

# Key Takeaways / Lessons Learnt
- SQL aggregation for analytics:
    SQL GROUP BY and MAX() queries are effective for implementing leaderboard and comparison features.

- Tooling awareness:
    Android Studio errors and inspectors can be misleading; understanding tool behaviour is as important as writing code.

-Jetpack Compose lifecycle awareness:
    Proper coroutine scoping is crucial to avoid crashes and database access issues.
