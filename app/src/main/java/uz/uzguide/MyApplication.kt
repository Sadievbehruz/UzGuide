package uz.uzguide

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class MyApplication : Application() {

    private var mapKit: com.yandex.mapkit.MapKit? = null

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("7734f10f-4cb4-41e3-a871-2b4dd55e7e81")

        MapKitFactory.initialize(this)

        try {
            com.yandex.mapkit.directions.DirectionsFactory.getInstance()
            com.yandex.mapkit.transport.TransportFactory.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapKit = MapKitFactory.getInstance()
    }
}