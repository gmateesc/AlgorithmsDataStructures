
builddir = ../classes
project = gabriel/patternsearch

app  := PatternSearch
lib  := lib/derby-10.11.1.1.jar

all:

	@echo " "
	@echo "Making gabriel/patternsearch code"
	mkdir -p classes
	cd src && javac  -d ${builddir} -Xlint:unchecked  ${project}/service/*.java
	cd src && javac  -d ${builddir} -Xlint:unchecked  ${project}/client/*.java
	cd src && javac  -d ${builddir} -Xlint:unchecked  ${project}/injector/*.java
	cd src && javac  -d ${builddir} -Xlint:unchecked  ${project}/${app}.java
	@echo " "
	@echo "Running ${app} app"
	java -cp .:classes:${lib} ${project}/${app}



clean:
	rm -rf classes derby*
