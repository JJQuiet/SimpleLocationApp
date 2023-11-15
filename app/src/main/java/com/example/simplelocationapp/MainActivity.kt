package com.example.simplelocationapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplelocationapp.ui.theme.SimpleLocationAppTheme
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    private lateinit var locationClient: LocationClient
    private lateinit var locationListener: MyLocationListener
    private lateinit var locationManager: LocationManager
    private lateinit var locationListenerGPS: LocationListener
    // 定义BaiduLBS经纬度的状态
    private var latitudeBaidu = mutableStateOf(0.0)
    private var longitudeBaidu = mutableStateOf(0.0)
    // 定义GPS经纬度的状态
    private var latitudeGPS = mutableStateOf(0.0)
    private var longitudeGPS = mutableStateOf(0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val REQUEST_CODE_LOCATION = 1001
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
        //region BaiduLBS Location
        LocationClient.setAgreePrivacy(true)
        locationClient  = LocationClient(applicationContext)
        val option = LocationClientOption()
        option.isOpenGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(5000)
        locationClient.locOption = option
        locationListener = MyLocationListener()
        locationClient.registerLocationListener(locationListener)
        locationClient.start()
        //endregion
        //region GPS Location
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 检查GPS是否启用
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS", Toast.LENGTH_SHORT).show()
            return
        }
        // 定义一个位置监听器
        locationListenerGPS = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 显示纬度和经度
                latitudeGPS.value = location.latitude
                longitudeGPS.value = location.longitude
                // 在这里使用纬度和经度更新用户界面

            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        // 请求位置更新
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListenerGPS)
        }
        //endregion
        setContent {
            val locationState = remember { mutableStateOf("Android") }
            locationListener.currentLocationState = locationState.toString()
//            SimpleLocationAppTheme {
//                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//                    Greeting(locationListener.currentLocationState)
//                }
//            }
//            Greeting(locationListener.currentLocationState)
//            ShowLocation(latitude, longitude))
            MainContent(latitudeBaidu,longitudeBaidu, latitudeGPS, longitudeGPS)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // 停止定位
        locationClient.stop()
        // 注销监听器
        locationClient.unRegisterLocationListener(locationListener)
        locationManager.removeUpdates(locationListenerGPS)
    }
    // 自定义的位置监听器
    inner class MyLocationListener : BDAbstractLocationListener() {
        var currentLocationState by mutableStateOf("Android")
//            private set
        override fun onReceiveLocation(location: BDLocation) {
            latitudeBaidu.value = location.latitude
            longitudeBaidu.value = location.longitude
            Log.d("BaiduLBS", "Lat: ${location.latitude}, Lon: ${location.longitude}")
            // TODO: 在此处使用 latitude 和 longitude 更新你的界面。
            val currentTime = System.currentTimeMillis()
//            currentLocationState = "Lat: $latitude, Lon: $longitude"
        }
    }
}
@Composable
fun Greeting(latitudeBaidu: State<Double>, longitudeBaidu: State<Double>, modifier: Modifier = Modifier) {
    Text(text = "BaiduLBS定位:", modifier = modifier)
    Text(
            text = "Hello, Lat: $latitudeBaidu, Long: $longitudeBaidu!",
            modifier = modifier
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleLocationAppTheme {
//        Greeting("Android")
    }
}
@Composable
fun ShowLocation(latitude: State<Double>, longitude: State<Double>) {
    Column {
        Text(text = "GPS定位:")
        Text(text = "纬度: ${latitude.value}")
        Text(text = "经度: ${longitude.value}")
    }
}
@Composable
fun MainContent(latitudeBaidu: State<Double>, longitudeBaidu: State<Double>, latitudeGPS: State<Double>, longitudeGPS: State<Double>) {
    Column {
        // 显示定位信息
        ShowLocation(latitudeGPS, longitudeGPS)  // 假设 latitude 和 longitude 是提供给这个函数的状态

        // 显示问候语
        Greeting(latitudeBaidu,longitudeBaidu)
    }
}