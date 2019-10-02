# CAS 2 Text
Project that converts a JPDF_PARSER generated cas to a text format that the datasheet_parser can use to create a new project.

# How to use

1. Create an exported cas from the jpdf-parser from GRAF.
2. Use the `./run.sh` script to run the project
3. Transfer the rsulting text file as input to the datasheet parser.

## Command line options

From `./run.sh -h`:
```text
Usage: ./run.sh [-h] -f=INPUTFILE [-o=OUTPUTFILE]
  -f, --file=INPUTFILE      the input cas file
  -h, --help                display this help message
  -o, --output=OUTPUTFILE   the output text file
```

### -f, --file=INPUTFILE
This is the input cas file from jpdf. This is a required option.

### -o, --output=OUTPUTFILE
THis is the name of the output text file. If this is not given, the output file will be `INPUTFILE.txt`.