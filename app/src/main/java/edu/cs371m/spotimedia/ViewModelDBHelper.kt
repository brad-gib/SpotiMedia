package edu.cs371m.spotimedia


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.cs371m.spotimedia.model.TokenPair


class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "tokens"


    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    private fun limitAndGet(query: Query,
                            resultListener: (List<TokenPair>) -> Unit) {
        query
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "Tokens fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(TokenPair::class.java)
                })
//            }
//            .addOnFailureListener {
//                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
//                resultListener(listOf())
//            }
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    fun fetchTokens(resultListener: (List<TokenPair>) -> Unit) {
        // XXX Write me and use limitAndGet
        val query = db.collection(rootCollection)
        limitAndGet(query, resultListener)
    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createToken(pair: TokenPair) {
        // XXX Write me: add photoMeta
        db.collection(rootCollection)
            .add(pair)
            .addOnSuccessListener {
                Log.d("Document addition", "Document $it added successfully")
                val query = db.collection(rootCollection)
                //limitAndGet(query)
            }
            .addOnFailureListener { Log.d("Document addition", "Document $it not added") }
    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removePhotoMeta(tokenPair: TokenPair) {
        // XXX Write me.  Make sure you delete the correct entry. What uniquely identifies a photoMeta?
        db.collection(rootCollection).document(tokenPair.firestoreID)
            .delete()
            .addOnSuccessListener {
                //limitAndGet(db.collection(rootCollection))
                Log.d("Document deletion", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { Log.w("Document deletion", "Error deleting document $it") }
    }
}