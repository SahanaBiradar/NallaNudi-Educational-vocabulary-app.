package com.example.nallanudiapp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.media.AudioManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import java.util.*
import com.example.nallanudiapp.SavedWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import kotlinx.coroutines.delay
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import kotlinx.coroutines.delay
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Mic
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech
    private var isTtsReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 🔔 NOTIFICATION CHANNEL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(

                "daily_word_channel",

                "Daily Word Reminder",

                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Reminder notifications for learning words"

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }

        // 🔊 TEXT TO SPEECH
        tts = TextToSpeech(this) { status ->

            if (status == TextToSpeech.SUCCESS) {

                val result = tts.setLanguage(Locale.US)

                isTtsReady =
                    result != TextToSpeech.LANG_MISSING_DATA &&
                            result != TextToSpeech.LANG_NOT_SUPPORTED

                tts.setSpeechRate(1.0f)

                tts.setPitch(1.0f)
            }
        }

        val db =
            AppDatabase.getDatabase(applicationContext)

        val dao = db.wordDao()

        CoroutineScope(Dispatchers.IO).launch {

            dao.insertAll(wordDatabase)
        }

        // 🔔 SHOW NOTIFICATION
        val builder = NotificationCompat.Builder(
            this,
            "daily_word_channel"
        )

            .setSmallIcon(R.drawable.ic_launcher_foreground)

            .setContentTitle("📚 Nalla Nudi")

            .setContentText(
                "Learn a new word today!"
            )

            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )

        with(NotificationManagerCompat.from(this)) {

            notify(1, builder.build())
        }

        setContent {

            AppNavigation(
                tts,
                dao,
                isTtsReady
            )
        }
    }

    override fun onDestroy() {

        super.onDestroy()

        tts.stop()

        tts.shutdown()
    }
}

//////////////////////////////////////////////////////
// 🔥 NAVIGATION
//////////////////////////////////////////////////////

@Composable
fun AppNavigation(
    tts: TextToSpeech,
    dao: WordDao,
    isTtsReady: Boolean
) {

    val context = LocalContext.current

    val db =
        AppDatabase.getDatabase(context)

    val userDao =
        db.userDao()

    // 🌙 DARK MODE
    var darkMode by remember {
        mutableStateOf(false)
    }

    // 🪄 SPLASH SCREEN
    var showSplash by remember {
        mutableStateOf(true)
    }

    // 📱 SCREEN STATE
    var screen by remember {
        mutableStateOf("login")
    }

    var loggedInEmail by remember {
        mutableStateOf("")
    }

    // ⏳ SPLASH TIMER
    LaunchedEffect(Unit) {

        delay(2500)

        showSplash = false
    }

    // 🌟 SPLASH SCREEN
    if (showSplash) {

        Box(

            modifier = Modifier
                .fillMaxSize()
                .background(

                    Brush.verticalGradient(

                        colors = listOf(
                            Color(0xFFCE93D8),
                            Color(0xFF7B1FA2)
                        )
                    )
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "✨",
                    fontSize = 70.sp
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Nalla Nudi",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Learn Smart. Speak Smart.",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

    } else {

        when (screen) {

            "login" -> LoginScreen(

                onLoginClick = {
                        input,
                        password,
                        callback ->

                    CoroutineScope(Dispatchers.IO).launch {

                        val user =
                            userDao.login(
                                input.trim(),
                                password.trim()
                            )

                        withContext(Dispatchers.Main) {

                            if (user != null) {

                                callback(true)

                                loggedInEmail =
                                    input.trim()

                                screen = "home"

                            } else {

                                callback(false)
                            }
                        }
                    }
                },

                onRegisterClick = {
                    screen = "register"
                }
            )

            "register" -> RegisterScreen(

                onRegisterDone = {
                    screen = "login"
                },

                onBackToLogin = {
                    screen = "login"
                },

                userDao = userDao
            )

            "home" -> MyAppUI(

                tts = tts,

                dao = dao,

                isTtsReady = isTtsReady,

                email = loggedInEmail,

                darkMode = darkMode,

                onDarkModeChange = {
                    darkMode = it
                },

                onLogout = {
                    screen = "login"
                }
            )
        }
    }
}

//////////////////////////////////////////////////////
// 🔐 LOGIN SCREEN (UPDATED)
//////////////////////////////////////////////////////

@Composable
fun LoginScreen(
    onLoginClick: (String, String, (Boolean) -> Unit) -> Unit,
    onRegisterClick: () -> Unit
) {

    var input by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔥 TITLE
        Text(
            text = "Nalla Nudi ✨",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A148C)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 🌟 CARD
        Card(
            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),

            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    "Login",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 📧 EMAIL OR PHONE
                OutlinedTextField(
                    value = input,

                    onValueChange = {
                        input = it
                    },

                    label = {
                        Text("Email or Phone")
                    },

                    shape = RoundedCornerShape(12.dp),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 🔒 PASSWORD
                OutlinedTextField(

                    value = password,

                    onValueChange = {
                        password = it
                    },

                    label = {
                        Text("Password")
                    },

                    shape = RoundedCornerShape(12.dp),

                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,

                                contentDescription = ""
                            )
                        }
                    },

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ❌ ERROR
                if (errorMsg.isNotEmpty()) {

                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 🔘 LOGIN BUTTON
                Button(

                    onClick = {

                        // ✅ VALIDATION
                        if (
                            input.isEmpty() ||
                            password.isEmpty()
                        ) {

                            errorMsg = "Fill all fields"
                            return@Button
                        }

                        onLoginClick(
                            input.trim(),
                            password.trim()
                        ) { success ->

                            if (!success) {

                                errorMsg =
                                    "Wrong email/phone or password"
                            }
                        }
                    },

                    shape = RoundedCornerShape(50),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B1FA2)
                    ),

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        "Login",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🔗 REGISTER
                TextButton(

                    onClick = onRegisterClick,

                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                ) {

                    Text(
                        "New user? Register here"
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    userDao: UserDao,
    onRegisterDone: () -> Unit,
    onBackToLogin: () -> Unit   // ⭐ NEW
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔥 TITLE
        Text(
            text = "Create Account ✨",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A148C)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 🌟 CARD
        Card(
            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),

            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // 👤 NAME
                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Name")
                    },

                    shape = RoundedCornerShape(12.dp),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 📧 EMAIL
                OutlinedTextField(
                    value = email,

                    onValueChange = {
                        email = it
                    },

                    label = {
                        Text("Email")
                    },

                    shape = RoundedCornerShape(12.dp),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 📱 PHONE
                OutlinedTextField(
                    value = phone,

                    onValueChange = {
                        phone = it
                    },

                    label = {
                        Text("Phone Number")
                    },

                    shape = RoundedCornerShape(12.dp),

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 🔒 PASSWORD
                OutlinedTextField(

                    value = password,

                    onValueChange = {
                        password = it
                    },

                    label = {
                        Text("Password")
                    },

                    shape = RoundedCornerShape(12.dp),

                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,

                                contentDescription = ""
                            )
                        }
                    },

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ❌ ERROR
                if (errorMsg.isNotEmpty()) {

                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 🟣 REGISTER BUTTON
                Button(

                    onClick = {

                        // ✅ EMPTY CHECK
                        if (
                            name.isEmpty() ||
                            email.isEmpty() ||
                            phone.isEmpty() ||
                            password.isEmpty()
                        ) {

                            errorMsg = "Fill all fields"
                            return@Button
                        }

                        // ✅ EMAIL VALIDATION
                        if (!email.contains("@")) {

                            errorMsg = "Invalid email"
                            return@Button
                        }

                        // ✅ PHONE VALIDATION
                        if (phone.length != 10) {

                            errorMsg = "Phone must be 10 digits"
                            return@Button
                        }

                        CoroutineScope(Dispatchers.IO).launch {

                            // ✅ CHECK EXISTING EMAIL
                            val existingEmail =
                                userDao.getUserByEmail(email)

                            // ✅ CHECK EXISTING PHONE
                            val existingPhone =
                                userDao.getUserByPhone(phone)

                            if (existingEmail != null) {

                                withContext(Dispatchers.Main) {
                                    errorMsg = "Email already exists"
                                }

                            } else if (existingPhone != null) {

                                withContext(Dispatchers.Main) {
                                    errorMsg = "Phone already exists"
                                }

                            } else {

                                // ✅ INSERT USER
                                userDao.insertUser(

                                    User(
                                        name = name,
                                        email = email,
                                        phone = phone,
                                        password = password
                                    )
                                )

                                withContext(Dispatchers.Main) {
                                    onRegisterDone()
                                }
                            }
                        }
                    },

                    shape = RoundedCornerShape(50),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7B1FA2)
                    ),

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        "Register",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🔙 BACK TO LOGIN
                TextButton(
                    onClick = {
                        onBackToLogin()
                    },

                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                ) {

                    Text(
                        "Already have an account? Login"
                    )
                }
            }
        }
    }
}

//////////////////////////////////////////////////////
// 🏠 MAIN APP UI
//////////////////////////////////////////////////////

@Composable
fun MyAppUI(
    tts: TextToSpeech,
    dao: WordDao,
    isTtsReady: Boolean,
    email: String,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
){

    var selectedTab by remember { mutableStateOf(0) }

    // 🌙 DARK MODE
    var darkMode by remember { mutableStateOf(false) }

    val backgroundColor =
        if (darkMode) Color(0xFF121212)
        else Color(0xFFFDF7FF)

    val navColor =
        if (darkMode) Color(0xFF1E1E1E)
        else Color(0xFFF3E5F5)

    val overlayColor =
        if (darkMode)
            Color.Black.copy(alpha = 0.65f)
        else
            Color.White.copy(alpha = 0.85f)

    Scaffold(

        containerColor = backgroundColor,

        bottomBar = {

            NavigationBar(
                containerColor = navColor
            ) {

                // 🔍 SEARCH
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },

                    label = {
                        Text(
                            "Search",
                            color =
                                if (darkMode)
                                    Color.White
                                else
                                    Color.Black
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,

                            tint =
                                if (selectedTab == 0)
                                    Color(0xFF7B1FA2)
                                else
                                    Color.Gray
                        )
                    }
                )

                // ⭐ SAVED
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },

                    label = {
                        Text(
                            "Saved",
                            color =
                                if (darkMode)
                                    Color.White
                                else
                                    Color.Black
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Default.Bookmark,
                            contentDescription = null,

                            tint =
                                if (selectedTab == 1)
                                    Color(0xFF7B1FA2)
                                else
                                    Color.Gray
                        )
                    }
                )

                // 🎴 FLASH
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },

                    label = {
                        Text(
                            "Flash",
                            color =
                                if (darkMode)
                                    Color.White
                                else
                                    Color.Black
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,

                            tint =
                                if (selectedTab == 2)
                                    Color(0xFF7B1FA2)
                                else
                                    Color.Gray
                        )
                    }
                )

                // 🧠 QUIZ
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },

                    label = {
                        Text(
                            "Quiz",
                            color =
                                if (darkMode)
                                    Color.White
                                else
                                    Color.Black
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,

                            tint =
                                if (selectedTab == 3)
                                    Color(0xFF7B1FA2)
                                else
                                    Color.Gray
                        )
                    }
                )

                // 🌟 DAILY
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },

                    label = {
                        Text(
                            "Daily",
                            color =
                                if (darkMode)
                                    Color.White
                                else
                                    Color.Black
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,

                            tint =
                                if (selectedTab == 4)
                                    Color(0xFF7B1FA2)
                                else
                                    Color.Gray
                        )
                    }
                )

                // 👤 PROFILE
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },

                    label = {
                        Text(
                            "Profile",
                            color =
                                if (darkMode)
                                    Color.White
                                else
                                    Color.Black
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,

                            tint =
                                if (selectedTab == 5)
                                    Color(0xFF7B1FA2)
                                else
                                    Color.Gray
                        )
                    }
                )
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {

            // 🌄 BACKGROUND IMAGE
            Image(
                painter = painterResource(id = R.drawable.bg_app),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 🌫 OVERLAY
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )

            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)

            when (selectedTab) {

                // 🔍 SEARCH
                0 -> SearchScreen(
                    tts = tts,
                    dao = dao,
                    db = db,
                    isTtsReady = isTtsReady,
                    modifier = Modifier.padding(padding),
                    darkMode = darkMode
                )

                // ⭐ SAVED
                1 -> SavedScreen(
                    db = db,
                    modifier = Modifier.padding(padding)
                )

                // 🎴 FLASH
                2 -> FlashScreen(
                    db = db,
                    modifier = Modifier.padding(padding)
                )

                // 🧠 QUIZ
                3 -> QuizScreen(
                    dao = dao,
                    modifier = Modifier.padding(padding),
                    darkMode = darkMode
                )

                // 🌟 DAILY
                4 -> DailyScreen(
                    dao = dao,
                    modifier = Modifier.padding(padding)
                )

                // 👤 PROFILE
                5 -> ProfileScreen(
                    userDao = db.userDao(),
                    email = email,
                    modifier = Modifier.padding(padding),
                    onLogout = onLogout,
                    darkMode = darkMode,
                    onDarkModeChange = {
                        darkMode = it
                    }
                )
            }
        }
    }
}
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7B1FA2)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SearchScreen(
    tts: TextToSpeech,
    dao: WordDao,
    db: AppDatabase,
    isTtsReady: Boolean,
    modifier: Modifier,
    darkMode: Boolean
) {

    val context = LocalContext.current



    var text by remember {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf("Search result here")
    }
    // 🎤 VOICE SEARCH
    val speechLauncher =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts.StartActivityForResult()

        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val spokenText =
                    result.data
                        ?.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                        )

                if (!spokenText.isNullOrEmpty()) {

                    text = spokenText[0]
                }
            }
        }

    var selectedSubject by remember {
        mutableStateOf("All")
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    // 🕘 RECENT SEARCHES
    var recentSearches by remember {
        mutableStateOf(listOf<String>())
    }

    // 💡 SUGGESTIONS
    var suggestions by remember {
        mutableStateOf(listOf<Word>())
    }

    // 🔊 SPEAKING
    var isSpeaking by remember {
        mutableStateOf(false)
    }

    val subjects = listOf(
        "All",
        "English",
        "Math",
        "Science",
        "Social",
        "Commerce"
    )

    val textColor =
        if (darkMode) Color.White
        else Color.Black

    val cardColor =
        if (darkMode)
            Color(0xFF1E1E1E)
        else
            Color.White.copy(alpha = 0.92f)

    val overlayColor =
        if (darkMode)
            Color.Black.copy(alpha = 0.65f)
        else
            Color.White.copy(alpha = 0.85f)

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        // 🌄 BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.bg_app),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 🌫 OVERLAY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )

        // 🌟 MAIN CARD
        Card(

            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp),

            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {

                // 🔍 TITLE
                Text(
                    text = "Search Words",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B1FA2)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 📚 SUBJECT DROPDOWN
                Box {

                    OutlinedButton(
                        onClick = {
                            expanded = true
                        },

                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Text(selectedSubject)
                    }

                    DropdownMenu(

                        expanded = expanded,

                        onDismissRequest = {
                            expanded = false
                        }
                    ) {

                        subjects.forEach {

                            DropdownMenuItem(

                                text = {
                                    Text(it)
                                },

                                onClick = {

                                    selectedSubject = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔍 SEARCH + 🎤 MIC
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    OutlinedTextField(

                        value = text,

                        onValueChange = {

                            text = it

                            CoroutineScope(Dispatchers.IO).launch {

                                val allWords =
                                    dao.getAllWords()

                                val filtered =
                                    allWords.filter {

                                        it.english.startsWith(
                                            text,
                                            ignoreCase = true
                                        )
                                    }

                                withContext(Dispatchers.Main) {

                                    suggestions =
                                        filtered.take(5)
                                }
                            }
                        },

                        label = {
                            Text("Enter word")
                        },

                        shape = RoundedCornerShape(16.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedBorderColor =
                                    Color(0xFF7B1FA2),

                                unfocusedBorderColor =
                                    Color.Gray,

                                focusedTextColor =
                                    textColor,

                                unfocusedTextColor =
                                    textColor
                            ),

                        modifier = Modifier.weight(1f)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    // 🎤 VOICE SEARCH BUTTON
                    IconButton(

                        onClick = {

                            val intent =
                                Intent(
                                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                                )

                            intent.putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )

                            intent.putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE,
                                Locale.getDefault()
                            )

                            speechLauncher.launch(intent)
                        }

                    ) {

                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Color(0xFF7B1FA2),

                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 💡 LIVE SUGGESTIONS
                if (
                    suggestions.isNotEmpty()
                    &&
                    text.isNotEmpty()
                ) {

                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(

                            containerColor =
                                if (darkMode)
                                    Color(0xFF2A2A2A)
                                else
                                    Color.White
                        ),

                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Column {

                            suggestions.forEach { word ->

                                Text(

                                    text = word.english,

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {

                                            text = word.english

                                            suggestions =
                                                emptyList()
                                        }
                                        .padding(14.dp),

                                    color = textColor
                                )

                                Divider()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🕘 RECENT SEARCHES
                if (recentSearches.isNotEmpty()) {

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Recent Searches 🕘",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        TextButton(
                            onClick = {
                                recentSearches =
                                    emptyList()
                            }
                        ) {

                            Text("Clear")
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    FlowRow(

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        recentSearches.forEach { recent ->

                            SuggestionChip(

                                onClick = {
                                    text = recent
                                },

                                label = {
                                    Text(recent)
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )
                }

                // 📊 RESULT CARD
                Card(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(18.dp),

                    colors = CardDefaults.cardColors(

                        containerColor =
                            if (darkMode)
                                Color(0xFF2A2A2A)
                            else
                                Color(0xFFF3E5F5)
                    ),

                    elevation =
                        CardDefaults.cardElevation(6.dp)
                ) {

                    Text(

                        text = result,

                        modifier = Modifier.padding(18.dp),

                        fontSize = 16.sp,

                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 🔍 SEARCH BUTTON
                AppButton("Search") {

                    CoroutineScope(Dispatchers.IO).launch {

                        val word =
                            dao.getWord(text)

                        withContext(Dispatchers.Main) {

                            if (
                                text.isNotEmpty()
                                &&
                                !recentSearches.contains(text)
                            ) {

                                recentSearches =
                                    listOf(text) +
                                            recentSearches

                                recentSearches =
                                    recentSearches.take(5)
                            }

                            result = if (word != null) {

                                if (
                                    selectedSubject == "All"
                                    ||
                                    word.subject ==
                                    selectedSubject
                                ) {

                                    "Kannada: ${word.kannada}\n\nMeaning: ${word.meaning}"

                                } else {

                                    "Word found in ${word.subject}"
                                }

                            } else {

                                "Word not found"
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 🔊 SPEAK BUTTON
                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    AppButton(

                        if (isSpeaking)
                            "Speaking... 🔊"
                        else
                            "Speak 🔊"

                    ) {

                        if (!isTtsReady) {

                            result = "TTS not ready"
                            return@AppButton
                        }

                        if (text.isEmpty()) {

                            result =
                                "Enter word first"

                            return@AppButton
                        }

                        isSpeaking = true

                        tts.speak(
                            text,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "tts1"
                        )

                        CoroutineScope(
                            Dispatchers.Main
                        ).launch {

                            delay(2000)

                            isSpeaking = false
                        }
                    }

                    AnimatedVisibility(isSpeaking) {

                        Text(
                            text =
                                "🔊 Pronouncing word...",

                            color =
                                Color(0xFF7B1FA2),

                            fontWeight =
                                FontWeight.SemiBold,

                            modifier =
                                Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ⭐ SAVE BUTTON
                AppButton("Save Word ⭐") {

                    CoroutineScope(Dispatchers.IO).launch {

                        val wordData =
                            dao.getWord(text)

                        if (wordData != null) {

                            db.savedWordDao()
                                .insertSavedWord(

                                    SavedWord(
                                        english =
                                            wordData.english,

                                        kannada =
                                            wordData.kannada,

                                        meaning =
                                            wordData.meaning,

                                        subject =
                                            wordData.subject
                                    )
                                )

                            withContext(Dispatchers.Main) {

                                result =
                                    "Word saved successfully ⭐"
                            }

                        } else {

                            withContext(Dispatchers.Main) {

                                result =
                                    "Search word first"
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SavedScreen(
    db: AppDatabase,
    modifier: Modifier
) {

    var savedWords by remember { mutableStateOf(listOf<SavedWord>()) }

    // Load data
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            savedWords = db.savedWordDao().getAllSavedWords()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Text(
            text = "Saved Words",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (savedWords.isEmpty()) {
            Text(
                text = "No saved words yet",
                modifier = Modifier.padding(16.dp)
            )
        } else {

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(savedWords) { word ->

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3E5F5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // 📌 ICON
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // 📝 TEXT
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = word.english,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A148C)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text("Kannada: ${word.kannada}")
                                Text("Meaning: ${word.meaning}")

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = word.subject,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            // 🗑 DELETE
                            IconButton(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        db.savedWordDao().deleteWord(word)

                                        // refresh list
                                        savedWords = db.savedWordDao().getAllSavedWords()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun FlashScreen(
    db: AppDatabase,
    modifier: Modifier
) {

    var words by remember { mutableStateOf(listOf<SavedWord>()) }
    var currentIndex by remember { mutableStateOf(0) }
    var showMeaning by remember { mutableStateOf(false) }

    val rotation = animateFloatAsState(
        targetValue = if (showMeaning) 180f else 0f,
        animationSpec = tween(500)
    )

    // Load data
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            words = db.savedWordDao().getAllSavedWords()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (words.isEmpty()) {
            Text("No flash words yet")
            return@Column
        }

        val currentWord = words[currentIndex]

        // 🔢 PROGRESS
        Text(
            text = "${currentIndex + 1} / ${words.size}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))


        // 🎴 FLASH CARD
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 8 * density
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF7B1FA2),
                                Color(0xFFBA68C8)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (rotation.value <= 90f) {
                    Text(
                        text = currentWord.english,
                        fontSize = 26.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Meaning: ${currentWord.meaning}\n\nKannada: ${currentWord.kannada}",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.graphicsLayer {
                            rotationY = 180f
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔘 BUTTON ROW
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // PREV
            Button(
                onClick = {
                    currentIndex =
                        if (currentIndex - 1 < 0) words.size - 1 else currentIndex - 1
                    showMeaning = false
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Prev")
            }

            // FLIP
            Button(
                onClick = { showMeaning = !showMeaning },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B1FA2)
                )
            ) {
                Text("Flip", color = Color.White)
            }

            // NEXT
            Button(
                onClick = {
                    currentIndex = (currentIndex + 1) % words.size
                    showMeaning = false
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔀 SHUFFLE BUTTON
        OutlinedButton(
            onClick = {
                words = words.shuffled()
                currentIndex = 0
                showMeaning = false
            }
        ) {
            Text("Shuffle 🔀")
        }
    }
}
@Composable
fun QuizScreen(
    dao: WordDao,
    modifier: Modifier,
    darkMode: Boolean
) {

    val context = LocalContext.current

    val subjects = listOf(
        "All",
        "English",
        "Math",
        "Science",
        "Social",
        "Commerce"
    )

    var selectedSubject by remember {
        mutableStateOf("All")
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var words by remember {
        mutableStateOf(listOf<Word>())
    }

    var currentWord by remember {
        mutableStateOf<Word?>(null)
    }

    var options by remember {
        mutableStateOf(listOf<String>())
    }

    var score by remember {
        mutableStateOf(0)
    }

    var highestScore by remember {
        mutableStateOf(0)
    }

    var correctAnswers by remember {
        mutableStateOf(0)
    }

    var wrongAnswers by remember {
        mutableStateOf(0)
    }

    var accuracy by remember {
        mutableStateOf(0)
    }

    var currentQuestion by remember {
        mutableStateOf(1)
    }

    var selectedAnswer by remember {
        mutableStateOf("")
    }

    var answerSubmitted by remember {
        mutableStateOf(false)
    }

    var quizFinished by remember {
        mutableStateOf(false)
    }

    // ⏳ TIMER
    var timeLeft by remember {
        mutableStateOf(15)
    }

    // LOAD QUIZ
    LaunchedEffect(selectedSubject) {

        withContext(Dispatchers.IO) {

            val allWords =
                if (selectedSubject == "All")
                    dao.getAllWords()
                else
                    dao.getWordsBySubject(selectedSubject)

            words = allWords.shuffled()

            if (words.isNotEmpty()) {

                currentWord = words.random()

                val wrongOptions =
                    words
                        .filter {
                            it.meaning != currentWord!!.meaning
                        }
                        .shuffled()
                        .take(3)
                        .map { it.meaning }

                options =
                    (wrongOptions + currentWord!!.meaning)
                        .shuffled()
            }
        }
    }

    // ⏳ TIMER LOGIC
    LaunchedEffect(currentQuestion) {

        timeLeft = 15

        while (timeLeft > 0) {

            delay(1000)
            timeLeft--
        }

        // AUTO NEXT WHEN TIME ENDS
        if (!answerSubmitted) {

            wrongAnswers++

            if (currentQuestion >= 10) {

                CoroutineScope(Dispatchers.IO).launch {

                    val db =
                        AppDatabase.getDatabase(context)

                    db.quizStatsDao().insertQuiz(

                        QuizStats(
                            score = score,
                            totalQuestions = 10
                        )
                    )
                }

                quizFinished = true

            } else {

                currentQuestion++

                currentWord = words.random()

                val wrongOptions =
                    words
                        .filter {
                            it.meaning != currentWord!!.meaning
                        }
                        .shuffled()
                        .take(3)
                        .map { it.meaning }

                options =
                    (wrongOptions + currentWord!!.meaning)
                        .shuffled()

                selectedAnswer = ""
                answerSubmitted = false
            }
        }
    }

    val cardColor =
        if (darkMode)
            Color(0xFF1E1E1E)
        else
            Color.White.copy(alpha = 0.92f)

    val textColor =
        if (darkMode)
            Color.White
        else
            Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TITLE
        Text(
            text = "Quiz Mode 🧠",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7B1FA2)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SUBJECT SELECTOR
        Box {

            OutlinedButton(
                onClick = {
                    expanded = true
                }
            ) {

                Text(selectedSubject)
            }

            DropdownMenu(
                expanded = expanded,

                onDismissRequest = {
                    expanded = false
                }
            ) {

                subjects.forEach {

                    DropdownMenuItem(

                        text = {
                            Text(it)
                        },

                        onClick = {

                            selectedSubject = it
                            expanded = false

                            score = 0
                            correctAnswers = 0
                            wrongAnswers = 0
                            currentQuestion = 1
                            quizFinished = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FINAL RESULT
        if (quizFinished) {

            accuracy =
                ((correctAnswers / 10f) * 100).toInt()

            if (score > highestScore) {
                highestScore = score
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                ),

                shape = RoundedCornerShape(24.dp),

                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Quiz Completed 🎉",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1FA2)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "$score / 10 ⭐",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Accuracy: $accuracy%",
                        color = textColor,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Correct: $correctAnswers ✅",
                        color = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Wrong: $wrongAnswers ❌",
                        color = Color.Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Highest Score: $highestScore 🏆",
                        color = Color(0xFFFF9800)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val message =
                        when {

                            score >= 9 ->
                                "Excellent Performance 🔥"

                            score >= 7 ->
                                "Great Job 👏"

                            score >= 5 ->
                                "Good Try 👍"

                            else ->
                                "Keep Practicing 📚"
                        }

                    Text(
                        text = message,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1FA2)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    // PLAY AGAIN
                    Button(

                        onClick = {

                            score = 0
                            correctAnswers = 0
                            wrongAnswers = 0
                            currentQuestion = 1
                            quizFinished = false

                            currentWord = words.random()

                            val wrongOptions =
                                words
                                    .filter {
                                        it.meaning != currentWord!!.meaning
                                    }
                                    .shuffled()
                                    .take(3)
                                    .map { it.meaning }

                            options =
                                (wrongOptions + currentWord!!.meaning)
                                    .shuffled()

                            selectedAnswer = ""
                            answerSubmitted = false
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B1FA2)
                        ),

                        shape = RoundedCornerShape(50)
                    ) {

                        Text(
                            "Play Again 🔄",
                            color = Color.White
                        )
                    }
                }
            }

            return@Column
        }

        // TOP STATS CARD
        Card(

            colors = CardDefaults.cardColors(
                containerColor =
                    if (darkMode)
                        Color(0xFF2A2A2A)
                    else
                        Color(0xFFF3E5F5)
            ),

            shape = RoundedCornerShape(20.dp),

            modifier = Modifier.fillMaxWidth()
        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Score ⭐",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "$score",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Question 📚",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "$currentQuestion / 10",
                        color = textColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text = "Timer ⏳",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "$timeLeft s",
                        color =
                            if (timeLeft <= 5)
                                Color.Red
                            else
                                Color(0xFF4CAF50),

                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        currentWord?.let { word ->

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                ),

                shape = RoundedCornerShape(24.dp),

                elevation = CardDefaults.cardElevation(10.dp),

                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "What is meaning of:",
                        fontSize = 18.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = word.english,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1FA2)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    options.forEach { option ->

                        val buttonColor = when {

                            !answerSubmitted ->
                                Color(0xFFCE93D8)

                            option == word.meaning ->
                                Color(0xFF4CAF50)

                            option == selectedAnswer ->
                                Color.Red

                            else ->
                                Color.Gray
                        }

                        Button(

                            onClick = {

                                if (!answerSubmitted) {

                                    selectedAnswer = option
                                    answerSubmitted = true

                                    if (option == word.meaning) {

                                        score++
                                        correctAnswers++

                                    } else {

                                        wrongAnswers++
                                    }
                                }
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),

                            shape = RoundedCornerShape(16.dp),

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {

                            Text(
                                option,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // NEXT BUTTON
                    Button(

                        onClick = {

                            if (currentQuestion >= 10) {

                                CoroutineScope(Dispatchers.IO).launch {

                                    val db =
                                        AppDatabase.getDatabase(context)

                                    db.quizStatsDao().insertQuiz(

                                        QuizStats(
                                            score = score,
                                            totalQuestions = 10
                                        )
                                    )
                                }

                                quizFinished = true
                                return@Button
                            }

                            currentQuestion++

                            currentWord = words.random()

                            val wrongOptions =
                                words
                                    .filter {
                                        it.meaning != currentWord!!.meaning
                                    }
                                    .shuffled()
                                    .take(3)
                                    .map { it.meaning }

                            options =
                                (wrongOptions + currentWord!!.meaning)
                                    .shuffled()

                            selectedAnswer = ""
                            answerSubmitted = false
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B1FA2)
                        ),

                        shape = RoundedCornerShape(50),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Next Question ➡",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}@Composable
fun DailyScreen(
    dao: WordDao,
    modifier: Modifier
) {

    var word by remember { mutableStateOf<Word?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Load random word
    fun loadWord() {
        CoroutineScope(Dispatchers.IO).launch {
            val allWords = dao.getAllWords()
            val newWord = if (allWords.isNotEmpty()) allWords.random() else null

            withContext(Dispatchers.Main) {
                word = newWord
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadWord()
    }

    // 🔥 Fade animation
    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        label = ""
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔥 TITLE
            Text(
                text = "Word of the Day ✨",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A148C)
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF7B1FA2))
            } else {

                word?.let { w ->

                    // 🌟 CARD
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { this.alpha = alpha }
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = w.english,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A1B9A)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Kannada",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )

                            Text(
                                text = w.kannada,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Meaning",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )

                            Text(
                                text = w.meaning,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = w.subject,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // 🔄 NEW WORD BUTTON
                    Button(
                        onClick = {
                            isLoading = true
                            loadWord()
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B1FA2)
                        )
                    ) {
                        Text("New Word 🔄", color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun ProfileScreen(
    userDao: UserDao,
    email: String,
    modifier: Modifier,
    onLogout: () -> Unit,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    var user by remember { mutableStateOf<User?>(null) }

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }

    // 📊 LIVE STATS
    var savedCount by remember { mutableStateOf(0) }
    var quizScore by remember { mutableStateOf(0) }
    var quizzesPlayed by remember { mutableStateOf(0) }
    var streakDays by remember { mutableStateOf(1) }

    // LOAD USER + STATS
    LaunchedEffect(email) {

        withContext(Dispatchers.IO) {

            user = userDao.getUserByEmail(email)

            user?.let {

                name = it.name
                password = it.password
                phone = it.phone
            }

            // ⭐ SAVED WORDS COUNT
            savedCount =
                db.savedWordDao()
                    .getAllSavedWords()
                    .size

            // 🧠 QUIZ STATS
            val quizList =
                db.quizStatsDao().getAllQuizStats()

            quizzesPlayed = quizList.size

            quizScore =
                db.quizStatsDao()
                    .getHighestScore() ?: 0

            streakDays =
                if (quizList.isEmpty())
                    1
                else
                    quizList.size
        }
    }

    val cardColor =
        if (darkMode)
            Color(0xFF1E1E1E)
        else
            Color.White.copy(alpha = 0.92f)

    val textColor =
        if (darkMode)
            Color.White
        else
            Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // 👤 PROFILE ICON
        Box(
            contentAlignment = Alignment.Center,

            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFFCE93D8))
        ) {

            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7B1FA2)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (user == null) {

            Text(
                "No user found",
                color = textColor
            )

            return@Column
        }

        Card(
            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp),

            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),

            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                if (isEditing) {

                    // 👤 NAME
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 📱 PHONE
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 🔒 PASSWORD
                    OutlinedTextField(

                        value = password,

                        onValueChange = {
                            password = it
                        },

                        label = {
                            Text("Password")
                        },

                        shape = RoundedCornerShape(14.dp),

                        visualTransformation =
                            if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    passwordVisible =
                                        !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,

                                    contentDescription = ""
                                )
                            }
                        },

                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(

                        onClick = {

                            CoroutineScope(Dispatchers.IO).launch {

                                userDao.updateUser(

                                    user!!.copy(
                                        name = name,
                                        phone = phone,
                                        password = password
                                    )
                                )
                            }

                            isEditing = false
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B1FA2)
                        ),

                        shape = RoundedCornerShape(50),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Save Changes",
                            color = Color.White
                        )
                    }

                } else {

                    // 👤 USER DETAILS
                    Text(
                        text = "Name",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "Email",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = email,
                        fontSize = 18.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "Phone",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = phone,
                        fontSize = 18.sp,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    // 📊 TITLE
                    Text(
                        text = "Learning Progress 📊",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1FA2)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // 🔥 STREAK CARD
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (darkMode)
                                    Color(0xFF2A2A2A)
                                else
                                    Color(0xFFFFF3E0)
                        ),

                        shape = RoundedCornerShape(18.dp),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                            verticalAlignment =
                                Alignment.CenterVertically,

                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    text = "Daily Streak 🔥",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "$streakDays Days Active",
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = "🔥",
                                fontSize = 40.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // 📊 STATS
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        // ⭐ SAVED WORDS
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (darkMode)
                                        Color(0xFF2A2A2A)
                                    else
                                        Color(0xFFE1F5FE)
                            ),

                            shape = RoundedCornerShape(18.dp),

                            modifier = Modifier.weight(1f)
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "⭐",
                                    fontSize = 28.sp
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = savedCount.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                Text(
                                    text = "Saved",
                                    color = Color.Gray
                                )
                            }
                        }

                        // 🧠 QUIZ
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (darkMode)
                                        Color(0xFF2A2A2A)
                                    else
                                        Color(0xFFF3E5F5)
                            ),

                            shape = RoundedCornerShape(18.dp),

                            modifier = Modifier.weight(1f)
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "🧠",
                                    fontSize = 28.sp
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "$quizScore/10",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                Text(
                                    text = "Quiz",
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // 🎯 QUIZZES PLAYED
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (darkMode)
                                    Color(0xFF2A2A2A)
                                else
                                    Color(0xFFE8F5E9)
                        ),

                        shape = RoundedCornerShape(18.dp),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    text = "Quizzes Played 🎯",
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = quizzesPlayed.toString(),
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = "📚",
                                fontSize = 34.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 🌙 DARK MODE
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            "Dark Mode 🌙",
                            fontSize = 18.sp,
                            color = textColor
                        )

                        Switch(
                            checked = darkMode,

                            onCheckedChange = {
                                onDarkModeChange(it)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // ✏ EDIT
                    Button(

                        onClick = {
                            isEditing = true
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B1FA2)
                        ),

                        shape = RoundedCornerShape(50),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Edit Profile ✏️",
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // 🚪 LOGOUT
                    Button(

                        onClick = {
                            onLogout()
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),

                        shape = RoundedCornerShape(50),

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Logout",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}