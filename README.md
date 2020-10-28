# CypherLib 1.2.2 - november 2020
A Java library for encryption/decryption and hashing
- class AES for encryp/decrypt with AES technique (for production);
- class AesUtil to check encrypted data
- class AlphaSubsUtil for encryp/decrypt with alphabets substitution (for education);
- class XorUtil for encrypt/decrypt with bit manipulation (XOR, for education).

You can download and open this project in NetBeans. It's a Java 8 maven project. So, dependencies are loaded automaticly from maven central. There are some test classes where you can learn how to use this library.

In MacOS terminal or Windows console, you can start the "test" suite with a Maven command :
- mvn test

You can check a specific test with (for example) :
- mvn test -Dtest=AesUtilTest

Documentation :<br>
    https://www.jcsinfo.ch/doc/cypherlib<br>

New in release 1.2.2 (28.10.2020) :
* update dependency version for "junit" (4.13.1) 

New in release 1.2.1 (30.3.2020) :
* new AesUtils.getLicenseProperties() method replace all get... license methods

New in release 1.2.0 (22.1.2020) :
* packages have been renamed with "ch.jcsinfo.cypher"
* grouid in pom.xml has been renamed : ch.emf.info to ch.jcsinfo.libs
* AesUtils has been renamed AES
* New class AesUtils

New in release 1.1.0 (14.8.2019) :
* New class helpers.Utils for decrypting login data

New in release 1.1.0 (14.8.2019) :
* New class helpers.Utils for decrypting login data

New in release 1.0.2 (25.8.2018) :
* Use GMT date (not local date) in Generate.passPhrase method

New in release 1.0.1 (17.2.2018) :
* First commit on GitHub
 
