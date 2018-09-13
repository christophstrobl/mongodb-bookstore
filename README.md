## Welcome to the ![Fantasy Bookstore](/fantasy_bookstore.png?raw=true)

The _Fantasy Bookstore_ is a tiny [SpringBoot](https://github.com/spring-projects/spring-boot) application outlining usage of different MongoDB Data Models for atomic operations.
The _Bookstore_ refers to and extends the [Model Data for Atomic Operations](https://docs.mongodb.com/manual/tutorial/model-data-for-atomic-operations/#model-data-for-atomic-operations) example.

Use [Application Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-adding-active-profiles) to activate the different approaches.

Profile | Description
--- | ---
sa | Synchronous Atomic Operations with denormalized Data Model
stxn | Synchronous Multi Document Transactions using just the native MongoClient
stx | Synchronous Spring managed Multi Document Transactions 
rtx | Reactive Multi Document Transactions
rcs | Active this profile along with one of the transactional (stx, rtx) ones to subscribe to changes on the `order` collection.
retry | Activate this profile to retry failed transactions via [Spring Retry](https://github.com/spring-projects/spring-retry).
reset | Reset the initial set of collections and pre fill it with test data

**Web Endpoints**

URL | Sample | Description
--- | --- | ---
GET  :8080/books | `http :8080/books` | List all books.
GET  :8080/book/{book} | `http :8080/book/bb4e114f` | A single Book.
POST :8080/book/{book}/order?customer= | `http POST :8080/book/bb4e114f/order?customer=christoph` | Place an order for a book.

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

### Synchronous Spring Managed Multi Document Transactions

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

### Synchronous Multi Document Transactions with native MongoClient

Just as in the sample above data is split between `Book` and `Order` whereas the `Order` references the `Book` via a `DBRef`.
However, here we're using the native operations of `MongoClient` and `ClientSession` to run the transaction.

**Spring Profile:** stxn   
**MongoDB Collections:** books, order  
**Components**: NativeMongoTransactionalOrderService, SyncBookstoreHandler 

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

### Retry Transactions

It may happen that multiple transactions try to alter the very same document which leads to write conflicts and therefore 
the abortion of the transaction.

This scenario can be provoked by:

* Start the application
* Open [Mongo Shell](https://docs.mongodb.com/manual/mongo/#the-mongo-shell) and execute
```bash
rs0:PRIMARY> session = db.getMongo().startSession({ "mode" : "primary" });
rs0:PRIMARY> session.startTransaction();
rs0:PRIMARY> session.getDatabase("fantasy-bookstore").books.update({ "_id" : "f430cb49", "available" : { "$gt" : 0 } },  { "$inc" : { "available" : -1 } });
```
* Call the order endpoint
```bash
~ $ http POST :8080/book/f430cb49/order?customer=cstrobl
```
* The Application will retry the operation for 3 times with 5 seconds delay in between.
* Switch to Mongo Shell again and _commit_ the transaction within time to have the other one succeed as well.
```bash
rs0:PRIMARY> session.commitTransaction();
```

**Spring Profile:** stx,retry   
**MongoDB Collections:** books, order  
**Components**: TransactionalOrderService, SyncBookstoreHandler, RetryTemplate 

## Requirements

### Java

The application requires Java 8 (or better).

### MongoDB

The Application requires [MongoDB 4.0](https://www.mongodb.com/download-center#production) or better running as [Replica Set](https://docs.mongodb.com/manual/tutorial/deploy-replica-set/#procedure).

### Project Lombok

Some of the code uses [Project Lombok](https://projectlombok.org/). Make sure to have it.
