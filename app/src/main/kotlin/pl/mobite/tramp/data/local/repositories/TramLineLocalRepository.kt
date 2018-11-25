package pl.mobite.tramp.data.local.repositories

import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


interface TramLineLocalRepository {

    fun getTramLineFromDb(tramLineDesc: TramLineDesc): TramLine?

    fun getTramLineFromJson(tramLineDesc: TramLineDesc): TramLine?

    fun storeTramLineInDb(tramLineDesc: TramLineDesc, tramLine: TramLine)
}