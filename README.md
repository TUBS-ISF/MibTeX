# MibTeX
Minimalistic tool to manage your references with BibTeX

There are many BibTeX management tools out there that read and write BibTeX files, and thus helping to manage a literature database. However, they often alter BibTeX entries in unwanted and unforeseen ways. As this can easily destroy references in scientific publications, we aim to provide a solution that is more minimalistic: MibTeX.

With MibTeX, you are supposed to write and edit the BibTeX file on your own and have full control over it. MibTeX helps you to translate your literature into several other formats that are more suitable for certain tasks. For example, there is an export to an HTML page, which can be used to explore your literature database. It can list all publications by a certain author or conference. You can sort publications by year or number of Google scholar citations. Furthermore, you can define your own tags to classify the literature according to your needs.

Besides the export to HTML, there is an export to CSV, which is useful to embed certain references on a website. Also, there is a bot that continuously updates Google scholar citations for all articles in the literature database.

## Setup Instructions

MibTeX is implemented in Java and comes as an Eclipse/Maven project.
Install and run it with `./MibTeX.sh` (Maven required).
As MibTeX requires numerous paths on your local system there are two ways to specify those settings by a configuration file.

Create a configuration file (by default, `options.ini` in the root directory) specifying the required paths:
```
[options]
bibtex-dir=[absolute path to your literature.bib file]
main-dir=[absolute path to your output folder]
out-dir-rel=[relative path to the created html page]
pdf-dir=[absolute path to the PDFs for your BibTeX entries]
pdf-dir-rel=[relative path to the PDFs for your BibTeX entries]
tags=[list of BibTeX tags you want to use on the website]
clean=[value true if you want to have the output directory cleaned before export]
citationService=[value true if you want to start the bot that reads from Google scholar]
citation-dir=[absolute path to the file that contains the file with the Google scholar citations]
out-format=[HTML_NEW for output as HTML page, see code for more options]
```

Here is an `example.ini` that contains real paths:
```
[options]
bibtex-dir=C:\\Users\\tthuem\\git\\BibTags\\
main-dir=C:\\Users\\tthuem\\git\\Literature\\Library\\
out-dir-rel=
pdf-dir=
pdf-dir-rel=
tags=tt-tags,tc-tags,sampling-tags,sampling2-tags,sampling3-tags,sampling4-tags,sampling5-tags,sampling6-tags,sampling7-tags,sampling8-tags,sampling9-tags,sampling10-tags
clean=false
citationService=false
citation-dir=C:\\Users\\tthuem\\git\\BibTags\\classification\\
out-format=HTML_NEW
```

## Running MibTeX (from Command Line)

### Running MibTeX Manually

1. Navigate with the Terminal to the `MibTeX` directory within this Git repository. You are at the right location when `ls` lists a `pom.xml` file.
2. You can build with Maven. The following Maven command will build a Jar file for you to run:
    ```shell
    mvn package
    ```
3. The generated Jar file is located in the `target` directory, which is Maven's build directory. You can run the file directly or copy it to another place beforehand. The first argument to MibTeX should be a path to your ini file. By default, we group those files in the `config` directory at the root of this repository.
    ```shell
	java -jar target/MibTeX-1.0-SNAPSHOT.jar ../config/typo3.ini
    ```

### Running MibTeX via Make

All the above commands are documented in our `Makefile` which resides right next to the `pom.xml` for Maven.
* To build run: `make build`
* To clean the build files run: `make clean`
* To run the Typo3 export: `make run-typo3-export`. This will fail in case you do not have a respective config file in `config/typo3.ini`. If your config is another directory, run MibTeX manually with it (see above) or adapt the Makefile (but do not commit those changes!).
