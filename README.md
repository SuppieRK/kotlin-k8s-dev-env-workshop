## Workshop: Through hardships to the cloud

### Set up Skaffold

- Update version in `build.gradle.kts` to `1.0.2`

- Create `skaffold.yaml` at project root directory with following content (you have to replace `PROJECT_ID` with your actual project ID in file)

```yaml
apiVersion: skaffold/v2alpha4
kind: Config
build:
  artifacts:
    - image: gcr.io/PROJECT_ID/demo
      jib: {}
deploy:
  helm:
    releases:
      - name: demo
        chartPath: demo
        values:
          image.repository: gcr.io/PROJECT_ID/demo
``` 

- Modify value of `image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"` in `demo/templates/deployment.yaml` in a following way

```yaml
          {{- if contains ":" .Values.image.repository }}
          image: {{ .Values.image.repository | quote }}
          {{- else }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          {{- end }}
```

**Note**: indentation is important! Keep everything indented at the same level as initial `image` key. 

## Finally - fun time!

What we actually have now is a fully setup environment that can launch your service with live reload ability in the cloud so that you can open even more Google Chrome tabs simultaneously :)

Kick off everything by executing in terminal

```shell script
skaffold dev --port-forward
```

and test your deployment as usual by accessing [http://localhost:8080/hello](http://localhost:8080/hello) and [http://localhost:8080/hello?subject=Kotlin](http://localhost:8080/hello?subject=Kotlin)

After which try to add your own endpoint, hit `Ctrl+S` or `Command+S` and see how image gets rebuilt and redeployed to your Google Kubernetes Engine cluster.

### Clean up

After you've finished this tutorial, clean up the resources you created on Google Cloud Platform so you won't be billed for them going forward. To clean, either delete your Kubernetes Engine resources, or delete the entire project.

#### Deleting Kubernetes Engine resources

To delete your app from Kubernetes Engine, you must remove both the load balancer and the Kubernetes Engine cluster.

- If you followed the tutorial precisely - you should have no deployments. Skaffold automatically calls `helm delete --purge` for your deployment when you hit `Ctrl+C`

- Delete the cluster, which deletes the resources used by the cluster, including virtual machines, disks, and network resources:
```shell script
gcloud container clusters delete demo-cluster
```

- Delete your Docker image
```shell script
gcloud container images delete gcr.io/${PROJECT_ID}/demo --force-delete-tags
```

### Deleting the project

Alternately, you can delete the project in its entirety. To do so using the gcloud tool, run:
```shell script
gcloud projects delete ${PROJECT_ID}
```
where `${PROJECT_ID}` is your Google Cloud Platform project ID.

**Warning**: Deleting a project has the following consequences:

If you used an existing project, you'll also delete any other work you've done in the project. You can't reuse the project ID of a deleted project. If you created a custom project ID that you plan to use in the future, you should delete the resources inside the project instead. This ensures that URLs that use the project ID, such as an appspot.com URL, remain available.

### Next steps

If you want to procure a static IP address and connect your domain name, you might find [this tutorial](https://cloud.google.com/kubernetes-engine/docs/tutorials/configuring-domain-name-static-ip) helpful.

See the [Kubernetes Engine documentation](https://cloud.google.com/kubernetes-engine/docs/) for more information on managing Kubernetes Engine clusters.

See the [Kubernetes documentation](https://kubernetes.io/docs/home/) for more information on managing your application deployment using Kubernetes.