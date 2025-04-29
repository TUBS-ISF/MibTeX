.PHONY: build run-typo3-export
version := 1.0
jar := target/MibTeX-$(version)-SNAPSHOT.jar

clean:
	mvn clean

build:
	mvn package

run-typo3-export: build
	java -jar ${jar} ../config/typo3.ini
