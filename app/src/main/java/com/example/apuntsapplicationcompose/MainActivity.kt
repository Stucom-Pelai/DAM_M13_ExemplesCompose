package com.example.apuntsapplicationcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apuntsapplicationcompose.ui.ExempleIcones
import com.example.apuntsapplicationcompose.ui.theme.ApuntsApplicationComposeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExempleViewModel()
        }
    }
}
///---
@Composable
fun ExempleViewModel(){
    val viewModel:AppViewModel= viewModel()
    //app//build.gradle.kts , dins de dependencies afegir:    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    when(viewModel.uiState.collectAsState().value.nomUsuari){
        "nom" -> {
            Nom(viewModel=viewModel)
        }
        else -> {
        NomIPuntuacio(viewModel = viewModel)
        }
    }
}

@Composable
fun NomIPuntuacio(viewModel: AppViewModel){
    Button(modifier = Modifier.padding(top = 30.dp),
        onClick = {
        Log.d("exemple","nom:"+viewModel.getNomUsuari() )
        viewModel.updateNomUsuari("nom")
            viewModel.updatePuntuacio(viewModel.getPuntuacio()+1)
    }) {
        Text(text = viewModel.getNomUsuari()+" - "+viewModel.getPuntuacio())
    }
}
@Composable
fun Nom(viewModel: AppViewModel){
    Button(onClick = {
        Log.d("exemple","nom i punt:"+ viewModel.getNomUsuari() )
        viewModel.updateNomUsuari("nom i punts") }) {
        Text(text = viewModel.getNomUsuari())
    }
}
//vm2 creem una clase de dades encarregada d'estructurar la informació a guardar
data class InfoUiState(
    val nomUsuari:String="nom",
    val puntuacio:Int=0
){}
//vm1 creem una clase que extengui de ViewModel. Volem que sigui encarregada de mantenir la informació
//sobre l'estat de l'aplicació. Volem que només aquesta classe pugui modificar l'estat de l'aplicació
class AppViewModel: ViewModel() {
    //vm3 StateFlow es un contenidor de dades observable. Amb MutableStateFlow creem un nou StateFlow
    private val _uiState = MutableStateFlow(InfoUiState())
    //vm4 Evitem que uiState sigui modificable desde fora el AppViewModel redefinint el seu métode get()
    //com que és un val no té métode set().
    val uiState: StateFlow<InfoUiState> get()= _uiState.asStateFlow()

    init{
        _uiState.value=InfoUiState("",0)
    }
    fun updateNomUsuari(nomUsuari: String){
        //amb copy creem una nova instancia de _uiState amb nous valors
        _uiState.update { (it.copy(
            nomUsuari=nomUsuari
        )) }
    }
    fun updatePuntuacio(puntuacio: Int){
        _uiState.update { (it.copy(puntuacio=puntuacio)) }
    }
    fun getNomUsuari():String{
        return _uiState.value.nomUsuari
    }
    fun getPuntuacio():Int{
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















