package ch.ergon.rs.dontdistract.model

import kotlinx.serialization.Serializable

@Serializable
class SessionDate(val year: Int, val month: Int, val day: Int) : java.io.Serializable