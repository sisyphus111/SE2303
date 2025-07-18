bot:
	cd bot-service && mvn spring-boot:run

es:
	cd community-service && ./start-elasticsearch.sh
	
community:
	cd community-service && mvn spring-boot:run

mood:
	cd mood-service && mvn spring-boot:run

notification:
	cd notification-service && mvn spring-boot:run

user:
	cd user-service && mvn spring-boot:run

alert:
	cd alert-service && mvn spring-boot:run

inspection:
	cd inspection-service && mvn spring-boot:run

talk:
	cd talk-service && mvn spring-boot:run