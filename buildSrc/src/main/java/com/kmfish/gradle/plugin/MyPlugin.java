package com.kmfish.gradle.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Time:2021/6/5 1:03 下午
 * Author: lijun3
 * Description:
 */
public class MyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("MyPlugin apply");
        MyTransform transform = new MyTransform(project);
        project.getExtensions().findByType(AppExtension.class).registerTransform(transform);
    }

}
