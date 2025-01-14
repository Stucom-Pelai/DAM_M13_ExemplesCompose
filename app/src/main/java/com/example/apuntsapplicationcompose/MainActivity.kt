package com.example.apuntsapplicationcompose

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter


import com.example.apuntsapplicationcompose.ui.ExempleIcones
import com.example.apuntsapplicationcompose.ui.theme.ApuntsApplicationComposeTheme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
//import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
//RETROFIT CONVERTERS
import retrofit2.converter.gson.GsonConverterFactory
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File
import java.io.FileOutputStream

import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
//            ExempleNavController()
            val remoteModel:RemoteViewModel=viewModel()
                ExempleRetrofit( remoteModel)

        }
    }
}

data class  RemoteMessage(
    val text:String="",
    val photo:String=""
)
interface RemoteMessageInterface {
    @GET("remote_path")
    suspend fun getRemoteMessage(): RemoteMessage

    @GET("params_path")
    suspend fun getRemoteMessageParams(@Query("param1")param1:String,
                                       @Query("param2")param2:String): RemoteMessage

    @Headers("Accept: application/json","Content-Type: application/json")
    @POST("post_path")
    suspend fun postRemoteMessage(@Body remoteMessage:RemoteMessage):RemoteMessage

    @Multipart
    @POST("/upload")
    suspend fun postImgTxt(
        @Part file: MultipartBody.Part,
        @Part("text") text: RequestBody,
    ): RemoteMessage

}
sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: RemoteMessage) : RemoteMessageUiState
    object Error : RemoteMessageUiState
    object Cargant : RemoteMessageUiState
}

class RemoteViewModel:ViewModel(){
    var remoteMessageUiState: RemoteMessageUiState by mutableStateOf(RemoteMessageUiState.Cargant)
        private set
    init {
        //getRemoteMessage()
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            // Mostra les capçaleres al Log
            val headers = response.headers
            for (name in headers.names()) {
                Log.d("exemple", " HEADERS: $name: ${headers[name]}")
            }
            Log.d("Response", "Status Code: ${response.code}")

            response // Retorna la resposta per continuar amb l'execució normal
        }
        .build()
    private val connexio =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
//    val contentType = "application/json".toMediaType()
    fun getRemoteMessage(){
        viewModelScope.launch {
            remoteMessageUiState=RemoteMessageUiState.Cargant
            try{

                val endPoint = connexio.create(RemoteMessageInterface::class.java)
                val resposta = endPoint.getRemoteMessage()
//                val resposta = connexio.create(RemoteMessageInterface::class.java).getRemoteMessage()
                Log.d("exemple", "RESPOSTA ${resposta.photo}")
                remoteMessageUiState=RemoteMessageUiState.Success(resposta)
            } catch (e: Exception) {
                Log.d("exemple", "RESPOSTA ERROR ${e.message} ${e.printStackTrace()}")
                remoteMessageUiState= RemoteMessageUiState.Error
            }
        }
    }
    fun getRemoteMessageParams(){
        viewModelScope.launch {
            remoteMessageUiState=RemoteMessageUiState.Cargant
            try{

                val endPoint = connexio.create(RemoteMessageInterface::class.java)
                val resposta = endPoint.getRemoteMessageParams("v1","v3")
//                val resposta = connexio.create(RemoteMessageInterface::class.java).getRemoteMessage()
                Log.d("exemple", "RESPOSTA ${resposta.photo}")
                remoteMessageUiState=RemoteMessageUiState.Success(resposta)
            } catch (e: Exception) {
                Log.d("exemple", "RESPOSTA ERROR ${e.message} ${e.printStackTrace()}")
                remoteMessageUiState= RemoteMessageUiState.Error
            }
        }
    }
    fun postRemoteMessage(){
        viewModelScope.launch {
            remoteMessageUiState=RemoteMessageUiState.Cargant
            try{
                val connexio =
                    Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                val endPoint = connexio.create(RemoteMessageInterface::class.java)
                val resposta = endPoint.postRemoteMessage(RemoteMessage("hi","santa"))
                Log.d("exemple", "RESPOSTA ${resposta.photo}")
                remoteMessageUiState=RemoteMessageUiState.Success(resposta)
            } catch (e: Exception) {
                Log.d("exemple", "RESPOSTA ERROR ${e.message} ${e.printStackTrace()}")
                remoteMessageUiState= RemoteMessageUiState.Error
            }
        }
    }
    fun postImageWithText(file:File,text: String){
        viewModelScope.launch {
            remoteMessageUiState=RemoteMessageUiState.Cargant
            try{
                val connexio =
                    Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                val endPoint = connexio.create(RemoteMessageInterface::class.java)
                //
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val textBody = text.toRequestBody("text/plain".toMediaTypeOrNull())
                //
                val resposta = endPoint.postImgTxt(body,textBody)
                Log.d("exemple", "RESPOSTA ${resposta.photo}")
                remoteMessageUiState=RemoteMessageUiState.Success(resposta)
            } catch (e: Exception) {
                Log.d("exemple", "RESPOSTA ERROR ${e.message} ${e.printStackTrace()}")
                remoteMessageUiState= RemoteMessageUiState.Error
            }
        }
    }
}

@Composable
fun ExempleRetrofit(remoteViewModel: RemoteViewModel) {

    val remoteMessageUiState = remoteViewModel.remoteMessageUiState

  Column( Modifier.fillMaxWidth().fillMaxHeight()) {
      Button(onClick = {
          Log.d("exemple", "clkRetrofit")
          remoteViewModel.getRemoteMessage()
      }, Modifier.align(alignment = Alignment.Start)) {
          Text("Exemple Retrofit")
      }
      when (remoteMessageUiState) {
          is RemoteMessageUiState.Cargant -> Text("Loading... info")
          is RemoteMessageUiState.Error -> Text("Error")
          is RemoteMessageUiState.Success -> {
              Text(remoteMessageUiState.remoteMessage.photo)
          }
      }
      Button(onClick = {
          Log.d("exemple", "clkRetrofitGETParams")
          remoteViewModel.getRemoteMessageParams()
      }, Modifier.align(alignment = Alignment.Start)) {
          Text("Exemple GET Params")
      }
      when (remoteMessageUiState) {
          is RemoteMessageUiState.Cargant -> Text("Loading... info")
          is RemoteMessageUiState.Error -> Text("Error")
          is RemoteMessageUiState.Success -> {
              Text(remoteMessageUiState.remoteMessage.photo)
          }
      }
      Button(onClick = {
          Log.d("exemple", "clkRetrofitPOST")
          remoteViewModel.postRemoteMessage()
      }, Modifier.align(alignment = Alignment.Start)) {
          Text("Exemple POST Params")
      }
      when (remoteMessageUiState) {
          is RemoteMessageUiState.Cargant -> Text("Loading... info")
          is RemoteMessageUiState.Error -> Text("Error")
          is RemoteMessageUiState.Success -> {
              Text(remoteMessageUiState.remoteMessage.photo)
          }
      }

      //SELECCIONA UN ARXIU
      val context = LocalContext.current
      var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
      val filePickerLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri ->
              selectedFileUri = uri
          }
      )
      Button(onClick = {
          filePickerLauncher.launch("image/*")
      }) {
          Text("Seleccionar Imagen")
      }
      selectedFileUri?.let { uri -> //if (selectedFileUri != null) {
          Button(onClick = {
              val file = uriToFile(uri, context)
              file?.let {
                  remoteViewModel.postImageWithText(it, "Text")
              }
          }) {
              Text("Enviar Imagen")
          }
      }
      when (remoteMessageUiState) {
          is RemoteMessageUiState.Cargant -> Text("Loading... info")
          is RemoteMessageUiState.Error -> Text("Error")
          is RemoteMessageUiState.Success -> {
              Text(remoteMessageUiState.remoteMessage.text)
              Text(remoteMessageUiState.remoteMessage.photo)
              // Mostrar la imagen usando Coil
              val imageUrl = remoteMessageUiState.remoteMessage.photo
              if (imageUrl.isNotEmpty()) {
                  Text("carregant imatge")
                  Image(painter = rememberAsyncImagePainter("http://10.0.2.2:8080/"+imageUrl), contentDescription = null,
                      modifier = Modifier.fillMaxWidth())
              }
          }
      }
  }
}


private fun uriToFile(uri: Uri, context: android.content.Context): File? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return null

    // Obtener el nombre del archivo y limpiar caracteres no permitidos
    val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: return null

    // Crear el archivo en el directorio de caché
    val file = File(context.cacheDir, fileName)

    // Copiar los datos del inputStream al outputStream del archivo
    inputStream.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return file
}

/*El componente Navigation tiene tres partes principales:

NavController: Es responsable de navegar entre los destinos, es decir, las pantallas en tu app.
NavGraph: Realiza la asignación de los destinos componibles a los que se navegará.
NavHost: Es el elemento componible que funciona como contenedor para mostrar el destino actual del NavGraph.
muestra otros destinos, según una ruta determinada.

    La ruta es una string que se corresponde con un destino.
     Por lo general, un destino es un único elemento componible (o un grupo de ellos) que corresponde a lo que ve el usuario.
    Puedes definir las rutas de una app mediante una clase de tipo enum.
     */
@Composable
fun ExempleNavController(){
    val viewModel: AppViewModel = viewModel()

    val navController = rememberNavController()
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { navController.navigate("Inici") }) {
                Text(text = "Inici")
            }
            Button(onClick = { navController.navigate("Login") }) {
                Text(text = "Login")
            }
            Button(onClick = { navController.navigate("Registre") }) {
                Text(text = "Registre")
            }
            Button(onClick = { navController.navigate("Retrofit") }) {
                Text(text = "Retrofit")
            }
        }
        NavHost(navController = navController, startDestination = "Inici") {
            composable(route = "Inici") {
                ExempleNavInici(viewModel = viewModel, navController)
            }
            composable(route = "Login") {
                ExempleNavLogin()
            }
            composable(route = "Registre") {
                Text(text = "Registre")
            }
            composable(route = "Retrofit") {
                //val remoteModel:RemoteViewModel=viewModel()
//                ExempleRetrofit(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ExempleNavInici(viewModel: AppViewModel,navController: NavController){
    Image(painter = painterResource(id = R.drawable.hhh), contentDescription ="login" ,
        modifier = Modifier.clickable { navController.navigate("Login")  })
}
@Composable
fun ExempleNavLogin(){
    Text(text = "LOGIN")
}


///---
@Composable
fun ExempleViewModel() {
    val viewModel: AppViewModel = viewModel()
    //app//build.gradle.kts , dins de dependencies afegir:    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    when (viewModel.uiState.collectAsState().value.nomUsuari) {
        "nom" -> {
            Nom(viewModel = viewModel)
        }

        else -> {
            NomIPuntuacio(viewModel = viewModel)
        }
    }
}

@Composable
fun NomIPuntuacio(viewModel: AppViewModel) {
    Button(modifier = Modifier.padding(top = 30.dp),
        onClick = {
            Log.d("exemple", "nom:" + viewModel.getNomUsuari())
            viewModel.updateNomUsuari("nom")
            viewModel.updatePuntuacio(viewModel.getPuntuacio() + 1)
        }) {
        Text(text = viewModel.getNomUsuari() + " - " + viewModel.getPuntuacio())
    }
}

@Composable
fun Nom(viewModel: AppViewModel) {
    Button(onClick = {
        Log.d("exemple", "nom i punt:" + viewModel.getNomUsuari())
        viewModel.updateNomUsuari("nom i punts")
    }) {
        Text(text = viewModel.getNomUsuari())
    }
}

//vm2 creem una clase de dades encarregada d'estructurar la informació a guardar
data class InfoUiState(
    val nomUsuari: String = "nom",
    val puntuacio: Int = 0
) {}


//vm1 creem una clase que extengui de ViewModel. Volem que sigui encarregada de mantenir la informació
//sobre l'estat de l'aplicació. Volem que només aquesta classe pugui modificar l'estat de l'aplicació
class AppViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var remoteMessageUiState: RemoteMessageUiState by mutableStateOf(RemoteMessageUiState.Cargant)
        private set
//    private val apiService = RemoteMessageService.retrofitService;
    //vm3 StateFlow es un contenidor de dades observable. Amb MutableStateFlow creem un nou StateFlow
//    private val _uiState = MutableStateFlow(InfoUiState.Loading)
    private val _uiState = MutableStateFlow(InfoUiState())
    //vm4 Evitem que uiState sigui modificable desde fora el AppViewModel redefinint el seu métode get()
    //com que és un val no té métode set().
    val uiState: StateFlow<InfoUiState> get() = _uiState.asStateFlow()


    init {
        _uiState.value = InfoUiState()

    }



    //Repositori de Info Retrofit
//    MutableState  carece de la capacidad de guardar el estado diferente: loading, success y failure (cargando, éxito y error).
//    El estado Loading indica que la app está esperando datos.
//    El estado Success indica que los datos se recuperaron correctamente del servicio web.
//    El estado Error indica cualquier error de red o conexión.
//    private val _infoState = MutableStateFlow(String())
//    val infoState: StateFlow<String> get() = _infoState.asStateFlow()
//    fun updateInfo(){
//        /*Un viewModelScope es el alcance integrado de corrutinas definido para cada ViewModel en tu app. Si se borra ViewModel, se cancela automáticamente cualquier corrutina iniciada en este alcance.
//Puedes usar viewModelScope para iniciar la corrutina y realizar la solicitud de servicio web en segundo plano. Como viewModelScope pertenece a ViewModel, la solicitud continúa incluso si la app pasa por un cambio de configuración.*/
//        viewModelScope.launch {
//            try {
//                Log.d("exemple","GET INFO");
//                val info= InfoApi.retrofitService.getInfo()
//                Log.d("exemple","info ${info}")
////                Log.d("exemple","info ${info.text} , ${info.photo}")
//
//                _infoState.update { (info) }
//
////                Log.d("exemple","info ${info.text} , ${info.photo}")
//            }catch (e:Exception){
//                e.printStackTrace()
//                Log.d("exemple","error! ${e.toString()}")
//            }
//        }
//    }
    fun updateNomUsuari(nomUsuari: String) {
        //amb copy creem una nova instancia de _uiState amb nous valors
        _uiState.update {
            (it.copy(
                nomUsuari = nomUsuari
            ))
        }
    }

    fun updatePuntuacio(puntuacio: Int) {
        _uiState.update { (it.copy(puntuacio = puntuacio)) }
    }

    fun getNomUsuari(): String {
        return _uiState.value.nomUsuari
    }

    fun getPuntuacio(): Int {
        return _uiState.value.puntuacio
    }



}



/////----
@Preview
@Composable
fun IconesPreview(){
    ExempleIcones()
}

@Preview (name="ExempleInput")
@Composable fun PreviewExempleInput(){
    ExempleInput()
}
@Composable
fun ExempleInput(){
    var valorField by remember { mutableStateOf<String>("") }
    Column {
        Text(text = "exemple $valorField", modifier = Modifier.padding(top = 20.dp))
        TextField(value = valorField,
            onValueChange ={ valorField=it},
            label={Text(text= stringResource(id = R.string.label_textField))},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite Icon",
                    tint = Color.Red
                )
                          },
            singleLine = true,
            //https://developer.android.com/reference/kotlin/androidx/compose/ui/text/input/KeyboardType
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ), modifier = Modifier.fillMaxWidth(1f)
        )
    }
}


//@Preview(name="ExempleLazyColumn")
//@Composable fun PreviewExempleLazyColumn(){
//    ExempleLazyVerticalGrid()
//}
@Composable
fun ExempleLazyVerticalGrid(){
    var textsList:List<String> = List(50){"valor"+(it).toString()}
    LazyVerticalGrid(
        //  columns = GridCells.Adaptive(minSize = 128.dp)
        columns = GridCells.Fixed(3)
    ) {
        items(textsList){
                item-> ElementGrid(text = item)
        }
    }
}
@Composable
fun ElementGrid(text: String, modifier: Modifier=Modifier){
    Text(" - $text", fontSize = 30.sp,
        modifier = Modifier.padding(bottom = 20.dp))
}
@Composable
fun ExempleLazyColumn(){
    var texts:List<String> = List(30){"valor"+(it).toString()}
    LazyColumn(modifier = Modifier) {
        items(texts){
            item->ElementColumna(item)
        }
    }
}
@Composable
fun ElementColumna(text:String){
    Text(" - $text", fontSize = 30.sp, modifier = Modifier.padding(bottom = 20.dp))

}

@Preview(name="ExempleMultiplesPantalles")
@Composable fun PreviewExempleMultiplesPantalles(){
    ExempleMultiplesPantalles()
}
@Composable
fun ExempleMultiplesPantalles(){
    var numPantalla by remember { mutableStateOf<Int>(1) }
    when (numPantalla){
        1-> Pantalla1( { numPantalla=2} )
        2-> Pantalla2( funOnClick={ numPantalla=1} )
    }
}
@Composable
fun Pantalla1(funOnClick:()->Unit) {
    Button(onClick = funOnClick) {
        Text(text = "Mostra Pantalla2")
    }
}
@Composable
fun Pantalla2(funOnClick:()->Unit) {
    Button(onClick = {
                    funOnClick()
                    Log.d("exemple","click btn2") }
    ) {
        Text(text = "Mostra Pantalla1")
    }
}
@Preview(name="ExempleState")
@Composable fun PreviewExempleState(){
    ExempleState()
}
@Composable
fun ExempleState(){
    var clicat by remember {   mutableStateOf<Int>(0)    }
    Column(modifier = Modifier.fillMaxWidth(1f)) {
        Text(text = "Conta clics", modifier = Modifier.padding(top = 20.dp))
        Button(onClick = { clicat=clicat+1}) {
            Text(text = "Clicat $clicat cops")
        }
    }
}

@Preview(showBackground = true, name="Exemple Preview")
@Composable
fun ExempleRowColumnPreview() {
    ExempleRowColumn()
}
@Composable
fun ExempleRowColumn(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 20.dp)
            .background(color = Color.Yellow),
        contentAlignment = Alignment.TopEnd
    ) {
    Column(modifier = Modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(1f)
            .background(color = Color.Cyan)) {
            Text(text = "1r text",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f))
            Text(text = "2n text", modifier = Modifier
                .weight(2f)
                .border(
                    color = colorResource(id = R.color.purple_500),
                    width = 2.dp
                ) )
        }
        Text(text = "hola",
            modifier = Modifier
                .border(color = Color.Green, width = 3.dp)
                .padding(10.dp))
        }
        Text("Hello Compose")
    }
}
@Preview(showBackground = true, name="Exemple Preview")
@Composable
fun ExemplePreview() {
    Exemple()
}
@Composable
fun Exemple(modifier: Modifier=Modifier){
        Row(verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(1f)) {
//                Greeting(
//                    name = "Android",modifier = Modifier.fillMaxWidth(fraction = 0.5f)
//                )
            for (k in 0..1) {
//                Image(
//                    painter = painterResource(id = R.drawable.hhh),
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxWidth(fraction = 0.5f)
//                        .weight(1f)
//                )
                ImatgeHHH(modifier.weight(1f))
            }

    }
}
@Composable
fun ImatgeHHH(modifier: Modifier){
    Image(
        painter = painterResource(id = R.drawable.hhh),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(fraction = 0.5f)

    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier=modifier.padding(15.dp)) {
        Text(text = "Hola $name!",
            fontSize = 30.sp
        )
        Text(text = "exemple composable",
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp))
    }
}

@Preview(showBackground = true, name="Previa 1")
@Composable
fun GreetingPreview1() {
    ApuntsApplicationComposeTheme {
        Row(){
            Greeting(
                name = "Androidd",modifier = Modifier.fillMaxWidth(fraction = 0.5f)
            )
            Image(painter = painterResource(id = R.drawable.hhh),
                contentDescription =null,
                modifier = Modifier.fillMaxWidth(fraction = 0.5f))
        }
    }
}















