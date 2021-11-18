package ch.ergon.rs.dontdistract.model

import kotlinx.serialization.Serializable

@Serializable
class WorkingSession(val remainingMinutes: Int, val endDate: SessionDate, val name: String) {

}