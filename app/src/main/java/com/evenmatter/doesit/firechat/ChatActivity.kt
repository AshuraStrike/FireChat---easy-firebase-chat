package com.evenmatter.doesit.firechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.Toast
import com.evenmatter.doesit.firechat.adapter.MsgAdapter
import com.evenmatter.doesit.firechat.model.ChatMsg
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    val sdf = SimpleDateFormat("hh:mm a")
    lateinit var root: DatabaseReference
    lateinit var adapter: MsgAdapter
    lateinit var editText: EditText
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val roomName = intent.getStringExtra("room_name")
        val userName = intent.getStringExtra("user_name")

        setToolbar("Room: $roomName")

        root = FirebaseDatabase.getInstance().reference.child(roomName)
        adapter = MsgAdapter(this, userName)

        editText = findViewById(R.id.input) as EditText
        recycler = findViewById(R.id.recycler_view) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val button = findViewById(R.id.fab) as FloatingActionButton
        button.setOnClickListener {
            if(editText.text.isNotBlank()) {
                var map = HashMap<String, Any>()
                val tempKey = root.push().key
                root.updateChildren(map)

                val messageRoot = root.child(tempKey)
                map.put("name", userName)
                map.put("msg", editText.text.toString())
                map.put("time", sdf.format(Calendar.getInstance().time))
                messageRoot.updateChildren(map)

                editText.text.clear()
            }
        }

        val childListener = object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(baseContext,"On Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                Toast.makeText(baseContext,"On Child Moved", Toast.LENGTH_SHORT).show()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
                append_chatConversation(dataSnapshot)
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                append_chatConversation(dataSnapshot)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                Toast.makeText(baseContext,"Some Messages Have Been Deleted", Toast.LENGTH_SHORT).show()
            }

        }
        root.addChildEventListener(childListener)

        editText.bringToFront()
    }

    lateinit var chat_name: String
    lateinit var chat_msg: String
    lateinit var chat_time: String

    fun append_chatConversation(dataSnapshot: DataSnapshot?){
        var i = dataSnapshot?.children?.iterator()
        while(i!!.hasNext()){
            chat_msg = (i.next() as DataSnapshot).value as String
            chat_name = (i.next() as DataSnapshot).value as String
            chat_time = (i.next() as DataSnapshot).value as String
        }
        adapter.data.add(ChatMsg(chat_msg,chat_name,chat_time))
        adapter.notifyDataSetChanged()

        scrollDown()
    }

    fun scrollDown(){
        recycler.smoothScrollToPosition(adapter.data.size-1)
    }

    fun setToolbar(title: String?){
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
    }
}
