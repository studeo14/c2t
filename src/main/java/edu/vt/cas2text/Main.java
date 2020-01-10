package edu.vt.cas2text;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import edu.vt.cas2text.cli.CLIOptions;

import java.io.IOException;

class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        var options = new CLIOptions();
        var cli = new CommandLine(options);
        try {
            cli.parseArgs(args);
            handleCli(cli, options);
        } catch (CommandLine.MissingParameterException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void handleCli(CommandLine cli, CLIOptions options) throws IOException {
        if (cli.isUsageHelpRequested()) {
            cli.usage(System.out);
        } else {
            if (options.verbose) {
                Configurator.setAllLevels("edu.vt", Level.DEBUG);
            }
            CasReader.readCas(options.inputFile);
        }
    }
}
