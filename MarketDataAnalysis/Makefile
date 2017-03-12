
# Source files
SOURCES  = $(shell ls src/*.cpp src/*.h)

# Compiler
CXX      := g++
CXXFLAGS := -std=c++11 -stdlib=libc++

TARGET := bin/ob_app

${TARGET}:  ${SOURCES}
	${CXX} ${CXXFLAGS} -c src/*.cpp
	${CXX} ${CXXFLAGS} -o ${TARGET} *.o
	rm -f *.o

run:  ${TARGET}
	${TARGET} test_data.txt 2>&1

clean:
	rm -f ${TARGET} *.o *.gch *.log *~ bin/*~ 
