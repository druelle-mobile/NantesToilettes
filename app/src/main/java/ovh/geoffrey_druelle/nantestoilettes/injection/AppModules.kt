package ovh.geoffrey_druelle.nantestoilettes.injection

import androidx.room.RoomDatabase
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import ovh.geoffrey_druelle.nantestoilettes.NantesToilettesApp
import ovh.geoffrey_druelle.nantestoilettes.data.local.database.AppDatabase
import ovh.geoffrey_druelle.nantestoilettes.data.remote.api.OpenDataNantesApi
import ovh.geoffrey_druelle.nantestoilettes.ui.favorites.FavoritesViewModel
import ovh.geoffrey_druelle.nantestoilettes.ui.map.MapViewModel
import ovh.geoffrey_druelle.nantestoilettes.ui.splashscreen.SplashScreenViewModel
import ovh.geoffrey_druelle.nantestoilettes.ui.toiletslist.ToiletsListViewModel
import ovh.geoffrey_druelle.nantestoilettes.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L

val appModules = module {
    // DataBase module part
    single { AppDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().toiletDao() }

    // Network module part
    single { Cache(androidApplication().cacheDir, 20L * 1024 * 1024) }
    single { provideOkHttpClient(get()) }
    single { provideRetrofitClient(get()) }
    single { provideApiService(get()) }

    // ViewModels module part
    viewModel { SplashScreenViewModel(api = get()) }
    viewModel { MapViewModel() }
    viewModel { ToiletsListViewModel() }
    viewModel { FavoritesViewModel() }
}

fun getModules(): List<Module>{
    return listOf(appModules)
}

// Database provider
fun provideDatabase(): RoomDatabase {
    return AppDatabase.getInstance(NantesToilettesApp.appContext)
}


// Networks providers
fun provideOkHttpClient(cache: Cache): OkHttpClient {
    return OkHttpClient.Builder().apply {
        cache(cache)
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
        addInterceptor(apiInterceptor())
    }.build()
}

fun apiInterceptor() = Interceptor { chain ->
    chain.proceed(chain.request().newBuilder()
        .apply {
            header("Accept", "application/json")
            header("Content-Type","application/json; charset=utf-8")
        }
        .build())
}

fun provideRetrofitClient(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): OpenDataNantesApi = retrofit.create(OpenDataNantesApi::class.java)
