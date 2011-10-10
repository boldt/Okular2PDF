Okular2PDF
===
### Background/About this project

Okular does not support the export of an annotated PDF to another PDF
or the printing of an annotated PDF (October 2010, see the resources below).
It is just possible to export the annotations and the PDF to an "Okular
Document Archive". Based on this archive, I decided to create this small
project to get the "Yellow Higlighter" exported to a new PDF. This annotated
PDF can be opened with other PDF readers and it can be printed as well.

### Get it:

	git clone git://github.com/boldt/Okular2PDF.git

### Build it:

    cd Okular2PDF
    mvn package

### Run it:

	java -jar target/Okular2PDF-*.jar NAME.okular

You will get the annotated PDF in the same directory as the NAME.okular:

    NAME.okular.annotated.pdf

Resources
---
  - [Home of Okular2PDF](http://www.dennis-boldt.de/Okular2PDF)
  - [Bug 151614 - store annotations with documents](http://bugs.kde.org/show_bug.cgi?id=151614)
  - [Bug 159005 - Print document with annotations](http://bugs.kde.org/show_bug.cgi?id=159005)