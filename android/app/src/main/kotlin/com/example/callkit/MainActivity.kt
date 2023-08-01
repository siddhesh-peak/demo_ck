package com.example.callkit

import android.util.Log
import androidx.annotation.NonNull
import com.example.callkit.database.Contact
import com.example.callkit.database.DatabaseHandler
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {

    private val CHANNEL = "com.sequoiacap.native.Tribe"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // This method is invoked on the main thread.
            call, result ->
            if (call.method == "updateContacts") {
                val data = call.arguments as List<Map<String, Any>>
                if(data.isNotEmpty()){
                    saveContactsToDb(data);
                }
                for (item in data) {
                    Log.d("METHOD C CHANNEL", "item = $item")
                }
            } else if (call.method == "deleteContacts") {
                val data = call.arguments as List<Map<String, Any>>
                if(data.isNotEmpty()){
                    deleteContactsFromDb(data);
                }
                for (item in data) {
                    Log.d("METHOD C CHANNEL", "item = $item")
                }
            }  else if (call.method == "deleteAllContacts") {

                deleteAllContactsFromDb()


        } else {
                result.notImplemented()
            }
        }
    }

    /// Save contact to DB
    private fun saveContactsToDb(contactList: List<Map<String, Any>>) {
        val db = DatabaseHandler(context)
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..")
        for (contact in contactList){
            val con = db.getContactsByNumber(contact["phone"].toString());
            if(con != null){
                con.name = contact["name"].toString()
                db.updateContact(con)
            }else{
                db.addContact(Contact(contact["name"].toString(), contact["phone"].toString()))
            }
        }

    }

    // Delete Contacts by Number
    private fun deleteContactsFromDb(contactList: List<Map<String, Any>>) {
        val db = DatabaseHandler(context)

        Log.d("Delete: ", "Deleting contactd ..")
        for (contact in contactList){
            val con = db.getContactsByNumber(contact["phone"].toString());
            if(con != null){
                con.name = contact["name"].toString()
                db.deleteContact(con)
            }
        }
    }


    // Deleting Contacts
     private fun deleteAllContactsFromDb() {
        val db = DatabaseHandler(context)

        Log.d("DELETE: ", "Deleting all contacts ..")
        db.deleteAllContact()
    }


}
