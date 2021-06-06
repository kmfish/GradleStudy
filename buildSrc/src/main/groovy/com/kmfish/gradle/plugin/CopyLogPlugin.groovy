package com.kmfish.gradle.plugin
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CopyLogPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        System.out.println("CopyLogPlugin apply");
        CopyLogTransform t = new CopyLogTransform(target)
        target.getExtensions().findByType(AppExtension.class)?.registerTransform(t)
    }
}