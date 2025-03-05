echo '{"sender": "user1", "receiver": "user", "message": "Hello!"}' | \
docker exec -i kafka kafka-console-producer.sh --bootstrap-server localhost:9092 --topic messages