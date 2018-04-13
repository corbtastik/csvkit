# csvkit

Spring Boot 2.0 sample application using Kotlin that saves CSV data under a 'tag'.

Examples below use [HTTPie](https://httpie.org/) client but you can use whatever client you want to make HTTP calls.

This project contains 3 branches

* sample-1 - Basic API & Persistence
* sample-2 - Hystrix Circuit Breaker on DB calls
* sample-3 - Caching

To checkout:
```bash
git clone https://github.com/corbtastik/csvkit

git checkout sample-1

# use sample-2 and sample-3 for pulling in features listed above

```

To build:
```bash
mvnw clean package
```

To run:
```bash
cd target
java -jar csvkit-1.0.0.RELEASE.jar
```

To Save CSV data (note lines need to end in a newline)
```bash
echo 'one,two,three\n' | http localhost:8080/csv/counting
echo 'four,five,six\n' | http localhost:8080/csv/counting
echo 'seven,eight,nine\n' | http localhost:8080/csv/counting
```

To Retrieve CSV data for a tag
```bash
http localhost:8080/csv/counting

>> HTTP/1.1 200 
>> Content-Length: 44
>> Content-Type: text/plain;charset=UTF-8

one,two,three
four,five,six
seven,eight,nine

```

To Remove CSV data for a tag
```bash
http DELETE localhost:8080/csv/counting
```
