# Stage 1: Build the JAR file
FROM eclipse-temurin:17-jdk AS build

# Set the working directory
WORKDIR /app

# Copy only the Gradle wrapper and build files first
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Run Gradle dependencies resolution separately to cache the dependencies layer
RUN ./gradlew --no-daemon build

# Copy the source code after caching dependencies
COPY src/ src/

# Build the fat JAR
RUN ./gradlew buildFatJar

# Stage 2: Create the final image
FROM eclipse-temurin:17-jdk

# Set JAVA_HOME and PATH if necessary
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/oving_5-all.jar .

# Set the entry point
ENTRYPOINT ["java", "-jar", "oving_5-all.jar"]