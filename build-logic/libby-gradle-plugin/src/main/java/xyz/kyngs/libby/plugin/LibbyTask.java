/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package xyz.kyngs.libby.plugin;

import com.grack.nanojson.JsonWriter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javax.inject.Inject;

public class LibbyTask extends DefaultTask {

    private final Configuration customScope;
    private final Project project;

    @Inject
    public LibbyTask(Configuration customScope, Project project) {
        this.customScope = customScope;
        this.project = project;
    }

    @TaskAction
    public void run() throws NoSuchAlgorithmException {
        var main = project
                .getExtensions()
                .getByType(JavaPluginExtension.class)
                .getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        var excludedDependencies = project.getExtensions().getByType(LibbyExtension.class).getExcludedDependencies();
        var noChecksumDependencies = project.getExtensions().getByType(LibbyExtension.class).getNoChecksumDependencies();

        var output = new File(project.getBuildDir().getPath() + "/libby", "libby.json");
        output.getParentFile().mkdirs();

        main.getResources().srcDir(output.getParentFile());

        var writer = JsonWriter.string();

        writer.object();
        writer.value("version", 0);

        writer.array("libraries");

        var md = MessageDigest.getInstance("SHA-256");

        for (ResolvedArtifact artifact : customScope.getResolvedConfiguration().getResolvedArtifacts()) {
            var id = artifact.getModuleVersion().getId();

            if (excludedDependencies.stream().anyMatch(id.toString()::matches)) continue;

            writer.object();
            writer.value("group", id.getGroup().replace(".", "{}"));
            writer.value("name", id.getName());
            writer.value("version", id.getVersion());
            if (artifact.getClassifier() != null) writer.value("classifier", artifact.getClassifier());
            if (!artifact.getType().equals("jar") || noChecksumDependencies.stream().anyMatch(id.toString()::matches)) {
                writer.end();
                continue;
            }
            var jar = artifact.getFile();

            try (var fis = new java.io.FileInputStream(jar)) {
                var bytes = fis.readAllBytes();
                var hash = md.digest(bytes);
                writer.value("checksum", Base64.getEncoder().encodeToString(hash));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            writer.end();
        }

        // End libraries array
        writer.end();

        writer.array("repositories");

        for (var repository : project.getRepositories()) {
            if (repository instanceof MavenArtifactRepository maven) {
                var path = maven.getUrl().toString();
                if (!path.startsWith("http")) continue;
                writer.value(path);
            }
        }

        // End repositories array
        writer.end();

        var relocations = extractShadowJarRelocations();

        if (relocations != null) {
            writer.object("relocations");
            for (var relocation : relocations) {
                writer.value(relocation.from.replace(".", "{}"), relocation.to.replace(".", "{}"));
            }
            // End relocations object
            writer.end();
        }

        writer.end();
        try {
            output.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fileWriter = new FileWriter(output)) {
            fileWriter.write(writer.done());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write custom scope dependencies to JSON file", e);
        }
    }

    private List<Relocation> extractShadowJarRelocations() {
        if (project.getTasks().findByName("shadowJar") == null) return null;
        return ShadowPluginIntegration.extractShadowJarRelocations(project); //Move to a separate class to avoid class loading issues
    }

    protected record Relocation(String from, String to) {
    }
}
