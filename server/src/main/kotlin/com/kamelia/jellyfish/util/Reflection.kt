package com.kamelia.jellyfish.util

import io.github.classgraph.ClassGraph
import kotlin.reflect.KClass

fun findAnnotated(annotation: KClass<out Annotation>): List<KClass<*>> {
    val pack = "com.kamelia.jellyfish"
    val annotationName = annotation.java.canonicalName
    return ClassGraph()
        .enableAllInfo()
        .acceptPackages(pack)
        .scan()
        .use { scanResult ->
            scanResult.getClassesWithAnnotation(annotationName)
                .map {
                    it.loadClass().kotlin
                }
        }
}
