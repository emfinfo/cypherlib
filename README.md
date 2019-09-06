# CypherLib 1.1.0 - august 2019
A Java library for encryption/decryption and hashing
- class AesUtil for encryp/decrypt with AES (for production);
- class AlphaSubsUtil for encryp/decrypt with alphabets substitution (for education);
- class XorUtil for encrypt/decrypt with bit manipulation (XOR, for education).
- class Util for decrypting Login data

You can download and open this project in NetBeans. It's a Java 8 maven project. So, dependencies are loaded automaticly from maven central. There are some test classes where you can learn how to use this library.

In MacOS terminal or Windows console, you can start the "test" suite with a Maven command :
- mvn test

You can check a specific test with (for example) :
- mvn test -Dtest=AesUtilTest

Documentation :<br>
    http://homepage.hispeed.ch/~jcsinfo/doc/cypherlib<br>

New in release 1.1.0 (14.8.2019) :
* New class helpers.Utils for decrypting login data

New in release 1.0.2 (25.8.2018) :
* Use GMT date (not local date) in Generate.passPhrase method

New in release 1.0.1 (17.2.2018) :
* First commit on GitHub

