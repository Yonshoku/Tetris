all: compile run

compile:
	mvn package

run: c:/code/jp/tetris/target/tetris-1.0-SNAPSHOT.jar
	java -jar c:/code/jp/tetris/target/tetris-1.0-SNAPSHOT.jar
