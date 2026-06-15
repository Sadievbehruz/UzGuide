package uz.uzguide

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.TransitOptions
import com.yandex.runtime.Error
import com.yandex.mapkit.transport.bicycle.Route as BicycleRoute
import com.yandex.mapkit.transport.bicycle.Session as BicycleSession
import com.yandex.mapkit.transport.masstransit.Route as MasstransitRoute
import com.yandex.mapkit.transport.masstransit.Session as MasstransitSession

enum class TransportMode { AUTOMOBILE, TRANSIT, PEDESTRIAN, MOPED_SCOOTER, BICYCLE_SAMOKAT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YandexMapView(
    latitude: Double,
    longitude: Double,
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val drivingRouter = remember { com.yandex.mapkit.directions.DirectionsFactory.getInstance().createDrivingRouter(com.yandex.mapkit.directions.driving.DrivingRouterType.COMBINED) }
    val masstransitRouter = remember { com.yandex.mapkit.transport.TransportFactory.getInstance().createMasstransitRouter() }
    val bicycleRouter = remember { com.yandex.mapkit.transport.TransportFactory.getInstance().createBicycleRouter() }

    var userLocation by remember { mutableStateOf<Point?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSuggestionSelected by remember { mutableStateOf(false) }

    var currentVisiblePlaces by remember { mutableStateOf(places) }
    var selectedPlaceForSheet by remember { mutableStateOf<Place?>(null) }

    var routeTargetPoint by remember { mutableStateOf<Point?>(null) }
    var selectedTransportMode by remember { mutableStateOf(TransportMode.AUTOMOBILE) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var drivingSession by remember { mutableStateOf<DrivingSession?>(null) }
    var masstransitSession by remember { mutableStateOf<MasstransitSession?>(null) }
    var bicycleSession by remember { mutableStateOf<BicycleSession?>(null) }

    fun getUserLocationInternal(ctx: Context, onLocationRetrieved: (Double, Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val networkLocation = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
            val gpsLocation = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
            val finalLocation = networkLocation ?: gpsLocation

            if (finalLocation != null) {
                onLocationRetrieved(finalLocation.latitude, finalLocation.longitude)
            } else {
                locationManager.requestSingleUpdate(
                    android.location.LocationManager.NETWORK_PROVIDER,
                    object : android.location.LocationListener {
                        override fun onLocationChanged(loc: android.location.Location) {
                            onLocationRetrieved(loc.latitude, loc.longitude)
                        }
                    },
                    null
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getUserLocationInternal(context) { lat, lon ->
                userLocation = Point(lat, lon)
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    LaunchedEffect(searchQuery, places) {
        val filtered = if (searchQuery.isBlank()) {
            places
        } else {
            places.filter { place ->
                val resourceName = "place_" + place.id.lowercase()
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("'", "")

                val resId = context.resources.getIdentifier(resourceName, "string", context.packageName)
                val localizedName = if (resId != 0) context.getString(resId) else place.id

                localizedName.contains(searchQuery, ignoreCase = true) ||
                        place.id.contains(searchQuery, ignoreCase = true) ||
                        place.city.contains(searchQuery, ignoreCase = true)
            }
        }

        currentVisiblePlaces = filtered

        val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return@LaunchedEffect
        mapObjects.clear()

        filtered.forEach { place ->
            val placePoint = Point(place.latitude, place.longitude)
            val placemark = mapObjects.addPlacemark()
            placemark.geometry = placePoint

            placemark.addTapListener(object : MapObjectTapListener {
                override fun onMapObjectTap(mapObject: com.yandex.mapkit.map.MapObject, point: Point): Boolean {
                    selectedPlaceForSheet = place
                    routeTargetPoint = placePoint
                    showBottomSheet = true
                    return true
                }
            })
        }

        if (filtered.size == 1) {
            val targetPlace = filtered.first()
            val point = Point(targetPlace.latitude, targetPlace.longitude)
            routeTargetPoint = point
            mapView.mapWindow?.map?.move(
                CameraPosition(point, 15.5f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1.2f),
                null
            )
        }
    }

    LaunchedEffect(userLocation, routeTargetPoint, selectedTransportMode) {
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

            drivingSession?.cancel()
            masstransitSession?.cancel()
            bicycleSession?.cancel()

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
            mapObjects.addPlacemark().geometry = startPoint

            try {
                when (selectedTransportMode) {
                    TransportMode.AUTOMOBILE -> {
                        val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(com.yandex.mapkit.directions.driving.DrivingRouterType.COMBINED)
                        val options = DrivingOptions()
                        val vehicleOptions = VehicleOptions().apply { vehicleType = VehicleType.DEFAULT }

                        drivingSession = drivingRouter.requestRoutes(points, options, vehicleOptions, object : DrivingSession.DrivingRouteListener {
                            override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                                if (routes.isNotEmpty() && mapView.mapWindow != null) {
                                    mapObjects.addPolyline(routes[0].geometry).apply {
                                        setStrokeColor(Color(0xFF2196F3).toArgb())
                                        strokeWidth = 6f
                                    }
                                }
                            }
                            override fun onDrivingRoutesError(p0: Error) {}
                        })
                    }
                    TransportMode.MOPED_SCOOTER -> {
                        val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(com.yandex.mapkit.directions.driving.DrivingRouterType.COMBINED)
                        val options = DrivingOptions()
                        val vehicleOptions = VehicleOptions().apply { vehicleType = VehicleType.DEFAULT }

                        drivingSession = drivingRouter.requestRoutes(points, options, vehicleOptions, object : DrivingSession.DrivingRouteListener {
                            override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                                if (routes.isNotEmpty() && mapView.mapWindow != null) {
                                    mapObjects.addPolyline(routes[0].geometry).apply {
                                        setStrokeColor(Color(0xFFE91E63).toArgb())
                                        strokeWidth = 5.5f
                                    }
                                }
                            }
                            override fun onDrivingRoutesError(p0: Error) {}
                        })
                    }
                    TransportMode.PEDESTRIAN -> {
                        val masstransitRouter = TransportFactory.getInstance().createMasstransitRouter()
                        masstransitSession = masstransitRouter.requestRoutes(points, TransitOptions(), RouteOptions(), object : MasstransitSession.RouteListener {
                            override fun onMasstransitRoutes(routes: List<MasstransitRoute>) {
                                if (routes.isNotEmpty() && mapView.mapWindow != null) {
                                    mapObjects.addPolyline(routes[0].geometry).apply {
                                        setStrokeColor(Color(0xFF4CAF50).toArgb())
                                        strokeWidth = 5f
                                    }
                                }
                            }
                            override fun onMasstransitRoutesError(p0: Error) {}
                        })
                    }
                    TransportMode.TRANSIT -> {
                        val masstransitRouter = TransportFactory.getInstance().createMasstransitRouter()
                        masstransitSession = masstransitRouter.requestRoutes(points, TransitOptions(), RouteOptions(), object : MasstransitSession.RouteListener {
                            override fun onMasstransitRoutes(routes: List<MasstransitRoute>) {
                                if (routes.isNotEmpty() && mapView.mapWindow != null) {
                                    mapObjects.addPolyline(routes[0].geometry).apply {
                                        setStrokeColor(Color(0xFF9C27B0).toArgb())
                                        strokeWidth = 5.5f
                                    }
                                }
                            }
                            override fun onMasstransitRoutesError(p0: Error) {}
                        })
                    }
                    TransportMode.BICYCLE_SAMOKAT -> {
                        val bicycleRouter = TransportFactory.getInstance().createBicycleRouter()
                        bicycleSession = bicycleRouter.requestRoutes(
                            points,
                            com.yandex.mapkit.transport.bicycle.VehicleType.BICYCLE,
                            object : BicycleSession.RouteListener {
                                override fun onBicycleRoutes(routes: List<BicycleRoute>) {
                                    if (routes.isNotEmpty() && mapView.mapWindow != null) {
                                        mapObjects.addPolyline(routes[0].geometry).apply {
                                            setStrokeColor(Color(0xFFFF9800).toArgb())
                                            strokeWidth = 5f
                                        }
                                    }
                                }
                                override fun onBicycleRoutesError(p0: Error) {}
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            drivingSession?.cancel()
            masstransitSession?.cancel()
            bicycleSession?.cancel()
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapKitFactory.initialize(ctx)
                mapView.onStart()
                MapKitFactory.getInstance().onStart()
                mapView.mapWindow?.map?.move(CameraPosition(Point(latitude, longitude), 13.0f, 0.0f, 0.0f))
                mapView
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 45.dp)
                .align(Alignment.TopCenter)
        ) {
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
                            isSuggestionSelected = false
                            mapView.mapWindow?.map?.mapObjects?.clear()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Tozalash")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (searchQuery.isNotEmpty() && !isSuggestionSelected && currentVisiblePlaces.isNotEmpty()) {
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
                                        searchQuery = place.id
                                        routeTargetPoint = Point(place.latitude, place.longitude)
                                        isSuggestionSelected = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00695C), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = place.id, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black)
                                    Text(text = place.city, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            if (routeTargetPoint != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TransportIconButton(
                            icon = Icons.Default.DirectionsCar,
                            isSelected = selectedTransportMode == TransportMode.AUTOMOBILE,
                            onClick = { selectedTransportMode = TransportMode.AUTOMOBILE }
                        )
                        TransportIconButton(
                            icon = Icons.Default.DirectionsBus,
                            isSelected = selectedTransportMode == TransportMode.TRANSIT,
                            onClick = { selectedTransportMode = TransportMode.TRANSIT }
                        )
                        TransportIconButton(
                            icon = Icons.Default.DirectionsRun,
                            isSelected = selectedTransportMode == TransportMode.PEDESTRIAN,
                            onClick = { selectedTransportMode = TransportMode.PEDESTRIAN }
                        )
                        TransportIconButton(
                            icon = Icons.Default.ElectricScooter,
                            isSelected = selectedTransportMode == TransportMode.MOPED_SCOOTER,
                            onClick = { selectedTransportMode = TransportMode.MOPED_SCOOTER }
                        )
                        TransportIconButton(
                            icon = Icons.Default.DirectionsBike,
                            isSelected = selectedTransportMode == TransportMode.BICYCLE_SAMOKAT,
                            onClick = { selectedTransportMode = TransportMode.BICYCLE_SAMOKAT }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
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

        FloatingActionButton(
            onClick = {
                getUserLocationInternal(context) { lat, lon ->
                    userLocation = Point(lat, lon)
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp, end = 16.dp)
                .align(Alignment.BottomEnd),
            containerColor = Color(0xFF00695C),
            contentColor = Color.White
        ) { Icon(Icons.Default.MyLocation, contentDescription = null) }

        if (showBottomSheet && selectedPlaceForSheet != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                val place = selectedPlaceForSheet!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = place.imageRes),
                        contentDescription = place.id,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
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
        onClick = {
            onClick()
        },
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