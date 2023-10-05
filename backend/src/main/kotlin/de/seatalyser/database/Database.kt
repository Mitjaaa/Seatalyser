package de.seatalyser.database

interface Database {

    fun connect()


    fun storeTrainLayout(): Unit = TODO()
    fun storeTrain()
    fun storeReservation()


    companion object {

        fun create(): Database = TODO()
    }
}
