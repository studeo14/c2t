package edu.vt.cas2text;

import picocli.CommandLine;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.vt.cas2text.cli.CLIOptions;
import edu.vt.cas2text.CasReader;

import java.io.File;
import java.io.IOException;

class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String... args) {
        var options = new CLIOptions();
        var cli = new CommandLine(options);
        try {
            cli.parseArgs(args);
            handleCli(cli, options);
        } catch (CommandLine.MissingParameterException | IOException e) {
            logger.error(e);
        }
    }

    private static void handleCli(CommandLine cli, CLIOptions options) throws IOException {
        if (cli.isUsageHelpRequested()) {
            cli.usage(System.out);
        } else if (options.outputFile != null) {
            CasReader.readCas(options.inputFile, options.outputFile, options.useHeaders);
        } else {
            File actualOutput = new File(options.inputFile.getName() + ".txt");
            CasReader.readCas(options.inputFile, actualOutput, options.useHeaders);
        }
    }
}
