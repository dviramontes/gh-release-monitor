FROM clojure:tools-deps
ARG ENVIRONMENT
ENV ENVIRONMENT=$(ENVIRONMENT:-prod)
RUN mkdir -p /app
WORKDIR /app
COPY . ./
COPY target/api.main-0.1.0-SNAPSHOT.jar .
CMD java -jar api.main-0.1.0-SNAPSHOT.jar
EXPOSE 8080