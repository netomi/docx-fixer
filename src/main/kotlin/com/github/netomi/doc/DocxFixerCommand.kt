/*
 *  Copyright (c) 2020-2022 Thomas Neidhart.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.netomi.doc

import org.kxml2.io.KXmlParser
import org.kxml2.io.KXmlSerializer
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import picocli.CommandLine
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStreamReader
import java.nio.file.*
import kotlin.io.path.*


/**
 * Command-line tool to fix docx files that have messed up xml content.
 */
@CommandLine.Command(
    name                 = "docx-fixer",
    description          = ["fixes docx files that have an xml tag mismatch upon loading."],
    parameterListHeading = "%nParameters:%n",
    optionListHeading    = "%nOptions:%n")
class DocxFixerCommand : Runnable {

    @CommandLine.Parameters(index = "0", arity = "1", paramLabel = "inputfile", description = ["input file to process (*.docx)"])
    private var inputFile: File? = null

    @CommandLine.Option(names = ["-o"], required = true, arity = "1", description = ["output file"])
    private var outputFile: File? = null

    override fun run() {
        inputFile?.apply {

            val inputFilePath  = toPath()
            val outputFilePath = outputFile!!.toPath()

            if (exists()) {
                if (outputFilePath.exists()) {
                    outputFilePath.deleteExisting()
                }
                Files.copy(inputFilePath, outputFilePath)

                FileSystems.newFileSystem(outputFilePath, null).use { fs: FileSystem ->
                    val fileInsideZipPath: Path = fs.getPath("word/document.xml")
                    if (fileInsideZipPath.exists()) {
                        println("fixing file '${fileInsideZipPath.name}'...")

                        val pp: XmlPullParser = KXmlParser()

                        // enable lenient mode that that will automatically close dangling tags.
                        pp.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true)
                        pp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)

                        val serializer: XmlSerializer = KXmlSerializer()
                        pp.setInput(InputStreamReader(fileInsideZipPath.inputStream()))

                        val outputStream = ByteArrayOutputStream()
                        serializer.setOutput(outputStream, null)

                        (Roundtrip(pp, serializer)).roundTrip()

                        println("replacing fixed file in output file '${outputFilePath.name}'...")
                        fileInsideZipPath.deleteExisting()
                        Files.copy(ByteArrayInputStream(outputStream.toByteArray()), fileInsideZipPath)

                        println("done fixing.")
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val cmdLine = CommandLine(DocxFixerCommand())
            cmdLine.execute(*args)
        }
    }
}