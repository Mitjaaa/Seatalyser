package de.seatalyser.api

import de.bixilon.kutil.concurrent.pool.DefaultThreadPool
import org.jsoup.Connection
import org.jsoup.Connection.Response

object APIFetcher {

    fun fetch(connection: Connection, callback: (Response) -> Unit, errorHandler: (Throwable) -> Unit) {
        DefaultThreadPool += { execute(connection, callback, errorHandler) }
    }

    private fun execute(connection: Connection, callback: (Response) -> Unit, errorHandler: (Throwable) -> Unit) {
        try {
            val response = connection.execute()
            callback.invoke(response)
        } catch (error: Throwable) {
            error.printStackTrace()
            errorHandler.invoke(error)
            return
        }
    }
}
