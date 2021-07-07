package com.bug.processor

import com.bug.annotations.BindInMultiBindModule
import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import java.lang.Exception
import java.lang.RuntimeException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class GenerateMultiBindModuleProcessor : AbstractProcessor() {

    private lateinit var processingEnvironment: ProcessingEnvironment

    private lateinit var filer: Filer
    private lateinit var messager: Messager

    override fun getSupportedSourceVersion(): SourceVersion? {
        return SourceVersion.latestSupported()
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        processingEnvironment = processingEnv
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(BindInMultiBindModule::class.java.canonicalName)

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(BindInMultiBindModule::class.java)
        if (elements.isEmpty()) {
            return false
        }
        val className = "GeneratedMultiBindModule"
        val typeSpec = TypeSpec.classBuilder(className)
            .addAnnotation(Module::class.java)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .apply {
                val methods = mutableListOf<MethodSpec>()
                elements.forEach { element ->
                    addOriginatingElement(element)

                    val method = MethodSpec.methodBuilder("binds${element.simpleName}")
                        .addModifiers(Modifier.ABSTRACT)
                        .addAnnotation(Binds::class.java)
                        .addAnnotation(IntoMap::class.java)
                        .addAnnotation(
                            AnnotationSpec.builder(
                                ClassName.bestGuess("com.bug.incrementalkapt.MultiBindKey")
                            ).addMember("value", CodeBlock.of("${element.simpleName}.class"))
                                .build()
                        )
                        .addParameter(ClassName.bestGuess("${element.packageString}.${element.simpleName}"), "dependency")
                        .returns(ClassName.bestGuess("com.bug.incrementalkapt.BaseMultiBindType"))
                        .build()
                    methods.add(method)
                }
                addMethods(methods)
            }
            .build()

        JavaFile.builder("com.bug.incrementalkapt", typeSpec)
            .build()
            .apply {
                writeJavaFile(this, "$className.java")
            }

        return true
    }

    private val Element.packageOf: PackageElement
        get() = (enclosingElement as? PackageElement) ?: enclosingElement.packageOf

    private val Element.packageString: String
        get() = packageOf.qualifiedName.toString()


    private fun writeJavaFile(outputFile: JavaFile, filename: String) {
        try {
            outputFile.writeTo(filer)
        } catch (e: Exception) {
            throw RuntimeException("Error generating $filename: $e")
        }
    }

}
