package de.maaxgr.ddltoktorm.backend.userinterface.routes

import de.maaxgr.ddltoktorm.backend.businesslogic.SchemaGenerator
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.postGenerate() {
    post("/generate") {
        val ddl = call.receiveOrNull<String>()

        when (val result = GenerateCall(
            ddl = ddl
        ).run()) {
            is GenerateCall.CallResult.BadRequest -> call.respond(HttpStatusCode.BadRequest, result.message)
            is GenerateCall.CallResult.Ok -> call.respond(HttpStatusCode.OK, result.ktormTables)
        }

    }

}

class GenerateCall(
    private val ddl: String?
) {

    suspend fun run(): CallResult {
        if (ddl == null) {
            return CallResult.BadRequest("DDL is not passed via body!")
        }

        val generator = SchemaGenerator()
        val tableData = generator.readInDDL(ddl)

        val schema = generator.createKtormSchema(tableData)

        return CallResult.Ok(schema.tableObjectString + "\n\n" + schema.entityObjectString)
    }

    sealed class CallResult {
        data class BadRequest(val message: String) : CallResult()
        data class Ok(val ktormTables: String) : CallResult()
    }


    data class RequestBody(
        val dialect: String, val ddl: String
    )

}