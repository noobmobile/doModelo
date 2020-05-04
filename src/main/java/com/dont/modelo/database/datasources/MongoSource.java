package com.dont.modelo.database.datasources;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.adapters.ItemStackAdapter;
import com.dont.modelo.database.adapters.LocationAdapter;
import com.dont.modelo.models.database.Storable;
import com.dont.modelo.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoSource implements IDataSource {

    private MongoClient mongoClient;
    private MongoCollection<BasicDBObject> mongoCollection;
    private final String collectionName = "dont.modelo";

    private final Gson gson;
    private final ExecutorService executor;

    public MongoSource(String ip, String database, String user, String password) {
        this.executor = Executors.newFixedThreadPool(3);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        this.gson = gsonBuilder.create();
        try {
            Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
            mongoClient = MongoClients.create("mongodb://" + user + ":" + password + "@" + ip + "/?authSource=admin");
            mongoCollection = mongoClient.getDatabase(database).getCollection(collectionName, BasicDBObject.class);
            Utils.debug(Utils.LogType.INFO, "conexao com mongodb estabelecida");
        } catch (Exception e) {
            System.out.println("nao foi possivel conexao com mysql: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Terminal.getPlugin(Terminal.class));
            if (mongoClient != null) {
                mongoClient.close();
                mongoClient = null;
            }
        }
    }

    @Override
    public <T extends Storable> T find(String key, Class<T> clazz) {
        MongoCursor<BasicDBObject> cursor = mongoCollection.find(Filters.eq("_id", key)).cursor();
        if (cursor.hasNext()) {
            BasicDBObject dbObject = cursor.next();
            return gson.fromJson(dbObject.getString("json"), clazz);
        }
        return null;
    }

    private final UpdateOptions UPSERT = new UpdateOptions().upsert(true);

    @Override
    public void insert(Storable storable, boolean async) {
        Runnable runnable = () -> {
            BasicDBObject object = new BasicDBObject().append("$set", new BasicDBObject("_id", storable.getKey()).append("json", gson.toJson(storable)));
            BasicDBObject query = new BasicDBObject().append("_id", storable.getKey());
            mongoCollection.updateOne(query, object, UPSERT);
        };
        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public void delete(String key, boolean async) {
        Runnable runnable = () -> {
            mongoCollection.deleteOne(Filters.eq("_id", key));
        };
        if (async) executor.submit(runnable);
        else runnable.run();
    }

    @Override
    public <T extends Storable> List<T> getAll(Class<T> clazz) {
        List<T> toReturn = new ArrayList<>();
        MongoCursor<BasicDBObject> cursor = mongoCollection.find().cursor();
        while (cursor.hasNext()) {
            BasicDBObject dbObject = cursor.next();
            try {
                T storable = gson.fromJson(dbObject.getString("json"), clazz);
                if (storable == null || storable.getKey() == null) continue;
                toReturn.add(storable);
            } catch (JsonSyntaxException e) { // por algum motivo, agora não é mais lançado essa exception e estou tendo que verificar manualmente
                continue;
            }
        }
        return toReturn;
    }

    @Override
    public boolean exists(String key) {
        return mongoCollection.find(Filters.eq("_id", key)).cursor().hasNext();
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    @Override
    public boolean isClosed() {
        return mongoClient == null;
    }
}