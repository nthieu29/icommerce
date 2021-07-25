
# icommerce
Simple online shopping application to sell products (backend only).

- [System Design](#system-design)
  - [1. Requirements](#1-requirements)
  - [2. High-level design](#2-high-level-design)
  - [3. Defining data model](#3-defining-data-model)
    - [Product Service](#product-service)
    - [Audit Service](#audit-service)
    - [Shopping Cart Service](#shopping-cart-service)
    - [Order Service](#order-service)
  - [4. Detailed design](#4-detailed-design)
    - [Authentication Service](#authentication-service)
    - [API Gateway](#api-gateway)
    - [Registry Service](#registry-service)
    - [Product Service](#product-service)
    - [Audit Service](#audit-service)
    - [Shopping Cart Service](#shopping-cart-service)
    - [Order Service](#order-service)
  - [5. Monitoring](#5-monitoring)
  - [6. Identifying and resolving single point of failures and bottlenecks](#6-identifying-and-resolving-single-point-of-failures-and-bottlenecks)
    - [Single point of failure](#single-point-of-failure)
    - [Bottlenecks](#bottlenecks)
- [Software development principles](#software-development-principles)
  - [KISS (Keep It Simple Stupid)](#kiss-keep-it-simple-stupid)
  - [YAGNI (You aren't gonna need it)](#yagni-you-arent-gonna-need-it)
  - [Separation of Concerns](#separation-of-concerns)
  - [DRY](#dry)
  - [Code For The Maintainer](#code-for-the-maintainer)
  - [Avoid Premature Optimization](#avoid-premature-optimization)
  - [Minimise Coupling](#minimise-coupling)
  - [Inversion of Control](#inversion-of-control)
  - [Single Responsibility Principle](#single-responsibility-principle)
- [Design Patterns](#design-patterns)
- [Application default configuration](#application-default-configuration)
- [How to run the application](#how-to-run-the-application)
  - [Setup development workspace](#setup-development-workspace)
  - [Run a microservice](#run-a-microservice)
- [API Documentation](#api-documentation)
- [Project folder structure and Frameworks, Libraries](#project-folder-structure-and-frameworks-libraries)
  - [Project folder structure](#project-folder-structure)
  - [Frameworks and Libraries](#frameworks-and-libraries)
- [References](#references)
- [Other projects](#other-projects)

## System Design

### 1. Requirements
A small start-up named "iCommerce" wants to build a very simple online shopping application to sell their products. In order to get to the market quickly, they just want to build an MVP version with a very limited set of functionalities:

a. The application is simply a simple web page that shows all products on which customers can filter, short and search for products based on different criteria such as name, price, brand, colour etc.

b. All product prices are subject to change at any time and the company wants to keep track of it.

c. If a customer finds a product that they like, they can add it to their shopping cart and proceed to place an order.

d. For audit support, all customers' activities such as searching, filtering and viewing product's details need to be stored in the database. However, failure to store customer activity is completely transparent to customer and should have no impact to the activity itself.

e. Customer can login simply by clicking the button “Login with Facebook”. No further account registration is required.

f. No online payment is supported yet. Customer is required to pay by cash when the product got delivered.

### 2. High-level design
At a high-level, we need some following services (or components) to handle above requirements:

![High Level Design](external-files/HighLevelDesign.png)

- **Product Service**: manages our products with CRUD operations. This service also provides the ability to allow user could filter, sort and search for products based on dynamic criteria.
- **Audit Service**: records all customers activities (filtering, sorting, viewing product detail).
- **Shopping Cart Service**: manages customers shopping carts with CRUD operations.
- **Order Service**: manages customer orders with CRUD operations.
- **Authentication Service**: authenticates customers, integrates with 3rd party identity platform like Facebook, Google...
- **API Gateway**: Route requests to multiple services using a single endpoint. This service allows us to expose multiple services on a single endpoint and route to the appropriate service based on the request.

### 3. Defining data model
In this part, we describes considerations for managing data in our architecture. For each service, we discuss data schema and datastore considerations.

In general, we follow the basic principle of microservices is that each service manages its own data. Two services should not share a data store.

![Wrong Datastore](external-files/datastore-microservices-wrong.png)
#### Product Service
The Product service stores information about all of our product. The storage requirements for the Product are:
- Long-term storage.
- Read-heavy (it's common for ecommerce application because the traffic from users to view, search, sort product are always much higher than the traffic from administrators to update product's information).
- Structured data: Category, Product, Brand, ProductPriceHistory. 
- Need complex joins (for example, in the UI, maybe we have a menu with different categories, user could choose a category and then view all products in that category, then each product will have many different variants like a T-shirt will have many different sizes and colors).

A relational database is appropriate in our case. For the simplicity of the assignment, we simplify the data schema like this:

![Product Schema](external-files/Product.png)


#### Audit Service
The Audit service listens for customer activities from the Product service. The storage requirements for the Audit service are:
- Long-term storage.
- Non-relational data.
- Able to handle massive amount a data (read-heavy as we mentioned in Product section above).
- Dynamic or flexible schema.

A document-oriented database is appropriate, and MongoDB is good fit in our scenario. It also supports to store Geolocation data and build-in queries to query data based on location -> it could help us if we want to know something like customers in which city will interest on which products (maybe for Product Recommendation Service).

We define the data schema like this:

![Audit Schema](external-files/CustomerActivity.png)

#### Shopping Cart Service
The Shopping Cart service stores information about shopping cart of the customers. The storage requirements for the Shopping Cart Service are:
- Short-term storage. Each customer will have their own shopping cart and only one shopping cart at the moment. After customer checkout, the shopping cart data will be cleared.
- Need retrieve/lookup shopping cart data quickly and update shopping cart data quickly (for example, there are some big sale periods in a year like 11/11 and 12/12, a lot of customers come to our system and update their shopping cart data to make the order).
- Support only 1 simple query: query by customer.

The HashMap data structure (with the key is customer and the value is shopping cart data) seems meet our needs because the get and put operations take only constant time. But we will get the problem if the server running Shopping Cart Service goes down (we will lose all the shopping cart data of customers) or if we run many instances of Shopping Cart Service (we need to sync data between these instances). Redis could solve these problem easily and meets all our needs with its Hash Table high read-write performance with schema like this.

| Key (Customer) | Value (Shopping Cart data)       |
|--------------- |:--------------------------------:|
| Username       | List of Product and its quantity |
        
![Shopping Cart Schema](external-files/ShoppingCart.png)
        
#### Order Service
The Order service stores information about all of our customer orders. The storage requirements for the Order are:
- Long-term storage.
- Able to handle a high volume of packages, requiring high write throughput (for example, there are some big sale periods in a year like 11/11 and 12/12, a lot of customers come to our system and make orders).
- Support simple queries. No complex joins or requirements for referential integrity.

Because the order data is simple and not relational, a document-oriented database is appropriate, and MongoDB can achieve high throughput and scale well in terms of volume of traffic or size of data (or both).

![Order Schema](external-files/Order.png)

### 4. Detailed design
![Detailed Design](external-files/DetailedDesign.png)

In this section, we go though all our services in detail.

#### Authentication Service
To simplify the setup, here we use [Okta](https://www.okta.com/products/customer-identity/authentication/) - an Identity Cloud Service, and it provides us some benefits:
- Single point to authenticate our customers: simplify our downstream microservice, these services just need to verify/validate the access token of users.
- Store data about our customer: In ecommerce application, we will need these data to send marketing/promotion emails...
- Perform almost popular social logins: Now we need support "Login with Facebook" only, but in the future maybe we need Google, LinkedIn...And we don't want to update all our downstream microservices to support more social logins. To simplify the process, we don't config Okta use Facebook Social Login, we login by username/password instead.

In the case we don't want to use cloud service, we could use Keycloak - an open source identity solution provides the same capacities.

#### API Gateway
We need API Gateway for following reasons:
- When a client needs to consume multiple services, setting up a separate endpoint for each service and having the client manage each endpoint can be challenging. Each service has a different API that the client must interact with, and the client must know about each endpoint in order to connect to the services. If an API changes, the client must be updated as well. If we refactor a service into two or more separate services, the code must change in both the service and the client.
- Simplify application development by moving shared service functionality, such as the use of SSL certificates, from other parts of the application into the gateway. Other common services such as authentication, authorization, logging, monitoring, or throttling can be difficult to implement and manage across a large number of deployments. It may be better to consolidate this type of functionality, in order to reduce overhead and the chance of errors. Simpler configuration results in easier management and scalability and makes service upgrades simpler.
![Gateway Offload](external-files/gateway-offload.png)
- Provide some consistency for request and response logging and monitoring.

**Implementation:**
- We use *spring-boot-starter-security* and *spring-boot-starter-oauth2-resource-server* to mark this service as a *resource server*. We also need to indicate how our application can obtain the public key necessary to validate the signature of the JWTs it receives as Bearer tokens by setting spring.security.oauth2.resourceserver.jwt.jwk-set-uri to Okta service.
- We use *spring-cloud-netflix* to route the client request to our downstream services.
- When user successfully authenticated, we use ZuulFilter to add custom HTTP Header "Username" to client request. We could extract more information from JWT token (like user group - ADMIN, USER...) and add them as HTTP Header but for simplicity, we skip it for now.
#### Registry Service
We use *spring-cloud-starter-netflix-eureka-server* to start Eureka Server for service registration and discovery in our system. It helps API Gateway routing requests by service name instead of hard-code URL. But if we deploy our system to Kubernetes, we don't need this anymore because Kubernetes provides Service discovery and load balancing out-of-box.

![Kubernetes](external-files/KubernetesDeployment.png)

#### Product Service
- As we mentioned above, our Product Service will use Postgres as a relational database.
- To support customer filter, sort and search for products based on dynamic criterias, we have 2 options: *Spring Specification* and *QueryDSL*. Here we go with *QueryDSL* because it simplify the implementation.
- To keep track all customer activity, we need to record all customer request parameters when client send GET request to our endpoint to view product detail or to filtering/sorting products. We use *Spring AOP* and define the PointCut to tell Spring which part of the code should be monitored, we also define Advice method to tell Spring how to record these parameters.
- To make sure failure to store customer activity is completely transparent to customer and should have no impact to the activity itself, we use *Spring Async* to run our AOP Advice in a separate thread.
- We use *Spring Cloud Stream* to send all customer activity data from Product Service to a message broker (to simplify the setup, here we use CloudAMQP - a cloud RabbitMQ service). In our case, Product Service acts as a message *Source*, and Audit Service acts as a message *Sink*. We don't want data will not be lost if Audit Service was temporary down, so we config queue as durable queue for guaranteed message delivery.

#### Audit Service
- Audit Service acts as a message Sink, it consumes and process message (message is customer activity in our case). And store to MongoDB.

#### Shopping Cart Service
- A simple CRUD service with *spring-boot-starter-data-redis* and backed by Redis.

#### Order Service
- A simple CRUD Service with *spring-boot-starter-data-rest* and backed by MongoDB. We use @RepositoryRestResource to expose resources without implementing controller/service.

### 5. Monitoring
In any complex application, at some point something will go wrong. In a microservices application, we need to track what's happening across dozens or even hundreds of services. To make sense of what's happening, we must collect telemetry from the application. Telemetry can be divided into logs and metrics.

- **Logs** are text-based records of events that occur while the application is running. They include things like application logs (trace statements) or web server logs. Logs are primarily useful for forensics and root cause analysis.

- **Metrics** are numerical values that can be analyzed. We can use them to observe the system in real time (or close to real time), or to analyze performance trends over time. To understand the system holistically, we must collect metrics at various levels of the architecture, from the physical infrastructure to the application, including:
     - **Node-level** metrics, including CPU, memory, network, disk, and file system usage. System metrics help us to understand resource allocation for each node in the cluster, and troubleshoot outliers.
     - **Container** metrics. For containerized applications, we need to collect metrics at the container level, not just at the VM level.
     - **Application** metrics. This includes any metrics that are relevant to understanding the behavior of a service. Some good examples in our case are response time, error rate,...

We have many different available solutions for monitoring: Cloud Providers (AWS CloudWatch, Azure Monitor...). In the case we want to go with cloud solution, we could use Graylog (refer [kubernetes-logging-setup](https://github.com/nthieu29/kubernetes-logging-setup)) for centralized log management and Prometheus, Grafana for metrics. The setup, configuration and integrate with Spring Boot is straightforward.

### 6. Identifying and resolving single point of failures and bottlenecks

- **Redundancy** is the duplication of critical components or functions of a system with the intention of increasing the reliability of the system. Redundancy plays a key role in removing the single points of failure in the system and provides backups if needed in a crisis
- **Replication** means sharing information to ensure consistency between redundant resources to improve reliability, fault-tolerance, or accessibility. Replication is widely used in many database management systems (DBMS), usually with a primary-replica relationship between the original and the copies. The primary server gets all the updates, which then ripple through to the replica servers. Each replica outputs a message stating that it has received the update successfully, thus allowing the sending of subsequent updates. We could use replication to avoid the cases like: instance of Redis goes down and our customers lose all their shopping cart data...

![Redundancy](external-files/RedundancyReplication.PNG)

#### Single point of failure
We have 2 single points of failure in our system and they could be eliminated with redundancy:

- *Registry Service*: if the Registry Service went down, all our services went down (because our API Gateway can not get service information/path for request routing). If we want to have high availability of the system, we need to made this service redundancy by having multiple replicas of this services running in the system. And this solution comes with tradeoff: we increase the complexity of the system, also the operation cost.
- *API Gateway*: similar with Registry Service above.

#### Bottlenecks
Solving this problem takes an iterative approach of:
1. Benchmark/Load Test.
1. Profile for bottlenecks.
1. Address bottlenecks while evaluating alternatives and trade-offs.
1. Repeat.

We continue benchmarking and monitoring your system to address bottlenecks as they come up and scaling is an iterative process.

## Software development principles
### KISS (Keep It Simple Stupid)
- Most systems work best if they are kept simple rather than made complex.
- Less code takes less time to write, has less bugs, and is easier to modify.
- > The best design is the simplest one that works - Albert Einstein.

**What applied:** Keep system design and the implementation code simple

### YAGNI (You aren't gonna need it)
- Don't implement something until it is necessary.
- Any work that's only used for a feature that's needed tomorrow, means losing effort from features that need to be done for the current iteration.

**What applied:** Always implement things when we actually need them, never when we just foresee that we need them.

### Separation of Concerns
- Separating a system into multiple distinct microservices, such that each service addresses a separate concern (product, order, shopping cart...).
- In each service, break program functionality into separate layers (as show in [Project folder structure](#project-folder-structure)).
- AOP to separate of cross-cutting concerns.

### DRY
- Put business rules, long expressions, if statements, math formulas, metadata, etc. in only one place.

### Code For The Maintainer
- Maintenance is by far the most expensive phase of any project.
- Always code as if the person who ends up maintaining your code is a violent psychopath who knows where you live.
- Always code and comment in such a way that if someone a few notches junior picks up the code, they will take pleasure in reading and learning from it.

**What applied:** Comprehensive documentation, make the code clean, add comment for some special intentions.

### Avoid Premature Optimization
- It is unknown upfront where the bottlenecks will be.
- After optimization, it might be harder to read and thus maintain.

**What applied:** Don't optimize until we need to, and only after profiling we discover a bottleneck optimise that.

### Minimise Coupling
- Eliminate, minimise, and reduce complexity of necessary relationships.
- By hiding implementation details, coupling is reduced.

**What applied:** Encapsulation in OOP, DI in Spring.

### Inversion of Control
IoC inverts the flow of control as compared to traditional control flow (Don't call us, we'll call you).
- In traditional programming: our custom code makes calls to a library.
- IoC: framework make calls to our custom code.

**What applied:** Spring IoC container with Constructor-Based Dependency Injection for main code and Field-Based Dependency Injection for test code.

### Single Responsibility Principle
Every class should have a single responsibility, and that responsibility should be entirely encapsulated by the class. Responsibility can be defined as a reason to change, so a class or module should have one, and only one, reason to change.

**What applied:** break system into multiple services, each services has only one responsibility. In each services, break into multiple layers, each layers were broken into multiple classes, each class has only one reason to change.

## Design Patterns
- **Object Mother:** pattern is essentially a special case of the Factory pattern used for creating test objects. It provides one or more factory methods that each create an object in a specific, meaningful configuration ([ProductMother.java](product-service/src/test/java/com/nthieu/productservice/helper/ProductMother.java))
- **Builder**: provide a flexible solution to object creation. ([Product.java](product-service/src/main/java/com/nthieu/productservice/entity/Product.java))

## Application default configuration
To make it easier for development process, we still expose these ports on the local machine to send request directly with services or to view actual data in the data stores. 
In production environment, we leverage the infrastructure to make the downstream services become unreachable from the client, we only expose one single point - API Gateway.

| Service               | Port |
| --------------------- | ---- |
| api-gateway           | 8080 |
| audit-service         | 8081 |
| order-service         | 8082 |
| product-service       | 8083 |
| registry-service      | 8761 |
| shopping-cart-service | 8084 |
| postgres              | 5432 |
| mongo                 | 27017|
| redis                 | 6379 |

_Note_: for development purpose, we could bypass authentication by adding "Username: ```<your-test-username>```" to HTTP Header when we send request to downstream services.

## How to run the application
### Setup development workspace
The setup development workspace process is simpler than ever with following steps:
1. Install [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
1. Install [Docker for Desktop](https://www.docker.com/products/docker-desktop).
1. Install [Maven](https://maven.apache.org/download.cgi?Preferred=ftp://mirror.reverse.net/pub/apache/).
1. Clone this project to your local machine.
1. Open terminal and make sure you're at the root directory of this project, run the command ```docker-compose up``` (this will automatically setup Postgres, MongoDB and Redis for you).

That's all.

### Run a microservice
You can run Spring Boot microservice in different ways, but first make sure you are in the root directory of the microservice you want to run:
- Run jar file (of course you need to build it first): ```mvn install && java -jar target/<service-name>-0.0.1-SNAPSHOT.jar```
- Run with Spring Boot: ```mvn spring-boot:run```

**Note in product-service:** Because we use code generation feature in QueryDSL to generate Qclass (for dynamic query feature) for our entities. So you need to run ```mvn compile``` to generate these classes (refer [here](http://www.querydsl.com/static/querydsl/3.2.0/reference/html/ch03s03.html) for more details).

## API Documentation
From the client's point of view, all the requests will be handled by only one single point - our API Gateway.

Please refer [iCommerce API Documentation](https://documenter.getpostman.com/view/1885209/Tzm3nxGk) for full request endpoint, HTTP Headers and request payload with example requests and responses.

Note that **all requests need JWT Access Token** (by adding "Authorization" to HTTP Header with value "Bearer ```<JWT Access Token>```").

You could use cURL or Postman or any tools in your flavor. Here we recommend using Postman because it makes things easier.
For Postman configuration and demo login credentials, please refer [How to configure Postman to use iCommerce API](https://github.com/nthieu29/icommerce/wiki/How-to-configure-Postman-to-use-iCommerce-API).

## Project folder structure and Frameworks, Libraries
### Project folder structure
Based on above design, the project folder structure is organized following:
- api-gateway: API Gateway
- audit-service: Audit Service
- external-files: external files
- order-service: Order Service
- product-service: Product Service
- registry-service: Registry Service
- shopping-cart-service: Shopping Cart Service

For each microservice, we will follow common 4 layers architecture:
- **Controller**: Handle HTTP request from client, invoke appropriate methods in service layer, return the result to client.
- **Service**: All business logic here. Data related calculations and all.
- **Repository**: all the Database related operations are done here.
- **Entity**: persistent domain object -  table in Databases.

### Frameworks and Libraries
The Frameworks/Libraries used in the project and their purposes:
- spring-cloud-starter-netflix-eureka-server : Eureka Server (Registry Service). This library allows services to find and communicate with each other without hard-coding hostname and port.
- spring-cloud-starter-netflix-eureka-client : Eureka Client, for registering the service with Service Registry.
- spring-cloud-starter-netflix-zuul : Zuul Proxy, turn our API Gateway into a reverse proxy.
- spring-boot-starter-oauth2-resource-server: Starter for using Spring Security's OAuth2 resource server features, turn our API Gateway into a resource server.
- spring-boot-starter-actuator : monitor and manage the application health.
- spring-boot-starter-web : for building REST API.
- spring-boot-starter-test : Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest and Mockito.
- spring-boot-starter-aop : for aspect-oriented programming with Spring AOP and AspectJ. We use this feature for implementing the customer audit feature.
- spring-boot-starter-data-jpa: for using Spring Data JPA with Hibernate.
- spring-boot-starter-data-mongodb: Starter for using MongoDB document-oriented database and Spring Data MongoDB.
- spring-cloud-starter-stream-rabbit: Spring Cloud Starter Stream Rabbit, we use this to send and get message from RabbitMQ.
- spring-boot-starter-data-rest: Starter for exposing Spring Data repositories over REST using Spring Data REST.
- spring-boot-starter-data-redis: Starter for using Redis key-value data store with Spring Data Redis.
- spring-boot-starter-validation: for using Java Bean Validation with Hibernate Validator.
- spring-boot-starter-security: for using Spring Security.
- spring-security-test: for the testing Spring Security.
- modelmapper: to make object mapping easy, by automatically determining how one object model maps to another, based on conventions.
- QueryDSL: build dynamic queries.

## References
- [Designing a microservices architecture](https://docs.microsoft.com/en-us/azure/architecture/microservices/design/) - *Azure Architecture Center | Microsoft Docs*
- [Cloud design patterns](https://docs.microsoft.com/en-us/azure/architecture/patterns/) - *Azure Architecture Center | Microsoft Docs*
- [The System Design Primer](https://github.com/donnemartin/system-design-primer)

## Other projects
Some of my other projects which you may be interested in:
- [covid-tracker](https://github.com/nthieu29/covid-tracker) - Track COVID-19 local and global coronavirus cases with active, recoveries and death in a simple, elegant UI.
- [build-your-own-data-structure](https://github.com/nthieu29/build-your-own-data-structure) - Building your own data structure.
- [little-tunnel](https://github.com/nthieu29/little-tunnel) - Simple, efficient and easy to use local proxy which help you visit websites blocked by your ISP.
