## Workshop: Through hardships to the cloud

### What you are going to learn

In this workshop we will learn how to build Kubernetes-ready developer environment using simple Spring Boot application as a source.

We will incrementally move through the whole process of making simple app a full-blown, Kubernetes powered dream of all DevOps engineers.

You are going to:

- Create a Spring Boot application
- `Docker`ize app in a neat fashion
- Write simple `Helm` chart to enable automated deployment
- Leverage `Skaffold` tool to abstract your application from your machine
- Deploy your app to Google Cloud Platform


### Before you begin

Before running this tutorial, you must set up a Google Cloud Platform project, and you need to have Docker and the Google Cloud SDK installed.

Create a project that will host your Spring Boot application. You can also reuse an existing project.

1. Use the Google Cloud Platform Console to create a new Cloud Platform project. Remember the project ID - you will need it later. Later commands in this tutorial will use `${PROJECT_ID}` as a substitution, so you might consider setting the `PROJECT_ID` environment variable in your shell via `export PROJECT_ID=your_project_id`.
2. Enable billing for your project.
3. Go to the API Library in the Cloud Console. Use it to enable the following APIs:
    - Kubernetes Engine API
    - Google Container Engine API

Perform the installations:

- Install JDK 8 or higher if you do not already have it.
- Install Docker if you do not already have it. Find instructions on the [Docker website](https://docs.docker.com/install/).
- Install the Google Cloud SDK if you do not already have it. Make sure you initialize the SDK and set the default project to the new project you created.
- Install the Kubernetes component of the Google Cloud SDK:

```shell script
gcloud components install kubectl
```

- Initialize `gcloud` as Docker credential helper to be able to push images to Google Container Registry

```shell script
gcloud auth configure-docker
```

- Install Helm. Find instructions on the [Helm website](https://helm.sh/docs/using_helm/#installing-helm). Skip installation of Tiller, it will be covered during the course of workshop.

**NOTE**: We are going to use Helm of version 2, because newer Tiller-less Helm of version 3 is not supported yet by Skaffold. [See for more details](https://github.com/GoogleContainerTools/skaffold/issues/2142).

For Mac OS and `brew` you can use `brew install helm@2`

- Install Skaffold. Find instructions on the [Skaffold website](https://skaffold.dev/docs/getting-started/#installing-skaffold).

### Creating a new app and running it locally

In this section, you will create a new Spring Boot app and make sure it runs. If you already have an app to deploy, you can use it instead.

- Use [start.spring.io](https://start.spring.io/) to generate a Spring Boot application using Kotlin as the language, Gradle as the build system. Alternatively, you can clone this Git repository.
    - Don't forget to include Spring Web dependency during the process
    - (_Optional, in case of website project generation_) Download the generated project and save it to a local folder.
- Open the resulting project in your favourite IDE or editor and create a new source file named `HelloController.kt` with the following contents:

```kotlin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/hello")
    fun sayHello(@RequestParam("subject", required = false) subject: String?) = "Hello, ${subject ?: "World"}!"
}
```

The package should match that of your group and artifact name.

- Make sure you have the right dependencies in your Gradle file to import `RestController` and `GetMapping` annotations:

```groovy
compile("org.springframework.boot:spring-boot-starter-web")
```

- Run the application from the command line using Gradle:

```shell script
./gradlew bootRun
```

**Note**: The `./gradlew bootRun` is a quick way to build and run the application. Later on when creating the Docker image, you'll need to first build the app using the Gradle build task and then run it.

- Open the browser and make sure your get a valid response when accessing [http://localhost:8080/hello](http://localhost:8080/hello). The result should be:

```
Hello, World!
```

- Open the browser and make sure your get a valid response when accessing [http://localhost:8080/hello?subject=Kotlin](http://localhost:8080/hello?subject=Kotlin). The result should be:

```
Hello, Kotlin!
```

### Next step

Switch to `01-dockerizing` branch
