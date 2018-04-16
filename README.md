# pint-publisher

Pint (= "Pint Is Not TeX") is a formatting tool designed to showcase the following ideas:

* editable formats for the exchange of print-oriented documents with robust formatting (such as Hybrid PDF)
* user-friendly, WYSIWYG editability of structured, implicitly laid out documents using a two-step approach

It can also be used to simply create PDFs from XML or directly from Java code.

The tool is currently at a very early stage of development. Compiling requires PDFBox 2.0.8. If you have any questions or would like to contribute at this stage, it's best to send me an email. More documentation and a build will come soon.

A sample file is included: 

In order to create a PDF from scratch, type the following:

```bash
$ java com.tamirhassan.publisher.Publisher sample-article.xml
```

This will create the PDF, as well as an annotated content file (sample-article-flex.xml) and layout file (sample-article-phys.xml).

In order to edit this document, you can edit the annotated content file and run:

```bash
$ java com.tamirhassan.publisher.Publisher -edit sample-article
```

This will combine the new annotated content file with the existing layout and typeset the content into the existing frames, where possible.

## Features

* Knuth-Plass linebreaking
* Inclusion of bitmap graphics
* Basic figure placement
* Multiple-column text
* X-Y decomposable layouts
* Support for OpenType CFF fonts (but no kerning at the moment)

## Roadmap

* Generation of hybrid, editable PDFs
* Balanced columns
* Baseline grid
* Handling of exceptions to deal with layout errors
* Use of alternative fonts where glyphs/symbols are missing
* Placement of new frames in edit mode
* Indentations
* Inclusion of vector graphics (and appropriate format conversion)
* Paragraph alignments other than justified
* Ruling lines
* Tables
