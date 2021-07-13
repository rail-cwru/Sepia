Sepia example documentation
---------------------------

TODO: JDK, Maven, and Eclipse installation instructions

Sepia is maintained as a Maven project. The use of Maven allows for
some nice project management features, including dependency
management.

Since Sepia is not yet published in the Maven Central repository, it
must be built and installed into the local user repository, first.

1. clone Sepia github repository

   open a terminal

   Change to your desired work location:

     cd ~/src/cwru/csds391/

   Clone the Sepia source git repository:

     git clone https://github.com/rail-cwru/Sepia.git

   Switch to the maven-enabled branch:

     cd Sepia

     git checkout mira-java11 # until merged

2. build and install Sepia:

     mvn -DskipTests=true clean package source:jar javadoc:jar install

3. switch to branch with example project:

     git checkout mira-maven-example

4. import into eclipse:
   - File > Import...
   - Maven > Existing Maven Projects
   - pick "example-project" directory

   Students can put main program source code in src/main/java, and
   unit tests in src/test/java.
