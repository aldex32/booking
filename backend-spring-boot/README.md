# Whisper Booking with Spring Boot

## Set up MySQL
### Install MySQL on Mac
`brew install mysql`
### Start MySQL
`brew services start mysql`
### Stop MySQL
`brew services stop mysql`
### Create database
`create database whisper;`
### Create user
`create user 'admin'@'localhost' identified by 'admin' PASSWORD EXPIRE NEVER;`
### Grant user
```
grant all on whisper.* to 'admin'@'localhost';
grant all on whisper.* to 'admin'@'%';
```

---
## Creating SSL self-signed keystore with user/pass: tomcat/tomcat
`keytool -genkey -alias tomcat -keyalg RSA -keystore src/main/resources/keystore.jks`

---
## Application
### Run
`mvn spring-boot:run`
### Login Credentials
```
user: admin
pass: WhisperAdmin
```
### Web URL
`https://localhost:8443`
### API URL
`https://localhost:8443/api/booking`
### Admin API Account
`https://localhost:8443/account/admin`
### Actuator monitor URL
`https://localhost:8443/monitor`