package com.kmfish.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

/**
 * Time:2021/6/5 2:30 下午
 * Author: lijun3
 * Description:
 */
class MyTask extends DefaultTask {

    @Inject
    public MyTask() {
        System.out.println("MyTask construct");
    }

    @TaskAction
    void action() {
        System.out.println("MyTask action");
    }

}
