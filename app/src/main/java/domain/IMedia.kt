package domain

interface IMedia {
	fun id(): Int
	fun rated(): Float?
	fun poster(): String
	fun name(): String
//	fun idReal(): String
}