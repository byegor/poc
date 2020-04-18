FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
COPY target/micronaut-dynamodb-client /srv/micronaut-dynamodb-client
EXPOSE 8080
ENV aws_secretkey="mock"
ENV aws_accesskey="mock"
ENV dynamo_endpoint="http://yegor:8000"
ENTRYPOINT ["/srv/micronaut-dynamodb-client"]