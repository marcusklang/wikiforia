Forked Changes
==============

Added support for Plain Text output format on top of existing XML format. Use Case: Needed support to extract text only from the Wikipedia in order to use it as a Corpus for different Machine Learning experiments.

To run it: Download wikiforia-x.y.z.jar from dist/ directory, open your terminal, go/cd to download location and run

	java -jar wikiforia-x.y.z.jar 
	     -pages [path to the file ending with multistream.xml.bz2] 
	     -output [output xml path]
	     -outputformat plain-text



Read Original Wikiforia README.md [here](https://github.com/marcusklang/wikiforia)
