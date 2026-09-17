/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package xyz.kyngs.libby.plugin;

import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator;
import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator;
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class ShadowPluginIntegration {
    protected static List<LibbyTask.Relocation> extractShadowJarRelocations(Project project) {
        var task = project.getTasks().withType(ShadowJar.class).named("shadowJar").get();

        var relocations = new ArrayList<LibbyTask.Relocation>();

        for (Relocator relocator : task.getRelocators()) {
            if (relocator instanceof SimpleRelocator simpleRelocator) {
                relocations.add(new LibbyTask.Relocation(simpleRelocator.getPattern(), simpleRelocator.getShadedPattern()));
            }
        }

        return relocations;
    }
}
