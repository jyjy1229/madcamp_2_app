package com.example.madstagrarn

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.madstagrarn.network.DataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FollowingAddActivity : AppCompatActivity() {
    private val readContactRequestCode = 100
    private val phoneList: ArrayList<Phone> = ArrayList()
    private val contactUserList: ArrayList<User> = ArrayList()
    private val dataService: DataService = DataService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_add)

        if(getContactWithPermission()) {
            getContactUsers()
        }
    }

    private fun getContactUsers() {
        dataService.service.getUsersByContact(phoneList).enqueue(object :
            Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                if(response.isSuccessful) {

                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun getAllContacts() {
        val recievedPhoneList: ArrayList<String> = ArrayList()
        val cr = contentResolver
        val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if ((cur?.getCount() ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            val phoneNumber: String =
                                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.e("phone", "$phoneNumber phone")
                            phoneList.add(Phone(name, phoneNumber))
                        }
                    }
                    pCur?.close()
                }

            }
        }
        cur?.close()
    }


    private fun getContactWithPermission() : Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            val permission: String = Manifest.permission.WRITE_CONTACTS
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    readContactRequestCode
                )
            } else {
                getAllContacts()
            }
        } else {
            getAllContacts()
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            readContactRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts()
                } else {
                    val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage(
                            "You have forcefully denied some of the required permissions " +
                                    "for this action. Please open settings, go to permissions and allow them."
                        )
                        .setPositiveButton("Settings") { dialog, which ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            Toast.makeText(
                                application,
                                "Permission required",
                                Toast.LENGTH_LONG
                            ).show()
                            this.finish()
                        }
                        .setCancelable(false)
                        .create()
                        .show()
                }
                return
            }
        }
    }

}