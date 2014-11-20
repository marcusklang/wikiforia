Wikiforia
=========

What is it?
-----------
It is a library and a tool for parsing Wikipedia XML dumps and converting them into plain text for other tools to use.

Why use it?
-----------
Subjectivly generates good results and is reasonably fast, on my laptop (4 physical cores, 8 logical threads, 2.3 Ghz Haswell Core i7) it achieves an average of 6000 pages/sec or 10 minutes for a 2014-08-18 Swedish Wikipedia dump. Your results may of course vary.

How to use?
-----------
Download a multistreamed wikipedia bzip2 dump. It consists of two files: one index and one with the pages.

For a Swedish Wikipedia dump 2014-08-18 it has the following file names:

	svwiki-20140818-pages-articles-multistream-index.txt.bz2
	svwiki-20140818-pages-articles-multistream.xml.bz2

Make sure their names are intact because otherwise the automatic language resolving does not work and it will fall back on English.

When the files have been downloaded and placed in the same directory.

Simply go to the dist/ directory and run

	java -jar wikiforia-1.0-SNAPSHOT.jar -pages [path to the file ending with multistream.xml.bz2] -output [output xml path]

This will run with default settings i.e. the number of cores you have and a batch size of 100. These settings can be overriden, for a full listing just run:

	java -jar wikiforia-1.0-SNAPSHOT.jar

API
---
The code can also be used directly to extract more information.

More information about this will be added, but for now take a look at se.lth.cs.nlp.wikiforia.App and the convert method to get an idea of how to use the code.

Credits
-------
**Peter Exner**, the author of [KOSHIK](https://github.com/peterexner/KOSHIK), the Sweble code is partially based by the KOSHIK version.

**[Sweble](http://sweble.org)**, developed by the Open Source Research Group at the Friedrich-Alexander-University of Erlangen-Nuremberg. This library is used to parse the Wikimarkup.

**[Woodstox](http://woodstox.codehaus.org)**, Quick XML parser, used to parse the XML and write XML output.

**[Apache Commons](http://commons.apache.org)**, a collection of useful and excellent libraries. Used CLI for the options.

Licence
-------
The license is GPLv2.
