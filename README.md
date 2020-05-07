# CS562 EMF and MF Query Processor

## Environment and Packages

- IDE: NetBeans 8.2 and project is built based on Ant build script

- Packages/libraries:

  - postgresql-8.3-604.jdbc4.jar;
  - commons-lang3-3.10.jar;
  - java-json.jar;
  - commons-io-2.6.jar;
  - JDK 1.8(java 8)

## Structure of files:

- Standard SQLs for standard SQL;
- JARS for external jar used in my project;
- QueryProcessor is where my project stores

## Instructions:

Please check comments of the project in the:

- `./QueryProcessor/src/QueryProcessorForMFandEMF/CodeGenerator.java` (Top Level where main is)
- and `./QueryProcessor/src/QueryProcessorForMFandEMF/Processor.java` (Core part)

About running:
Here I built the runnable jar in the `./QueryProcessor/dist/QueryProcessor.jar`:

- Go to the file: run `java -cp "QueryProcessor.jar:lib/*" QueryProcessorForMFandEMF.CodeGenerator`. You can get the generated code by instruction there.

- After the file is generated. You can check the output code in the `./QueryProcessor/dist/src/outputFile`. To run any of the output code. Go to this file and run `java -cp "../../lib/*" {OUTPUTFILE_NAME}.java`.
