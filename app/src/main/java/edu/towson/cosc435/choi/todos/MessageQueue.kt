package edu.towson.cosc435.choi.todos

import androidx.lifecycle.MutableLiveData


class MessageQueue {

    companion object {
        val Channel: MutableLiveData<Boolean> = MutableLiveData()
    }

}