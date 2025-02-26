package com.example.service

import io.fabric8.kubernetes.api.model.*
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class KubernetesCodeExecutor(
    private val namespace: String = "default"
) {
    private val log: Logger = LoggerFactory.getLogger(KubernetesCodeExecutor::class.java)
    private val client: KubernetesClient = KubernetesClientBuilder().build()

    fun executeCode(code: String, className: String): String {
        val timestamp = System.currentTimeMillis()
        val podName = "code-execution-$timestamp"

        return try {
            log.info("Creating execution pod: $podName")
            createExecutionPod(podName, code, className)
            waitForPodCompletion(podName)

            val result  = getPodLogs(podName)
            result
        } finally {
            cleanup(podName)
        }
    }

    private fun createExecutionPod(podName: String, code: String, className: String): Pod {
        return client.pods().inNamespace(namespace).resource(
            PodBuilder()
                .withNewMetadata().withName(podName).addToLabels("role", "executor").endMetadata()
                .withNewSpec()
                .withRestartPolicy("Never")
                .addNewContainer()
                .withName("executor")
                .withImage("eclipse-temurin:17-jdk")
                .withCommand("sh", "-c", """
                    set -e
                    echo "pod $podName started"
                    echo "$code" > /tmp/$className.java
                    echo "javac /tmp/$className.java"
                    javac /tmp/$className.java
                    echo "java -cp /tmp $className"
                    java -cp /tmp $className
                    echo "Execution completed, pod $podName shutting down"
                """.trimIndent())
                .withVolumeMounts(
                    VolumeMountBuilder()
                        .withName("java-code")
                        .withMountPath("/tmp")
                        .build()
                )
                .endContainer()
                .addNewVolume()
                .withName("java-code")
                .withNewEmptyDir().endEmptyDir()
                .endVolume()
                .endSpec()
                .build()
        ).create()
    }

    private fun waitForPodCompletion(podName: String): Boolean {
        var podStatus = ""
        val maxAttempts = 30
        var attempts = 0

        while (podStatus != "Succeeded" && podStatus != "Failed" && attempts < maxAttempts) {
            try {
                val pod = client.pods().inNamespace(namespace).withName(podName).get()
                if (pod == null) {
                    log.warn("Pod $podName not found")
                    return false
                }

                podStatus = pod.status.phase
                log.debug("Pod status: $podStatus (attempt $attempts/$maxAttempts)")

                if (podStatus != "Succeeded" && podStatus != "Failed") {
                    Thread.sleep(1000)
                    attempts++
                }
            } catch (e: Exception) {
                log.warn("Error checking pod status: ${e.message}")
                attempts++
                Thread.sleep(1000)
            }
        }

        val completed = podStatus == "Succeeded" || podStatus == "Failed"
        if (!completed) {
            log.warn("Pod execution timeout after $maxAttempts seconds")
        } else {
            log.info("Pod execution completed with status: $podStatus")
        }

        return completed
    }

    private fun getPodLogs(podName: String): String {
        return try {
            // Retry a few times if logs aren't available immediately
            var logs = ""
            var attempts = 0

            while (logs.isBlank() && attempts < 5) {
                logs = client.pods()
                    .inNamespace(namespace)
                    .withName(podName)
                    .getLog()

                if (logs.isBlank()) {
                    log.debug("Logs empty, retrying... (attempt ${attempts+1}/5)")
                    attempts++
                    Thread.sleep(1000)
                }
            }

            log.info("Pod logs retrieved, size: ${logs.length} characters")
            if (logs.isBlank()) {
                "No output captured from execution"
            } else {
                logs
            }
        } catch (e: Exception) {
            log.error("Error getting pod logs: ", e)
            "Failed to retrieve logs: ${e.message}"
        }
    }

    private fun cleanup(podName: String) {
        try {
            client.pods().inNamespace(namespace).withName(podName).delete()
            log.info("Deleted pod: $podName")
        } catch (e: Exception) {
            log.error("Error cleaning up pod $podName: ${e.message}")
        }
    }
}