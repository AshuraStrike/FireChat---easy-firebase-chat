package com.evenmatter.doesit.firechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    lateinit var adapter: ArrayAdapter<String>
    lateinit var editText: EditText
    lateinit var name: String
    var listOfRooms: ArrayList<String> = ArrayList()
    private lateinit var root: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        root = FirebaseDatabase.getInstance().reference.root

        initAuthListener()

/**("TODO: En algun lugar algo hace que la primera carga no jale alv morro")*/

        // Name.... set text app name

        editText = findViewById(R.id.input) as EditText
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,listOfRooms)

        val listView = findViewById(R.id.list_view_rooms) as ListView
        val button = findViewById(R.id.fab) as FloatingActionButton

        listView.adapter = adapter

        button.setOnClickListener{
            if(editText.text.isNotBlank()) {
                var map = HashMap<String, Any>()
                map.put(editText.text.toString(), "")
                root.updateChildren(map)
                Toast.makeText(this, "Room ${editText.text} Created!", Toast.LENGTH_SHORT).show()
                editText.text.clear()
            }
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("room_name", (view as TextView).text.toString())
            intent.putExtra("user_name", name)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.signOut -> {
                mAuth.signOut()
            }
        }
        return true
    }

    private fun setToolbar(title: String){
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
    }

    private fun initAuthListener(){
        mAuthListener = FirebaseAuth.AuthStateListener {
            if (mAuth.currentUser != null && mAuth.currentUser?.isEmailVerified == true){
                Toast.makeText(this,"Auth",Toast.LENGTH_SHORT).show()
                name = mAuth?.currentUser?.displayName.toString()
                setToolbar(name)
                val lateListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        var set = HashSet<String>()

                        var i = dataSnapshot?.children?.iterator()
                        while(i!!.hasNext()){
                            set.add((i!!.next() as DataSnapshot).key)
                        }
                        listOfRooms.clear()
                        listOfRooms.addAll(set)

                        adapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(p0: DatabaseError?) {
                    }
                }
                root.addValueEventListener(lateListener)
            } else {
                Toast.makeText(this,"De-Auth",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

    /*fun requestUsername() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Username:")

        val inputField = EditText(this)

        builder.setCancelable(false)
        builder.setView(inputField)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            name = inputField.text.toString()
            if(name.isBlank()) requestUsername()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
            requestUsername()
        })

        builder.show()
    }*/
}
