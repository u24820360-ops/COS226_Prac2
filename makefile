Main.class : Main.java BakeryLock.java Counter.java Lock.java TaskGenerator.java VolatileInt.java VolatileBoolean.java 
	javac *.java

run : Main.class
	java Main

all : Main.class

clean:
	rm -f *.class && clear
