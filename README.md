# HARP (Written in Java)

HARP-Java is a Java library, GUI, and CLI for resolving local IPv4 addresses.

## Installation

1. Navigate to [adoptium.net](https://adoptium.net/temurin/releases/) and download either the JRE (Java Runtime Environment) or the JDK (Java Development Kit) depending on whether or not you want to build the project. Download the installer and run it (recommended) or download the .tar.gz file and run the java command from the bin folder.
2. Then download the latest release from [releases](https://github.com/KadTheAad/harp-java/releases). It should end in "-shaded.jar"

## Usage
There are 3 ways to use HARP-Java, as a Java Library, interfacing with the GUI, or using the CLI.

To open the GUI run the following command in your Terminal (On Mac/Linux):
```bash
java -jar <path-to-downloaded-jar>
```

To use HARP-Java through the Command Line Interface (CLI), you need to pass arguments to the jar file. Here is the basic command structure:
java -jar <path-to-downloaded-jar> [options]
The available options are:  
* `-s, --sender`: Run as a sender.
* `-i, --interface <interface>`: The network interface to use. By default, it uses the first available network interface.
* `-t, --to <address>`: The address of the other computer you are trying to connect to. This is a required argument.
* `-f, --from <address>`: Your address. This is a required argument.
* `-p, --password <password>`: The password that is used to verify the other computer and is used to create a secure connection. Must be the same on both computers. This is a required argument.
Here is an example of how to use it:
```bash
java -jar HARP-1.0-SNAPSHOT-shaded.jar \
     --sender \
     --interface en0 \
     --to Comp2 \
     --from Me \
     --password pass123
```
This command is used to run the HARP program from the command line with specific options. Here's what each part of the command does:  
* `java -jar HARP-1.0-SNAPSHOT-shaded.jar`: This part of the command is used to run the Java program contained in the `HARP-1.0-SNAPSHOT-shaded.jar` file. The `java -jar` command is used to run Java programs packaged as JAR files.  
* `--sender`: This option is used to specify that the program should run as a sender.  
* `--interface en0`: This option is used to specify the network interface that the program should use. In this case, it's set to `en0`.  
* `--to Comp2`: This option is used to specify the address of the other computer you are trying to connect to. In this case, it's set to `Comp2`.  
* `--from Me`: This option is used to specify your address. In this case, it's set to `Me`.  
* `--password pass123`: This option is used to specify the password that is used to verify the other computer and is used to create a secure connection. It must be the same on both computers. In this case, it's set to `pass123`.

The backslashes at the end of each line are used to split the command across multiple lines for readability. They are not part of the command itself and can be removed if the command is written on a single line.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[GNU LGPLv3](https://choosealicense.com/licenses/lgpl-3.0/)
