# rss-feed-source-task

The rss feed source task is a spring cloud task that ingests articles from rss feeds and inserts data into a datastore

### Required Environment variables
| Environment Variable | Description                                                                  |
|----------------------|------------------------------------------------------------------------------| 
| POSTGRES_HOST        | The hostname of the postgres database                                        |
| POSTGRES_PORT        | The port of the postgres database                                            |
| POSTGRES_DB_NAME     | The name of the db for the postgres database                                 |
| POSTGRES_USERNAME    | Postgres username for access. Should contain write permissions               |
| POSTGRES_PASSWORD    | Postgres password for authentication                                         |
| MONGO_HOST           | The hostname of the mongodb databas                                          |
| MONGO_PORT           | The port of the mongodb database                                             |
| MONGO_USERNAME       | The username for access. Should container write permisions                   |
| MONGO_PASSWORD       | Mongodb password for authentication                                          |
| MONGO_DB_NAME        | Mongodb Database name                                                        |
| MONGO_AUTH_DB        | **Optional** Authentication database name for mongodb. By default is "admin" |


### Configuration options
| Configuration                         | Type   | Description                                                                                                            |
|---------------------------------------|--------|------------------------------------------------------------------------------------------------------------------------|
| rss-feed.retries.max-attempts         | number | The maximum amount of retries when reading a given rss source.                                                         |
| rss-feed.retries.max-attempts         | number | The maximum amount of retries when an error occurs reading a given rss source.                                         |
| rss-feed.retries.retry-backoff-millis | number | The time in milliseconds between retry attempts when retrying to read from a given rss source                          |

