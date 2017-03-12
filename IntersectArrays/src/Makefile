

.SUFFIXES: .java .class

package := gabriel/intersection
pack    := $(subst /,., $(package))

#SOURCES := $(shell ls ${package}/alg/*java | sed 's/.*\///g')
SOURCES := $(shell ls ${package}/alg/*java)
CLASSES := $(subst .java,.class, $(SOURCES))

#SOURCES_UI := $(shell ls ${package}/ui/*java | sed 's/.*\///g')
SOURCES_UI := $(shell ls ${package}/ui/*java)
CLASSES_UI := $(subst .java,.class, $(SOURCES_UI))


#opts := -Xlint:unchecked
#rt_opts := -Drandom.range=55000 



#
# Targets
#

.PHONY: all ui alg jar zip run test clean

all: run


#
# Alg
#
#alg:
#	javac ${package}/alg/IntersectArrays.java

alg: ${CLASSES}

# Use this rule for 
#  SOURCES := $(shell ls ${package}/alg/*java)
#
${package}/alg/%.class: ${package}/alg/%.java
	javac ${opts} ${package}/alg/$*.java

# Use this rule for 
#  SOURCES := $(shell ls ${package}/alg/*java | sed 's/.*\///g')
#
#%.class: ${package}/alg/%.java
#	javac ${opts} ${package}/alg/$*.java



#
# UI
#

#ui: 
#	javac ${package}/ui/IntersectUI.java 

ui: ${CLASSES_UI}


# Use this rule for 
#  SOURCES_UI := $(shell ls ${package}/ui/*java)
#
${package}/ui/%.class: ${package}/ui/%.java
	javac ${opts} ${package}/ui/$*.java


# Use this rule for 
#  SOURCES_UI := $(shell ls ${package}/ui/*java | sed 's/.*\///g')
#
#%.class: ${package}/ui/%.java
#	javac ${opts} ${package}/ui/$*.java


#
# Jar
#
jar: alg ui
	 jar cvfm IntersectArraysApp.jar manifest.txt gabriel


#
# Run
#
run: jar
	#java ${rt_opts} ${pack}.ui.IntersectUI
	java -jar IntersectArraysApp.jar 


#
# Test the intersect algorithm
#
test: alg
	java ${pack}.alg.IntersectArrays


#
# Clean
#
clean:
	rm -rf *~ */*~ */*/*~ */*/*/*~  */*.class */*/*.class */*/*/*.class

#
# Zip
#
zip: clean
	cd ../ && zip -r IntersectArraysApp_src.zip src && cd -


debug:
	@echo "SOURCES    = ${SOURCES}"
	@echo "SOURCES_UI = ${SOURCES_UI}"
	@echo "CLASSES    = ${CLASSES}"
	@echo "CLASSES_UI = ${CLASSES_UI}"

