"""
Usage: CMG.py (-m MODULE) [-p PACKAGE]

Process FILE and optionally apply correction to either left-hand side or
right-hand side.

Arguments:
  MODULE                Mandatory input file
  PACKAGE               Correction angle, needs FILE, --left or --right to be present
  PATH                  Path to the future files location
  TESTPATH              Path to the future test files location

Options:
  -h --help
  -m MODULE             The name of the module to be generated
  -p PACKAGE            The name of the package in which the module will be placed
"""

import os
from docopt import docopt

TEMPLATE_PATH = "Chisel-Module-Generator/templates/"
DEFAULT_PATH = "src/main/scala/"
DEFAULT_TEST_PATH = "src/test/scala/"
EXTENSION = "scala"

def replace(packageName, moduleName, template):
    template = template.replace("<?@package@?>", packageName)
    template = template.replace("<?@module@?>", moduleName)
    return template

def read(fileName):
    readFile = open(TEMPLATE_PATH+fileName, "r")
    content = readFile.read()
    readFile.close()
    return content

def write(isATest, fileName, content):
    destinationPath = DEFAULT_TEST_PATH if(isATest) else DEFAULT_PATH
    completePath = "{}{}.{}".format(destinationPath, fileName, EXTENSION)
    writeFile = open(completePath, "w")
    writeFile.write(content)
    writeFile.close()

def strip(filename):
    return filename[len("module"):len(filename)-len(".txt")]

def readReplaceWrite(templateFile, packageName, moduleName):
    content = read(templateFile)
    content = replace(packageName, moduleName, content)
    filename = strip(templateFile)
    isATest = bool(len(filename))
    filename = "{0}/{0}{1}".format(packageName, filename)
    write(isATest, filename, content)

def manageArgs():
    args = docopt(__doc__)
    if(args['-p'] == None ):
        args['-p'] = args['-m'].lower()
    return args

def setUpPath(packageName):
    paths = ["{}{}/".format(path, packageName) for path in [DEFAULT_PATH, DEFAULT_TEST_PATH]]
    for directory in paths:
        if(not os.path.exists(directory)):
            os.makedirs(directory)

if (__name__ == '__main__'):
    args = manageArgs()
    setUpPath(args['-p'])
    templateFiles = os.listdir(TEMPLATE_PATH)
    for file in templateFiles:
        readReplaceWrite(file, args['-p'], args['-m'])
