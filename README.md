# sparqlmon

SPARQL endpoint monitoring microservice system.

# Build and run

```
./build.sh
docker-compose up -d
```

# Running tests
```
cd test
docker-compose up -d
cd ..
sbt test
cd test
docker-compose down --volume
cd ..
```
