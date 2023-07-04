# ECA USER MANAGEMENT SERVICE

This Application is to register tenants, flat owners in the apartments services.

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://github.com/mgupta24/eca-apartment-management-system.git
```

**2. Run the app using maven**

```bash
cd eca
cd eca-user-mgmt
mvn spring-boot:run
```

That's it! The application can be accessed at `http://localhost:6090`.

You may also package the application in the form of a jar and then run the jar file like so -

```bash
mvn clean package
java -jar target/eca-user-mgmt*.jar
```

# OR

Simply run the docker image container using docker

```bash
docker build -t ecausermgmt:latest .
docker run -d -p 6090:6090 ecausermgmt:latest
```

