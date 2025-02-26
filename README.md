# Kubernetes Code Executor
The application executes Java code in an isolated Kubernetes Pod. Every time code is sent to the Ktor server in the Kubernetes cluster it creates a new pod, compiles the java code and executes it. The server pod extracts the logs from the pod and sends it back to the web site. This allows the user great freedom to execute potentially harmful code without the risk of ruining the host system.


## How to run project
It's recommended to use Minikube for local development, this limits the cluster to a single worker node but works fine for the purposes of this application.

#### Minikube Docs: https://kubernetes.io/docs/tutorials/hello-minikube/

### 1.
After starting Minikube you can run the redeploy.sh script: (Note: there is some trouble with the way the script builds new images so each time the script is run a new image is created. just delete them for now.)
```bash
/.redeploy.sh
```

### 2. Start the service
execute the service with this command:
```bash
minikube service ktor-server-service
```
Use the URL printed by this command to access the web site, it will look something like http://localhost:30442/run-code

