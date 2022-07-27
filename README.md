# About

**docx-fixer** is a small tool to fix problems with corrupted `docx` documents. Nowadays, the content of a docx file is mainly stored in xml format,
which in some cases might be ill-formed, leading to a situation where it is not possible anymore to open it with word processors or other xml related tools.

A typical error message where this tool can help is (from Libreoffice):

```
SAXException: [word/document.xml line 2]: Opening and ending tag mismatch: Fallback line 0 and p
```

The tool might be able to help fix the problem by making sure that the embedded xml content is well-formed again, allowing to open the file correctly again.

# Usage

```bash
Usage: docx-fixer -o=<outputFile> inputfile
fixes docx files that have an xml tag mismatch upon loading.

Parameters:
      inputfile      input file to process (*.docx)

Options:
  -o=<outputFile>    output file
```