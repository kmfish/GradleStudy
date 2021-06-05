package com.kmfish.gradle.plugin;

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
        System.out.println("123456");
    }
}
