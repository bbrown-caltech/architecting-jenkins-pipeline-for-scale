# architecting-jenkins-pipeline-for-scale
CalTech - SimpliLearn - CICD Project 02: Architecting Jenkins Pipeline for Scale

<br />

## Source Code

<br />

### project02/src/main/java/com/simplilearn/cicd/project02/Project02Application.java

```java
package com.simplilearn.cicd.project02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Project02Application {

	public static void main(String[] args) {
		SpringApplication.run(Project02Application.class, args);
	}

}

```

<br />

### project02/src/main/java/com/simplilearn/cicd/project02/controllers/DiagnosticsController.java

```java
package com.simplilearn.cicd.project02;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DiagnosticsController {
    
    @GetMapping(value = "/healthz")
    public Mono<ResponseEntity<String>> healthz() {
        return Mono.just(ResponseEntity.ok().body("The test service is running."));
    }
}

```

<br />

### project02/src/test/java/com/simplilearn/cicd/project02/controllers/Project02ApplicationTests.java

```java
package com.simplilearn.cicd.project02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Project02ApplicationTests {
  
    @Autowired
    private TestRestTemplate restTemplate;
  
	@Test
	void testHealthcheck() {
        URI targetUrl = UriComponentsBuilder.fromUriString("/healthz")
            .build().toUri();
        
        String message = this.restTemplate.getForObject(targetUrl, String.class);
        
        assertEquals(message, "The test service is running.");
	}

}

```

<br />

### project02/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.5.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.simplilearn.cicd</groupId>
	<artifactId>project02</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>project02</name>
	<description>CalTech - SimpliLearn - CICD Project 02: Architecting Jenkins Pipeline for Scale</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-rest</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.session</groupId>
			<artifactId>spring-session-core</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.wso2.orbit.io.projectreactor/reactor-core -->
		<dependency>
			<groupId>org.wso2.orbit.io.projectreactor</groupId>
			<artifactId>reactor-core</artifactId>
			<version>3.3.9.wso2v1</version>
		</dependency>
		
		<!-- Jupiter API for writing tests -->
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-surefire-provider</artifactId>
            <version>1.1.0</version>
        </dependency>

		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter-engine</artifactId>
			<version>5.1.0</version>
			<scope>test</scope>
		</dependency>
		
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.19.1</version>
				<dependencies>
					<dependency>
						<groupId>org.junit.platform</groupId>
						<artifactId>junit-platform-surefire-provider</artifactId>
						<version>1.1.0</version>
					</dependency>
					<dependency>
						<groupId>org.junit.jupiter</groupId>
						<artifactId>junit-jupiter-engine</artifactId>
						<version>5.1.0</version>
					</dependency>
				</dependencies>
			</plugin>
		</plugins>
	</build>

</project>

```

<br />

### docker/docker-compose.yml

```yaml
version: '3.5'
services:

##########################################################################
#  CORE SERVICES
##########################################################################
  nginx:
    container_name: nginx
    image: ${NGINX}
    restart: unless-stopped
    networks:
      - simplilearn
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./conf/:/etc/nginx/:ro
      - ./certs:/etc/nginx/certs

##########################################################################
#  CI/CD Application(s) and Service(s)
##########################################################################
  jenkins-master:
    container_name: jenkins-master
    build: ./
    user: root
    restart: unless-stopped
    networks:
      - simplilearn
    ports:
      - "8081:8080"
    environment:
      JENKINS_OPTS: "--prefix=/jenkins"
    volumes:
      - jenkins_master_data:/var/jenkins_home:z
      - /var/run/docker.sock:/var/run/docker.sock
      - ./conf/.ssh:/var/jenkins_home/.ssh
      - ./conf/.m2:/var/jenkins_home/.m2

  jenkins-node01:
    container_name: jenkins-node01
    build: ./
    user: root
    restart: unless-stopped
    networks:
      - simplilearn
    ports:
      - "8082:8080"
    environment:
      JENKINS_OPTS: "--prefix=/jenkins"
    volumes:
      - jenkins_node01_data:/var/jenkins_home:z
      - /var/run/docker.sock:/var/run/docker.sock
      - ./conf/.ssh:/var/jenkins_home/.ssh
      - ./conf/.m2:/var/jenkins_home/.m2

  jenkins-node02:
    container_name: jenkins-node02
    build: ./
    user: root
    restart: unless-stopped
    networks:
      - simplilearn
    ports:
      - "8083:8080"
    environment:
      JENKINS_OPTS: "--prefix=/jenkins"
    volumes:
      - jenkins_node02_data:/var/jenkins_home:z
      - /var/run/docker.sock:/var/run/docker.sock
      - ./conf/.ssh:/var/jenkins_home/.ssh
      - ./conf/.m2:/var/jenkins_home/.m2



volumes:
  jenkins_master_data:
  jenkins_node01_data:
  jenkins_node02_data:

networks:
  simplilearn:
    driver: bridge
```

<br />

### docker/Dockerfile

```Dockerfile
FROM jenkins/jenkins:2.293

USER root

RUN apt-get update
RUN apt-get install -y openssh-server
RUN apt-get install -y maven

COPY ./jenkins.sh /usr/local/bin/jenkins.sh

RUN chmod +x /usr/local/bin/jenkins.sh
RUN chown jenkins:jenkins /usr/local/bin/jenkins.sh

USER jenkins

ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/jenkins.sh"]
```

<br />

### docker/jenkins.sh

```bash
#! /bin/bash -e

# Start OpenSSH Server
mkdir -p /run/sshd
nohup /usr/sbin/sshd -D >/dev/null 2>&1 &

mkdir -p /var/jenkins_home/.m2/repository

chown -R jenkins:jenkins /var/jenkins_home/.ssh
chown -R jenkins:jenkins /var/jenkins_home/.m2

chmod -R 775 /var/jenkins_home/.m2
chmod 600 /var/jenkins_home/.ssh/authorized_keys
chmod 700 /var/jenkins_home/.ssh

: "${JENKINS_WAR:="/usr/share/jenkins/jenkins.war"}"
: "${JENKINS_HOME:="/var/jenkins_home"}"
: "${COPY_REFERENCE_FILE_LOG:="${JENKINS_HOME}/copy_reference_file.log"}"
: "${REF:="/usr/share/jenkins/ref"}"
touch "${COPY_REFERENCE_FILE_LOG}" || { echo "Can not write to ${COPY_REFERENCE_FILE_LOG}. Wrong volume permissions?"; exit 1; }
echo "--- Copying files at $(date)" >> "$COPY_REFERENCE_FILE_LOG"
find "${REF}" \( -type f -o -type l \) -exec bash -c '. /usr/local/bin/jenkins-support; for arg; do copy_reference_file "$arg"; done' _ {} +

# if `docker run` first argument start with `--` the user is passing jenkins launcher arguments
if [[ $# -lt 1 ]] || [[ "$1" == "--"* ]]; then

  # read JAVA_OPTS and JENKINS_OPTS into arrays to avoid need for eval (and associated vulnerabilities)
  java_opts_array=()
  while IFS= read -r -d '' item; do
    java_opts_array+=( "$item" )
  done < <([[ $JAVA_OPTS ]] && xargs printf '%s\0' <<<"$JAVA_OPTS")

  readonly agent_port_property='jenkins.model.Jenkins.slaveAgentPort'
  if [ -n "${JENKINS_SLAVE_AGENT_PORT:-}" ] && [[ "${JAVA_OPTS:-}" != *"${agent_port_property}"* ]]; then
    java_opts_array+=( "-D${agent_port_property}=${JENKINS_SLAVE_AGENT_PORT}" )
  fi

  if [[ "$DEBUG" ]] ; then
    java_opts_array+=( \
      '-Xdebug' \
      '-Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=y' \
    )
  fi

  jenkins_opts_array=( )
  while IFS= read -r -d '' item; do
    jenkins_opts_array+=( "$item" )
  done < <([[ $JENKINS_OPTS ]] && xargs printf '%s\0' <<<"$JENKINS_OPTS")

  exec java -Duser.home="$JENKINS_HOME" "${java_opts_array[@]}" -jar ${JENKINS_WAR} "${jenkins_opts_array[@]}" "$@"
fi

# As argument is not jenkins, assume user want to run his own process, for example a `bash` shell to explore this image
exec "$@"
```

<br />

### docker/conf/nginx.conf

```code

events {}

http {
   
    upstream jenkins-master {
        server jenkins-master:8080;
    }

    upstream jenkins-node01 {
        server jenkins-node01:8080;
    }

    upstream jenkins-node02 {
        server jenkins-node02:8080;
    }

    # Support hhttp2/ websocket handshakes
    map $http_upgrade $connection_upgrade {
        default upgrade;
        '' close;
    }
    
    
    # redirect http to https
    server {
        listen 80 default_server;
        server_name jenkins-master.brianbrown.me;
        return 301 https://$host$request_uri;
    }
    
    
    # Jenkins Master Node
    server {
        
        listen 443  ssl http2;
        listen [::]:443  ssl http2;
        
        server_name jenkins-master.brianbrown.me;
        
        # Support only TLSv1.2
        ssl_protocols TLSv1.2;
        ssl_certificate                        /etc/nginx/certs/jenkins-master.brianbrown.me.pem;
        ssl_certificate_key                    /etc/nginx/certs/jenkins-master.brianbrown.me.key;
        ssl_client_certificate                 /etc/nginx/certs/jenkins-master.brianbrown.me.crt;
        ssl_verify_client optional_no_ca;
        ssl_verify_depth 3;
        recursive_error_pages on;

        location / {
            proxy_set_header        Host $host:$server_port;
            proxy_set_header        X-Real-IP $remote_addr;
            proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header        X-Forwarded-Proto $scheme;
            
            proxy_pass http://jenkins-master;
            proxy_read_timeout  90;
            
            # Fix the “It appears that your reverse proxy set up is broken" error.
            proxy_redirect http://jenkins-master https://jenkins-master.brianbrown.me;

            # Required for new HTTP-based CLI
            proxy_http_version 1.1;
            proxy_request_buffering off;
            # workaround for https://issues.jenkins-ci.org/browse/JENKINS-45651
            # add_header 'X-SSH-Endpoint' 'jenkins-master.brianbrown.me:50022' always;
            add_header 'X-SSH-Endpoint' 'jenkins-master.brianbrown.me:50022' always;
        }

    }

    # Jenkins Worker Node 01
    server {
        
        listen 443  ssl http2;
        listen [::]:443  ssl http2;
        
        server_name jenkins-node01.brianbrown.me;
        
        # Support only TLSv1.2
        ssl_protocols TLSv1.2;
        ssl_certificate                        /etc/nginx/certs/jenkins-node01.brianbrown.me.pem;
        ssl_certificate_key                    /etc/nginx/certs/jenkins-node01.brianbrown.me.key;
        ssl_client_certificate                 /etc/nginx/certs/jenkins-node01.brianbrown.me.crt;
        ssl_verify_client optional_no_ca;
        ssl_verify_depth 3;
        recursive_error_pages on;

        location / {
            proxy_set_header        Host $host:$server_port;
            proxy_set_header        X-Real-IP $remote_addr;
            proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header        X-Forwarded-Proto $scheme;
            
            proxy_pass http://jenkins-node01;
            proxy_read_timeout  90;
            
            # Fix the “It appears that your reverse proxy set up is broken" error.
            proxy_redirect http://jenkins-node01 https://jenkins-node01.brianbrown.me;

            # Required for new HTTP-based CLI
            proxy_http_version 1.1;
            proxy_request_buffering off;
            # workaround for https://issues.jenkins-ci.org/browse/JENKINS-45651
            add_header 'X-SSH-Endpoint' 'jenkins-node01.brianbrown.me:50022' always;
            # add_header 'X-SSH-Endpoint' 'jenkins-node01.brianbrown.me:50002' always;
        }

    }

    # Jenkins Worker Node 02
    server {
        
        listen 443  ssl http2;
        listen [::]:443  ssl http2;
        
        server_name jenkins-node02.brianbrown.me;
        
        # Support only TLSv1.2
        ssl_protocols TLSv1.2;
        ssl_certificate                        /etc/nginx/certs/jenkins-node02.brianbrown.me.pem;
        ssl_certificate_key                    /etc/nginx/certs/jenkins-node02.brianbrown.me.key;
        ssl_client_certificate                 /etc/nginx/certs/jenkins-node02.brianbrown.me.crt;
        ssl_verify_client optional_no_ca;
        ssl_verify_depth 3;
        recursive_error_pages on;

        location / {
            proxy_set_header        Host $host:$server_port;
            proxy_set_header        X-Real-IP $remote_addr;
            proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header        X-Forwarded-Proto $scheme;
            
            proxy_pass http://jenkins-node02;
            proxy_read_timeout  90;
            
            # Fix the “It appears that your reverse proxy set up is broken" error.
            proxy_redirect http://jenkins-node02 https://jenkins-node02.brianbrown.me;

            # Required for new HTTP-based CLI
            proxy_http_version 1.1;
            proxy_request_buffering off;
            # workaround for https://issues.jenkins-ci.org/browse/JENKINS-45651
            add_header 'X-SSH-Endpoint' 'jenkins-node02.brianbrown.me:50022' always;
            # add_header 'X-SSH-Endpoint' 'jenkins-node02.brianbrown.me:50002' always;
        }

    }


}

```

<br />

### docker/conf/.m2/settings.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings
 xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
 <pluginGroups/>
 
  <profiles>
    <profile>
      <id>caltech</id>
      <repositories>
        <repository>
          <id>central</id>
          <name>Central Repository</name>
          <url>https://repo.maven.apache.org/maven2</url>
          <layout>default</layout>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
      </repositories>

      <pluginRepositories>
        <pluginRepository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </pluginRepository>
        <pluginRepository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>caltech</activeProfile>
  </activeProfiles>
</settings>

```
