# embedded-redis-java
easy to embedded redis, typically used in unit test
## RESP protocol
https://redis.io/docs/reference/protocol-spec
## helpful http command
### PUT KEY
curl -XPUT localhost:16379/keys -d '{"key":"k1", "value":"v1"}' -iv
### GET KEY
curl -XGET localhost:16379/keys/k1 -iv
