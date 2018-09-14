package com.github.jasync.mysql.example

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import io.javalin.Javalin
import io.javalin.JavalinEvent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val connection: Connection = PostgreSQLConnection(
            Configuration(
                    username = "username",
                    password = "password",
                    host = "host.com",
                    port = 5432,
                    database = "schema"
            )
    )
    val app = Javalin.create()
            .event(JavalinEvent.SERVER_STARTING) {
                logger.info("--- SERVER STARTING!")
                connection.connect().get()
                logger.info("--- connection STARTED!")
            }
            .event(JavalinEvent.SERVER_STOPPING) {
                logger.info("--- SERVER STOPPING!")
                connection.disconnect().get()
                logger.info("--- connection STOPPED!")
            }
            .start(7000)

    app.get("/") { ctx ->
        ctx.result(connection.sendPreparedStatement("select 0"))
    }
}
