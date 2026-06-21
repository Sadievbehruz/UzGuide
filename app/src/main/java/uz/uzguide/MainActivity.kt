package uz.uzguide

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


val allPlaces = listOf(
    // --- SAMARKAND ---
    Place(R.string.place_registan_square, "Samarkand", "Monuments", 4.9, 39.654850, 66.975700, R.drawable.registan, id = "1", aboutFileName = "registan_square.txt").apply { area = "0.02" },
    Place(R.string.place_gur_e_amir_mausoleum, "Samarkand", "Monuments", 4.8, 39.648350, 66.969150, R.drawable.gur_amir, id = "2", aboutFileName = "gur_e_amir.txt").apply { area = "0.008" },
    Place(R.string.place_shah_i_zinda, "Samarkand", "Monuments", 4.9, 39.663050, 66.987050, R.drawable.shahi_zinda, id = "3", aboutFileName = "shah_i_zinda.txt").apply { area = "0.012" },
    Place(R.string.place_bibi_khanym_mosque, "Samarkand", "Mosque", 4.7, 39.660650, 66.979300, R.drawable.bibi_khanym, id = "4", aboutFileName = "bibi_khanym.txt").apply { area = "0.018" },
    Place(R.string.place_ulugbek_observatory, "Samarkand", "Museum", 4.6, 39.674750, 67.006450, R.drawable.observatory, id = "5", aboutFileName = "ulugbek_observatory.txt").apply { area = "0.005" },
    Place(R.string.place_saint_daniel_mausoleum, "Samarkand", "Monuments", 4.5, 39.663950, 67.001150, R.drawable.daniel, id = "6", aboutFileName = "saint_daniel.txt").apply { area = "0.003" },
    Place(R.string.place_afrasiyab_museum, "Samarkand", "Museum", 4.4, 39.670150, 66.993450, R.drawable.afrasiyab, id = "7", aboutFileName = "afrasiyab_museum.txt").apply { area = "0.004" },
    Place(R.string.place_imam_al_bukhari_complex, "Samarkand", "Monuments", 4.9, 39.812220, 66.804250, R.drawable.bukhari, id = "8", aboutFileName = "imam_al_bukhari.txt").apply { area = "0.025" },

    // --- BUKHARA ---
    Place(R.string.place_ark_of_bukhara, "Bukhara", "Fortress", 4.7, 39.777850, 64.410850, R.drawable.ark_bukhara, id = "9", aboutFileName = "ark_of_bukhara.txt").apply { area = "0.042" },
    Place(R.string.place_po_i_kalyan_complex, "Bukhara", "Monuments", 4.9, 39.775620, 64.414950, R.drawable.kalyan, id = "10", aboutFileName = "poi_kalyan.txt").apply { area = "0.015" },
    Place(R.string.place_samanid_mausoleum, "Bukhara", "Monuments", 4.8, 39.777150, 64.400850, R.drawable.samanid, id = "11", aboutFileName = "samanid_mausoleum.txt").apply { area = "0.002" },
    Place(R.string.place_chashma_ayub_mausoleum, "Bukhara", "Monuments", 4.5, 39.778150, 64.402050, R.drawable.ayub, id = "12", aboutFileName = "chashma_ayub.txt").apply { area = "0.003" },
    Place(R.string.place_lyabi_hauz_ensemble, "Bukhara", "Monuments", 4.7, 39.773150, 64.420650, R.drawable.lyabi_hauz, id = "13", aboutFileName = "lyabi_hauz.txt").apply { area = "0.011" },
    Place(R.string.place_sitorai_mohi_khosa, "Bukhara", "Resort", 4.6, 39.812450, 64.441450, R.drawable.sitora, id = "14", aboutFileName = "sitorai_mohi_khosa.txt").apply { area = "0.065" },
    Place(R.string.place_chor_bakr_necropolis, "Bukhara", "Monuments", 4.5, 39.774350, 64.336150, R.drawable.chor_bakr, id = "15", aboutFileName = "chor_bakr.txt").apply { area = "0.030" },
    Place(R.string.place_naqshbandi_memorial, "Bukhara", "Monuments", 4.8, 39.801450, 64.536750, R.drawable.naqshbandi, id = "16", aboutFileName = "naqshbandi_memorial.txt").apply { area = "0.022" },
    Place(R.string.place_magok_i_attari_mosque, "Bukhara", "Mosque", 4.4, 39.773850, 64.417850, R.drawable.magoki, id = "17", aboutFileName = "magoki_attari.txt").apply { area = "0.001" },
    Place(R.string.place_chor_minor_madrasah, "Bukhara", "Madrasah", 4.6, 39.774950, 64.427450, R.drawable.chor_minor, id = "18", aboutFileName = "chor_minor.txt").apply { area = "0.002" },

    // --- KHIVA ---
    Place(R.string.place_juma_mosque, "Khiva", "Mosque", 4.7, 41.378050, 60.360050, R.drawable.juma_khiva, id = "19", aboutFileName = "juma_mosque_khiva.txt").apply { area = "0.003" },
    Place(R.string.place_pahlavon_mahmud_mausoleum, "Khiva", "Monuments", 4.8, 41.377550, 60.358950, R.drawable.pahlavon, id = "20", aboutFileName = "pahlavon_mahmud.txt").apply { area = "0.004" },
    Place(R.string.place_islam_khoja_minaret, "Khiva", "Monuments", 4.9, 41.377050, 60.360650, R.drawable.islam_khoja, id = "21", aboutFileName = "islam_khoja.txt").apply { area = "0.002" },
    Place(R.string.place_kalta_minor, "Khiva", "Monuments", 4.8, 41.378350, 60.358250, R.drawable.kalta_minor, id = "22", aboutFileName = "kalta_minor.txt").apply { area = "0.001" },
    Place(R.string.place_kunya_ark_citadel, "Khiva", "Fortress", 4.7, 41.379250, 60.358150, R.drawable.kunya_ark, id = "23", aboutFileName = "kunya_ark.txt").apply { area = "0.012" },
    Place(R.string.place_toshhovli_palace, "Khiva", "Fortress", 4.6, 41.379450, 60.362150, R.drawable.toshhovli, id = "24", aboutFileName = "toshhovli_palace.txt").apply { area = "0.007" },
    Place(R.string.place_muhammad_amin_khan_madrasah, "Khiva", "Madrasah", 4.7, 41.378750, 60.358550, R.drawable.amin_khan, id = "25", aboutFileName = "muhammad_amin_khan.txt").apply { area = "0.005" },

    // --- TASHKENT ---
    Place(R.string.place_islamic_civilization_center, "Tashkent", "Museum", 4.9, 41.336420, 69.239350, R.drawable.islamic_center, id = "26", aboutFileName = "islamic_civilization.txt").apply { area = "0.045" },
    Place(R.string.place_hazrati_imam_complex, "Tashkent", "Monuments", 4.8, 41.336850, 69.238650, R.drawable.hazrati_imam, id = "28", aboutFileName = "hazrati_imam.txt").apply { area = "0.020" },
    Place(R.string.place_kukeldash_madrasah, "Tashkent", "Madrasah", 4.5, 41.323150, 69.236150, R.drawable.kukeldash, id = "29", aboutFileName = "kukeldash_madrasah.txt").apply { area = "0.006" },
    Place(R.string.place_history_museum_of_uzbekistan, "Tashkent", "Museum", 4.6, 41.311650, 69.270450, R.drawable.history_museum, id = "30", aboutFileName = "history_museum.txt").apply { area = "0.008" },
    Place(R.string.place_amir_temur_museum, "Tashkent", "Museum", 4.7, 41.313550, 69.279650, R.drawable.temur_museum, id = "31", aboutFileName = "amir_temur_museum.txt").apply { area = "0.005" },
    Place(R.string.place_applied_arts_museum, "Tashkent", "Museum", 4.5, 41.301181275464316, 69.25941858029898, R.drawable.applied_arts, id = "32", aboutFileName = "applied_arts.txt").apply { area = "0.003" },
    Place(R.string.place_independence_square, "Tashkent", "Monuments", 4.8, 41.315850, 69.269450, R.drawable.independence_sq, id = "33", aboutFileName = "independence_square.txt").apply { area = "0.120" },
    Place(R.string.place_tashkent_tv_tower, "Tashkent", "Monuments", 4.7, 41.346350, 69.285150, R.drawable.tv_tower, id = "34", aboutFileName = "tashkent_tv_tower.txt").apply { area = "0.010" },
    Place(R.string.place_new_uzbekistan_park, "Tashkent", "Resort", 4.8, 41.304550, 69.451450, R.drawable.new_uzb_park, id = "35", aboutFileName = "new_uzbekistan_park.txt").apply { area = "1.040" },

    // --- RESORTS & NATURE ---
    Place(R.string.place_amirsoy_mountain_resort, "Bostanlyk", "Resort", 4.9, 41.4887714200921, 69.9451241094759, R.drawable.amirsoy, id = "36", aboutFileName = "amirsoy_resort.txt").apply { area = "0.650" },
    Place(R.string.place_charvak_reservoir, "Bostanlyk", "Resort", 4.7, 41.628850, 70.024450, R.drawable.charvak, id = "37", aboutFileName = "charvak_reservoir.txt").apply { area = "37.0" },
    Place(R.string.place_chimgan_mountains, "Bostanlyk", "Resort", 4.6, 41.549250, 70.052550, R.drawable.chimgan, id = "38", aboutFileName = "chimgan_mountains.txt").apply { area = "15.0" },
    Place(R.string.place_zaamin_national_park, "Jizzakh", "Resort", 4.8, 39.638950, 68.455650, R.drawable.zaamin, id = "39", aboutFileName = "zaamin_national_park.txt").apply { area = "241.1" },
    Place(R.string.place_aydar_lake, "Jizzakh", "Resort", 4.5, 40.916750, 66.983350, R.drawable.aydar_lake, id = "40", aboutFileName = "aydar_lake.txt").apply { area = "3000.0" },
    Place(R.string.place_sangardak_waterfall, "Surkhandarya", "Resort", 4.6, 38.483350, 67.450050, R.drawable.sangardak, id = "41", aboutFileName = "sangardak_waterfall.txt").apply { area = "0.050" },
    Place(R.string.place_arashan_lakes, "Namangan", "Resort", 4.5, 41.166750, 70.433350, R.drawable.arashan, id = "42", aboutFileName = "arashan_lakes.txt").apply { area = "2.500" },
    Place(R.string.place_shohimardon_resort, "Fergana", "Resort", 4.7, 39.983350, 71.800050, R.drawable.shohimardon, id = "43", aboutFileName = "shohimardon_resort.txt").apply { area = "90.0" },

    // --- OTHERS ---
    Place(R.string.place_ak_saray_palace, "Shahrisabz", "Fortress", 4.6, 39.058350, 66.830650, R.drawable.aksaray, id = "44", aboutFileName = "ak_saray_palace.txt").apply { area = "0.080" },
    Place(R.string.place_hudayar_khan_palace, "Kokand", "Fortress", 4.7, 40.533350, 70.933350, R.drawable.khudayar, id = "45", aboutFileName = "khudayar_khan.txt").apply { area = "0.040" },
    Place(R.string.place_sultan_saodat_ensemble, "Termez", "Monuments", 4.5, 37.266750, 67.333350, R.drawable.saodat, id = "46", aboutFileName = "sultan_saodat.txt").apply { area = "0.015" },
    Place(R.string.place_savitsky_art_museum, "Nukus", "Museum", 4.9, 42.466750, 59.616750, R.drawable.savitsky, id = "47", aboutFileName = "savitsky_museum.txt").apply { area = "0.007" }
)

val TealPrimary = Color(0xFF26A69A)
val TealDark = Color(0xFF0F3D39)
val InfoBackgroundGradient = Color.Black.copy(alpha = 0.5f)

@Composable
fun getLocalizedPlaceName(englishName: String): String {
    val context = androidx.compose.ui.platform.LocalContext.current

    val resourceName = "place_" + englishName.lowercase()
        .replace(" ", "_")
        .replace("-", "_")
        .replace("'", "")

    val resId = context.resources.getIdentifier(resourceName, "string", context.packageName)

    return if (resId != 0) androidx.compose.ui.res.stringResource(id = resId) else englishName
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (!locales.isEmpty) {
            AppCompatDelegate.setApplicationLocales(locales)
        }
        super.onCreate(savedInstanceState)
        val currentLang = AppCompatDelegate.getApplicationLocales()[0]?.language

        enableEdgeToEdge()

        setContent {
            UzGuideTheme {
                val context = LocalContext.current
                val sharedPreferences = remember { context.getSharedPreferences("UzGuidePrefs", Context.MODE_PRIVATE) }
                var currentScreen by remember { mutableStateOf("home") }
                var selectedPlace by remember { mutableStateOf<Place?>(null) }
                var userLocation by remember { mutableStateOf<android.location.Location?>(null) }

                val savedPlaceNames = remember {
                    val savedSet = sharedPreferences.getStringSet("saved_places", emptySet()) ?: emptySet()
                    mutableStateListOf<String>().apply { addAll(savedSet) }
                }

                val auth = remember { FirebaseAuth.getInstance() }
                var currentUser by remember { mutableStateOf(auth.currentUser) }
                var isSplashFinished by remember { mutableStateOf(false) }
                var selectedMapPoint by remember { mutableStateOf<com.yandex.mapkit.geometry.Point?>(null) }

                LaunchedEffect(Unit) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getUserLocation(context) { lat, lon ->
                            val loc = android.location.Location("GPS").apply {
                                latitude = lat
                                longitude = lon
                            }
                            userLocation = loc
                        }
                    } else {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1001
                        )
                    }
                }

                val onSaveToggle: (Place) -> Unit = { place ->
                    val targetId = place.id

                    if (savedPlaceNames.contains(targetId)) {
                        savedPlaceNames.remove(targetId)
                    } else {
                        savedPlaceNames.add(targetId)
                    }

                    sharedPreferences.edit().putStringSet("saved_places", savedPlaceNames.toSet()).apply()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {

                        !isSplashFinished -> {
                            SplashScreen(onFinished = { isSplashFinished = true })
                        }
                        currentUser == null -> {
                            LoginScreen(onSignInSuccess = { currentUser = auth.currentUser })
                        }
                        else -> {

                            var showMenuBottomSheet by remember { mutableStateOf(false) }

                            var userLat by remember { mutableDoubleStateOf(41.3111) }
                            var userLon by remember { mutableDoubleStateOf(69.2401) }

                            BackHandler(enabled = currentScreen != "home") {
                                if (currentScreen == "detail") {
                                    selectedPlace = null
                                }
                                currentScreen = "home"
                            }
                            val isDarkTheme = isSystemInDarkTheme()
                            val screenBackgroundColor = if (isDarkTheme) Color(0xFF0F3D39) else Color.White

                            Scaffold(
                                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                containerColor = screenBackgroundColor,
                                bottomBar = {
                                    val showBottomBar = currentScreen in listOf("home", "map", "saved", "hotel", "market", "profile")
                                    if (showBottomBar) {
                                        BottomNavigationBar(
                                            currentScreen = currentScreen,
                                            onNavigate = {
                                                currentScreen = it
                                                selectedPlace = null
                                            }
                                        )
                                    }
                                }
                            ) { innerPadding ->

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    when (currentScreen) {
                                        "detail" -> {
                                            if (selectedPlace != null) {
                                                val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
                                                PlaceDetailScreen(
                                                    place = selectedPlace!!,
                                                    userLat = userLocation?.latitude ?: 41.3111,
                                                    userLon = userLocation?.longitude ?: 69.2401,
                                                    onBackClick = {
                                                        focusManager.clearFocus()
                                                        selectedPlace = null
                                                        currentScreen = "home"
                                                    },
                                                    onSaveToggle = onSaveToggle,
                                                    isSaved = savedPlaceNames.contains(selectedPlace!!.id),
                                                    onGetDirections = { latitude, longitude ->
                                                        focusManager.clearFocus()
                                                        selectedMapPoint = com.yandex.mapkit.geometry.Point(latitude, longitude)
                                                        currentScreen = "map"
                                                    }
                                                )
                                            } else {
                                                currentScreen = "home"
                                            }
                                        }

                                        "home" -> Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .statusBarsPadding()
                                        ) {
                                            androidx.compose.runtime.key(currentScreen) {
                                                UzbekistanTravelScreenContent(
                                                    savedPlaceNames = savedPlaceNames,
                                                    onSaveToggle = onSaveToggle,
                                                    onPlaceClick = { place ->
                                                        selectedPlace = place
                                                        currentScreen = "detail"
                                                    },
                                                    onGetDirections = { latitude, longitude ->
                                                        selectedMapPoint = com.yandex.mapkit.geometry.Point(latitude, longitude)
                                                        selectedPlace = null
                                                        currentScreen = "map"
                                                    }
                                                )
                                            }
                                        }

                                        "map" -> {
                                            YandexMapView(
                                                places = allPlaces,
                                                latitude = userLocation?.latitude ?: 41.3111,
                                                longitude = userLocation?.longitude ?: 69.2401,
                                                selectedTargetPoint = selectedMapPoint,
                                                onRouteCleared = { selectedMapPoint = null },
                                                onPlaceClick = { place ->
                                                    selectedPlace = place
                                                    currentScreen = "detail"
                                                }
                                            )
                                        }

                                        "saved" -> Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                                            SavedScreenContent(
                                                places = allPlaces.filter { savedPlaceNames.contains(it.id) },
                                                userLat = userLat,
                                                userLon = userLon,
                                                onRemove = { place -> onSaveToggle(place) },
                                                onPlaceClick = { place ->
                                                    selectedPlace = place
                                                    currentScreen = "detail"
                                                }
                                            )
                                        }

                                        "hotel" -> Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .statusBarsPadding()
                                        ) {
                                            val hotelImages = listOf(
                                                R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4,
                                                R.drawable.h5, R.drawable.h6, R.drawable.h7, R.drawable.h8,
                                                R.drawable.h9, R.drawable.h10, R.drawable.h11, R.drawable.h12, R.drawable.h13
                                            )
                                            HotelsScreen(
                                                hotelImages = hotelImages,
                                                onBackClick = { currentScreen = "home" }
                                            )
                                        }

                                        "market" -> Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .statusBarsPadding()
                                        ) {
                                            val marketImages = listOf(
                                                R.drawable.m1, R.drawable.m2, R.drawable.m3,
                                                R.drawable.m4, R.drawable.m5, R.drawable.m6,
                                                R.drawable.m7, R.drawable.m8, R.drawable.m9,
                                                R.drawable.m10, R.drawable.m11
                                            )
                                            MarketScreen(
                                                marketImages = listOf(R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5, R.drawable.m6, R.drawable.m7, R.drawable.m8, R.drawable.m9, R.drawable.m10, R.drawable.m11),
                                                onBackClick = { currentScreen = "home" }
                                            )
                                        }

                                        "profile" -> Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                                            ProfileScreen(
                                                onBackClick = { currentScreen = "home" },
                                                onLoggedOut = {
                                                    auth.signOut()
                                                    currentUser = null
                                                },
                                                onLanguageChange = {
                                                    sharedPreferences.edit().putBoolean("lang_selected", false).apply()
                                                    (context as? android.app.Activity)?.recreate()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("866875425245-mn9fj684lstj6ov64n90pqgmop7dtkpc.apps.googleusercontent.com")
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

private fun getUserLocation(context: Context, onLocationReceived: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            } else {
                onLocationReceived(41.3111, 69.2401)
            }
        }
    } else {
        onLocationReceived(41.3111, 69.2401)
    }
}

suspend fun fetchRealWeather(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
    try {

        val apiKey = "cf6fe398d16247dae034a452f61f9b16"
        val urlString = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$apiKey"

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            val main = json.getJSONObject("main")
            val temp = main.getDouble("temp").toInt()

            val weatherArray = json.getJSONArray("weather")
            val description = if (weatherArray.length() > 0) {
                weatherArray.getJSONObject(0).getString("main")
            } else ""

            val emoji = when (description.lowercase()) {
                "clear" -> "☀️"
                "clouds" -> "☁️"
                "rain", "drizzle" -> "🌧️"
                "thunderstorm" -> "⛈️"
                "snow" -> "❄️"
                else -> "🌡️"
            }

            return@withContext "$emoji $temp°C"
        } else {
            return@withContext "--°C"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext "--°C"
    }
}

fun loadTextFromAssets(context: Context, placeBaseName: String): String {
    val currentLocale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
    val languageCode = currentLocale?.language ?: "en"

    val fullFileName = when (languageCode) {
        "uz" -> "${placeBaseName}_uz.txt"
        "ru" -> "${placeBaseName}_ru.txt"
        else -> "${placeBaseName}.txt"
    }

    return try {
        context.assets.open(fullFileName).bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        e.printStackTrace()
        if (fullFileName != "${placeBaseName}.txt") {
            try {
                context.assets.open("${placeBaseName}.txt").bufferedReader().use { it.readText() }
            } catch (innerException: IOException) {
                context.getString(R.string.desc_not_available)
            }
        } else {
            context.getString(R.string.desc_not_available)
        }
    }
}

fun parseCombinedStyles(text: String): AnnotatedString {
    return buildAnnotatedString {
        val pattern = Regex("<b>(.*?)</b>|\\*\\*(.*?)\\*\\*")
        var lastIndex = 0

        pattern.findAll(text).forEach { matchResult ->
            append(text.substring(lastIndex, matchResult.range.first))

            val boldContent = matchResult.groupValues[1]
            val italicContent = matchResult.groupValues[2]

            if (boldContent.isNotEmpty()) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(boldContent)
                }
            } else if (italicContent.isNotEmpty()) {
                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(italicContent)
                }
            }
            lastIndex = matchResult.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}

@Composable
fun LoginScreen(onSignInSuccess: () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val mainTextColor = if (isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color.Gray
    val cardBackground = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val tealPrimary = Color(0xFF26A69A)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { taskAuth ->
                    if (taskAuth.isSuccessful) {
                        println("Firebase: Successfully signed in!")
                        onSignInSuccess()
                    } else {
                        println("Firebase error: ${taskAuth.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            println("Google Sign-In error: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.uzguide_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = stringResource(id = R.string.welcome),
                color = tealPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.app_subtitle),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = tealPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable {
                        val signInIntent = getGoogleSignInClient(context).signInIntent
                        launcher.launch(signInIntent)
                    },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, tealPrimary),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(id = R.string.sign_in_google),
                        color = mainTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

data class Place(
    @StringRes val nameResId: Int,
    val city: String,
    val category: String,
    val rating: Double,
    val latitude: Double,
    val longitude: Double,
    @DrawableRes val imageRes: Int,
    val id: String,
    val aboutFileName: String,
    var area: String = "N/A",
    var price: String = "Free",
    var description: String = ""
)

fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return String.format(java.util.Locale.US, "%.1f km", r * c)
}

@Composable
fun PlaceDetailScreen(
    place: Place,
    userLat: Double = 41.311081,
    userLon: Double = 69.240562,
    onBackClick: () -> Unit,
    onSaveToggle: (Place) -> Unit,
    isSaved: Boolean,
    onGetDirections: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var weatherInfo by remember { mutableStateOf("Loading...") }
    var isExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF0F3D39) else Color(0xFFFFFFFF)
    val bottomBarBackgroundColor = if (isDarkTheme) Color(0xFF1E2625) else Color.White
    val noDetailsMessage = stringResource(id = R.string.no_details_available)

    val currentLanguage = context.resources.configuration.locales[0].language

    val aboutAnnotatedText = remember(place.aboutFileName, currentLanguage) {
        val rawText = if (place.aboutFileName.isNotEmpty()) {
            val baseFileName = place.aboutFileName.replace(".txt", "").lowercase()
            loadTextFromAssets(context, baseFileName)
        } else {
            place.description.ifEmpty { noDetailsMessage }
        }
        parseCombinedStyles(rawText)
    }


    LaunchedEffect(place) {
        weatherInfo = fetchRealWeather(place.latitude, place.longitude)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                Image(
                    painter = painterResource(id = place.imageRes),
                    contentDescription = stringResource(id = place.nameResId),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.75f)
                                ),
                                startY = 300f
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 0.dp, start = 16.dp, end = 16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    IconButton(
                        onClick = { onSaveToggle(place) },
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Default.FavoriteBorder, "Save", tint = if (isSaved) Color.Red else Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, end = 24.dp, bottom = 44.dp)
                ) {
                    Text(
                        text = stringResource(id = place.nameResId),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Location", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = place.city, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFFFFF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = place.rating.toString(), color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                color = if (isDarkTheme) Color(0xFF1E2625) else Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDarkTheme) Color(0xFF2A3332) else Color(0xFFF5F5F5),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val areaValue = if (place.area.isNotEmpty()) {
                            val areaDouble = place.area.toDoubleOrNull() ?: 0.0
                            if (areaDouble < 1.0) "${(areaDouble * 1_000_000).toInt()} m²" else "${place.area} km²"
                        } else "N/A"

                        DetailInfoTag(
                            icon = Icons.Default.SquareFoot,
                            label = stringResource(id = R.string.label_area),
                            value = areaValue,
                            isDark = isDarkTheme
                        )
                        DetailInfoTag(
                            icon = Icons.Default.Cloud,
                            label = stringResource(id = R.string.label_weather),
                            value = if (weatherInfo == "Loading...") stringResource(id = R.string.loading) else weatherInfo,
                            isDark = isDarkTheme
                        )
                        DetailInfoTag(
                            icon = Icons.Default.Payments,
                            label = stringResource(id = R.string.ticket_title),
                            value = place.price,
                            isDark = isDarkTheme
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                        Text(
                            text = stringResource(id = R.string.about_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF0F3D39)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = aboutAnnotatedText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) Color.LightGray else Color.DarkGray,
                            lineHeight = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(bottomBarBackgroundColor)
                .navigationBarsPadding()
                .padding(start = 14.dp, end = 14.dp, bottom = 4.dp, top = 4.dp)
        ) {
            Button(
                onClick = {
                    onGetDirections(place.latitude, place.longitude)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.get_directions),
                    color = if (isDarkTheme) Color(0xFF0F3D39) else Color.White
                )

            }
        }
    }
}

@Composable
private fun DetailInfoTag(
    icon: ImageVector,
    label: String,
    value: String,
    isDark: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDark) Color(0xFF4DB6AC) else Color(0xFF0F3D39),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
    }
}

@Composable
fun InfoTag(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color(0xFF00695C))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UzbekistanTravelScreenContent(
    onProfileClick: () -> Unit = {},
    onHotelsClick: () -> Unit = {},
    onMarketClick: () -> Unit = {},
    onVRClick: () -> Unit = {},
    onSaveToggle: (Place) -> Unit,
    savedPlaceNames: List<String>,
    onPlaceClick: (Place) -> Unit,
    isSaved: Boolean = false,
    onGetDirections: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val selectedCities = remember { mutableStateListOf<String>() }
    val selectedCategories = remember { mutableStateListOf<String>() }

    var userLat by remember { mutableDoubleStateOf(41.3111) }
    var userLon by remember { mutableDoubleStateOf(69.2401) }

    var searchQuery by remember { mutableStateOf("") }
    var displayedPlaces by remember { mutableStateOf(allPlaces) }

    val isDark = isSystemInDarkTheme()
    val titleColor = if (isDark) Color.White else Color(0xFF00695C)

    LaunchedEffect(Unit) {
        getUserLocation(context) { lat, lon ->
            userLat = lat
            userLon = lon
        }
    }

    BackHandler(enabled = selectedPlace != null) {
        selectedPlace = null
    }

    val filteredPlaces = remember(searchQuery, selectedCities.toList(), selectedCategories.toList(), context) {
        allPlaces.filter { place ->
            val placeName = context.getString(place.nameResId).lowercase()

            val cityMatch = selectedCities.isEmpty() || selectedCities.contains(place.city)
            val categoryMatch = selectedCategories.isEmpty() || selectedCategories.contains(place.category)

            val searchMatch = searchQuery.isEmpty() ||
                    placeName.contains(searchQuery.lowercase()) ||
                    place.city.lowercase().contains(searchQuery.lowercase())

            cityMatch && categoryMatch && searchMatch
        }.sortedByDescending { it.rating }
    }

    if (selectedPlace != null) {
        selectedPlace?.let { place ->
            PlaceDetailScreen(
                place = place,
                onBackClick = { selectedPlace = null },
                onSaveToggle = onSaveToggle,
                isSaved = savedPlaceNames.contains(place.id),
                onGetDirections = onGetDirections
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 50.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.discover_uzbekistan),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Text(
                            text = stringResource(id = R.string.best_spots),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                item {
                    val carouselImages = remember { allPlaces.map { it.imageRes }.take(10) }
                    FullScreenWidthImageCarousel(
                        places = allPlaces,
                        onPlaceClick = { clickedPlace ->
                            onPlaceClick(clickedPlace)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    SearchBarSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.all_destinations),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = titleColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (filteredPlaces.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_destinations_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(items = filteredPlaces, key = { it.id }) { place ->
                        CityCard(
                            place = place,
                            userLat = userLat,
                            userLon = userLon,
                            isSaved = savedPlaceNames.contains(place.id),
                            onSaveClick = { onSaveToggle(place) },
                            onClick = { onPlaceClick(place) }
                        )
                    }
                }
            }

            if (showFilterDialog) {
                FilterDialogCustom(
                    selectedCities = selectedCities,
                    selectedCategories = selectedCategories,
                    onDismiss = { showFilterDialog = false },
                    onApply = { citiesResult, categoriesResult ->
                        showFilterDialog = false

                        selectedCities.clear()
                        selectedCities.addAll(citiesResult)

                        selectedCategories.clear()
                        selectedCategories.addAll(categoriesResult)

                        val filteredList = allPlaces.filter { place ->
                            val matchesCity = citiesResult.isEmpty() || citiesResult.any { city ->
                                place.city.equals(city, ignoreCase = true)
                            }

                            val matchesCategory = categoriesResult.isEmpty() || categoriesResult.any { cat ->
                                place.category.equals(cat, ignoreCase = true)
                            }

                            matchesCity && matchesCategory
                        }

                        displayedPlaces = filteredList
                    }
                )
            }
        }
    }
}

@Composable
fun CityCard(
    place: Place,
    userLat: Double,
    userLon: Double,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(vertical = 6.dp, horizontal = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = place.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { onSaveClick() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isSaved) Color.Red else Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = place.nameResId), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(place.city, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFFFFF), modifier = Modifier.size(18.dp))
                    Text(" ${place.rating}  |  ", color = Color.White, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text(" ${calculateDistanceKm(userLat, userLon, place.latitude, place.longitude)}", color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialogCustom(
    selectedCities: List<String>,
    selectedCategories: List<String>,
    onDismiss: () -> Unit,
    onApply: (List<String>, List<String>) -> Unit,
) {
    val tempCities = remember { mutableStateListOf<String>().apply { addAll(selectedCities) } }
    val tempCategories = remember { mutableStateListOf<String>().apply { addAll(selectedCategories) } }

    val cities = listOf(
        R.string.city_tashkent to "Tashkent",
        R.string.city_samarkand to "Samarkand",
        R.string.city_bukhara to "Bukhara",
        R.string.city_khiva to "Khiva",
        R.string.city_jizzakh to "Jizzakh",
        R.string.city_bostanlyk to "Bostanlyk",
        R.string.city_shahrisabz to "Shahrisabz",
        R.string.city_kokand to "Kokand",
        R.string.city_termez to "Termez",
        R.string.city_nukus to "Nukus",
        R.string.city_surkhandarya to "Surkhandarya",
        R.string.city_namangan to "Namangan",
        R.string.city_fergana to "Fergana"
    )

    val categories = listOf(
        R.string.cat_mosque to "Mosque",
        R.string.cat_museum to "Museum",
        R.string.cat_fortress to "Fortress",
        R.string.cat_madrasah to "Madrasah",
        R.string.cat_resort to "Resort"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.filter_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00695C)
                    )

                    if (tempCities.isNotEmpty() || tempCategories.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                tempCities.clear()
                                tempCategories.clear()
                            },
                            border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFD32F2F)
                            ),
                            modifier = androidx.compose.ui.Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = androidx.compose.ui.Modifier.size(16.dp)
                                )

                                Text(
                                    text = stringResource(id = R.string.filter_clear),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(id = R.string.select_cities),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        cities.forEach { (stringRes, cityValue) ->
                            val isSelected = tempCities.contains(cityValue)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) tempCities.remove(cityValue)
                                    else tempCities.add(cityValue)
                                },
                                label = { Text(stringResource(id = stringRes)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00695C),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(id = R.string.select_categories),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { (stringRes, catValue) ->
                            val isSelected = tempCategories.contains(catValue)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) tempCategories.remove(catValue)
                                    else tempCategories.add(catValue)
                                },
                                label = { Text(stringResource(id = stringRes)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00695C),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onApply(tempCities.toList(), tempCategories.toList()) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                    ) {
                        Text(stringResource(id = R.string.btn_apply), fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.btn_cancel), color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    val backgroundImage = if (isDark) {
        R.drawable.login_image
    } else {
        R.drawable.login_image
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 500.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.uzguide_logo),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(
                color = if (isDark) Color.White else TealPrimary,
                strokeWidth = 4.dp
            )
        }
    }
}

@Composable
fun SearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 16.dp),
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF00695C))
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00695C),
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullScreenWidthImageCarousel(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit
) {
    if (places.isEmpty()) return

    val infinitePageCount = 20000
    val startIndex = (infinitePageCount / 2) - (infinitePageCount / 2 % places.size)

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { infinitePageCount }
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentPadding = PaddingValues(horizontal = 15.dp),
        pageSpacing = 4.dp,
        userScrollEnabled = true
    ) { page ->
        val place = places[page % places.size]

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp, horizontal = 5.dp)
                .clickable { onPlaceClick(place) },
            shape = RoundedCornerShape(15.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 30.dp,
            tonalElevation = 20.dp
        ) {
            Image(
                painter = painterResource(id = place.imageRes),
                contentDescription = stringResource(id = place.nameResId),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val selectedContainerColor = if (isDark) Color(0xFF005049) else Color(0xFFE0F2F1)
    val selectedContentColor = if (isDark) Color.White else Color(0xFF00695C)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val mainItems = listOf(
            CustomNavItem(stringResource(R.string.home), "home", R.drawable.ic_home_light, R.drawable.ic_home_dark),
            CustomNavItem(stringResource(R.string.map), "map", R.drawable.ic_map_light, R.drawable.ic_map_dark),
            CustomNavItem(stringResource(R.string.hotel), "hotel", R.drawable.ic_hotel_light, R.drawable.ic_hotel_dark),
            CustomNavItem(stringResource(R.string.market), "market", R.drawable.ic_market_light, R.drawable.ic_market_dark)
        )

        mainItems.forEach { item ->
            val isSelected = currentScreen == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                label = { Text(text = item.label, fontSize = 10.sp) },
                icon = {
                    Icon(
                        painter = painterResource(id = if (isDark) item.iconDark else item.iconLight),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) selectedContentColor else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = selectedContainerColor
                )
            )
        }

        val isMoreSelected = listOf("saved", "profile", "vr").contains(currentScreen)

        NavigationBarItem(
            selected = isMoreSelected || expanded,
            onClick = { expanded = !expanded },
            label = { Text(text = "More", fontSize = 10.sp) },
            icon = {
                Box {
                    Icon(
                        painter = painterResource(id = if (isDark) R.drawable.ic_more_dark else R.drawable.ic_more_light),
                        contentDescription = "More",
                        modifier = Modifier.size(24.dp),
                        tint = if (isMoreSelected || expanded) selectedContentColor else Color.Gray
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menu_vr_mode)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if(isDark) R.drawable.ic_vr_dark else R.drawable.ic_vr_light),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                            },
                            onClick = {
                                expanded = false
                                onNavigate("vr")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.saved)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if(isDark) R.drawable.ic_saved_dark else R.drawable.ic_saved_light),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                            },
                            onClick = {
                                expanded = false
                                onNavigate("saved")
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.profile)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if(isDark) R.drawable.ic_profile_dark else R.drawable.ic_profile_light),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                            },
                            onClick = {
                                expanded = false
                                onNavigate("profile")
                            }
                        )
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = selectedContainerColor
            )
        )
    }
}

data class CustomNavItem(
    val label: String,
    val route: String,
    val iconLight: Int,
    val iconDark: Int,
)

@Composable
fun YandexMapComponent(latitude: Double, longitude: Double) {
    val context = LocalContext.current
    val mapView = remember { com.yandex.mapkit.mapview.MapView(context) }

    AndroidView(
        factory = { ctx ->
            MapKitFactory.initialize(ctx)
            MapKitFactory.getInstance().onStart()
            mapView.onStart()

            val destination = Point(latitude, longitude)

            mapView.mapWindow.map.move(
                CameraPosition(destination, 15.0f, 0.0f, 0.0f)
            )

            mapView.mapWindow.map.mapObjects.clear()
            mapView.mapWindow.map.mapObjects.addPlacemark(destination)

            mapView
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
}

@Composable
fun MapScreen(
    targetPoint: com.yandex.mapkit.geometry.Point? = null,
    placesList: List<Place>,
    onRouteCleared: () -> Unit = {},
    onPlaceSelect: (Place) -> Unit
) {
    val latitude = targetPoint?.latitude ?: 41.3111
    val longitude = targetPoint?.longitude ?: 69.2401

    Box(modifier = Modifier.fillMaxSize()) {
        YandexMapView(
            places = placesList,
            latitude = latitude,
            longitude = longitude,
            selectedTargetPoint = targetPoint,
            onRouteCleared = onRouteCleared,
            onPlaceClick = onPlaceSelect
        )
    }
}

@Composable
fun ProfileScreen(onBackClick: () -> Unit, onLoggedOut: () -> Unit, onLanguageChange: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val sharedPref = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val account = remember { GoogleSignIn.getLastSignedInAccount(context) }

    var userName by remember {
        mutableStateOf(sharedPref.getString("user_name", account?.displayName ?: "User") ?: "User")
    }
    var userEmail by remember { mutableStateOf(account?.email ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    fun changeLanguage(context: Context, languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)

        val activity = context as? Activity
        activity?.recreate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF121212) else Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = if (isDark) Color.White else Color.Black)
            }
            Text(
                text = stringResource(id = R.string.profile_settings),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )
            TextButton(onClick = {
                if (isEditing) {
                    sharedPref.edit().putString("user_name", userName).apply()
                }
                isEditing = !isEditing
            }) {
                Text(if (isEditing) stringResource(id = R.string.btn_save) else stringResource(id = R.string.btn_edit), color = Color(0xFF00695C), fontWeight = FontWeight.SemiBold)
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = Color(0xFF00695C).copy(alpha = 0.1f)) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(20.dp), tint = Color(0xFF00695C))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isEditing) {
                OutlinedTextField(value = userName, onValueChange = { userName = it }, label = { Text("Full Name") })
            } else {
                Text(userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = if (isDark) Color.White else Color.Black)
                Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { googleSignInClient.signOut().addOnCompleteListener { onLoggedOut() } },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = stringResource(id = R.string.sign_out), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ZoomBox(
    images: List<Int>,
    modifier: Modifier = Modifier,
    initialDelay: Long = 0,
    currentlyVisibleImages: MutableList<Int>,
) {
    var currentIndex by remember {
        mutableIntStateOf(images.indices.firstOrNull { !currentlyVisibleImages.contains(images[it]) } ?: 0)
    }

    DisposableEffect(currentIndex) {
        val currentImage = images[currentIndex]
        currentlyVisibleImages.add(currentImage)
        onDispose {
            currentlyVisibleImages.remove(currentImage)
        }
    }

    LaunchedEffect(Unit) {
        delay(initialDelay)

        while (true) {
            val randomStep = (4000..7000).random().toLong()
            delay(randomStep)

            val availableIndices = images.indices.filter {
                !currentlyVisibleImages.contains(images[it])
            }

            if (availableIndices.isNotEmpty()) {
                currentIndex = availableIndices.random()
            }
        }
    }

    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            (fadeIn(tween(1500)) + scaleIn(initialScale = 0.8f, animationSpec = tween(1500)))
                .togetherWith(fadeOut(tween(1500)) + scaleOut(targetScale = 1.2f, animationSpec = tween(1500)))
        },
        label = "ZoomAnimation",
        modifier = modifier.clip(RoundedCornerShape(16.dp))
    ) { index ->

        Image(
            painter = painterResource(id = images[index]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun HotelsScreen(
    hotelImages: List<Int>,
    onBackClick: () -> Unit,
) {
    val currentlyVisibleImages = remember { mutableStateListOf<Int>() }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF0F3D39) else Color.White
    val titleColor = if (isDark) Color.White else Color(0xFF0F3D39)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(top = 120.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZoomBox(hotelImages, Modifier.weight(1f), 0, currentlyVisibleImages)
                ZoomBox(hotelImages, Modifier.weight(1.3f), 800, currentlyVisibleImages)
            }

            ZoomBox(hotelImages, Modifier.weight(1.5f).fillMaxWidth(), 1600, currentlyVisibleImages)

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZoomBox(hotelImages, Modifier.weight(1.2f), 2400, currentlyVisibleImages)
                ZoomBox(hotelImages, Modifier.weight(1f), 3200, currentlyVisibleImages)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.title_luxury_hotels),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = titleColor,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFE4A951),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.coming_soon),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F3D39),
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun MarketScreen(
    marketImages: List<Int>,
    onBackClick: () -> Unit,
) {
    val currentlyVisibleMarketImages = remember { mutableStateListOf<Int>() }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF0F3D39) else Color.White
    val titleColor = if (isDark) Color.White else Color(0xFF0F3D39)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(top = 120.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZoomBox(marketImages, Modifier.weight(1.2f), 200, currentlyVisibleMarketImages)
                ZoomBox(marketImages, Modifier.weight(1f), 900, currentlyVisibleMarketImages)
            }

            ZoomBox(marketImages, Modifier.weight(1.4f).fillMaxWidth(), 1700, currentlyVisibleMarketImages)

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ZoomBox(marketImages, Modifier.weight(1f), 2500, currentlyVisibleMarketImages)
                ZoomBox(marketImages, Modifier.weight(1.2f), 3300, currentlyVisibleMarketImages)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.title_national_market),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = titleColor,
                modifier = Modifier.padding(horizontal = 20.dp),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFE4A951),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.coming_soon),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F3D39),
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun SavedScreenContent(
    places: List<Place>,
    userLat: Double,
    userLon: Double,
    onRemove: (Place) -> Unit,
    onPlaceClick: (Place) -> Unit,
) {

    println("Saved places count: ${places.size}")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.saved_places_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00695C)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (places.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_saved_places))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(places) { place ->
                    CityCard(
                        place = place,
                        userLat = userLat,
                        userLon = userLon,
                        isSaved = true,
                        onSaveClick = { onRemove(place) },
                        onClick = { onPlaceClick(place) }
                    )
                }
            }
        }
    }
}
