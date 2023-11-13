package com.example.simplelocationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.util.Log






class MainActivity : ComponentActivity() {
    private lateinit var locationClient: LocationClient
    private lateinit var locationListener: MyLocationListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val REQUEST_CODE_LOCATION = 1001

        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有授权，请求权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
        //Todo:
        LocationClient.setAgreePrivacy(true)
        // 初始化 LocationClient
        locationClient  = LocationClient(applicationContext)
        // 配置 LocationClientOption
        val option = LocationClientOption()
        option.isOpenGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(5000)
        locationClient.locOption = option
        // 注册监听器
        locationListener = MyLocationListener()
        locationClient.registerLocationListener(locationListener)
        setContent {

            val locationState = remember { mutableStateOf("Android") }
            locationListener.currentLocationState = locationState.toString()
            SimpleLocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(locationListener.currentLocationState)
//                    Greeting(locationState.value)
                }
            }
        }
        // 开始定位
        locationClient.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        // 停止定位
        locationClient.stop()
        // 注销监听器
        locationClient.unRegisterLocationListener(locationListener)
    }

    // 自定义的位置监听器
    inner class MyLocationListener : BDAbstractLocationListener() {
        var currentLocationState by mutableStateOf("Android")
//            private set

        override fun onReceiveLocation(location: BDLocation) {
            val latitude = location.latitude
            val longitude = location.longitude
            // TODO: 在此处使用 latitude 和 longitude 更新你的界面。
            val currentTime = System.currentTimeMillis()
            Log.d("LocationListener", "蒋建琪Update at: $currentTime. Lat: $latitude, Lon: $longitude")
            currentLocationState = "Lat: $latitude, Lon: $longitude"
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleLocationAppTheme {
        Greeting("Android")
    }
}