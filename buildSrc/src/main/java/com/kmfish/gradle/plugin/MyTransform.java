package com.kmfish.gradle.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Time:2021/6/5 10:22 下午
 * Author: lijun3
 * Description:
 */
class MyTransform extends Transform {

    protected Project project;
    protected Logger logger;

    MyTransform(Project project) {
        this.project = project;
        this.logger = project.getLogger();
    }

    @Override
    public String getName() {
        return "MyTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

//    @Override
//    public Set<? super QualifiedContent.Scope> getReferencedScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT;
//    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation invocation) throws TransformException, InterruptedException, IOException {
        logger.quiet("Begin {}, incremental = {}", getName(), invocation.isIncremental());
        Collection<TransformInput> inputs = invocation.getInputs();
        for (TransformInput in: inputs) {
            Collection<DirectoryInput> dIns = in.getDirectoryInputs();
            for (DirectoryInput dIn: dIns) {
                File inputDir = dIn.getFile();
                File outputDir = invocation.getOutputProvider().getContentLocation(
                        dIn.getName(), dIn.getContentTypes(), dIn.getScopes(), Format.DIRECTORY);

                logger.info("DirectoryInput:" + dIn.toString());
                logger.info("DirectoryInput outputDir:" + outputDir.toString());
                FileUtils.copyDirectory(inputDir, outputDir);
            }
            Collection<JarInput> jarInputs = in.getJarInputs();
            for (JarInput jarInput: jarInputs) {
                logger.info("JarInput:" + jarInput.toString());
            }

            logger.info("TransformInput:" + in.toString());
        }
    }
}
