package com.kmfish.gradle.plugin.buildtracker

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

/**
 * record task build time
 */
class BuildTrackerPlugin implements Plugin<Project> {

    Map<String, Long> tasksBuildTime = new HashMap<>()

    @Override
    void apply(Project target) {
        println("BuildTrackerPlugin apply")

        //创建一个 Extension，配置输出结果
        final BuildTimeCostExtension timeCostExt = target.getExtensions().create(
                "taskExecTime", BuildTimeCostExtension)

//        target.afterEvaluate {
//            addRecordTimeForTask(target)
//            printBuildTime(target.gradle)
//        }

        // 配置阶段，此时timeCostExt还没更新到使用者配置的值
        println("BuildTrackerPlugin apply timeCostExt:" + timeCostExt)

        implWithExecutionListener(target)
        printBuildTime(target.gradle, timeCostExt)
    }

    // 一开始我这样写，但这个其实只是记录了apply plugin的这个project的task，不全。
//    private void addRecordTimeForTask(Project target) {
//        target.tasks.forEach { task ->
//            long startTime = 0
//            task.doFirst {
//                startTime = System.currentTimeMillis()
//            }
//            task.doLast {
//                long cost = System.currentTimeMillis() - startTime
//                tasksBuildTime.put(task.path, cost)
//            }
//        }
//    }

    private void printBuildTime(Gradle gradle, BuildTimeCostExtension timeCostExt) {
        gradle.buildFinished { r ->
            println("buildFinished " + r.failure)
            // 构建结束，此时timeCostExt已更新到用户配置后的状态了
            println("buildFinished timeCostExt:" + timeCostExt)
            println("------------task build cost:------------")
            long totalCost = 0

            if (timeCostExt.sorted) {
                // sort by time desc
                tasksBuildTime = tasksBuildTime.sort { a, b ->
                    b.value - a.value
                }
            }

            tasksBuildTime.each {
                if (it.value > timeCostExt.threshold) {
                    println("$it.value ms      $it.key")
                }
                totalCost += it.value
            }

            println("total build cost: $totalCost ms")
        }
    }

    private void implWithExecutionListener(Project project) {
        long startTime = 0
        //监听每个task的执行
        project.getGradle().addListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                //task开始执行之前搜集task的信息
                //记录开始时间
                startTime = System.currentTimeMillis()
//                timeInfo.path = task.getPath()
//                timeCostMap.put(task.getPath(), timeInfo)
//                taskPathList.add(task.getPath())
            }

            @Override
            void afterExecute(Task task, TaskState taskState) {
                //task执行完之后，记录结束时的时间
//                TaskExecTimeInfo timeInfo = timeCostMap.get(task.getPath())
                long end = System.currentTimeMillis()
                //计算该 task 的执行时长
                long total = end - startTime
                tasksBuildTime.put(task.path, total)
            }
        })

    }
}