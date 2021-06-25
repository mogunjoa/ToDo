package com.example.todolist

import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.todolist.adapter.TodoListAdapter
import com.example.todolist.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var dbHelper: DBHelper
    lateinit var todoAdapter: TodoListAdapter
    private var todoItems = ArrayList<TodoItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.act = this
        binding.lifecycleOwner = this

        createDB()
    }


    fun createDB() {    //DB생성
        dbHelper = DBHelper(this@MainActivity, "Jin.db", null, 1)
        val db: SQLiteDatabase = dbHelper.writableDatabase
        dbHelper.onCreate(db)

        setInit()
    }

    fun setInit() {
        loadTodoDB()

        binding.fbPlus.setOnClickListener {
            val dialog = Dialog(this, android.R.style.Theme_Material_Light_Dialog)
            dialog.setContentView(R.layout.dialog_register)

            val btCancel = dialog.findViewById<Button>(R.id.btCancel)
            val btConfirm = dialog.findViewById<Button>(R.id.btConfirm)

            btCancel.setOnClickListener {
                dialog.dismiss()
            }
            btConfirm.setOnClickListener {
                val title = dialog.findViewById<EditText>(R.id.etTitle).text.toString()
                val content = dialog.findViewById<EditText>(R.id.etContent).text.toString()

                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                dbHelper.insertTodo(title, content, currentTime)

                val item = TodoItem(
                    title = title,
                    content = content,
                    date = currentTime
                )

                todoAdapter.addItem(item)
                todoAdapter.notifyDataSetChanged()
                binding.rvTodo.smoothScrollToPosition(0)
                Toast.makeText(this, "할 일 목록이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    fun loadTodoDB() {
        todoItems = dbHelper.selectTodo()
        todoAdapter = TodoListAdapter(todoItems, dbHelper)
        binding.rvTodo.setHasFixedSize(true)
        binding.rvTodo.adapter = todoAdapter
    }
}