package ca.eastarcti.json;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("unused")
public class MojangVersionData {
    public static class Arguments {
        public JsonElement[] game;
        public JsonElement[] jvm;
    }
    public static class AssetIndex {
        public String id;
        public String sha1;
        public int size;
        public int totalSize;
        public String url;
    }
    public static class Downloads {
        public static class Artifact {
            public String sha1;
            public int size;
            public String url;
        }

        public Artifact client;
        public Artifact client_mappings;
        public Artifact server;
        public Artifact server_mappings;
    }
    public static class JavaVersion {
        public String component;
        public int majorVersion;
    }
    public static class Library {
        public static class Downloads {
            public static class Artifact {
                public String path;
                public String sha1;
                public int size;
                public String url;
            }
            public static class Classifiers {
                @SerializedName("linux-x86_64")
                public Artifact linux_x86_64;
                @SerializedName("natives-linux")
                public Artifact natives_linux;
                @SerializedName("natives-macos")
                public Artifact natives_macos;
                @SerializedName("natives-windows")
                public Artifact natives_windows;
            }

            @Nullable
            public Artifact artifact;
            @Nullable
            public Classifiers classifiers;
        }
        public static class Rule {
            public static class OS {
                public String name;
            }

            public String action;
            public OS os;
        }

        public String name;
        public Downloads downloads;
        public Rule[] rules;
    }
    public static class Logging {
        public static class Client {
            public static class File {
                public String id;
                public String sha1;
                public int size;
                public String url;
            }

            public File file;
            public String type;
        }

        public Client client;
    }

    public Arguments arguments;
    public AssetIndex assetIndex;
    public String assets;
    public int complianceLevel;
    public Downloads downloads;
    public String id;
    public JavaVersion javaVersion;
    public Library[] libraries;
    public Logging logging;
    public String mainClass;
    public int minimumLauncherVersion;
    public String releaseTime;
    public String time;
    public String type;
}
