package ru.endlesscode.bukkitgradle.server

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class PrepareServer extends DefaultTask {
    @Input
    ServerCore core

    private Path serverDir

    @TaskAction
    void prepareServer() {
        this.serverDir = core.serverDir

        resolveEula()
        copyPluginToServerDir()
    }

    void resolveEula() {
        Path eulaFile = serverDir.resolve("eula.txt")

        boolean eula = project.bukkit.run.eula
        eulaFile.text = "eula=$eula"
    }

    void copyPluginToServerDir() {
        String jarName = project.jar.archiveName
        Path jar = project.jar.archivePath.toPath()
        Files.copy(jar, serverDir.resolve(jarName), StandardCopyOption.REPLACE_EXISTING)
    }
}
