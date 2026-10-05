package com.example.telemedicine

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.telemedicine.ui.screens.BookVideoConsultScreen
import com.example.telemedicine.ui.screens.DoctorHomeScreen
import com.example.telemedicine.ui.screens.DoctorPatientDetailScreen
import com.example.telemedicine.ui.screens.LoginScreen
import com.example.telemedicine.ui.screens.MedicalRecordsScreen
import com.example.telemedicine.ui.screens.OrderMedicineScreen
import com.example.telemedicine.ui.screens.PatientHomeScreen
import com.example.telemedicine.ui.screens.ProfileScreen
import com.example.telemedicine.ui.screens.SymptomAnalysisScreen
import com.example.telemedicine.ui.screens.VideoCallScreen
import com.example.telemedicine.ui.screens.WorkerHomeScreen
import com.example.telemedicine.ui.theme.TelemedicineTheme
import com.example.telemedicine.ui.screens.WelfareDashboardScreen // <-- NEW

/* ----------------------- Top-level app routes (existing) ----------------------- */
private const val ROUTE_LOGIN = "login"
private const val ROUTE_PATIENT_HOME = "patientHome"
private const val ROUTE_DOCTOR_HOME = "doctorHome"
private const val ROUTE_WORKER_HOME = "workerHome"
private const val ROUTE_VIDEO_CALL = "videoCall"
private const val ROUTE_WELFARE_DASHBOARD = "welfareDashboard"
private const val ROUTE_DOCTOR_PATIENT_DETAIL = "doctorPatientDetail"
private const val ARG_NAME = "name"
private const val ARG_CALLER = "caller"

/* ----------------------- NEW: direct route for Welfare dashboard -------------- */
private const val ROUTE_WELFARE = "welfareDashboard"

/* -------------- Patient-area subroutes (nested NavHost inside patient) -------- */
private object PatientRoutes {
    const val HOME = "home"
    const val SYMPTOM = "symptom"
    const val CONSULT = "consult"
    const val RECORDS = "records"
    const val ORDER = "order"
    const val PROFILE = "profile"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TelemedicineApp() }
    }
}

@Composable
fun TelemedicineApp() {
    TelemedicineTheme {
        val navController = rememberNavController()
        var language by rememberSaveable { mutableStateOf(AppLanguage.ENGLISH) }

        TelemedicineNavHost(
            navController = navController,
            language = language,
            onLanguageChange = { language = it },
        )
    }
}

@Composable
private fun TelemedicineNavHost(
    navController: NavHostController,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val logout: () -> Unit = {
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        /* ---------------------- LOGIN (unchanged) ---------------------- */
        composable(ROUTE_LOGIN) {
            LoginScreen(
                language = language,
                onLanguageChange = onLanguageChange,
                onLogin = { role, name ->
                    val route = role.navRoute()
                    val encodedName = Uri.encode(name)
                    navController.navigate("$route?$ARG_NAME=$encodedName")
                }
            )
        }

        /* ---------------------- PATIENT AREA (nested bottom-nav) ---------------------- */
        composable(
            route = "$ROUTE_PATIENT_HOME?$ARG_NAME={$ARG_NAME}",
            arguments = listOf(
                navArgument(ARG_NAME) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val displayName = entry.arguments?.getString(ARG_NAME).orEmpty()

            // The patient area is a nested NavHost with its own bottom bar & routes
            PatientAreaNav(
                language = language,
                displayName = displayName,
                onLanguageChange = onLanguageChange,
                onLogout = logout,
                // This lambda lets child screens open your existing VideoCallScreen route
                openVideoCall = { navController.navigate("$ROUTE_VIDEO_CALL/patient") }
            )
        }

        /* ---------------------- DOCTOR & WORKER (unchanged) ---------------------- */
        composable(
            route = "$ROUTE_DOCTOR_HOME?$ARG_NAME={$ARG_NAME}",
            arguments = listOf(
                navArgument(ARG_NAME) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val displayName = entry.arguments?.getString(ARG_NAME).orEmpty()
            DoctorHomeScreen(
                language = language,
                displayName = displayName,
                onBack = { navController.popBackStack() },
                onStartVideoCall = { navController.navigate("$ROUTE_VIDEO_CALL/doctor") },
                onUploadPrescription = { /* TODO: implement upload flow */ },
                onRecordVoiceNote = { /* TODO: implement voice note flow */ },
                onLanguageChange = onLanguageChange,
                onLogout = logout,
                onOpenPatientDetail = { patient ->
                    val encoded = Uri.encode(patient)
                    navController.navigate("$ROUTE_DOCTOR_PATIENT_DETAIL/$encoded")
                }
            )
        }

        composable(
            route = "$ROUTE_DOCTOR_PATIENT_DETAIL/{$ARG_NAME}",
            arguments = listOf(navArgument(ARG_NAME) { type = NavType.StringType })
        ) { entry ->
            val name = Uri.decode(entry.arguments?.getString(ARG_NAME).orEmpty())
            DoctorPatientDetailScreen(
                language = language,
                patientName = name,
                onBack = { navController.popBackStack() },
                onLanguageChange = onLanguageChange,
                onLogout = logout
            )
        }

        composable(
            route = "$ROUTE_WELFARE_DASHBOARD?$ARG_NAME={$ARG_NAME}",
            arguments = listOf(
                navArgument(ARG_NAME) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            WelfareDashboardScreen(
                onBack = { navController.popBackStack() },
                onExport = { /* TODO */ }
            )
        }

        composable(
            route = "$ROUTE_WORKER_HOME?$ARG_NAME={$ARG_NAME}",
            arguments = listOf(
                navArgument(ARG_NAME) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val displayName = entry.arguments?.getString(ARG_NAME).orEmpty()
            WorkerHomeScreen(
                language = language,
                displayName = displayName,
                onBack = { navController.popBackStack() },
                onLanguageChange = onLanguageChange,
                onLogout = logout
            )
        }

        /* ---------------------- VIDEO CALL (unchanged) ---------------------- */
        composable(
            route = "$ROUTE_VIDEO_CALL/{$ARG_CALLER}",
            arguments = listOf(navArgument(ARG_CALLER) { type = NavType.StringType })
        ) { entry ->
            val caller = entry.arguments?.getString(ARG_CALLER).orEmpty()
            VideoCallScreen(
                language = language,
                caller = caller,
                onClose = { navController.popBackStack() },
                onLanguageChange = onLanguageChange,
                onLogout = logout
            )
        }

        /* ---------------------- NEW: top-level Welfare dashboard ---------------------- */
        composable(ROUTE_WELFARE) {
            WelfareDashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/* ---------------------- Patient area with bottom bar ---------------------- */
@Composable
private fun PatientAreaNav(
    language: AppLanguage,
    displayName: String,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    openVideoCall: () -> Unit
) {
    val nav = rememberNavController()

    val bottomItems = listOf(
        PatientRoutes.HOME to Icons.Outlined.Home,
        PatientRoutes.CONSULT to Icons.Outlined.Videocam,
        PatientRoutes.RECORDS to Icons.Outlined.Description,
        PatientRoutes.PROFILE to Icons.Outlined.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current = nav.currentBackStackEntryAsState().value?.destination?.route
                bottomItems.forEach { (route, icon) ->
                    val label = when (route) {
                        PatientRoutes.HOME -> localizedText(language, "Home", "होम")
                        PatientRoutes.CONSULT -> localizedText(language, "Consult", "परामर्श")
                        PatientRoutes.RECORDS -> localizedText(language, "Records", "रिकॉर्ड")
                        PatientRoutes.PROFILE -> localizedText(language, "Profile", "प्रोफ़ाइल")
                        else -> localizedText(language, "Home", "होम")
                    }
                    NavigationBarItem(
                        selected = current == route,
                        onClick = {
                            nav.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = PatientRoutes.HOME,
            modifier = Modifier.padding(inner)
        ) {
            composable(PatientRoutes.HOME) {
                PatientHomeScreen(
                    language = language,
                    displayName = displayName,
                    onBack = { /* no-op on home */ },
                    onStartVideoCall = { openVideoCall() },
                    onLanguageChange = onLanguageChange,
                    onLogout = onLogout,
                    onOpenSymptom = { nav.navigate(PatientRoutes.SYMPTOM) },
                    onOpenConsult = { nav.navigate(PatientRoutes.CONSULT) },
                    onOpenRecords = { nav.navigate(PatientRoutes.RECORDS) },
                    onOpenOrder = { nav.navigate(PatientRoutes.ORDER) }
                )
            }
            composable(PatientRoutes.SYMPTOM) {
                SymptomAnalysisScreen(
                    language = language,
                    onConsult = { nav.navigate(PatientRoutes.CONSULT) },
                    onLanguageChange = onLanguageChange,
                    onLogout = onLogout
                )
            }
            composable(PatientRoutes.CONSULT) {
                BookVideoConsultScreen(
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onLogout = onLogout
                )
            }
            composable(PatientRoutes.RECORDS) {
                MedicalRecordsScreen(
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onLogout = onLogout
                )
            }
            composable(PatientRoutes.ORDER) {
                OrderMedicineScreen(
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onLogout = onLogout
                )
            }
            composable(PatientRoutes.PROFILE) {
                ProfileScreen(
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onLogout = onLogout
                )
            }

            /* -------- NEW: nested Welfare tab that shows the same dashboard -------- */
        }
    }
}
