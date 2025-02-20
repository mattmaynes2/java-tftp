# Make Core
#
# Makes the core classes and bundles them
#
# @author Matthew Maynes

#
# Name of this project
#
NAME = core

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
FLAGS = -classpath $(LIB):$(BIN):. -sourcepath $(SRC)

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
# Compiles all of the classes in the project
#
classes: sh
	mkdir -p $(BIN)
	$(JC) $(FLAGS) -d $(BIN) $(foreach package, $(PACKAGES), $(SRC)/$(package)/*.java)
#
# Compiles the project verbosely with all of the java compiler messages
#
verbose: sh
	$(JC) -version -Xlint:all $(FLAGS) -d $(BIN) $(foreach package, $(PACKAGES), $(SRC)/$(package)/*.java)
	$(JC) -version -Xlint:all $(FLAGS) -d $(BIN) $(foreach package, $(TESTPACKAGES), $(SRC)/$(package)/*.java)

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
	mkdir -p $(OUT)
	cd $(BIN) && $(JAR) cvf ../$(OUT)/$(NAME)-$(VERSION).jar $(foreach package, $(PACKAGES), $(package)/*.class)
	if [ -e $(OUT)/$(NAME).jar ]; then rm $(OUT)/$(NAME).jar; fi
	cd $(OUT) && ln -s $(NAME)-$(VERSION).jar $(NAME).jar

#
# Cleans the project
#
clean:
	mkdir -p $(BIN)
	cd $(BIN) && rm -f $(foreach package, $(PACKAGES), $(package)/*.class)
	find . -name *~ -exec rm -rf {} \;

