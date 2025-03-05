echo '{"sender": "user1", "receiver": "user", "message": "Hello again", "timestamp": "2025-03-05T03:31:02.5989289"}' | \
docker exec -i kafka kafka-console-producer.sh --bootstrap-server localhost:9092 --topic chat
