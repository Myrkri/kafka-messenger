docker exec -i kafka kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic chat --from-beginning < /dev/null
