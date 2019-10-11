## Workshop: Through hardships to the cloud

### Dockerizing your application

The next step is to produce a Docker image that builds and runs your application in a Docker container.

#### Creating Docker image

To create Docker image, we could create `Dockerfile` file with correct set of instructions but there is another way - [Google JIB plugin](https://github.com/GoogleContainerTools/jib) for your favourite build tool.

**Note**: further steps require setup of `gcloud` and `docker` CLI tools. If you missed it - return back to `master` branch for instructions.

- Update service version from `0.0.1-SNAPSHOT` to `1.0.0`

- Add JIB Gradle plugin to our project `build.gradle.kts`

```kotlin
plugins {
    id("com.google.cloud.tools.jib") version "2.2.0"
}
```

- Modify JIB output image configuration in `build.gradle.kts`

```kotlin
jib {
    to {
        // Tagging our image as recommended by Google
        // https://cloud.google.com/solutions/best-practices-for-building-containers#tagging_using_semantic_versioning
        tags = setOf(
                // Specific X.Y.Z version
                project.version.toString(),
                // Latest patch release of the X.Y minor branch
                project.version.toString().substringBeforeLast("."),
                // Latest patch release of the latest minor release of the X major branch
                project.version.toString().substringBefore("."),
                // Most recent (possibly stable) image
                "latest"
        )
    }
    container {
        ports = listOf("8080")
        // Good list of default flags intended for Java 8 (>= 8u191) containers
        jvmFlags = listOf(
                "-server",
                "-Djava.awt.headless=true",
                "-XX:InitialRAMFraction=2",
                "-XX:MinRAMFraction=2",
                "-XX:MaxRAMFraction=2",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=100",
                "-XX:+UseStringDeduplication"
        )
    }
}
```

- Create a file called `.dockerignore` in your project directory and copy the following content into it. Alternately, you can inspect `.dockerignore` in this Git project to study and customize.

```docker
.gradle
build
out
```

#### Test the Dockerfile

- Build the image

```shell script
./gradlew jibDockerBuild --image demo
```

- Check that your image was tagged correctly by inspecting the output of `docker images`

```shell script
demo    1
demo    1.0
demo    1.0.0
demo    latest
```

- Run your image

```shell script
docker run -it --rm -p 8080:8080 demo
```

- Open the browser and make sure your get a valid response when accessing [http://localhost:8080/hello](http://localhost:8080/hello). The result should be:

```
Hello, World!
```

- Open the browser and make sure your get a valid response when accessing [http://localhost:8080/hello?subject=Kotlin](http://localhost:8080/hello?subject=Kotlin). The result should be:

```
Hello, Kotlin!
```

Now you are ready to prepare your application for real deployment!

### Next step

Switch to `02-helm` branch
