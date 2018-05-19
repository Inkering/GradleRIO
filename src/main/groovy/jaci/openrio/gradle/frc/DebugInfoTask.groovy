package jaci.openrio.gradle.frc

import com.google.gson.GsonBuilder
import groovy.transform.CompileStatic
import jaci.gradle.deploy.artifact.ArtifactBase
import jaci.gradle.deploy.artifact.ArtifactsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DebugInfoTask extends DefaultTask {

    @TaskAction
    void writeDebugInfo() {
        def cfg = []
        FRCPlugin.deployExtension(project).artifacts.all { ArtifactBase art ->
            if (art instanceof FRCJavaArtifact) {
                art.targets.all { String target ->
                    cfg << [
                        artifact: art.name,
                        target: target,
                        debugfile: "${art.name}_${target}.debugconfig".toString(),
                        language: "java"
                    ]
                }
            } else if (art instanceof FRCNativeArtifact) {
                art.targets.all { String target ->
                    cfg << [
                        artifact: art.name,
                        target: target,
                        component: (art as FRCNativeArtifact).component,
                        debugfile: "${art.name}_${target}.debugconfig".toString(),
                        language: "cpp"
                    ]
                }
            }
        }

        def file = new File(project.buildDir, "debug/debuginfo.json")
        file.parentFile.mkdirs()

        def gbuilder = new GsonBuilder()
        gbuilder.setPrettyPrinting()
        file.text = gbuilder.create().toJson(cfg)
    }

}