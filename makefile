JFLAGS = -g
JC = javac  
JVM= java

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

all: classes run create

CLASSES = \
	Utility.java \
	HttpRequestProcessor.java \
	BasicHttpServer.java 
    
MANIFEST = $(wildcard *.MF)  

default: classes

classes: $(CLASSES:.java=.class)

run: $(classes) $(MANIFEST)
	jar cvfm server.jar $(MANIFEST) $(classes)

create: server.jar createexecutable.sh
	cat createexecutable.sh server.jar > server && chmod +x server
clean:
	$(RM) *.class
