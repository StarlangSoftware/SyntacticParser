# SyntacticParser

This tool analyzes the syntactic structure of a sentence by breaking it down into components and establishing the grammatical relations between said components.

Video Lectures
============

[<img src=video1.jpg width="50%">](https://youtu.be/4erjWXDlJRI)[<img src=video2.jpg width="50%">](https://youtu.be/XwmCs21kosU)[<img src=video3.jpg width="50%">](https://youtu.be/2pWtrpLGYTA)[<img src=video4.jpg width="50%">](https://youtu.be/ajF3p1p6yMM)

Class Diagram
============

<img src="classDiagram.png">

For Developers
============

## Requirements

* [Java Development Kit 8 or higher](#java), Open JDK or Oracle JDK
* [Maven](#maven)
* [Git](#git)

### Java 

To check if you have a compatible version of Java installed, use the following command:

    java -version
    
If you don't have a compatible version, you can download either [Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/install/)    

### Maven
To check if you have Maven installed, use the following command:

    mvn --version
    
To install Maven, you can follow the instructions [here](https://maven.apache.org/install.html).      

### Git

Install the [latest version of Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

## Download Code

In order to work on code, create a fork from GitHub page. 
Use Git for cloning the code to your local or below line for Ubuntu:

	git clone <your-fork-git-link>

A directory called SyntacticParser will be created. Or you can use below link for exploring the code:

	git clone https://github.com/starlangsoftware/SyntacticParser.git

## Open project with IntelliJ IDEA

Steps for opening the cloned project:

* Start IDE
* Select **File | Open** from main menu
* Choose `SyntacticParser/pom.xml` file
* Select open as project option
* Couple of seconds, dependencies with Maven will be downloaded. 


## Compile

**From IDE**

After being done with the downloading and Maven indexing, select **Build Project** option from **Build** menu. After compilation process, user can run SyntacticParser.

**From Console**

Go to `SyntacticParser` directory and compile with 

     mvn compile 

## Generating jar files

**From IDE**

Use `package` of 'Lifecycle' from maven window on the right and from `SyntacticParser` root module.

**From Console**

Use below line to generate jar file:

     mvn install



------------------------------------------------

SyntacticParser
============
+ [Maven Usage](#maven-usage)


### Maven Usage

        <dependency>
            <groupId>io.github.starlangsoftware</groupId>
            <artifactId>SyntacticParser</artifactId>
            <version>1.0.0</version>
        </dependency>
