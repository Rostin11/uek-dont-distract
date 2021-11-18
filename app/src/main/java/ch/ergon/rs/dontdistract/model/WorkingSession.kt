package ch.ergon.rs.dontdistract.model

import kotlinx.serialization.Serializable

@Serializable
class WorkingSession(val remainingSeconds: Int, val endDate: SessionDate, val name: String) {

}