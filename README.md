# pint-publisher

Pint (= "Pint Is Not TeX") is a formatting tool designed to showcase the following ideas:

* editable formats for the exchange of print-oriented documents with robust formatting (such as Hybrid PDF)
* user-friendly, WYSIWYG editability of structured, implicitly laid out documents using a two-step approach

It can also be used to simply create PDFs from XML or directly from Java code.

The tool is currently at a very early stage of development. Compiling requires PDFBox 2.0.8. More documentation will come soon.

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
