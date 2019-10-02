package edu.vt.cas2text.cli;

import java.io.File;

import picocli.CommandLine.Option;

public class CLIOptions {
    @Option(names={"-f", "--file"}, required=true, paramLabel="INPUTFILE", description="the input cas file")
    public File inputFile;

    @Option(names={"-o", "--output"}, paramLabel="OUTPUTFILE", description="the output text file")
    public File outputFile;

    @Option(names={"-h", "--help"}, usageHelp=true, description="display this help message")
    private boolean usageHelpRequested;
}
