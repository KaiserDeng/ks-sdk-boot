#######################################
######### 镜像构建Dockerfile ##########
#######################################
# docker 基础环境依赖
FROM java:8

VOLUME /tmp

ADD zjsdk-1.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

#  同步容器时区使其与宿主机保持一致
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

