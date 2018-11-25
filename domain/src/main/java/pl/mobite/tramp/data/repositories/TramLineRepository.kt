package pl.mobite.tramp.data.repositories

import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


interface TramLineRepository {

    @Throws(Throwable::class)
    fun getTramLineFromLocal(tramLineDesc: TramLineDesc): TramLine?

    @Throws(Throwable::class)
    fun getTramLineFromRemote(tramLineDesc: TramLineDesc): TramLine

}