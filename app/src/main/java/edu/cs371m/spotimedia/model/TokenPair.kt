package edu.cs371m.spotimedia.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class TokenPair(
    // Auth information
    var userUID: String = "",
    var accessToken: String = "",
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = ""
)