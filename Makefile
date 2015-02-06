TARGET:=target/wikiforia-1.2.jar
SOURCE:=$(shell find src/ -type f)

.PHONY: all clean

all: $(TARGET) | target/

$(TARGET): $(SOURCE)
	mvn clean compile package install

clean:
	rm -rf target/



