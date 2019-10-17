FROM openjdk:8u222-jre
COPY ./liferay-properties-server/build/libs/liferay-properties-server-0.1-SNAPSHOT-all.jar /usr/
WORKDIR /usr/
EXPOSE 55555
CMD ["java", "-DliferayLanguageServerSocketServer=true", "-jar", "liferay-properties-server-0.1-SNAPSHOT-all.jar"]