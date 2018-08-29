## Welcome to the MongoDB Bookstore

The _MongoDB Bookstore_ is a tiny [SpringBoot](https://github.com/spring-projects/spring-boot) application outlining usage of different MongoDB Data Models for atomic operations.
The _Bookstore_ refers to and extends the [Model Data for Atomic Operations](https://docs.mongodb.com/manual/tutorial/model-data-for-atomic-operations/#model-data-for-atomic-operations) example.

Use [Application Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-adding-active-profiles) to activate the different approaches.

Profile | Description
--- | ---
sa | Synchronous Atomic Operations with denormalized Data Model
stx | Synchronous Multi Document Transactions
rtx | Reactive Multi Document Transactions
rcs | Active this profile along with one of the transactional (stx, rtx) ones to subscribe to changes on the `order` collection.
reset | Reset the initial set of collections and pre fill it with test data

**Web Endpoints**

URL | Sample | Description
--- | --- | ---
:8080/books | `http :8080/books` | List all books.
:8080/book/{book}/order?customer= | `http :8080/book/bb4e114f/order?customer=christoph` | Place an order for a book.

### Synchronous Atomic Operations with denormalized Data Model

The denormalized Data Model keeps all required data in one place, the Document. Hence all updates of the one document are atomic.
Each and every `Order` is tracked by `checkout` within the `Book` itself.  

```json
{
     "_id" : "bb4e114f",
     "title" : "The Painted Man",
     "author" : [ "Peter V. Brett" ],
     "published_date" : "2009-08-01",
     "pages" : 544,
     "language" : "English",
     "publisher_id" : "Harper Collins Publishers",
     "available" : 3,
     "checkout" : [ { "by": "cstrobl", "date" : "2018-08-27T10:11:59.853Z" } ]
 }

```

**Spring Profile:** sa   
**MongoDB Collections:** books  
**Components**: AtomicOrderService, SyncBookstoreHandler    

### Synchronous Multi Document Transactions

The transactional approach splits data between `Book` and `Order` whereas the `Order` references the `Book` via a `DBRef`.
Still the number of available copies is kept within the `books` collection.

```json
{
    "_id" : "bb4e114f",
    "title" : "The Painted Man",
    "author" : [ "Peter V. Brett" ],
    "published_date" : "2009-08-01",
    "pages" : 544,
    "language" : "English",
    "publisher_id" : "Harper Collins Publishers",
    "available" : 3
}
```

```json
{
    "by" : "cstrobl",
    "date" : "2018-08-27T10:11:59.853Z",
    "books" : [ { "$ref" : "books", "$id" : "bb4e114f" } ]
}
```

**Spring Profile:** stx   
**MongoDB Collections:** books, order  
**Components**: TransactionalOrderService, SyncBookstoreHandler 

### Reactive Multi Document Transactions

Just as the synchronous transactional approach data is split between `Book` and `Order`, this time using a reactive API 
for processing the checkout inside a transaction.

```json
{
    "_id" : "bb4e114f",
    "title" : "The Painted Man",
    "author" : [ "Peter V. Brett" ],
    "published_date" : "2009-08-01",
    "pages" : 544,
    "language" : "English",
    "publisher_id" : "Harper Collins Publishers",
    "available" : 3
}
```

```json
{
    "by" : "cstrobl",
    "date" : "2018-08-27T10:11:59.853Z",
    "books" : [ { "$ref" : "books", "$id" : "bb4e114f" } ]
}
```

**Spring Profile:** rtx   
**MongoDB Collections:** books, order  
**Components**: ReactiveOrderService, ReactiveBookstoreHandler 

## Requirements

### Java

The application requires Java 8 (or better).

### MongoDB

The Application requires [MongoDB 4.0](https://www.mongodb.com/download-center#production) or better running as [Replica Set](https://docs.mongodb.com/manual/tutorial/deploy-replica-set/#procedure).

### Project Lombok

Some of the code uses [Project Lombok](https://projectlombok.org/). Make sure to have it.
