package ca.eastarcti.json;

@SuppressWarnings("unused")
public class MojangVersionManifestData {
    public static class LatestData {
        public String release;
        public String snapshot;
    }
    public LatestData latest;
    public static class VersionData {
        public String id;
        public String type;
        public String url;
        public String time;
        public String releaseTime;
    }
    public VersionData[] versions;
}
