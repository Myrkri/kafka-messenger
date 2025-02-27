FROM amazoncorretto:17.0.10 as build

WORKDIR /app

COPY . .

#RUN yum update -y && yum install -y dos2unix && \
#    dos2unix mvnw && \
#    chmod +x mvnw

RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.10

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]