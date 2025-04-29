# MibTeX
Minimalistic tool to manage your references with BibTeX

There are many BibTeX management tools out there that read and write BibTeX files, and thus helping to manage a literature database. However, they often alter BibTeX entries in unwanted and unforeseen ways. As this can easily destroy references in scientific publications, we aim to provide a solution that is more minimalistic: MibTeX.

With MibTeX, you are supposed to write and edit the BibTeX file on your own and have full control over it. MibTeX helps you to translate your literature into several other formats that are more suitable for certain tasks. For example, there is an export to an HTML page, which can be used to explore your literature database. It can list all publications by a certain author or conference. You can sort publications by year or number of Google scholar citations. Furthermore, you can define your own tags to classify the literature according to your needs.

Besides the export to HTML, there is an export to CSV, which is useful to embed certain references on a website. Also, there is a bot that continuously updates Google scholar citations for all articles in the literature database.

## Setup Instructions

MibTeX is implemented in Java and comes as an Eclipse/Maven project.
Install and run it with `./MibTeX.sh` (Maven required).
As MibTeX requires numerous paths on your local system there are two ways to specify those settings: as command-line parameters or by a configuration file. Of those two options, we recommend the configuration file.

### Option 1: Configuration File (recommended)
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

Run MibTeX like this:
`java.exe -cp bin;lib/* "de.mibtex.BibtexViewer" "C:\\Users\\tthuem\\Tools\\example.ini"`
