package com.github.netomi.doc

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer
import java.io.IOException

class Roundtrip(
    var parser:     XmlPullParser,
    var serializer: XmlSerializer) {

    @Throws(XmlPullParserException::class, IOException::class)
    fun writeStartTag() {
        if (!parser.getFeature(XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES)) {
            var i = parser.getNamespaceCount(parser.depth - 1)
            while (i < parser.getNamespaceCount(parser.depth) - 1) {
                serializer.setPrefix(parser.getNamespacePrefix(i), parser.getNamespaceUri(i))
                i++
            }
        }

        serializer.startTag(parser.namespace, parser.name)

        for (i in 0 until parser.attributeCount) {
            serializer.attribute(
                parser.getAttributeNamespace(i),
                parser.getAttributeName(i),
                parser.getAttributeValue(i)
            )
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun writeToken() {
        when (parser.eventType) {
            XmlPullParser.START_DOCUMENT -> serializer.startDocument(null, null)

            XmlPullParser.END_DOCUMENT -> serializer.endDocument()

            XmlPullParser.START_TAG -> writeStartTag()

            XmlPullParser.END_TAG -> serializer.endTag(parser.namespace, parser.name)
            XmlPullParser.IGNORABLE_WHITESPACE -> serializer.ignorableWhitespace(parser.text)
            XmlPullParser.TEXT -> if (parser.text == null) System.err.println("null text error at: " + parser.positionDescription) else serializer.text(parser.text)
            XmlPullParser.ENTITY_REF -> if (parser.text != null) serializer.text(parser.text) else serializer.entityRef(parser.name)
            XmlPullParser.CDSECT -> serializer.cdsect(parser.text)
            XmlPullParser.PROCESSING_INSTRUCTION -> serializer.processingInstruction(parser.text)
            XmlPullParser.COMMENT -> serializer.comment(parser.text)
            XmlPullParser.DOCDECL -> serializer.docdecl(parser.text)

            else -> throw RuntimeException("unrecognized event: " + parser.eventType)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun roundTrip() {
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            writeToken()
            parser.nextToken()
        }
        writeToken()
        serializer.flush()
    }
}