package net.thekimbroughs.util;

import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.ext.jdbc.JDBCClient;

import java.util.logging.Handler;

import static java.lang.String.format;

public class PostgresDatabaseUtil {
    PostgresDatabaseUtil() {}

    public static final String CORE_SELECT_QUERY = "" +
            "SELECT jsonb_set(" +
            "   jsonb_set(" +
            "       jsonb_set(data, {id}, to_jsonb(id), true), " +
            "   {created}, to_jsonb(created), true), " +
            "{modified}, to_jsonb(modified), true) " +
            "FROM %s";

    public static final String SELECT_BY_ID = CORE_SELECT_QUERY + " id = ?";

    public static final String INSERT_STATEMENT = "INSERT INTO %s (data) VALUES (?)";

    public static final String UPDATE_STATEMENT = "UPDATE %s SET modified = now(), data = ?::JSONB WHERE id = ?";

    public static final String DELETE_STATEMENT = "DELETE FROM %s WHERE id = ?";

    public static Single<ResultSet> getAll(JDBCClient sqlClient, String aggregate) {
        return sqlClient.rxQuery(format(CORE_SELECT_QUERY, aggregate));
    }

    public static Single<ResultSet> getById(JDBCClient sqlclient, String aggregate, String id) {
        return sqlclient.rxQueryWithParams(format(SELECT_BY_ID, aggregate), new JsonArray().add(id));
    }

    public static Single<UpdateResult> createOne(JDBCClient sqlClient, String aggregate, JsonObject data) {
        return sqlClient.rxUpdateWithParams(format(INSERT_STATEMENT, aggregate), new JsonArray().add(data.encode()));
    }

    public static Single<UpdateResult> updateOne(JDBCClient sqlClient, String aggregate, String id, JsonObject data) {
        return sqlClient.rxUpdateWithParams(format(UPDATE_STATEMENT, aggregate), new JsonArray().add(data).add(id));
    }

    public static Single<UpdateResult> deleteOne(JDBCClient sqlClient, String aggregate, String id) {
        return sqlClient.rxUpdateWithParams(format(DELETE_STATEMENT, aggregate), new JsonArray().add(id));
    }
}
