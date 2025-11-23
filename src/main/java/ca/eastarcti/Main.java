package ca.eastarcti;

import ca.eastarcti.json.FabricIntermediaryData;
import ca.eastarcti.json.FabricYarnData;
import ca.eastarcti.json.MojangVersionData;
import ca.eastarcti.json.MojangVersionManifestData;
import com.google.gson.Gson;
import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public class Main {
    private static final String LICENSE = """
Creative Commons Legal Code

CC0 1.0 Universal

    CREATIVE COMMONS CORPORATION IS NOT A LAW FIRM AND DOES NOT PROVIDE
    LEGAL SERVICES. DISTRIBUTION OF THIS DOCUMENT DOES NOT CREATE AN
    ATTORNEY-CLIENT RELATIONSHIP. CREATIVE COMMONS PROVIDES THIS
    INFORMATION ON AN "AS-IS" BASIS. CREATIVE COMMONS MAKES NO WARRANTIES
    REGARDING THE USE OF THIS DOCUMENT OR THE INFORMATION OR WORKS
    PROVIDED HEREUNDER, AND DISCLAIMS LIABILITY FOR DAMAGES RESULTING FROM
    THE USE OF THIS DOCUMENT OR THE INFORMATION OR WORKS PROVIDED
    HEREUNDER.

Statement of Purpose

The laws of most jurisdictions throughout the world automatically confer
exclusive Copyright and Related Rights (defined below) upon the creator
and subsequent owner(s) (each and all, an "owner") of an original work of
authorship and/or a database (each, a "Work").

Certain owners wish to permanently relinquish those rights to a Work for
the purpose of contributing to a commons of creative, cultural and
scientific works ("Commons") that the public can reliably and without fear
of later claims of infringement build upon, modify, incorporate in other
works, reuse and redistribute as freely as possible in any form whatsoever
and for any purposes, including without limitation commercial purposes.
These owners may contribute to the Commons to promote the ideal of a free
culture and the further production of creative, cultural and scientific
works, or to gain reputation or greater distribution for their Work in
part through the use and efforts of others.

For these and/or other purposes and motivations, and without any
expectation of additional consideration or compensation, the person
associating CC0 with a Work (the "Affirmer"), to the extent that he or she
is an owner of Copyright and Related Rights in the Work, voluntarily
elects to apply CC0 to the Work and publicly distribute the Work under its
terms, with knowledge of his or her Copyright and Related Rights in the
Work and the meaning and intended legal effect of CC0 on those rights.

1. Copyright and Related Rights. A Work made available under CC0 may be
protected by copyright and related or neighboring rights ("Copyright and
Related Rights"). Copyright and Related Rights include, but are not
limited to, the following:

  i. the right to reproduce, adapt, distribute, perform, display,
     communicate, and translate a Work;
 ii. moral rights retained by the original author(s) and/or performer(s);
iii. publicity and privacy rights pertaining to a person's image or
     likeness depicted in a Work;
 iv. rights protecting against unfair competition in regards to a Work,
     subject to the limitations in paragraph 4(a), below;
  v. rights protecting the extraction, dissemination, use and reuse of data
     in a Work;
 vi. database rights (such as those arising under Directive 96/9/EC of the
     European Parliament and of the Council of 11 March 1996 on the legal
     protection of databases, and under any national implementation
     thereof, including any amended or successor version of such
     directive); and
vii. other similar, equivalent or corresponding rights throughout the
     world based on applicable law or treaty, and any national
     implementations thereof.

2. Waiver. To the greatest extent permitted by, but not in contravention
of, applicable law, Affirmer hereby overtly, fully, permanently,
irrevocably and unconditionally waives, abandons, and surrenders all of
Affirmer's Copyright and Related Rights and associated claims and causes
of action, whether now known or unknown (including existing as well as
future claims and causes of action), in the Work (i) in all territories
worldwide, (ii) for the maximum duration provided by applicable law or
treaty (including future time extensions), (iii) in any current or future
medium and for any number of copies, and (iv) for any purpose whatsoever,
including without limitation commercial, advertising or promotional
purposes (the "Waiver"). Affirmer makes the Waiver for the benefit of each
member of the public at large and to the detriment of Affirmer's heirs and
successors, fully intending that such Waiver shall not be subject to
revocation, rescission, cancellation, termination, or any other legal or
equitable action to disrupt the quiet enjoyment of the Work by the public
as contemplated by Affirmer's express Statement of Purpose.

3. Public License Fallback. Should any part of the Waiver for any reason
be judged legally invalid or ineffective under applicable law, then the
Waiver shall be preserved to the maximum extent permitted taking into
account Affirmer's express Statement of Purpose. In addition, to the
extent the Waiver is so judged Affirmer hereby grants to each affected
person a royalty-free, non transferable, non sublicensable, non exclusive,
irrevocable and unconditional license to exercise Affirmer's Copyright and
Related Rights in the Work (i) in all territories worldwide, (ii) for the
maximum duration provided by applicable law or treaty (including future
time extensions), (iii) in any current or future medium and for any number
of copies, and (iv) for any purpose whatsoever, including without
limitation commercial, advertising or promotional purposes (the
"License"). The License shall be deemed effective as of the date CC0 was
applied by Affirmer to the Work. Should any part of the License for any
reason be judged legally invalid or ineffective under applicable law, such
partial invalidity or ineffectiveness shall not invalidate the remainder
of the License, and in such case Affirmer hereby affirms that he or she
will not (i) exercise any of his or her remaining Copyright and Related
Rights in the Work or (ii) assert any associated claims and causes of
action with respect to the Work, in either case contrary to Affirmer's
express Statement of Purpose.

4. Limitations and Disclaimers.

 a. No trademark or patent rights held by Affirmer are waived, abandoned,
    surrendered, licensed or otherwise affected by this document.
 b. Affirmer offers the Work as-is and makes no representations or
    warranties of any kind concerning the Work, express, implied,
    statutory or otherwise, including without limitation warranties of
    title, merchantability, fitness for a particular purpose, non
    infringement, or the absence of latent or other defects, accuracy, or
    the present or absence of errors, whether or not discoverable, all to
    the greatest extent permissible under applicable law.
 c. Affirmer disclaims responsibility for clearing rights of other persons
    that may apply to the Work or any use thereof, including without
    limitation any person's Copyright and Related Rights in the Work.
    Further, Affirmer disclaims responsibility for obtaining any necessary
    consents, permissions or other rights required for any use of the
    Work.
 d. Affirmer understands and acknowledges that Creative Commons is not a
    party to this document and has no duty or obligation with respect to
    this CC0 or use of the Work.""";
    // TODO: Show available builds in README
    private static final String README_TEMPLATE = """
# @minecraft-types/yarn-<MINECRAFT_VERSION>

Typescript definitions for Minecraft <MINECRAFT_VERSION> (Fabric/Yarn mappings), all known builds.

## Usage

Install from npm or pnpm:

```bash
npm install @minecraft-types/yarn-<MINECRAFT_VERSION>
# or
pnpm add @minecraft-types/yarn-<MINECRAFT_VERSION>
```

Then, in your `tsconfig.json`, add the following to the `compilerOptions` section:

```json
{
  "compilerOptions": {
    "types": [
      "@minecraft-types/yarn-<MINECRAFT_VERSION>"
    ]
  }
}
```

If you require a specific yarn build number instead of the default (latest) build, specify it like this:
```json
{
  "compilerOptions": {
    "types": [
      "@minecraft-types/yarn-<MINECRAFT_VERSION>/build.<BUILD_NUMBER>"
    ]
  }
}
```

Replace `<BUILD_NUMBER>` with the desired build number.

Note: adding the `types` array explicitly tells TypeScript which global type packages to include. When you set `compilerOptions.types` it will only include the packages listed there and will not automatically include other `@types/*` packages (for example `node`). If you need other ambient types, add them to the array as well or consider using `typeRoots` or a project `global.d.ts` instead.

## Using the types and handling globals

By default, this will expose the types under the `Packages` namespace (ex. `Packages.java.io.BufferedInputStream`).

If you work in an environment where the global namespace is polluted differently with Java types, you can re-namespace the types by creating a `global.d.ts` file in your project with the following content:

```ts
// global.d.ts
declare global {
  const java: typeof Packages.java;
  // Add other root namespaces as needed (e.g., javax, com, org, etc.)
  // const com: typeof Packages.com;

  // If your environment exposes something other than 'Packages', you can alias it here
  // const SomeOtherPackages: typeof Packages;
}

export {}; // keep file a module so TS merges properly
```

If instead, you'd prefer to only pollute the global namespace within that file, you can add the following to your typescript files:
```ts
declare const java: typeof Packages.java;
// Add other root namespaces as needed (e.g., javax, com, org, etc.)
// declare const com: typeof Packages.com;

// If your environment exposes something other than 'Packages', you can alias it here
// declare const SomeOtherPackages: typeof Packages;
```

## Examples

- Referencing a Java type directly from the ambient `Packages` types:

```ts
// Uses the package name as exposed by the types
type BIS = Packages.java.io.BufferedInputStream;

declare const inStream: BIS;
```

- Using the `java` alias from the `global.d.ts` example:

```ts
const s: string = java.lang.String.valueOf(123);
```

## License

CC0-1.0
""";
    private static final String PACKAGE_TEMPLATE = """
{
  "name": "@minecraft-types/yarn-<MINECRAFT_VERSION>",
  "version": "1.0.3",
  "description": "Typescript definitions for Minecraft <MINECRAFT_VERSION> (Fabric/Yarn mappings), all known builds.",
  "homepage": "https://github.com/EastArctica/minecraft-types#readme",
  "bugs": {
    "url": "https://github.com/EastArctica/minecraft-types/issues"
  },
  "license": "CC0-1.0",
  "author": {
    "name": "East_Arctica",
    "url": "https://github.com/EastArctica"
  },
  "files": [
    "README.md",
    "LICENSE",
    "*/index.d.ts"
  ],
  "repository": {
    "type": "git",
    "url": "https://github.com/EastArctica/minecraft-types.git",
    "directory": "yarn-<MINECRAFT_VERSION>/"
  },
  "types": "build.<DEFAULT_BUILD_NUMBER>/index.d.ts"
}""";
    private static final String FABRIC_META_URL = "https://meta.fabricmc.net/v2/versions/";
    private static final String FABRIC_INTERMEDIARY_URL = FABRIC_META_URL + "intermediary/";
    private static final String FABRIC_YARN_URL = FABRIC_META_URL + "yarn/";
    private static final String FABRIC_MAVEN_URL = "https://maven.fabricmc.net/";
    private static final String MOJANG_VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest.json";
    private static final String WORK_DIR = "working";
    private static final String LIBS_DIR = WORK_DIR + "/libs";
    private static final String VERSIONS_DIR = WORK_DIR + "/versions";

    private static FabricIntermediaryData[] intermediaries;
    private static FabricYarnData[] yarns;
    private static MojangVersionManifestData versionManifest;

    // Counters for summary
    private static int intermediariesProcessed = 0;
    private static int intermediariesDownloaded = 0;
    private static int minecraftJarsDownloaded = 0;
    private static int libsDownloaded = 0;
    private static int yarnsMatchedTotal = 0;
    private static int yarnsDownloaded = 0;
    private static int yarnsAlreadyPresent = 0; // yarn.jar already existed on disk
    private static int namedJarsCreated = 0;
    private static int namedJarsSkipped = 0;
    private static int yarnsFailed = 0;
    // Yarn entries that were matched but not processed because the intermediary had no matching Minecraft version
    private static int yarnsSkippedNoMinecraftVersion = 0;
    // Yarn entries that were matched but not processed because processing the intermediary failed
    private static int yarnsSkippedDueToIntermediaryProcessingFailure = 0;
    // Yarn entries that reached the remapping step (either created or skipped because named jar existed)
    private static int yarnsAttemptedRemap = 0;
    private static int intermediariesFailed = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Ensure working directories exists
        Files.createDirectories(Paths.get(WORK_DIR));
        Files.createDirectories(Paths.get(LIBS_DIR));
        Files.createDirectories(Paths.get(VERSIONS_DIR));

        intermediaries = getUrl(FABRIC_INTERMEDIARY_URL, FabricIntermediaryData[].class);
        yarns = getUrl(FABRIC_YARN_URL, FabricYarnData[].class);
        versionManifest = getUrl(MOJANG_VERSION_MANIFEST_URL, MojangVersionManifestData.class);

        for (FabricIntermediaryData intermediary : intermediaries) {
            FabricYarnData[] matchingYarns = java.util.Arrays.stream(yarns)
                    .filter(yarn -> yarn.gameVersion.equals(intermediary.version))
                    .toArray(FabricYarnData[]::new);

            intermediariesProcessed++;
            yarnsMatchedTotal += matchingYarns.length;

            try {
                processIntermediary(intermediary, matchingYarns);
            } catch (NoSuchElementException e) {
                System.out.println("  No matching Minecraft version found for intermediary " + intermediary.version);
                // Count all matching yarns as skipped because we couldn't find the Mojang version to process them
                yarnsSkippedNoMinecraftVersion += matchingYarns.length;
            } catch (Exception e) {
                System.out.println("  Error processing intermediary " + intermediary.version);
                e.printStackTrace();
                // Count all matching yarns as skipped because the intermediary processing failed
                yarnsSkippedDueToIntermediaryProcessingFailure += matchingYarns.length;
                intermediariesFailed++;
            }
        }

        // Print quick summary
        System.out.println();
        System.out.println("Summary:");
        System.out.println("  Intermediary entries processed: " + intermediariesProcessed);
        System.out.println("  Intermediaries downloaded: " + intermediariesDownloaded);
        System.out.println("  Minecraft jars downloaded: " + minecraftJarsDownloaded);
        System.out.println("  Library jars downloaded: " + libsDownloaded);
        System.out.println("  Yarn mappings matched total: " + yarnsMatchedTotal);
        System.out.println("  Yarn jars already present: " + yarnsAlreadyPresent);
        System.out.println("  Yarn jars downloaded: " + yarnsDownloaded);
        System.out.println("  Yarn remap attempts (reached remap step): " + yarnsAttemptedRemap);
        System.out.println("  Named jars created: " + namedJarsCreated);
        System.out.println("  Named jars skipped (already existed): " + namedJarsSkipped);
        System.out.println("  Yarn matched but not processed (missing Minecraft version): " + yarnsSkippedNoMinecraftVersion);
        System.out.println("  Yarn skipped because intermediary processing failed: " + yarnsSkippedDueToIntermediaryProcessingFailure);
        System.out.println("  Yarn processing failures: " + yarnsFailed);
        System.out.println("  Intermediary processing failures: " + intermediariesFailed);
    }

    private static <T> T getUrl(String url, Class<T> clazz) throws IOException, InterruptedException {
        HttpResponse<String> response;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching URL: " + url);
            throw e;
        }
        String responseBody = response.body();

        Gson gson = new Gson();
        return gson.fromJson(responseBody, clazz);
    }

    private static void downloadJar(String downloadUrl, Path outputPath) throws IOException, InterruptedException {
        // Create temp file in same directory to ensure atomic move works
        Path tempFile = Files.createTempFile(outputPath.getParent(), "download-", ".tmp");

        try {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(downloadUrl.replaceAll(" ", "%20")))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofFile(tempFile));
            } catch (IOException | InterruptedException e) {
                System.err.println("Error downloading jar from URL: " + downloadUrl);
                throw e;
            }

            // Atomically move temp file to final location
            Files.move(tempFile, outputPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Downloaded to: " + outputPath.toAbsolutePath());

        } catch (IOException | InterruptedException e) {
            // Clean up temp file on failure
            Files.deleteIfExists(tempFile);
            throw e;
        }
    }


    private static void remapJar(String from, String to, Path mappingsJar, Path clientJarPath, Path outputJarPath, List<Path> classpath) throws IOException {
        Path iTinyOnDisk = extractTinyToTemp(mappingsJar);
        TinyRemapper iRemapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(iTinyOnDisk, from, to))
                .renameInvalidLocals(true)
                .rebuildSourceFilenames(true)
                .fixPackageAccess(true)
                .ignoreConflicts(true)
                .resolveMissing(true)
                .build();

        try (OutputConsumerPath out = new OutputConsumerPath(outputJarPath)) {
            // Copy non-class resources, fix META-INF
            out.addNonClassFiles(clientJarPath, NonClassCopyMode.FIX_META_INF, iRemapper);

            // Feed inputs and classpath
            iRemapper.readInputs(clientJarPath);
            for (Path cp : classpath) {
                iRemapper.readClassPath(cp);
            }

            iRemapper.apply(out);
        } catch (IOException e) {
            // Delete output jar if it was partially created
            if (Files.exists(outputJarPath)) {
                Files.delete(outputJarPath);
            }
            throw new RuntimeException(e);
        } finally {
            iRemapper.finish();
        }
    }

    private static Path downloadLibrary(MojangVersionData.Library lib) throws Exception {
        // TODO: Handle classifiers
        if (lib.downloads.artifact == null) return null;

        // Check if file is already downloaded and cached
        Path libPath = Paths.get(LIBS_DIR, lib.downloads.artifact.sha1, lib.name.replaceAll(":", "-") + ".jar");
        String libUrl = lib.downloads.artifact.url;
        if (!Files.exists(libPath)) {
            System.out.println("  Downloading library: " + libUrl);
            // TODO: Currently we tolerate missing libs, but we should probably fail instead?
            try {
                Files.createDirectories(libPath.getParent());
                downloadJar(libUrl, libPath);
                libsDownloaded++;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            // TODO: Validate hash
            if (!createSha1(libPath.toFile()).equals(lib.downloads.artifact.sha1)) {
                System.out.println("  Library hash mismatch, re-downloading: " + lib.name);
                try {
                    Files.createDirectories(libPath.getParent());
                    downloadJar(libUrl, libPath);
                    libsDownloaded++;
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }

        return libPath;
    }

    private static void downloadYarn(FabricYarnData yarn,
                                     String versionDir,
                                     FabricIntermediaryData intermediary,
                                     Path intermediaryPath,
                                     Path clientJarPath,
                                     List<Path> classpath) throws Exception {
        String yarnDir = Paths.get(versionDir, "build." + yarn.build).toString();
        Files.createDirectories(Paths.get(yarnDir));

        String yarnVersion = yarn.version;
        String yarnGroupId = yarn.maven.split(":")[0];
        String yarnArtifactId = yarn.maven.split(":")[1];
        String yarnVersionPart = yarn.maven.split(":")[2];
        String downloadUrl = FABRIC_MAVEN_URL + yarnGroupId.replace('.', '/') + "/" +
                yarnArtifactId + "/" + yarnVersionPart + "/" +
                yarnArtifactId + "-" + yarnVersionPart + "-v2" + ".jar";
        System.out.println("  Matching Yarn version: " + yarnVersion);

        // Check if yarn is already downloaded
        Path yarnPath = Paths.get(yarnDir, "yarn.jar");
        if (!Files.exists(yarnPath)) {
            System.out.println("    Download URL: " + downloadUrl);
            try {
                downloadJar(downloadUrl, yarnPath);
                yarnsDownloaded++;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Yarn jar already present on disk
            yarnsAlreadyPresent++;
        }

        // If the yarn jar still doesn't exist after attempting download, record a failure and skip
        if (!Files.exists(yarnPath)) {
            System.err.println("    Yarn jar missing after download attempt for " + yarn.version + ", skipping this yarn.");
            yarnsFailed++;
            return;
        }

        // We're about to check/create the named jar (remapping step)
        yarnsAttemptedRemap++;

        // TODO: Use real temp dir
        Path intermediaryTempJar = Paths.get(WORK_DIR, "intermediary-" + intermediary.version + "-temp.jar");

        // Remap if needed
        if (!Files.exists(intermediaryTempJar)) {
            remapJar("official", "intermediary", intermediaryPath, clientJarPath, intermediaryTempJar, classpath);
            System.out.println("Remapped official->intermediary: " + intermediaryTempJar.toAbsolutePath());
        }

        Path outputJar = Paths.get(yarnDir, "named.jar");
        if (!Files.exists(outputJar)) {
            try {
                remapJar("intermediary", "named", yarnPath, intermediaryTempJar, outputJar, classpath);
                namedJarsCreated++;
                System.out.println("Remapped intermediary->named: " + outputJar.toAbsolutePath());
            } catch(Exception e) {
                System.err.println("    Error remapping Yarn " + yarn.version);
                e.printStackTrace();
                yarnsFailed++;
                throw e;
            } finally {
                // Cleanup intermediary temp jar
                if (Files.exists(intermediaryTempJar)) {
                    Files.delete(intermediaryTempJar);
                }
            }
        } else {
            System.out.println("    Named jar already exists, skipping: " + outputJar.toAbsolutePath());
            namedJarsSkipped++;
        }
    }

    // official -> intermediary -> multiple named jars
    private static void processIntermediary(FabricIntermediaryData intermediary, FabricYarnData[] yarns) throws Exception {
        System.out.println("Intermediary version: " + intermediary.version);

        String versionDir = Paths.get(VERSIONS_DIR, "yarn-" + intermediary.version).toString();
        try {
            Files.createDirectories(Paths.get(versionDir));
        } catch (IOException e) {
            System.err.println("  Error creating version directory: " + versionDir);
            throw new RuntimeException(e);
        }

        // Download intermediary
        String groupId = intermediary.maven.split(":")[0];
        String artifactId = intermediary.maven.split(":")[1];
        String version = intermediary.maven.split(":")[2];
        String intermediaryUrl = FABRIC_MAVEN_URL + groupId.replace('.', '/') + "/" +
                artifactId + "/" + version + "/" +
                artifactId + "-" + version + "-v2" + ".jar";

        // Check if intermediary is already downloaded
        Path intermediaryPath = Paths.get(versionDir, "intermediary.jar");
        if (!Files.exists(intermediaryPath)) {
             System.out.println("  Download URL: " + intermediaryUrl);
             downloadJar(intermediaryUrl, intermediaryPath);
             intermediariesDownloaded++;
        }

        // Download minecraft version jar
        String mojangUrl = Arrays.stream(versionManifest.versions)
                .filter(v -> v.id.equals(intermediary.version))
                .findFirst().orElseThrow().url;

        Path clientJarPath = Paths.get(versionDir, "client.jar");
        MojangVersionData versionData = getUrl(mojangUrl, MojangVersionData.class);
        String clientJarUrl = versionData.downloads.client.url;
        if (!Files.exists(clientJarPath)) {
            System.out.println("  Downloading Minecraft version jar: " + clientJarUrl);
            downloadJar(clientJarUrl, clientJarPath);
            minecraftJarsDownloaded++;
        } else {
            // TODO: Validate hash
        }

        // Download libs
        List<Path> classpath = new ArrayList<>();
        for (MojangVersionData.Library lib : versionData.libraries) {
            try {
                Path libPath = downloadLibrary(lib);
                if (libPath != null)
                    classpath.add(libPath);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (FabricYarnData yarn : yarns) {
            try {
                downloadYarn(yarn, versionDir, intermediary, intermediaryPath, clientJarPath, classpath);
            } catch (Exception e) {
                System.err.println("    Error processing Yarn " + yarn.version);
                e.printStackTrace();
                yarnsFailed++;
            }
        }

        // Create LICENSE
        Path licensePath = Paths.get(versionDir, "LICENSE");
        if (!Files.exists(licensePath)) {
            try {
                Files.writeString(licensePath, LICENSE, StandardOpenOption.CREATE);
            } catch (IOException e) {
                System.err.println("  Error writing LICENSE file.");
                throw new RuntimeException(e);
            }
        }

        // Update package.json
        Path packageJsonPath = Paths.get(versionDir, "package.json");
        String packageJsonContent = PACKAGE_TEMPLATE
                .replaceAll("<MINECRAFT_VERSION>", intermediary.version)
                .replaceAll("<DEFAULT_BUILD_NUMBER>", Arrays.stream(yarns)
                        .filter(yarn -> yarn.gameVersion.equals(intermediary.version))
                        .mapToInt(yarn -> yarn.build)
                        .max()
                        .orElse(0) + "");
        try {
            Files.writeString(packageJsonPath, packageJsonContent, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("  Error writing package.json file.");
            throw new RuntimeException(e);
        }

        // Update README.md
        Path readmePath = Paths.get(versionDir, "README.md");
        String readmeContent = README_TEMPLATE
                .replaceAll("<MINECRAFT_VERSION>", intermediary.version);
        try {
            Files.writeString(readmePath, readmeContent, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("  Error writing README.md file.");
            throw new RuntimeException(e);
        }
    }

    private static String createSha1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream fis = new FileInputStream(file)) {
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }
        }
        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static Path extractTinyToTemp(Path mappingsJar) throws IOException {
        URI uri = URI.create("jar:" + mappingsJar.toUri());
        try (FileSystem fs = FileSystems.newFileSystem(uri, Map.of("create", "false"))) {
            Path inner = fs.getPath("/mappings/mappings.tiny");
            if (!Files.exists(inner)) inner = fs.getPath("/mappings.tiny");

            Path tmp = Files.createTempFile("tiny-", ".tiny");
            Files.copy(inner, tmp, StandardCopyOption.REPLACE_EXISTING);
            tmp.toFile().deleteOnExit();
            return tmp;
        }
    }
}
