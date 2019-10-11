## Workshop: Through hardships to the cloud

### Creating Kubernetes cluster

Kubernetes Engine lets you create Kubernetes clusters to host your application. These are clusters of VMs in the cloud, managed by a Kubernetes server.

- Choose a cluster name. For the rest of these instructions, I'll assume that name is `demo-cluster`.

- Create the cluster.

```shell script
gcloud container clusters create demo-cluster --num-nodes=2
```

This command creates a cluster of two machines. You can choose a different size, but two is a good starting point.

It might take several minutes for the cluster to be created. You can check the [Cloud Console](http://cloud.google.com/console), under the Kubernetes Engine section, to see that your cluster is running. You will also be able to see the individual running VMs under the Compute Engine section. Note that once the cluster is running, you will be charged for the VM usage.

- Configure the gcloud command-line tool to use your cluster by default, so you don't have to specify it every time for the remaining gcloud commands.

```shell script
gcloud config set container/cluster demo-cluster
```

Replace the name if you named your cluster differently.

### Preparing Helm

Helm helps you manage Kubernetes applications - Helm Charts help you define, install and upgrade event the most complex Kubernetes applications.

- Install Tiller - Helm server-side component

```shell script
kubectl create serviceaccount -n kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
```

- Initialize Helm

```shell script
helm init --service-account tiller
```

### Creating Helm Chart for application

- Initialize Helm chart by executing

```shell script
helm create demo
```

#### Modify Helm Chart

- `deployment.yaml`
    - Change value of `spec.template.spec.containers.ports.containerPort` from `80` to `{{ .Values.service.port }}`
    - Remove `spec.template.spec.containers.livenessProbe`
    - Remove `spec.template.spec.containers.readinessProbe`
- `service.yaml`
    - Change value of `spec.ports.targetPort` from `http` to `{{ .Values.service.port }}`
- `values.yaml`
    - Change value of `image.tag` from `stable` to `latest`
    - Change value of `image.pullPolicy` from `IfNotPresent` to `Always`
    - Change value of `service.type` from `ClusterIP` to `NodePort`
    - Change value of `service.port` from `80` to `8080`
    
#### Test Helm Chart

- Helm provides you linter to use

```shell script
helm lint ./demo
```

- Helm also allows you to render the template locally

```shell script
helm template ./demo
```

- Helm is also able to do a dry run and test connection to Kubernetes cluster

```shell script
helm install --name demo --dry-run --debug ./demo
```

### Build Docker image and deploy it to Kubernetes cluster using Helm chart

- Update version in `build.gradle.kts` to `1.0.1`

- Build image using JIB, which will automatically push image to Google Container Registry

```shell script
./gradlew jib --image=gcr.io/${PROJECT_ID}/demo
```

- Deploy image to Kubernetes cluster via Helm chart

```shell script
helm install --name demo ./demo --set image.repository=gcr.io/${PROJECT_ID}/demo
```

- We haven't used any load balancer to expose our service to public web, so to test it we need to do port forwarding (**Note**: Ctrl+C after executing this command will stop port forwarding)

```shell script
kubectl port-forward $(kubectl get pod --selector="app.kubernetes.io/instance=demo,app.kubernetes.io/name=demo" --output jsonpath='{.items[0].metadata.name}') 8080:8080
```

- Open the browser and make sure your get a valid response when accessing [http://localhost:8080/hello](http://localhost:8080/hello). The result should be:

```
Hello, World!
```

- Open the browser and make sure your get a valid response when accessing [http://localhost:8080/hello?subject=Kotlin](http://localhost:8080/hello?subject=Kotlin). The result should be:

```
Hello, Kotlin!
```

### Clean up Helm deployment for further steps

To completely delete Helm deployment, use

```shell script
helm del --purge demo
```

### Next step

Switch to `03-skaffold` branch
