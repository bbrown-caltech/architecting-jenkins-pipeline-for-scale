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