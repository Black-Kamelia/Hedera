package com.kamelia.jellyfish.core

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.util.pipeline.PipelineContext

inline fun <reified T : Any> Route.requestOrCatch(
    method: (String, suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit) -> Route,
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit,
): Route = apply {
    method(path) {
        runCatching {
            val body = call.receive<T>()
            block(body)
        }.getOrElse { e ->
            advisors.find { it.throwableClass.isInstance(e) }
                ?.let { it.handle(e, call) }
                ?: throw e
        }
    }
}

inline fun Route.requestOrCatch(
    method: (String, suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit) -> Route,
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
): Route = apply {
    method(path) {
        runCatching {
            block()
        }.getOrElse { e ->
            advisors.find { it.throwableClass.isInstance(e) }
                ?.let { it.handle(e, call) }
                ?: throw e
        }
    }
}

inline fun <reified T : Any> Route.getOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit,
) = requestOrCatch(
    { spath, pipeline -> get(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun Route.getOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = requestOrCatch(
    { spath, pipeline -> get(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun <reified T : Any> Route.postOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit,
) = requestOrCatch(
    { spath, pipeline -> post(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun Route.postOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = requestOrCatch(
    { spath, pipeline -> post(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun <reified T : Any> Route.putOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit,
) = requestOrCatch(
    { spath, pipeline -> put(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun Route.putOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = requestOrCatch(
    { spath, pipeline -> put(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun <reified T : Any> Route.deleteOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit,
) = requestOrCatch(
    { spath, pipeline -> delete(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun Route.deleteOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = requestOrCatch(
    { spath, pipeline -> delete(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun <reified T : Any> Route.patchOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit,
) = requestOrCatch(
    { spath, pipeline -> patch(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)

inline fun Route.patchOrCatch(
    vararg advisors: ExceptionAdvisor<Throwable> = BasicAdvisor,
    path: String = "",
    crossinline block: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = requestOrCatch(
    { spath, pipeline -> patch(spath, pipeline) },
    *advisors,
    path = path,
    block = block
)
