package io.servertap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.servertap.api.v1.PlayerApi;
import io.servertap.api.v1.ServerApi;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static spark.Spark.*;

public class PluginEntrypoint extends JavaPlugin {

    private final Logger log = getLogger();

    @Override
    public void onEnable() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        // Standard request logger
        before("/*", (req, res) -> log.info("Request to " + req.pathInfo()));

        //Routes for v1 of the API
        path(Constants.API_VERSION, () -> {
            // Pings
            get("/ping", ServerApi::ping, gson::toJson);
            post("/ping", ServerApi::ping, gson::toJson);

            // Server routes
            get("/server", ServerApi::base, gson::toJson);
            get("/worlds", ServerApi::worlds, gson::toJson);
            get("/worlds/:world", ServerApi::world, gson::toJson);

            // Communication
            post("/broadcast", ServerApi::broadcast, gson::toJson);

            // Player routes
            get("/players", PlayerApi::playersGet, gson::toJson);
        });

        // Default fallthrough. Just give them a 404.
        get("/*", (req, res) -> halt(404, "Nothing here"));
    }

}
