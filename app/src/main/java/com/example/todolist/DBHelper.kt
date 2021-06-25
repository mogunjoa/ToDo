package com.example.todolist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory

import android.database.sqlite.SQLiteOpenHelper


class DBHelper(
    context: Context?,
    name: String?,
    factory: CursorFactory?,
    version: Int
) :
    SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE if not exists TodoList (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, date TEXT NOT NULL)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        val sql = "DROP TABLE if exists mytable"
        db.execSQL(sql)
        onCreate(db)
    }

    // SELECT 문
    fun selectTodo() : ArrayList<TodoItem> {
        var todoItems = arrayListOf<TodoItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM TodoList ORDER BY date DESC", null)
        if(cursor.count != 0) {
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val date = cursor.getString(cursor.getColumnIndex("date"))

                val todoItem = TodoItem(
                    title = title,
                    content = content,
                    date = date
                )

                todoItems.add(todoItem)
            }
        }
        cursor.close()

        return todoItems
    }

    // INSERT 문
    fun insertTodo(_title: String, _content: String, _date: String) {
        val db = writableDatabase
        db.execSQL("INSERT INTO TodoList('title', 'content', 'date') VALUES('$_title', '$_content', '$_date')")
    }

    // UPDATE 문
    fun updatgeTodo(_title: String, _content: String, _date: String, beforeDate: String) {
        val db = writableDatabase
        db.execSQL("UPDATE TodoList SET title = '$_title', content = '$_content', date = '$_date' WHERE date = '$beforeDate'")
    }

    // DELETE 문
    fun deleteTodo(beforeDate: String) {
        val db = writableDatabase
        db.execSQL("DELETE FROM TodoList WHERE date = '$beforeDate'")
    }


}