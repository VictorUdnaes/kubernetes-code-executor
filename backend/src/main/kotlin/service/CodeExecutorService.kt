package com.example.service


class CodeExecutorService {
    companion object {
        fun executeCode(codeRequest: String) : String {
            val executor = KubernetesCodeExecutor()
            val result = executor.executeCode(codeRequest, "HelloWorld")
            println("Execution result: $result")
            return result
        }
    }
}