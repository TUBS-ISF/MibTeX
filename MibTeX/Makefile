.PHONY: build run-typo3-export
version := 1.0

build:
	mvn package

run-typo3-export:
	java -jar target/MibTeX-$(version)-SNAPSHOT.jar ../config/typo3.ini
