FROM openjdk:17-jdk-alpine

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /data

RUN apk add --no-cache curl jq freetype fontconfig ttf-dejavu

WORKDIR /data

EXPOSE 8888

ADD ./target/jeecg-module-mgn-start.jar ./

CMD sleep 1;java $JAVA_OPTS  -Dfile.encoding=utf-8 -Djava.security.egd=file:/dev/./urandom -jar jeecg-module-mgn-start.jar