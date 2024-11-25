package com.example.apuntsapplicationcompose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apuntsapplicationcompose.R

@Composable
fun ExempleIcones(){
    Column(modifier = Modifier.padding(top = 20.dp)) {
//        Image(painter = painterResource(id = R.drawable.hhh),contentDescription = null)
//        val imageBitmap=ImageBitmap.imageResource(id = R.drawable.hhh)
//        Image(painter = BitmapPainter(imageBitmap), contentDescription = null)
//
//        Image(painter= painterResource(id = R.drawable.dice_1), contentDescription = null)
//        val imageVector = ImageVector.vectorResource(id = R.drawable.dice_1)
////        Image(painter = VectorPainter(imageVector), contentDescription = null )
        Icon(painter = painterResource(id = R.drawable.dice_1),
            contentDescription = null,
            tint = Color.Cyan)
        Icon( //Icons. conté icones predeterminades de material design
            imageVector = Icons.Rounded.Call,
            contentDescription = "Favorite Icon",
            tint = Color.Red,    //color de la icona, per defecte LocalContentColor.current
            modifier = Modifier
                .size(48.dp) //ampliem la icona
                .rotate(45f) //la imatge de la icona és girada
                .scale(1.5f)//la imatge de la icona és escalada
        )
        Icon(
            bitmap = ImageBitmap.imageResource(id = R.drawable.hhh),
            contentDescription = "HHH",
            modifier = Modifier.scale(0.5f)
        )
    }

}

