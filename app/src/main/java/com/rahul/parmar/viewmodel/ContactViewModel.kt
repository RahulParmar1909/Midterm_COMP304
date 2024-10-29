package com.rahul.parmar.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.rahul.parmar.Model.Contact

class ContactViewModel : ViewModel() {
    val contacts = mutableStateListOf<Contact>()

    fun addContact(contact: Contact) {
        contacts.add(contact)
    }
}