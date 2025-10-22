package me.juansierra.unabshop

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


//retornos boleanos
fun validateEmail(email: String): Pair<Boolean, String> {
	return when{
		email.isEmpty() -> Pair(false, "El correo es requerido")
		!Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Pair(false, "El correo es invalido")
		!email.endsWith("@test.com")-> Pair(false, "Ese email no es corporativo.")
		else -> {
			Pair(true, "")
		}
	}
}

fun validatePassword(password: String): Pair<Boolean, String> {
	return when{
		password.isEmpty() -> Pair(false, "La contraseña es requerida")
		password.length < 6 -> Pair(false, "La contraseña debe tener al menos 6 caracteres")

		!password.any{it.isDigit() } -> Pair(false, "La contraseña debe contener al menos un digito")
		else -> {
			Pair(true, "")
		}
	}
}

fun validateName(name: String): Pair<Boolean, String>{
	return when{
		name.isEmpty() -> Pair(false, "Nombre requerido")
		name.length < 3 -> Pair(false, "El nombre requiere de al menos 3 caracteres")
		else -> {
			Pair(true, "")
		}
	}
}

fun validatePasswordConfirm(password: String, confirmpassword: String): Pair<Boolean, String>{
	return when{
		confirmpassword.isEmpty() -> Pair(false, "Contraseña requerida")
		confirmpassword != password -> Pair(false, "Las 2 contraseñas no coinciden")
		else -> {
			Pair(true, "")
		}
	}
}