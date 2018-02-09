# Salesforce Commerce Cloud cc-code-analyzer

The cc-code-analyzer is intended for SFCC developers who must deal with cache customizations.
SFCC allows caching of ISML templates and the implemented include logic may sometimes be confusing to find the effective cache.

## Installing the plug-in

1. Download the [CodeAnalyzer.p2-1.1.4-SNAPSHOT.zip](https://github.com/tmontovert/cc-code-analyzer/blob/master/CodeAnalyzer.p2/target/CodeAnalyzer.p2-1.1.4-SNAPSHOT.zip) file.
2. Locally unzip the file
3. Add the extracted repository as an update site within Help > Install New Software.
3. Install the Code Analyzer
4. restart eclipse

## Usage

1. Open eclipse with the SFCC project.
2. Right click on any file of the projet and select Code Analyzer > Settings.
3. Fill out the field with the cartridge path of the studied code base.
4. Right click on a .isml file and select Code Analyzer > Analyze to proceed the caching analysis.
5. The output of the analysis can be seen in the console (select Window > Show View > Console if not enabled).

## Development setup

1. Make sure you have installed 
	- Eclipse SDK
	- [maven](http://download.eclipse.org/technology/m2e/releases) via update site
	- [git](http://download.eclipse.org/egit/github/updates) via update site
2. Import the code into the workspace.
3. Double click on the pom.xml to show the required dependencies.
4. Select all dependencies and click finish to install all the dependencies.
5. Restart eclipse to complete the installation of the dependencies.

### Build

1. To build the project, right click on the CodeAnalyzer.releng project and select Run as > Maven install.

### Tests

1. To test the project, locate the repository ../cc-code-analyzer/CodeAnalyzer.p2/target/repository/ 
2. Add it as an update site within Help > Install New Software.
3. Restart eclipse to have the plugin installed from the specified repository.

### Tip notes

You can launch another instance with the currently built version of the plugin (open -n Eclipse.app to open a second eclipse instance on MacOS).

To run maven install successfully within Eclipse, you may need to modify some the compiler settings from error to warning only under:
		Eclipse > Preferences > Plug-in Development > Compilers

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details
