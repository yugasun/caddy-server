# Start with a base image containing Java runtime (JDK 17)
FROM eclipse-temurin:17.0.9_9-jdk

# Add Maintainer Info
LABEL maintainer="yuga.sun.bj@gmail.com"

# Install curl and unzip
RUN apt-get update && apt-get install -y curl unzip

WORKDIR /app

ADD . /app

# Download and install Maven
ENV MAVEN_VERSION 3.9.5
RUN curl -L https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -o maven.tar.gz
RUN tar xzvf maven.tar.gz -C /opt && ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn && rm maven.tar.gz

# install xcaddy
#RUN apt install -y debian-keyring debian-archive-keyring apt-transport-https curl
#RUN curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg && \
#  curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | tee /etc/apt/sources.list.d/caddy-stable.list && \
#  apt-get update && apt install caddy && apt-get clean && rm -rf /var/lib/apt/lists/* && \
#  caddy version

# Make port 8080 available to the world outside this container
EXPOSE 8080

RUN mvn clean package -DskipTests

# The application's jar file
ARG JAR_FILE=target/caddy-server-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
RUN cp ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar", "--sprint.config.location=/app/config/application.yml"]