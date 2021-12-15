package dev.buijs.klutter.plugins.adapter.codegenerator


/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
private const val generatedAdapterImportLine = "import dev.buijs.klutter.adapter.GeneratedKlutterAdapter"
private const val methodChannelImportLine = "import io.flutter.plugin.common.MethodChannel"

internal class KlutterActivityPrinter {

    fun print(metaFile: KtFileContent): FileContent {

        val source = metaFile.content.reader().readLines()
        val output = mutableListOf<String>()

        source.forEach { line -> output.add(line) }

        var packageLine: Int? = null
        var importsStartingLine: Int? = null
        var configureFlutterEngineLine: Int? = null
        var containsMethodChannelImport = false
        var containsGeneratedAdapterImport = false

        source.forEachIndexed { index, line ->
            when {
                line.startsWith("package ") -> {
                    packageLine = index
                }

                line.startsWith("import ") -> {
                    importsStartingLine = importsStartingLine ?: index

                    if(line.contains(methodChannelImportLine)){
                        containsMethodChannelImport = true
                    } else if(line.contains(generatedAdapterImportLine)){
                        containsGeneratedAdapterImport = true
                    }
                }

                line.contains("fun configureFlutterEngine") -> {
                    configureFlutterEngineLine = index
                }
            }
        }

        if(packageLine == null) {
            throw KlutterCodeGenerationException("""
                Could not determine package name for class containing @KlutterAdaptor annotation.
                Aborting code generation because this likely indicates a problem. 
                Please check the KlutterAdapterPlugin configuration in the root build.gradle(.kts) 
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(importsStartingLine == null) {
            throw KlutterCodeGenerationException("""
                No import statements found in class containing @KlutterAdapter annotation.
                Aborting code generation because this likely indicates a problem. 
                A MainActivity class should at least extend FlutterActivity which requires an import.
                Please check the KlutterAdapterPlugin configuration in the root build.gradle(.kts) 
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())
        }

        if(configureFlutterEngineLine == null) {
            throw KlutterCodeGenerationException("""
                Could not find a function in the MainActivity which has the name "configureFlutterEngine".
                Aborting code generation because this likely indicates a problem.
                Please check the KlutterAdapterPlugin configuration in the root build.gradle(.kts) 
                and verify the paths pointing to the flutter/android/app folder.
                Also verify if your Flutter project has no issues.
                """.trimIndent())}

        if(!containsMethodChannelImport){
            output.add(importsStartingLine!!, methodChannelImportLine)
        }

        if(!containsGeneratedAdapterImport){
            output.add(importsStartingLine!!, generatedAdapterImportLine)
        }

        output.add((configureFlutterEngineLine!! + 4), """        MethodChannel(flutterEngine.dartExecutor,"KLUTTER")""")
        output.add((configureFlutterEngineLine!! + 5), """            .setMethodCallHandler{ call, result ->""")
        output.add((configureFlutterEngineLine!! + 6), """                GeneratedKlutterAdapter().handleMethodCalls(call, result)""")
        output.add((configureFlutterEngineLine!! + 7), """            }""")

        return FileContent(file = metaFile.file, content = output.joinToString("\r\n"))
    }

}