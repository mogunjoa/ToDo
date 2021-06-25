package com.example.todolist.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DBHelper
import com.example.todolist.R
import com.example.todolist.TodoItem
import com.example.todolist.databinding.ItemListBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoListAdapter(
	private val data: ArrayList<TodoItem>,
	private val dbHelper: DBHelper
) :
	RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = DataBindingUtil.inflate<ItemListBinding>(
			LayoutInflater.from(parent.context),
			R.layout.item_list,
			parent,
			false
		)

		return ViewHolder(binding)
	}

	override fun getItemCount(): Int {
		return data.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(data[position])

		holder.itemView.run {
			setOnClickListener {
				println("position:::$position")

				val strArray = arrayOf("수정하기", "삭제하기")
				val builder = AlertDialog.Builder(context)
				builder.setTitle("원하는 작업을 선택해주세요.")
				builder.setItems(strArray) { _, selectPosition ->

					if(selectPosition == 0) {	//수정
						val dialog = Dialog(context, android.R.style.Theme_Material_Light_Dialog)
						dialog.setContentView(R.layout.dialog_register)

						dialog.findViewById<EditText>(R.id.etTitle).setText(data[position].title)
						dialog.findViewById<EditText>(R.id.etContent).setText(data[position].content)

						val btCancel = dialog.findViewById<Button>(R.id.btCancel)
						val btConfirm = dialog.findViewById<Button>(R.id.btConfirm)

						btCancel.setOnClickListener {
							dialog.dismiss()
						}
						btConfirm.setOnClickListener {
							val title = dialog.findViewById<EditText>(R.id.etTitle).text.toString()
							val content = dialog.findViewById<EditText>(R.id.etContent).text.toString()

							val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
							val beforeTime = data[position].date
							dbHelper.updatgeTodo(title, content, currentTime, beforeTime)

							data[position].title = title
							data[position].content = content
							data[position].date = currentTime
							notifyItemChanged(position, data[position])
							notifyDataSetChanged()
							dialog.dismiss()

							Toast.makeText(context, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
						}

						dialog.show()
					} else if(selectPosition == 1) {	//삭제
						val beforeTime = data[position].date
						dbHelper.deleteTodo(beforeTime)

						data.removeAt(position)
						notifyItemRemoved(position)
						notifyDataSetChanged()

						Toast.makeText(context, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
					}
				}
				builder.show()
			}
		}
	}

	inner class ViewHolder(private val binding: ItemListBinding) :
		RecyclerView.ViewHolder(binding.root) {

		init {

		}

		fun bind(todoItem: TodoItem) {
			binding.tvTitle.text = todoItem.title
			binding.tvContent.text = todoItem.content
			binding.tvDate.text = todoItem.date
		}

	}

	fun addItem(item: TodoItem){
		data.add(0, item)
		notifyItemInserted(0)
	}
}