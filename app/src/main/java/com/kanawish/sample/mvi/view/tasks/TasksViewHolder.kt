package com.kanawish.sample.mvi.view.tasks

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.kanawish.sample.mvi.R
import com.kanawish.sample.mvi.intent.toViewEvent
import com.kanawish.sample.mvi.model.Task
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class TasksViewHolder(
        itemView: View,
        private val viewEventConsumer: (TasksViewEvent) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    // NOTE: I believe we can't use kotlin's synthetic accessors. -- TODO: Double check
    private val checkBox: CheckBox = itemView.findViewById(R.id.complete)
    private val titleView: TextView = itemView.findViewById(R.id.title)

    private val disposables = CompositeDisposable()

    private fun checkBoxChanges(task: Task): Observable<TasksViewEvent> {
        return checkBox.checkedChanges().skipInitialValue()
                .toViewEvent { checked ->
                    TasksViewEvent.TaskCheckBoxClick(task, checked)
                }
    }

    private fun titleViewClicks(task: Task): Observable<TasksViewEvent> {
        return titleView.clicks()
                .toViewEvent { TasksViewEvent.TaskClick(task) }
    }

    // TODO: Evaluate performance costs of on bind/unbind.
    fun bind(task: Task) {
        // NOTE: This is important to avoid triggering intents on re-bind.
        disposables.clear()

        // Binds task after applying view changes.
        titleView.text = task.title
        checkBox.isChecked = task.completed

        disposables += checkBoxChanges(task).subscribe(viewEventConsumer)
        disposables += titleViewClicks(task).subscribe(viewEventConsumer)
    }

    fun unbind() {
        disposables.clear()

        // Clear the viewHolder to avoid it showing old info when brought back.
        titleView.text = ""
        checkBox.isChecked = false
    }

}