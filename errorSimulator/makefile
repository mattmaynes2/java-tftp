# Make Core
#
# Makes the core classes and bundles them
#
# @author Matthew Maynes

#
# Name of this project
#
NAME = host

#
# The version should be updated per each new release build. The output jar
# will be labeled with the current version
#
VERSION = $(shell if [ -e VERSION ]; then cat VERSION; else echo "0.0.0"; fi)

#
# Java Packages
#
PACKAGES = $(shell if [ -e PACKAGES ]; then cat PACKAGES; else echo "."; fi)

#
# Java Dependencies
#
DEPS = $(shell if [ -e DEPS ]; then cat DEPS; else echo ""; fi)

#
# Executable Scripts
#
SCRIPTS = $(shell if [ -e SCRIPTS ]; then cat SCRIPTS; else echo ""; fi)

#
# Compilers
#
JC 		= javac
JDOC 	= javadoc
JAR 	= jar

#
# Key Directories
#
SRC = src
BIN = bin
LIB = lib
OUT = out
DOC = doc

#
# Flags
#
FLAGS = -classpath $(LIB)/core.jar:$(BIN):. -sourcepath $(SRC)

#
# The default directive is used when only make is called. It
# class all of the other directives
#
default: classes

#
# Cleans the project and compiles all directives in the make file
# except for the test directive
#
release: clean classes javadoc jar

#
# Moves executable scripts from script locations to bin folder
#
sh:
	if [ "$(SCRIPTS)" ]; then cp $(SCRIPTS) $(BIN); fi 

#
# Copy the dependencies for the project
#
dep: 
	rm -f $(LIB)/*
	if [ "$(DEPS)" ]; then cp $(DEPS) $(LIB); fi

#
# Compiles all of the classes in the project
#
classes: dep
	$(JC) $(FLAGS) -d $(BIN) $(foreach package, $(PACKAGES), $(SRC)/$(package)/*.java)

#
# Compiles the project verbosely with all of the java compiler messages
#
verbose: sh
	$(JC) -version -Xlint $(FLAGS) -d $(BIN) $(foreach package, $(PACKAGES), $(SRC)/$(package)/*.java)
	$(JC) -version -Xlint $(FLAGS) -d $(BIN) $(foreach package, $(TESTPACKAGES), $(SRC)/$(package)/*.java)

#
# Compiles and outputs all of the javadoc into the API folder
#
javadoc:
	$(JDOC) $(FLAGS) -d $(DOC) $(foreach package, $(PACKAGES), $(SRC)/$(package)/*.java)

#
# Compiles all of the tests in the project
#
tests: sh
	$(JC) $(FLAGS) -d $(BIN) $(foreach package, $(TESTPACKAGES), $(SRC)/$(package)/*.java)

#
# Jars the project into a convenient jar output
#
jar:
	cd $(BIN) && $(JAR) cvf ../$(OUT)/$(NAME)-$(VERSION).jar $(foreach package, $(PACKAGES), $(package)/*.class)


#
# Cleans the project
#
clean:
	cd $(BIN) && rm -f $(foreach package, $(PACKAGES), $(package)/*.class)
	find . -name *~ -exec rm -rf {} \;

