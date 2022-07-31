package eu.vibemc.lifesteal.other;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import eu.vibemc.lifesteal.Main;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class Debug {
    @NotNull
    public static final String URL = "https://paste.helpch.at/";

    @NotNull
    public static final Gson gson = new Gson();

    @NotNull
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.LONG)
            .withLocale(Locale.ENGLISH)
            .withZone(ZoneId.of("Europe/Warsaw"));

    @NotNull
    public static CompletableFuture<String> postDebug(@NotNull final String dump) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpURLConnection connection = ((HttpURLConnection) new java.net.URL(URL + "documents")
                        .openConnection());
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setDoOutput(true);

                connection.connect();

                try (final OutputStream stream = connection.getOutputStream()) {
                    stream.write(dump.getBytes(StandardCharsets.UTF_8));
                }

                try (final InputStream stream = connection.getInputStream()) {
                    final String json = CharStreams.toString(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    return gson.fromJson(json, JsonObject.class).get("key").getAsString();
                }
            } catch (final IOException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    @NotNull
    public static String makeDebug(@NotNull final Main plugin) {
        final StringBuilder builder = new StringBuilder();

        builder.append("Generated: ")
                .append(DATE_FORMAT.format(Instant.now()))
                .append("\n\n");

        builder.append("P-LifeSteal: ")
                .append(plugin.getDescription().getVersion())
                .append("\n\n");

        builder.append("Server Info: ")
                .append(plugin.getServer().getBukkitVersion())
                .append('/')
                .append(plugin.getServer().getVersion())
                .append("\n");

        builder.append("Java Version: ")
                .append(System.getProperty("java.version"))
                .append("\n\n");

        builder.append("Plugin Info:")
                .append('\n');

        List<Plugin> plugins = Arrays.stream(plugin.getServer().getPluginManager().getPlugins())
                .sorted(Comparator.comparing(Plugin::getName))
                .collect(Collectors.toList());

        int size = plugins.stream().map(pl -> pl.getName().length())
                .max(Integer::compareTo)
                .orElse(0);

        for (final Plugin other : plugins) {
            builder.append("  ")
                    .append(String.format("%-" + size + "s", other.getName()))
                    .append(" [Version: ")
                    .append(other.getDescription().getVersion())
                    .append("]")
                    .append("\n");
        }

        // get whole plugin config
        builder.append("\nPlugin Config:")
                .append('\n');

        File config = new File(Main.getInstance().getDataFolder(), "config.yml");
        // get config file content and add 2 spaces to each line
        try {
            List<String> lines = Files.readAllLines(config.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                builder.append("  ")
                        .append(line)
                        .append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
