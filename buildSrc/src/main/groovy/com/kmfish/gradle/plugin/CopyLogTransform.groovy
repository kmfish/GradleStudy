package com.kmfish.gradle.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class CopyLogTransform extends Transform {

    private Project project

    CopyLogTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "CopyLogTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        if (!transformInvocation.isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll();
        }

        transformInvocation.inputs.each {
            it.jarInputs.each {
                File dest = transformInvocation.getOutputProvider().getContentLocation(it.getName(), it.contentTypes, it.scopes, Format.JAR)
                if (transformInvocation.isIncremental()) {
                    switch (it.getStatus()) {
                        case Status.NOTCHANGED:
                            project.logger.quiet "NOTCHANGED input is: ${it} dest: ${dest}"
                            break;
                        case Status.CHANGED:
                        case Status.ADDED:
                            project.logger.quiet "${it.getStatus()} input is: ${it} dest: ${dest}"
                            FileUtils.copyFile(it.file, dest)
                            break;
                        case Status.REMOVED:
                            project.logger.quiet "REMOVED input is: ${it} dest: ${dest}"
                            FileUtils.delete(dest)
                            break;
                    }
                } else {
                    project.logger.quiet "no incremental: ${it}  dest: ${dest}"
                    FileUtils.copyFile(it.file, dest)
                }
            }
            it.directoryInputs.each {
                File inputDir = it.getFile()
                File outputDir = transformInvocation.outputProvider.getContentLocation(it.getName(), it.contentTypes, it.scopes, Format.DIRECTORY)
                if (transformInvocation.isIncremental()) {
                    for (Map.Entry<File, Status> changedInput : it.getChangedFiles().entrySet()) {
                        File inputFile = changedInput.getKey()
                        String relativePath = FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir)
                        File outputFile = new File(outputDir, relativePath)
                        switch (changedInput.getValue()) {
                            case Status.NOTCHANGED:
                                project.logger.quiet "NOTCHANGED input is: ${it} dest: ${outputFile}"
                                break;
                            case Status.REMOVED:
                                project.logger.quiet "REMOVED input is: ${it} dest: ${outputFile}"
                                FileUtils.deleteQuietly(outputFile)
                                break
                            case Status.ADDED:
                            case Status.CHANGED:
                                project.logger.quiet "${changedInput.getValue()} input is: ${it} dest: ${outputFile}"
                                if (inputFile.isFile() && !inputFile.isDirectory()) {
                                    FileUtils.deleteQuietly(outputFile)
                                    FileUtils.copyFile(inputFile, outputFile)
                                }
                        }
                    }
                } else {
                    project.logger.quiet "no incremental: ${it} dest: ${outputDir}"
                    if (outputDir.exists()) {
                        FileUtils.deleteDirectoryContents(outputDir)
                    }
                    FileUtils.copyDirectory(inputDir, outputDir)
                }
            }
        }
    }
}