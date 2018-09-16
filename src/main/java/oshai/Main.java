package oshai;

import com.github.jasync.sql.db.Configuration;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import io.javalin.Javalin;
import io.javalin.JavalinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Connection connection = new PostgreSQLConnection(
      new Configuration(
        "postgres",
        "localhost",
        5432,
        "mysecretpassword",
        "postgres"
      )
    );
    Javalin app = Javalin.create()
      .event(JavalinEvent.SERVER_STARTING, () -> {
        logger.info("--- SERVER STARTING!");
        connection.connect().get();
        logger.info("--- connection STARTED!");
      })
      .event(JavalinEvent.SERVER_STOPPING, () -> {
        logger.info("--- SERVER STOPPING!");
        connection.disconnect().get();
        logger.info("--- connection STOPPED!");
      })
      .start(7000);

    app.get("/", (ctx) -> {
      final CompletableFuture<QueryResult> queryResultCompletableFuture = connection.sendPreparedStatement("select 0");
      ctx.result(
        queryResultCompletableFuture
          .thenApply((t) -> "got result: " + t.getRows().get(0).get(0))
      );
    });
  }
}
