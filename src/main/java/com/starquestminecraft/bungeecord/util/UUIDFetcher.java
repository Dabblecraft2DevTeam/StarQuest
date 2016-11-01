package com.starquestminecraft.bungeecord.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class UUIDFetcher {

    public static final long FEBRUARY_2015 = 1422748800000L;

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private static final String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";

    private static final Map<String, Profile> PROFILE_CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, String> NAME_CACHE = new ConcurrentHashMap<>();

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    private UUIDFetcher() {

    }

    /**
     * Aynchronously fetches the profile for a specified name
     *
     * @param name The name
     */
    public static Future<Profile> getProfileAsync(final String name) {

        return POOL.submit(new Callable<Profile>() {

            @Override
            public Profile call() {
                return getProfile(name);
            }

        });

    }

    /**
     * Synchronously fetches and returns the profile for a specified name
     *
     * @param name The name
     *
     * @return The profile
     */
    public static Profile getProfile(final String name) {
        return getProfileAt(name, System.currentTimeMillis());
    }

    /**
     * Asynchronously fetches the profile for a specified name and time
     *
     * @param name      The name
     * @param timestamp Time when the player had this name in milliseconds
     */
    public static Future<Profile> getProfileAsync(final String name, final long timestamp) {

        return POOL.submit(new Callable<Profile>() {

            @Override
            public Profile call() {
                return getProfileAt(name, timestamp);
            }

        });

    }

    /**
     * Synchronously fetches the UUID for a specified name and time
     *
     * @param name      The name
     * @param timestamp Time when the player had this name in milliseconds
     *
     * @see UUIDFetcher#FEBRUARY_2015
     */
    public static Profile getProfileAt(final String name, final long timestamp) {

        String key = name.toLowerCase();

        if(PROFILE_CACHE.containsKey(key)) {
            return PROFILE_CACHE.get(key);
        }

        try {

            HttpURLConnection connection = (HttpURLConnection)new URL(String.format(PROFILE_URL, key, timestamp / 1000)).openConnection();

            connection.setReadTimeout(5000);

            Profile profile;

            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                profile = GSON.fromJson(br, Profile.class);
            }

            PROFILE_CACHE.put(key, profile);
            NAME_CACHE.put(profile.id, profile.name);

            return profile;

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    /**
     * Asynchronously fetches the name for a specified UUID
     *
     * @param uuid The UUID
     */
    public static Future<String> getNameAsync(final UUID uuid) {

        return POOL.submit(new Callable<String>() {

            @Override
            public String call() {
                return getName(uuid);
            }

        });

    }

    /**
     * Synchronously fetches and returns the name for a specified UUID
     *
     * @param uuid The UUID
     *
     * @return The name
     */
    public static String getName(final UUID uuid) {

        if(NAME_CACHE.containsKey(uuid)) {
            return NAME_CACHE.get(uuid);
        }

        try {

            HttpURLConnection connection = (HttpURLConnection)new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();

            connection.setReadTimeout(5000);

            Profile[] history;

            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                history = GSON.fromJson(br, Profile[].class);
            }

            Profile current = history[history.length - 1];

            PROFILE_CACHE.put(current.name.toLowerCase(), current);
            NAME_CACHE.put(uuid, current.name);

            return current.name;

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    public static class Profile {

        private String name;
        private UUID id;

        private Profile(final UUID id, final String name) {

            this.id = id;
            this.name = name;

        }

        public UUID getID() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    private static class UUIDTypeAdapter extends TypeAdapter<UUID> {

        @Override
        public void write(final JsonWriter writer, final UUID uuid) throws IOException {
            writer.value(fromUUID(uuid));
        }

        @Override
        public UUID read(final JsonReader reader) throws IOException {
            return fromString(reader.nextString());
        }

        public static String fromUUID(final UUID uuid) {
            return uuid.toString().replace("-", "");
        }

        public static UUID fromString(final String str) {
            return UUID.fromString(str.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }

    }

}
