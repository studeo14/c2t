#!/usr/bin/env python3

import os, subprocess, glob
from pathlib import Path

def get_tests():
    (_, tests, _) = next(os.walk("./tests"))
    return tests

def run_inputs(tests):
    for test in tests:
        inFile = "./tests/{}/in.xmi".format(test)
        resFile = "./tests/{}/in.project".format(test)
        # check for raw text test
        if Path(inFile).exists():
            res = subprocess.run(["java", "-jar", "./cas2text.jar", "-f", inFile])
            os.rename("./in.project", resFile)
        else:
            inFile = "./tests/{}/in.txt".format(test)
            if Path(inFile).exists():
                res = subprocess.run(["java", "-jar", "./cas2text.jar", "-tf", inFile])
                os.rename("./in.project", resFile)
            else:
                print("Error, infile not found!")


def run_compare(tests):
    results = []
    for test in tests:
        inFile = "./tests/{}/in.project".format(test)
        baseFile = "./tests/{}/out.project".format(test)
        res = subprocess.run(["java", "-jar", "./tests/RegressionTester.jar", "-b", baseFile, "-t", inFile])
        if res.returncode != 0:
            os.rename("./in.diffs", "./tests/{}.diffs".format(test))
            results.append((test, False))
        else:
            results.append((test, True))
    return results

def show_results(results):
    for result in results:
        print("{}\t:\t{}".format(result[0], "Pass" if result[1] else "Fail, see tests/{}.diffs for more info.".format(result[0])))

def main():
    tests = get_tests()
    run_inputs(tests)
    results = run_compare(tests)
    show_results(results)

if __name__ == "__main__":
    main()
