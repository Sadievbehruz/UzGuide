package uz.uzguide

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error

enum class TransportMode { AUTOMOBILE, TRANSIT, PEDESTRIAN, MOPED_SCOOTER, BICYCLE_SAMOKAT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YandexMapView(
    latitude: Double,
    longitude: Double,
    places: List<Place>,
    selectedTargetPoint: Point?,
    onRouteCleared: () -> Unit,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    var userLocation by remember { mutableStateOf<Point?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSuggestionSelected by remember { mutableStateOf(false) }

    val currentVisiblePlaces by remember(searchQuery, places) {
        derivedStateOf {
            if (searchQuery.isEmpty() || isSuggestionSelected) {
                places
            } else {
                places.filter { place ->
                    val placeName = context.getString(place.nameResId)
                    placeName.contains(searchQuery, ignoreCase = true) ||
                            place.city.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    var selectedPlaceForSheet by remember { mutableStateOf<Place?>(null) }

    var routeTargetPoint by remember { mutableStateOf<Point?>(null) }
    var selectedTransportMode by remember { mutableStateOf(TransportMode.AUTOMOBILE) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var drivingSession by remember { mutableStateOf<DrivingSession?>(null) }
    var isNavigating by remember { mutableStateOf(false) }

    var distanceValue by remember { mutableStateOf("") }

    fun calculateAndFormatDistance(start: Point?, end: Point?) {
        if (start != null && end != null) {
            val distanceInMeters = Geo.distance(start, end)
            distanceValue = if (distanceInMeters >= 1000) {
                String.format("%.1f km", distanceInMeters / 1000)
            } else {
                "${distanceInMeters.toInt()} m"
            }
        } else {
            distanceValue = ""
        }
    }

    fun startInAppNavigation(mView: MapView, location: Point?) {
        if (location == null) return
        val map = mView.mapWindow?.map ?: return
        map.move(
            CameraPosition(location, 17.5f, map.cameraPosition.azimuth, 45.0f),
            Animation(Animation.Type.SMOOTH, 1.0f),
            null
        )
    }

    LaunchedEffect(selectedTargetPoint) {
        if (selectedTargetPoint != null) {
            routeTargetPoint = selectedTargetPoint
        }
    }

    LaunchedEffect(latitude, longitude, isNavigating, routeTargetPoint) {
        val currentPoint = Point(latitude, longitude)
        userLocation = currentPoint

        calculateAndFormatDistance(currentPoint, routeTargetPoint)

        val map = mapView.mapWindow?.map
        if (map != null) {
            if (isNavigating) {
                map.move(
                    CameraPosition(currentPoint, 17.5f, map.cameraPosition.azimuth, 45.0f),
                    Animation(Animation.Type.SMOOTH, 0.5f),
                    null
                )
            } else if (searchQuery.isEmpty()) {
                map.move(
                    CameraPosition(currentPoint, 14.5f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1.0f),
                    null
                )
            }
        }
    }

    fun getUserLocationInternal(ctx: Context, onLocationRetrieved: (Double, Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val networkLocation = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
            val gpsLocation = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
            val finalLocation = gpsLocation ?: networkLocation

            if (finalLocation != null) {
                onLocationRetrieved(finalLocation.latitude, finalLocation.longitude)
            }
        }
    }

    LaunchedEffect(userLocation, routeTargetPoint, selectedTransportMode, currentVisiblePlaces) {
        val startPoint = userLocation
        val endPoint = routeTargetPoint

        if (startPoint != null && endPoint != null) {
            val mapWindow = mapView.mapWindow ?: return@LaunchedEffect
            val map = mapWindow.map ?: return@LaunchedEffect
            val mapObjects = map.mapObjects ?: return@LaunchedEffect

            val points = listOf(
                RequestPoint(startPoint, RequestPointType.WAYPOINT, null, null),
                RequestPoint(endPoint, RequestPointType.WAYPOINT, null, null)
            )

            try { drivingSession?.cancel() } catch (e: Exception) {}
            mapObjects.clear()

            currentVisiblePlaces.forEach { p ->
                val pm = mapObjects.addPlacemark()
                pm.geometry = Point(p.latitude, p.longitude)
                pm.addTapListener { _, _ ->
                    selectedPlaceForSheet = p
                    routeTargetPoint = Point(p.latitude, p.longitude)
                    showBottomSheet = true
                    true
                }
            }

            try {
                val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
                val color = when (selectedTransportMode) {
                    TransportMode.AUTOMOBILE -> Color(0xFF2196F3)
                    TransportMode.MOPED_SCOOTER -> Color(0xFFE91E63)
                    TransportMode.BICYCLE_SAMOKAT -> Color(0xFFFF9800)
                    TransportMode.PEDESTRIAN, TransportMode.TRANSIT -> {
                        val alertMessage = context.getString(R.string.transit_not_available)
                        Toast.makeText(context, alertMessage, Toast.LENGTH_SHORT).show()
                        Color(0xFF4CAF50)
                    }
                }

                drivingSession = drivingRouter.requestRoutes(points, DrivingOptions(), VehicleOptions().apply { vehicleType = VehicleType.DEFAULT }, object : DrivingSession.DrivingRouteListener {
                    override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                        if (routes.isNotEmpty() && mapView.mapWindow != null) {
                            mapObjects.addPolyline(routes[0].geometry).apply {
                                setStrokeColor(color.toArgb())
                                strokeWidth = 6f
                            }
                        }
                    }
                    override fun onDrivingRoutesError(p0: Error) {}
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapKitFactory.initialize(ctx)
                val mapKit = MapKitFactory.getInstance()
                val userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = true

                mapView.onStart()
                mapKit.onStart()
                mapView
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 45.dp)
                .align(Alignment.TopCenter)
        ) {
            if (!isNavigating) {
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        isSuggestionSelected = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty() || routeTargetPoint != null) {
                            IconButton(onClick = {
                                searchQuery = ""
                                routeTargetPoint = null
                                distanceValue = ""
                                isSuggestionSelected = false
                                mapView.mapWindow?.map?.mapObjects?.clear()
                                onRouteCleared()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (searchQuery.isNotEmpty() && !isSuggestionSelected && currentVisiblePlaces.isNotEmpty() && !isNavigating) {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(items = currentVisiblePlaces, key = { it.id }) { place ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchQuery = context.getString(place.nameResId)
                                        routeTargetPoint = Point(place.latitude, place.longitude)
                                        isSuggestionSelected = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00695C), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(id = place.nameResId),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Text(text = place.city, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            if (routeTargetPoint != null && !isNavigating) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TransportIconButton(icon = Icons.Default.DirectionsCar, isSelected = selectedTransportMode == TransportMode.AUTOMOBILE, onClick = { selectedTransportMode = TransportMode.AUTOMOBILE })
                        TransportIconButton(icon = Icons.Default.DirectionsBus, isSelected = selectedTransportMode == TransportMode.TRANSIT, onClick = { selectedTransportMode = TransportMode.TRANSIT })
                        TransportIconButton(icon = Icons.Default.DirectionsRun, isSelected = selectedTransportMode == TransportMode.PEDESTRIAN, onClick = { selectedTransportMode = TransportMode.PEDESTRIAN })
                        TransportIconButton(icon = Icons.Default.ElectricScooter, isSelected = selectedTransportMode == TransportMode.MOPED_SCOOTER, onClick = { selectedTransportMode = TransportMode.MOPED_SCOOTER })
                        TransportIconButton(icon = Icons.Default.DirectionsBike, isSelected = selectedTransportMode == TransportMode.BICYCLE_SAMOKAT, onClick = { selectedTransportMode = TransportMode.BICYCLE_SAMOKAT })
                    }
                }
            }
        }

        if (!isNavigating) {
            Column(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val pos = mapView.mapWindow?.map?.cameraPosition
                        if (pos != null) {
                            mapView.mapWindow?.map?.move(CameraPosition(pos.target, pos.zoom + 1.0f, pos.azimuth, pos.tilt), Animation(Animation.Type.SMOOTH, 0.2f), null)
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF00695C),
                    modifier = Modifier.size(45.dp)
                ) { Icon(Icons.Default.Add, contentDescription = null) }

                FloatingActionButton(
                    onClick = {
                        val pos = mapView.mapWindow?.map?.cameraPosition
                        if (pos != null) {
                            mapView.mapWindow?.map?.move(CameraPosition(pos.target, pos.zoom - 1.0f, pos.azimuth, pos.tilt), Animation(Animation.Type.SMOOTH, 0.2f), null)
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF00695C),
                    modifier = Modifier.size(45.dp)
                ) { Icon(Icons.Default.Remove, contentDescription = null) }
            }
        }

        if (!isNavigating) {
            FloatingActionButton(
                onClick = {
                    getUserLocationInternal(context) { lat, lon ->
                        userLocation = Point(lat, lon)
                        mapView.mapWindow?.map?.move(CameraPosition(Point(lat, lon), 15.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 0.8f), null)
                    }
                },
                modifier = Modifier.padding(bottom = 100.dp, end = 16.dp).align(Alignment.BottomEnd),
                containerColor = Color(0xFF00695C),
                contentColor = Color.White
            ) { Icon(Icons.Default.MyLocation, contentDescription = null) }
        }

        if (routeTargetPoint != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (distanceValue.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF00695C)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text(
                                text = stringResource(id = R.string.distance_label, distanceValue),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!isNavigating) {
                    Button(
                        onClick = {
                            isNavigating = true
                            startInAppNavigation(mapView, userLocation)
                            val startText = context.getString(R.string.navigation_started)
                            Toast.makeText(context, startText, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3D39)),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.start_navigation), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {
                            isNavigating = false
                            routeTargetPoint = null
                            distanceValue = ""
                            mapView.mapWindow?.map?.mapObjects?.clear()
                            onRouteCleared()
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.end_navigation), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        if (showBottomSheet && selectedPlaceForSheet != null && !isNavigating) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                val place = selectedPlaceForSheet!!
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp, start = 24.dp, end = 24.dp)) {
                    Image(
                        painter = painterResource(id = place.imageRes),
                        contentDescription = place.id,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = place.id, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF00695C))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showBottomSheet = false
                            onPlaceClick(place)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                    ) {
                        Text(stringResource(id = R.string.more_details_button), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TransportIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFF00695C) else Color.Transparent,
                shape = androidx.compose.foundation.shape.CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else Color.Gray
        )
    }
}