default: all

all:
	cd core && make clean && make && make jar
	cd client && make clean && make
	cd server && make clean && make
	cd errorSimulator && make clean && make

clean:
	cd core && make clean 
	cd client && make clean
	cd server && make clean
	cd errorSimulator && make clean

javadoc:
	cd core && make javadoc
	cd client && make javadoc
	cd server && make javadoc
	cd errorSimulator && make javadoc
