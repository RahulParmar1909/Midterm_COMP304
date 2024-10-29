package com.rahul.parmar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.parmar.Model.Contact
import com.rahul.parmar.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

class ParmarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactManagerScreen()
        }
    }
}

@Composable
fun ContactManagerScreen(viewModel: ContactViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedContactType by remember { mutableStateOf("") }
    val availableTypes = listOf("Friend", "Family", "Work")

    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = name.isBlank()
        )
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = phone,
            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = phoneError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        if (phoneError) {
            Text("Enter a valid 10-digit phone number", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        if (emailError) {
            Text("Enter a valid email address", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Contact Type", style = MaterialTheme.typography.bodyLarge)
        availableTypes.forEach { type ->
            RadioButtonWithLabel(
                label = type,
                selected = selectedContactType == type,
                onSelected = { selectedContactType = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

      
        Button(
            onClick = {
                phoneError = phone.length != 10
                emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (name.isNotBlank() && !phoneError && !emailError) {
                    val contact = Contact(name, phone, email, selectedContactType)
                    viewModel.addContact(contact)

                    scope.launch {
                        snackbarHostState.showSnackbar("Contact Added: $name")
                    }

                    // Reset Fields
                    name = ""
                    phone = ""
                    email = ""
                    selectedContactType = ""
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please enter valid details.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Contact")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // List of Contacts
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.contacts) { contact ->
                ContactListItem(contact)
            }
        }

        // Snackbar Host
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun RadioButtonWithLabel(label: String, selected: Boolean, onSelected: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = { onSelected(label) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun ContactListItem(contact: Contact) {
    Text(
        "${contact.name} - ${contact.phone} - ${contact.email} - ${contact.type}",
        modifier = Modifier.padding(8.dp)
    )
}
