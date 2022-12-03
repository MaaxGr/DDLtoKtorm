package de.maaxgr.ddltoktorm.backend.businesslogic

class SchemaGenerator() {

    fun readInDDL(schema: String): Table {
        val flatSql = schema.replace("\n", " ")

        val firstBracket = flatSql.indexOf("(")
        val lastBracket = flatSql.lastIndexOf(")")

        val prefixString = flatSql.substring(0, firstBracket).trim()

        val columnDefinitions = flatSql.substring(firstBracket + 1, lastBracket).trim()

        var databaseAndtableName = prefixString.split(" ").last()

        if (databaseAndtableName.contains(".")) {
            databaseAndtableName =  databaseAndtableName.split(".").last()
        }

        println(databaseAndtableName)

        val columns = columnDefinitions.split(",")

        val table = Table(databaseAndtableName, columns = columns.map { analyzeColumn(it) })
        return table
    }

    fun createKtormSchema(table: Table): GeneratedSchema {

        val capitalizeName = table.name.snakeToUpperCamelCase()

        val tableName = "DB${capitalizeName}Table"
        val entityName = "DB${capitalizeName}Entity"

        val lines1 = mutableListOf<String>()
        val lines2 = mutableListOf<String>()

        lines1.add("object $tableName: Table<$entityName>(\"${table.name}\") {")
        lines2.add("interface $entityName: Entity<$entityName> {")
        lines2.add("    companion object : Entity.Factory<$entityName>()")
        lines2.add("")

        table.columns.forEach { column ->
            val variableName = column.name.snakeToLowerCamelCase()

            lines1.add("    val $variableName = ${columnToString1(column, variableName)}")
            lines2.add("    val $variableName: ${columnToString2(column, variableName)}")
        }
        lines1.add("}")
        lines2.add("}")

        println()
        println()
        println()

        val output1 = lines1.joinToString(System.lineSeparator())
        println(output1)

        println()

        val output2 = lines2.joinToString(System.lineSeparator())
        println(output2)

        return GeneratedSchema(
            tableObjectString = output1,
            entityObjectString = output2
        )
    }

    data class GeneratedSchema(
        val tableObjectString: String,
        val entityObjectString: String
    )

    private fun columnToString1(column: Column, variableName: String): String {
        return when(column.type) {
            is ColumnType.Int -> "int(\"${column.name}\").bindTo { it.${variableName} }"
            is ColumnType.Timestamp -> "jdbcTimestamp(\"${column.name}\").bindTo { it.${variableName} }"
            is ColumnType.Varchar -> "varchar(\"${column.name}\").bindTo { it.${variableName} }"
            is ColumnType.Long -> "long(\"${column.name}\").bindTo { it.${variableName} }"
            else -> throw IllegalArgumentException("Type ${column.type}??")
        }
    }

    private fun columnToString2(column: Column, variableName: String): String {
        return when(column.type) {
            is ColumnType.Int -> "Int"
            is ColumnType.Timestamp -> "Timestamp"
            is ColumnType.Varchar -> "String"
            is ColumnType.Long -> "Long"
            else -> throw IllegalArgumentException("Type ${column.type}??")
        }
    }

    data class Table(
        val name: String,
        val columns: List<Column>
    )

    data class Column(
        val name: String,
        val type: ColumnType
    )

    sealed class ColumnType {
        object Int: ColumnType()
        object Timestamp: ColumnType()
        object Long: ColumnType()
        data class Varchar(val length: kotlin.Int): ColumnType()
    }

    fun analyzeColumn(columnString: String): Column {
        val columnParts = columnString.trim().split(" ").filter { it.isNotBlank() }

        val columnName = columnParts.first()
        val columnType = columnParts[1].toLowerCase()

        val columnTypeReal = when {
            columnType == "int" -> ColumnType.Int
            columnType == "timestamp" -> ColumnType.Timestamp
            columnType == "bigint" -> ColumnType.Long
            columnType in listOf("tinytext", "text") -> ColumnType.Varchar(-1)
            columnType.contains("varchar") -> ColumnType.Varchar(columnType.split("(", ")")[1].toInt())
            else -> throw IllegalArgumentException("Invalid type $columnType")
        }

        return Column(columnName, columnTypeReal)
    }

}