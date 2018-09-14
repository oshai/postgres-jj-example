package com.github.jasync.mysql.example

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.general.ArrayRowData
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
  logger.info("starting")

  val connection: Connection = PostgreSQLConnection(
      Configuration(
          username = "username",
          password = "password",
          host = "host.com",
          port = 5432,
          database = "schema"
      )
  )
  connection.connect().get()
  val future = connection.sendPreparedStatement("select 0")
  val queryResult = future.get()
  println((queryResult.rows!![0] as ArrayRowData).columns.toList())
  println((queryResult.rows!![1] as ArrayRowData).columns.toList())
  connection.disconnect().get()
}
