package com.kmfish.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Time:2021/6/5 2:30 下午
 * Author: lijun3
 * Description:
 */
class MyTask extends DefaultTask {

    public MyTask() {
        System.out.println("MyTask construct");
    }

    @TaskAction
    void action() {
        System.out.println("MyTask action");
    }

}
