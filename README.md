# SpectraSports
A Sports Academy Management Application

# Tech Stack: 
>  ### Java 20 </br>
>  ### Spring boot 3 </br>
>  ### Gradle 8.3 </br>
>  ### Postgres 14.2
***
<br />

# How to build the project
`gradle clean build` 
***
<br />

# Build the project using docker (Dockerfile)
` docker build -t  jeyam/spectra-sports:v1.0 .`
***
# Run the docker container using our docker image

```
docker run -e DB_HOST=host.docker.internal -e DB_NAME=postgres -e DB_USER=postgres -e DB_PASSWORD=mysecretpassword -e DOMAIN=localhost -e S3_ACCESS_KEY=<s3-access-key> -e S3_SECRET_KEY=<s3-secret-key>  -e S3_BUCKET_NAME=<s3-bucket-name> -e SENDINBLUE_API_KEY=<sendinblue-api-key> -p 8080:8080 jeyam/spectra-sports:v1.0
```

<br />

### _NOTE: Make sure your postgres database server is running on port `5432` with the specified username and password_
***
