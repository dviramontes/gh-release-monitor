FROM clojure:tools-deps
ARG ENVIRONMENT
ENV ENVIRONMENT=$(ENVIRONMENT:-prod)
RUN mkdir -p /app
WORKDIR /app
COPY . ./
EXPOSE 8080
CMD java -jar api.main-0.1.0-SNAPSHOT.jar
