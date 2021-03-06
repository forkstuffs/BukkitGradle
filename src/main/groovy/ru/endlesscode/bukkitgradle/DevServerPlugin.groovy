package ru.endlesscode.bukkitgradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.server.ServerCore
import ru.endlesscode.bukkitgradle.server.idea.IdeaRunConfigurationBuilder
import ru.endlesscode.bukkitgradle.server.task.PrepareServer
import ru.endlesscode.bukkitgradle.server.task.RunServer

import java.nio.file.Files
import java.nio.file.Path

class DevServerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ServerCore serverCore = new ServerCore(project)
        project.task('runServer', type: RunServer, dependsOn: 'prepareServer') {
            group = BukkitGradlePlugin.GROUP
            description = 'Run dev server'
            core = serverCore
        }

        PrepareServer prepareServer = project.task(
                'prepareServer',
                type: PrepareServer,
                dependsOn: ['build', 'copyServerCore']
        ) {
            group = BukkitGradlePlugin.GROUP
            description = 'Prepare server ro run. Configure server and copy compiled plugin to plugins dir'
            core = serverCore
        } as PrepareServer

        Path runConfigurationsDir = project.rootProject.projectDir.toPath().resolve(".idea/runConfigurations")
        project.task('buildIdeaRun', dependsOn: 'prepareServer') {
            group = BukkitGradlePlugin.GROUP
            description = 'Configure IDEA server run configuration'
        }.doLast {
            if (Files.notExists(runConfigurationsDir.parent)) {
                throw new GradleException("This task only for IntelliJ IDEA.")
            }

            Files.createDirectories(runConfigurationsDir)
            def serverDir = prepareServer.serverDir.toRealPath()
            IdeaRunConfigurationBuilder.build(runConfigurationsDir, serverDir, prepareServer.run)
        }

        project.afterEvaluate {
            def serverDir = prepareServer.serverDir.toRealPath()
            IdeaRunConfigurationBuilder.build(runConfigurationsDir, serverDir, prepareServer.run)
        }
    }
}
