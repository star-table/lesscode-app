FROM openjdk:8-jdk-alpine

ENV LANG zh_CN.UTF-8

RUN echo -e "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main\n\
https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/community" > /etc/apk/repositories

RUN apk --update add curl bash ttf-dejavu && \
      rm -rf /var/cache/apk/*

COPY app-service/target/lesscode-app-service.jar /data/app/lesscode-app/lesscode-app.jar
COPY bin /data/app/lesscode-app/bin

WORKDIR /data/app/lesscode-app/bin

RUN sed -i 's/bash/sh/g' *.sh \
    && echo -e "\ntail -f /dev/null" >> start.sh \
    && cat start.sh

EXPOSE 10666

CMD ["./start.sh"]